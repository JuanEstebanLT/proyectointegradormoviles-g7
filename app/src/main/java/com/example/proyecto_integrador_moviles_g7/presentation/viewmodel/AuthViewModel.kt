package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyecto_integrador_moviles_g7.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuthViewModel : ViewModel() {

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
}
