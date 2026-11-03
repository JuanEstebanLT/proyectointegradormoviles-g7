package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetalleCategoriaViewModel : ViewModel() {

    // Lista de movimientos de la categoría
    private val _movimientos =
        MutableStateFlow<List<Movimiento>>(emptyList())

    // Lista observable desde la interfaz
    val movimientos: StateFlow<List<Movimiento>> =
        _movimientos.asStateFlow()

    // Filtrar movimientos por categoría
    fun filtrarPorCategoria(
        todosLosMovimientos: List<Movimiento>,
        categoria: String
    ) {
        if (categoria.isBlank()) {
            _movimientos.value = emptyList()
            return
        }

        _movimientos.value = todosLosMovimientos.filter {
            it.categoria.equals(categoria.trim(), ignoreCase = true)
        }
    }

    // Eliminar un movimiento
    fun eliminarMovimiento(id: String) {
        _movimientos.value = _movimientos.value.filter {
            it.id != id
        }
    }
}
