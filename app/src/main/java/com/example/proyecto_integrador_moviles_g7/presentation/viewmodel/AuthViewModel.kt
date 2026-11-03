package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyecto_integrador_moviles_g7.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class AuthUiState(
    val esDemostracion: Boolean = true,
    val accesoDemostracionRealizado: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Lista de usuarios registrados
    private val _usuarios =
        MutableStateFlow<List<Usuario>>(emptyList())

    // Lista observable desde la interfaz
    val usuarios: StateFlow<List<Usuario>> =
        _usuarios.asStateFlow()

    // Registrar un usuario
    fun registrarUsuario(
        nombre: String,
        correo: String,
        telefono: String
    ): Boolean {

        if (
            nombre.isBlank() ||
            correo.isBlank() ||
            telefono.isBlank()
        ) {
            return false
        }

        val correoExiste = _usuarios.value.any {
            it.correo.equals(correo.trim(), ignoreCase = true)
        }

        if (correoExiste) {
            return false
        }

        val nuevoUsuario = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = nombre.trim(),
            correo = correo.trim(),
            telefono = telefono.trim()
        )

        _usuarios.value = _usuarios.value + nuevoUsuario

        return true
    }

    // Eliminar un usuario
    fun eliminarUsuario(id: String) {
        _usuarios.value = _usuarios.value.filter {
            it.id != id
        }
    }

    /**
     * Habilita únicamente el flujo local de demostración. No valida credenciales,
     * no crea una sesión y no representa autenticación real.
     */
    fun iniciarAccesoDemostracion() {
        _uiState.update { it.copy(accesoDemostracionRealizado = true) }
    }
}
