package com.example.proyecto_integrador_moviles_g7.data.network

import com.example.proyecto_integrador_moviles_g7.data.dto.UsuarioDto
import com.example.proyecto_integrador_moviles_g7.data.local.AlmacenSesion
import com.example.proyecto_integrador_moviles_g7.data.local.ProveedorLocal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Sesión autenticada. El interceptor de [ClienteApi] adjunta el token a cada llamada.
 * Se mantiene en memoria para usarla rápido y se guarda en el teléfono (DataStore) para
 * no tener que iniciar sesión al reabrir la app.
 */
object SesionActual {
    @Volatile var token: String? = null
        private set
    @Volatile var usuario: UsuarioDto? = null
        private set

    private val _expirada = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    /** Emite cuando la API responde 401 con una sesión iniciada (token vencido o inválido). */
    val expirada: SharedFlow<Unit> = _expirada.asSharedFlow()

    fun iniciar(token: String, usuario: UsuarioDto) {
        this.token = token
        this.usuario = usuario
        persistir { it.guardar(token, usuario) }
    }

    fun actualizarUsuario(usuario: UsuarioDto) {
        this.usuario = usuario
        persistir { it.actualizarUsuario(usuario) }
    }

    fun cerrar() {
        token = null
        usuario = null
        persistir { it.borrar() }
    }

    /** Recupera la sesión guardada en el teléfono. Devuelve true si había una. */
    suspend fun restaurar(): Boolean {
        val (tokenGuardado, usuarioGuardado) = ProveedorLocal.almacenSesion?.leer() ?: return false
        token = tokenGuardado
        usuario = usuarioGuardado
        return true
    }

    fun notificarExpirada() {
        if (token != null) _expirada.tryEmit(Unit)
    }

    private fun persistir(accion: suspend (AlmacenSesion) -> Unit) {
        val almacen = ProveedorLocal.almacenSesion ?: return
        ProveedorLocal.scope.launch {
            try {
                accion(almacen)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Si no se puede guardar, la sesión sigue funcionando en memoria
            }
        }
    }
}
