package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_integrador_moviles_g7.data.repository.ApiFinanzasRepository
import com.example.proyecto_integrador_moviles_g7.data.repository.AuthRepository

/**
 * Conserva el repositorio de la sesión durante la vida de la Activity, incluso ante
 * cambios de configuración. Los datos viven en la Finanzas G7 API.
 */
class FinanzasSessionViewModel : ViewModel() {
    val repository = ApiFinanzasRepository(viewModelScope)
    private val authRepository = AuthRepository()

    fun cerrarSesion() {
        authRepository.cerrarSesion()
        repository.limpiar()
    }
}
