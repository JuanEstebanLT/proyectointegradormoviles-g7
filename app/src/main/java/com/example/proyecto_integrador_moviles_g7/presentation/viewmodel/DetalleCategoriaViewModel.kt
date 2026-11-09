package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DetalleCategoriaUiState(
    val categoria: Categoria? = null,
    val movimientos: List<Movimiento> = emptyList(),
    val gastoCategoria: Long = 0L,
    val totalGastos: Long = 0L
) {
    val promedio: Long
        get() = if (movimientos.isEmpty()) 0L else gastoCategoria / movimientos.size

    val porcentajeDelTotal: Float
        get() = if (totalGastos == 0L) 0f else gastoCategoria * 100f / totalGastos
}

class DetalleCategoriaViewModel(
    private val categoriaId: Int,
    private val repository: FinanzasRepository
) : ViewModel() {

    val uiState: StateFlow<DetalleCategoriaUiState> = repository.state
        .map { state ->
            val categoria = state.categorias.firstOrNull { it.id == categoriaId }
            val movimientos = state.movimientos
                .filter { it.categoriaId == categoriaId }
                .sortedByDescending { it.fecha }
            DetalleCategoriaUiState(
                categoria = categoria,
                movimientos = movimientos,
                gastoCategoria = movimientos
                    .filter { it.tipo == TipoMovimiento.GASTO }
                    .sumOf { it.monto },
                totalGastos = state.movimientos
                    .filter { it.tipo == TipoMovimiento.GASTO }
                    .sumOf { it.monto }
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DetalleCategoriaUiState())

    fun agregarMovimiento(descripcion: String, monto: Long): Boolean {
        val categoria = repository.state.value.categorias.firstOrNull { it.id == categoriaId }
            ?: return false
        return repository.agregarMovimiento(descripcion, monto, categoria.tipo, categoriaId)
    }

    fun eliminarMovimiento(id: String): Boolean = repository.eliminarMovimiento(id)

    companion object {
        fun factory(
            categoriaId: Int,
            repository: FinanzasRepository
        ): ViewModelProvider.Factory = simpleViewModelFactory {
            DetalleCategoriaViewModel(categoriaId, repository)
        }
    }
}
