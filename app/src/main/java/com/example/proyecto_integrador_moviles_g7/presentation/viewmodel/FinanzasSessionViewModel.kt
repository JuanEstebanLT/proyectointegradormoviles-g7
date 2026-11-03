package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyecto_integrador_moviles_g7.data.repository.InMemoryFinanzasRepository

/**
 * Conserva la única fuente en memoria durante la vida de la Activity, incluso
 * ante cambios de configuración. Los datos se pierden al finalizar el proceso.
 */
class FinanzasSessionViewModel : ViewModel() {
    val repository = InMemoryFinanzasRepository()
}
