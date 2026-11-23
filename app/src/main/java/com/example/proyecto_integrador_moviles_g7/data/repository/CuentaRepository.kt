package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.api.FinanzasApiService
import com.example.proyecto_integrador_moviles_g7.data.dto.ActualizarConfiguracionDto
import com.example.proyecto_integrador_moviles_g7.data.dto.ActualizarPerfilDto
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
    private val api: FinanzasApiService = ClienteApi.servicio
) {
    private val _metaAhorro = MutableStateFlow(0L)
    /** Meta de ahorro mensual en pesos (0 = sin meta). */
    val metaAhorro: StateFlow<Long> = _metaAhorro.asStateFlow()

    private val _usuario = MutableStateFlow(SesionActual.usuario?.aModelo())
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _notificacionesLeidas = MutableStateFlow<Set<String>>(emptySet())
    /** Claves de las alertas que el usuario ya vio (solo durante la sesión). */
    val notificacionesLeidas: StateFlow<Set<String>> = _notificacionesLeidas.asStateFlow()

    fun marcarLeidas(claves: Collection<String>) {
        _notificacionesLeidas.value = _notificacionesLeidas.value + claves
    }

    /** Vuelve a leer el usuario guardado en la sesión (tras iniciar sesión o recargar). */
    fun sincronizarUsuario() {
        _usuario.value = SesionActual.usuario?.aModelo()
    }

    suspend fun cargarConfiguracion(): Result<Long> = llamar {
        val id = usuarioId()
        api.obtenerConfiguracion(id).metaAhorroMensual.roundToLong().also { _metaAhorro.value = it }
    }

    suspend fun guardarMetaAhorro(meta: Long): Result<Long> = llamar {
        val id = usuarioId()
        api.actualizarConfiguracion(id, ActualizarConfiguracionDto(meta.toDouble()))
            .metaAhorroMensual.roundToLong()
            .also { _metaAhorro.value = it }
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
