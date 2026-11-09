package com.example.proyecto_integrador_moviles_g7.data.network

import com.example.proyecto_integrador_moviles_g7.data.dto.UsuarioDto

/**
 * Sesión autenticada en memoria. El interceptor de [ClienteApi] adjunta el token a cada llamada.
 * Se pierde al cerrar la app: hay que volver a iniciar sesión.
 */
object SesionActual {
    @Volatile var token: String? = null
        private set
    @Volatile var usuario: UsuarioDto? = null
        private set

    fun iniciar(token: String, usuario: UsuarioDto) {
        this.token = token
        this.usuario = usuario
    }

    fun actualizarUsuario(usuario: UsuarioDto) {
        this.usuario = usuario
    }

    fun cerrar() {
        token = null
        usuario = null
    }
}
