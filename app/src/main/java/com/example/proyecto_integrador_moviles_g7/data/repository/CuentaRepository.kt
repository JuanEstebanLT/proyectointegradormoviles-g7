package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.api.FinanzasApiService
import com.example.proyecto_integrador_moviles_g7.data.dto.ActualizarConfiguracionDto
import com.example.proyecto_integrador_moviles_g7.data.dto.ActualizarPerfilDto
import com.example.proyecto_integrador_moviles_g7.data.local.AlmacenSesion
import com.example.proyecto_integrador_moviles_g7.data.local.CacheFinanzas
import com.example.proyecto_integrador_moviles_g7.data.local.ProveedorLocal
import kotlinx.coroutines.launch
import com.example.proyecto_integrador_moviles_g7.data.model.Sugerencia
import com.example.proyecto_integrador_moviles_g7.data.model.Usuario
import com.example.proyecto_integrador_moviles_g7.data.network.ClienteApi
import com.example.proyecto_integrador_moviles_g7.data.network.SesionActual
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToLong

/**
 * Datos de la cuenta que no son movimientos ni categorías: perfil, meta de ahorro,
 * sugerencias y (para administradores) la lista de usuarios.
 * Cada operación devuelve un [Result] con un mensaje de error listo para mostrar.
 */
class CuentaRepository(
    private val api: FinanzasApiService = ClienteApi.servicio,
    private val cache: CacheFinanzas? = ProveedorLocal.cache,
    private val almacen: AlmacenSesion? = ProveedorLocal.almacenSesion
) {
    private val _metaAhorro = MutableStateFlow(0L)
    /** Meta de ahorro mensual en pesos (0 = sin meta). */
    val metaAhorro: StateFlow<Long> = _metaAhorro.asStateFlow()

    private val _usuario = MutableStateFlow(SesionActual.usuario?.aModelo())
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _notificacionesLeidas = MutableStateFlow<Set<String>>(emptySet())
    /** Claves de las alertas que el usuario ya vio (se guardan en el teléfono). */
    val notificacionesLeidas: StateFlow<Set<String>> = _notificacionesLeidas.asStateFlow()

    fun marcarLeidas(claves: Collection<String>) {
        val nuevas = _notificacionesLeidas.value + claves
        if (nuevas == _notificacionesLeidas.value) return
        _notificacionesLeidas.value = nuevas
        val id = _usuario.value?.id ?: return
        almacen?.let { a -> ProveedorLocal.scope.launch { intentar { a.guardarNotificacionesLeidas(id, nuevas) } } }
    }

    /** Vuelve a leer el usuario guardado en la sesión (tras iniciar sesión o recargar). */
    fun sincronizarUsuario() {
        _usuario.value = SesionActual.usuario?.aModelo()
    }

    /** Carga lo guardado en el teléfono (meta y notificaciones leídas) para mostrarlo al instante. */
    suspend fun cargarDatosLocales() {
        val usuario = SesionActual.usuario ?: return
        intentar { cache?.metaAhorro(usuario.id)?.let { _metaAhorro.value = it } }
        intentar { almacen?.notificacionesLeidas(usuario.id.toString())?.let { _notificacionesLeidas.value = it } }
    }

    suspend fun cargarConfiguracion(): Result<Long> = llamar {
        val id = usuarioId()
        api.obtenerConfiguracion(id).metaAhorroMensual.roundToLong().also {
            _metaAhorro.value = it
            intentar { cache?.guardarMeta(id, it) }
        }
    }

    suspend fun guardarMetaAhorro(meta: Long): Result<Long> = llamar {
        val id = usuarioId()
        api.actualizarConfiguracion(id, ActualizarConfiguracionDto(meta.toDouble()))
            .metaAhorroMensual.roundToLong()
            .also {
                _metaAhorro.value = it
                intentar { cache?.guardarMeta(id, it) }
            }
    }

    /** Las escrituras locales son opcionales: si fallan, la app sigue con los datos de la API. */
    private suspend fun intentar(accion: suspend () -> Unit) {
        try {
            accion()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Se ignora: se reintentará en la próxima sincronización
        }
    }

    suspend fun actualizarPerfil(nombre: String, telefono: String): Result<Usuario> = llamar {
        val actualizado = api.actualizarPerfil(usuarioId(), ActualizarPerfilDto(nombre.trim(), telefono.trim()))
        SesionActual.actualizarUsuario(actualizado)
        actualizado.aModelo().also { _usuario.value = it }
    }

    suspend fun sugerencias(): Result<List<Sugerencia>> = llamar {
        api.listarSugerencias().map { dto ->
            Sugerencia(dto.id, dto.titulo, dto.mensaje, dto.categorias.map { it.nombre })
        }
    }

    suspend fun usuarios(): Result<List<Usuario>> = llamar {
        api.listarUsuarios().map { it.aModelo() }
    }

    fun limpiar() {
        _metaAhorro.value = 0L
        _usuario.value = null
        _notificacionesLeidas.value = emptySet()
    }

    private fun usuarioId(): Long = checkNotNull(SesionActual.usuario) { "No hay una sesión iniciada" }.id

    private suspend fun <T> llamar(accion: suspend () -> T): Result<T> =
        try {
            Result.success(accion())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(ClienteApi.mensajeDeError(e), e))
        }
}
