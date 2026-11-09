package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.api.FinanzasApiService
import com.example.proyecto_integrador_moviles_g7.data.dto.ActualizarUsuarioDto
import com.example.proyecto_integrador_moviles_g7.data.dto.CategoriaDto
import com.example.proyecto_integrador_moviles_g7.data.dto.CrearMovimientoDto
import com.example.proyecto_integrador_moviles_g7.data.dto.MovimientoDto
import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.data.network.ClienteApi
import com.example.proyecto_integrador_moviles_g7.data.network.SesionActual
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID
import kotlin.math.roundToLong

/**
 * Repositorio respaldado por la Finanzas G7 API. Mantiene el mismo contrato que
 * [InMemoryFinanzasRepository]: las escrituras se aplican de inmediato en el estado
 * (optimistas) y se envían a la API en segundo plano; si la API las rechaza se
 * revierten y el motivo se publica en [errores].
 */
class ApiFinanzasRepository(
    private val scope: CoroutineScope,
    private val api: FinanzasApiService = ClienteApi.servicio
) : FinanzasRepository {

    private val _state = MutableStateFlow(FinanzasState())
    override val state: StateFlow<FinanzasState> = _state.asStateFlow()

    private val _errores = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val errores: SharedFlow<String> = _errores.asSharedFlow()

    /** Descarga usuario, categorías y movimientos del usuario autenticado. */
    suspend fun cargar() = coroutineScope {
        val usuarioId = checkNotNull(SesionActual.usuario) { "No hay una sesión iniciada" }.id
        val usuario = async { api.obtenerUsuario(usuarioId) }
        val categorias = async { api.listarCategorias() }
        val movimientos = async { api.listarMovimientos() }

        SesionActual.actualizarUsuario(usuario.await())
        _state.value = FinanzasState(
            saldoInicial = usuario.await().saldoInicial.roundToLong(),
            categorias = categorias.await().map { it.aModelo() },
            movimientos = movimientos.await().map { it.aModelo() }
        )
    }

    fun recargar() = ejecutar(alFallar = {}) { cargar() }

    fun limpiar() {
        _state.value = FinanzasState()
    }

    override fun establecerSaldoInicial(monto: Long): Boolean {
        val usuarioId = SesionActual.usuario?.id ?: return false
        if (monto <= 0L) return false

        val anterior = _state.value.saldoInicial
        _state.update { it.copy(saldoInicial = monto) }
        ejecutar(alFallar = { _state.update { it.copy(saldoInicial = anterior) } }) {
            SesionActual.actualizarUsuario(api.actualizarUsuario(usuarioId, ActualizarUsuarioDto(monto.toDouble())))
        }
        return true
    }

    override fun agregarMovimiento(
        descripcion: String,
        monto: Long,
        tipo: TipoMovimiento,
        categoriaId: Int,
        fecha: LocalDateTime,
        medioPago: String
    ): Boolean {
        val descripcionNormalizada = descripcion.trim()
        val medioPagoNormalizado = medioPago.trim()
        val categoria = _state.value.categorias.firstOrNull { it.id == categoriaId }
        if (
            descripcionNormalizada.isEmpty() ||
            medioPagoNormalizado.isEmpty() ||
            monto <= 0L ||
            categoria == null ||
            categoria.tipo != tipo
        ) return false

        // Se muestra al instante con un id temporal y se reemplaza por el que asigne la API
        val temporal = Movimiento(
            id = "tmp-${UUID.randomUUID()}",
            descripcion = descripcionNormalizada,
            monto = monto,
            tipo = tipo,
            categoriaId = categoriaId,
            fecha = fecha,
            medioPago = medioPagoNormalizado
        )
        _state.update { it.copy(movimientos = it.movimientos + temporal) }

        ejecutar(alFallar = { quitarMovimiento(temporal.id) }) {
            val creado = api.crearMovimiento(
                CrearMovimientoDto(
                    categoriaId = categoriaId.toLong(),
                    tipo = tipo.name,
                    monto = monto.toDouble(),
                    descripcion = descripcionNormalizada,
                    medioPago = medioPagoNormalizado,
                    fechaMovimiento = fecha.toLocalDate().toString()
                )
            ).aModelo()
            _state.update { estado ->
                estado.copy(movimientos = estado.movimientos.map { if (it.id == temporal.id) creado else it })
            }
        }
        return true
    }

    override fun eliminarMovimiento(id: String): Boolean {
        val movimiento = _state.value.movimientos.firstOrNull { it.id == id } ?: return false
        // Un movimiento con id temporal todavía no existe en la API
        val idRemoto = id.toLongOrNull() ?: return false

        quitarMovimiento(id)
        ejecutar(alFallar = { _state.update { it.copy(movimientos = it.movimientos + movimiento) } }) {
            val respuesta = api.eliminarMovimiento(idRemoto)
            if (!respuesta.isSuccessful && respuesta.code() != 404) throw HttpException(respuesta)
        }
        return true
    }

    private fun quitarMovimiento(id: String) {
        _state.update { estado -> estado.copy(movimientos = estado.movimientos.filterNot { it.id == id }) }
    }

    private fun ejecutar(alFallar: () -> Unit, accion: suspend () -> Unit) {
        scope.launch {
            try {
                accion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                alFallar()
                _errores.tryEmit(ClienteApi.mensajeDeError(e))
            }
        }
    }
}

private fun CategoriaDto.aModelo() = Categoria(
    id = id.toInt(),
    nombre = nombre,
    tipo = TipoMovimiento.valueOf(tipo),
    presupuesto = presupuestoMensual.roundToLong()
)

private fun MovimientoDto.aModelo(): Movimiento {
    // La API guarda la fecha del movimiento; la hora se toma del momento en que se registró
    val hora = runCatching {
        OffsetDateTime.parse(creadoEn).atZoneSameInstant(ZoneId.systemDefault()).toLocalTime()
    }.getOrDefault(LocalTime.NOON)

    return Movimiento(
        id = id.toString(),
        descripcion = descripcion,
        monto = monto.roundToLong().coerceAtLeast(1L),
        tipo = TipoMovimiento.valueOf(tipo),
        categoriaId = categoriaId.toInt(),
        fecha = LocalDate.parse(fechaMovimiento).atTime(hora),
        medioPago = medioPago
    )
}
