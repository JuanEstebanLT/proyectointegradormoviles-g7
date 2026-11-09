package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_integrador_moviles_g7.data.model.Usuario
import com.example.proyecto_integrador_moviles_g7.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    /** Usuario autenticado; cuando deja de ser null la pantalla navega. */
    val usuario: Usuario? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun iniciarSesion(correo: String, contrasena: String) {
        val error = when {
            !correoValido(correo) -> "Ingresa un correo válido"
            contrasena.isEmpty() -> "Ingresa tu contraseña"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        ejecutar { authRepository.iniciarSesion(correo, contrasena) }
    }

    fun registrar(nombre: String, correo: String, telefono: String, contrasena: String) {
        val error = when {
            nombre.isBlank() -> "Ingresa tu nombre"
            !correoValido(correo) -> "Ingresa un correo válido"
            contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        ejecutar { authRepository.registrar(nombre, correo, telefono, contrasena) }
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun correoValido(correo: String) = Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()

    private fun ejecutar(accion: suspend () -> Result<Usuario>) {
        if (_uiState.value.cargando) return
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            accion().fold(
                onSuccess = { usuario -> _uiState.update { it.copy(cargando = false, usuario = usuario) } },
                onFailure = { e -> _uiState.update { it.copy(cargando = false, error = e.message) } }
            )
        }
    }
}
