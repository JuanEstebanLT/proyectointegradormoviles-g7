package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.api.FinanzasApiService
import com.example.proyecto_integrador_moviles_g7.data.dto.InicioSesionDto
import com.example.proyecto_integrador_moviles_g7.data.dto.RegistroUsuarioDto
import com.example.proyecto_integrador_moviles_g7.data.dto.SesionDto
import com.example.proyecto_integrador_moviles_g7.data.dto.UsuarioDto
import com.example.proyecto_integrador_moviles_g7.data.model.Usuario
import com.example.proyecto_integrador_moviles_g7.data.network.ClienteApi
import com.example.proyecto_integrador_moviles_g7.data.network.SesionActual
import kotlinx.coroutines.CancellationException

/** Inicio de sesión y registro contra la API. Guarda el token en [SesionActual]. */
class AuthRepository(
    private val api: FinanzasApiService = ClienteApi.servicio
) {
    suspend fun iniciarSesion(correo: String, contrasena: String): Result<Usuario> =
        abrirSesion { api.iniciarSesion(InicioSesionDto(correo.trim(), contrasena)) }

    suspend fun registrar(nombre: String, correo: String, telefono: String, contrasena: String): Result<Usuario> =
        abrirSesion {
            api.registrar(
                RegistroUsuarioDto(nombre.trim(), correo.trim(), contrasena, telefono.trim().ifEmpty { null })
            )
        }

    fun cerrarSesion() = SesionActual.cerrar()

    private suspend fun abrirSesion(llamada: suspend () -> SesionDto): Result<Usuario> =
        try {
            val sesion = llamada()
            SesionActual.iniciar(sesion.token, sesion.usuario)
            Result.success(sesion.usuario.aModelo())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(ClienteApi.mensajeDeError(e), e))
        }
}

fun UsuarioDto.aModelo() = Usuario(
    id = id.toString(),
    nombre = nombre,
    correo = correo,
    telefono = telefono.orEmpty(),
    rol = rol
)
