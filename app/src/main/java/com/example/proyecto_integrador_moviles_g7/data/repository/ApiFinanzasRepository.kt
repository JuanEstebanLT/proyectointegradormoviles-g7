package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.api.FinanzasApiService
import com.example.proyecto_integrador_moviles_g7.data.dto.ActualizarCategoriaDto
import com.example.proyecto_integrador_moviles_g7.data.dto.ActualizarMovimientoDto
import com.example.proyecto_integrador_moviles_g7.data.dto.ActualizarUsuarioDto
import com.example.proyecto_integrador_moviles_g7.data.dto.CategoriaDto
import com.example.proyecto_integrador_moviles_g7.data.dto.CrearCategoriaDto
import com.example.proyecto_integrador_moviles_g7.data.dto.CrearMovimientoDto
import com.example.proyecto_integrador_moviles_g7.data.dto.MovimientoDto
import com.example.proyecto_integrador_moviles_g7.data.local.CacheFinanzas
import com.example.proyecto_integrador_moviles_g7.data.local.ProveedorLocal
import java.io.IOException
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
    private val api: FinanzasApiService = ClienteApi.servicio,
    /** Copia local en Room (null en tests o si no se inició el almacenamiento). */
    private val cache: CacheFinanzas? = ProveedorLocal.cache
) : FinanzasRepository {

    private val _state = MutableStateFlow(FinanzasState())
    override val state: StateFlow<FinanzasState> = _state.asStateFlow()

    private val _errores = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val errores: SharedFlow<String> = _errores.asSharedFlow()

    private val _cargando = MutableStateFlow(false)
    /** true mientras se descargan los datos de la sesión. */
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _sinConexion = MutableStateFlow(false)
    /** true si la última llamada a la API falló por red: se muestran los datos guardados. */
    val sinConexion: StateFlow<Boolean> = _sinConexion.asStateFlow()

    private val _ultimaSincronizacion = MutableStateFlow<Long?>(null)
    /** Momento (epoch millis) de la última sincronización con la API, o null si no hubo. */
    val ultimaSincronizacion: StateFlow<Long?> = _ultimaSincronizacion.asStateFlow()

    /**
     * Primero muestra los datos guardados en el teléfono (si los hay) y luego descarga
     * usuario, categorías y movimientos de la API y actualiza la copia local.
     */
    suspend fun cargar() = coroutineScope {
        val usuarioId = checkNotNull(SesionActual.usuario) { "No hay una sesión iniciada" }.id

        val estado = _state.value
        if (estado.categorias.isEmpty() && estado.movimientos.isEmpty()) {
            leerCache(usuarioId)?.let { guardados ->
                _state.value = guardados.state
                _ultimaSincronizacion.value = guardados.sincronizadoEn
            }
        }

        val usuario = async { api.obtenerUsuario(usuarioId) }
        val categorias = async { api.listarCategorias() }
        val movimientos = async { api.listarMovimientos() }

        SesionActual.actualizarUsuario(usuario.await())
        _state.value = FinanzasState(
            saldoInicial = usuario.await().saldoInicial.roundToLong(),
            categorias = categorias.await().map { it.aModelo() },
            movimientos = movimientos.await().map { it.aModelo() }
        )
        _sinConexion.value = false
        guardarCache()
    }

    fun recargar() {
        _cargando.value = true
        scope.launch {
            try {
                cargar()
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _sinConexion.value = true
                val hayDatos = _state.value.categorias.isNotEmpty() || _state.value.movimientos.isNotEmpty()
                _errores.tryEmit(
                    if (hayDatos) "Sin conexión: estás viendo los datos guardados en tu teléfono."
                    else ClienteApi.mensajeDeError(e)
                )
            } catch (e: Exception) {
                _errores.tryEmit(ClienteApi.mensajeDeError(e))
            } finally {
                _cargando.value = false
            }
        }
    }

    /** Limpia la memoria. Los datos guardados se borran aparte con [borrarDatosLocales]. */
    fun limpiar() {
        _state.value = FinanzasState()
        _sinConexion.value = false
        _ultimaSincronizacion.value = null
    }

    /** Borra la copia local del usuario (al cerrar sesión). */
    suspend fun borrarDatosLocales(usuarioId: Long) {
        try {
            cache?.borrar(usuarioId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Si falla, la caché se reemplaza en la próxima sincronización de ese usuario
        }
    }

    private suspend fun leerCache(usuarioId: Long) =
        try {
            cache?.leer(usuarioId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

    /** Guarda el estado actual en el teléfono (tras sincronizar o tras un cambio confirmado). */
    private suspend fun guardarCache() {
        val usuarioId = SesionActual.usuario?.id ?: return
        try {
            cache?.guardar(usuarioId, _state.value)
            _ultimaSincronizacion.value = System.currentTimeMillis()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // La caché es opcional: si falla, la app sigue funcionando con la API
        }
    }

    override fun establecerSaldoInicial(monto: Long): Boolean {
        val usuarioId = SesionActual.usuario?.id ?: return false
        if (monto <= 0L || !ReglasFinanzas.saldoInicialPermitido(_state.value, monto)) return false

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
            categoria.tipo != tipo ||
            !ReglasFinanzas.altaPermitida(_state.value, tipo, monto)
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
        if (!ReglasFinanzas.bajaPermitida(_state.value, movimiento)) return false

        quitarMovimiento(id)
        ejecutar(alFallar = { _state.update { it.copy(movimientos = it.movimientos + movimiento) } }) {
            val respuesta = api.eliminarMovimiento(idRemoto)
            if (!respuesta.isSuccessful && respuesta.code() != 404) throw HttpException(respuesta)
        }
        return true
    }

    override fun actualizarMovimiento(id: String, descripcion: String, monto: Long, categoriaId: Int): Boolean {
        val state = _state.value
        val anterior = state.movimientos.firstOrNull { it.id == id } ?: return false
        // Un movimiento con id temporal todavía no existe en la API
        val idRemoto = id.toLongOrNull() ?: return false
        val descripcionNormalizada = descripcion.trim()
        if (!ReglasFinanzas.edicionMovimientoValida(state, anterior, descripcionNormalizada, monto, categoriaId)) {
            return false
        }

        reemplazarMovimiento(anterior.copy(descripcion = descripcionNormalizada, monto = monto, categoriaId = categoriaId))
        ejecutar(alFallar = { reemplazarMovimiento(anterior) }) {
            val actualizado = api.actualizarMovimiento(
                idRemoto,
                ActualizarMovimientoDto(
                    categoriaId = categoriaId.toLong().takeIf { categoriaId != anterior.categoriaId },
                    monto = monto.toDouble(),
                    descripcion = descripcionNormalizada
                )
            ).aModelo()
            reemplazarMovimiento(actualizado)
        }
        return true
    }

    override fun crearCategoria(nombre: String, tipo: TipoMovimiento, presupuesto: Long): Boolean {
        val nombreNormalizado = nombre.trim()
        if (!ReglasFinanzas.nombreCategoriaValido(_state.value, nombreNormalizado) || presupuesto < 0L) return false

        // La categoría aparece cuando la API confirma su id (no hay ids temporales para categorías)
        ejecutar(alFallar = {}) {
            val creada = api.crearCategoria(
                CrearCategoriaDto(nombreNormalizado, tipo.name, presupuesto.toDouble())
            ).aModelo()
            _state.update { estado ->
                if (estado.categorias.any { it.id == creada.id }) estado
                else estado.copy(categorias = estado.categorias + creada)
            }
        }
        return true
    }

    override fun actualizarCategoria(id: Int, nombre: String, presupuesto: Long): Boolean {
        val nombreNormalizado = nombre.trim()
        val state = _state.value
        val anterior = state.categorias.firstOrNull { it.id == id } ?: return false
        if (!ReglasFinanzas.nombreCategoriaValido(state, nombreNormalizado, excluirId = id) || presupuesto < 0L) {
            return false
        }

        reemplazarCategoria(anterior.copy(nombre = nombreNormalizado, presupuesto = presupuesto))
        ejecutar(alFallar = { reemplazarCategoria(anterior) }) {
            reemplazarCategoria(
                api.actualizarCategoria(
                    id.toLong(),
                    ActualizarCategoriaDto(nombreNormalizado, presupuesto.toDouble())
                ).aModelo()
            )
        }
        return true
    }

    override fun eliminarCategoria(id: Int): Boolean {
        val state = _state.value
        val indice = state.categorias.indexOfFirst { it.id == id }
        if (indice < 0 || ReglasFinanzas.tieneMovimientos(state, id)) return false
        val categoria = state.categorias[indice]

        _state.update { it.copy(categorias = it.categorias.filterNot { c -> c.id == id }) }
        ejecutar(alFallar = {
            _state.update { estado ->
                val lista = estado.categorias.toMutableList()
                lista.add(indice.coerceAtMost(lista.size), categoria)
                estado.copy(categorias = lista)
            }
        }) {
            val respuesta = api.eliminarCategoria(id.toLong())
            if (!respuesta.isSuccessful && respuesta.code() != 404) throw HttpException(respuesta)
        }
        return true
    }

    private fun reemplazarMovimiento(movimiento: Movimiento) {
        _state.update { estado ->
            estado.copy(movimientos = estado.movimientos.map { if (it.id == movimiento.id) movimiento else it })
        }
    }

    private fun reemplazarCategoria(categoria: Categoria) {
        _state.update { estado ->
            estado.copy(categorias = estado.categorias.map { if (it.id == categoria.id) categoria else it })
        }
    }

    private fun quitarMovimiento(id: String) {
        _state.update { estado -> estado.copy(movimientos = estado.movimientos.filterNot { it.id == id }) }
    }

    private fun ejecutar(alFallar: () -> Unit, accion: suspend () -> Unit) {
        scope.launch {
            try {
                accion()
                // La API confirmó el cambio: hay conexión y se actualiza la copia local
                _sinConexion.value = false
                guardarCache()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                alFallar()
                if (e is IOException) {
                    _sinConexion.value = true
                    _errores.tryEmit("Sin conexión: el cambio no se guardó. Inténtalo de nuevo cuando tengas internet.")
                } else {
                    _errores.tryEmit(ClienteApi.mensajeDeError(e))
                }
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
