package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasRepository
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

data class GastoCategoria(
    val categoria: Categoria,
    val total: Long
)

data class DashboardUiState(
    val saldoInicial: Long = 0L,
    val saldoDisponible: Long = 0L,
    val totalIngresos: Long = 0L,
    val totalGastos: Long = 0L,
    val movimientosRecientes: List<Movimiento> = emptyList(),
    val gastosPorCategoria: List<GastoCategoria> = emptyList(),
    val categorias: List<Categoria> = emptyList()
)

class DashboardViewModel(
    private val repository: FinanzasRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = repository.state
        .map(::crearUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, crearUiState(repository.state.value))

    fun agregarMovimiento(
        descripcion: String,
        monto: Long,
        tipo: TipoMovimiento,
        categoriaId: Int,
        fecha: LocalDateTime = LocalDateTime.now(),
        medioPago: String = "Efectivo"
    ): Boolean = repository.agregarMovimiento(
        descripcion,
        monto,
        tipo,
        categoriaId,
        fecha,
        medioPago
    )

    fun eliminarMovimiento(id: String): Boolean = repository.eliminarMovimiento(id)

    companion object {
        fun factory(repository: FinanzasRepository): ViewModelProvider.Factory =
            simpleViewModelFactory { DashboardViewModel(repository) }

        fun crearUiState(state: FinanzasState): DashboardUiState {
            val ingresos = state.movimientos
                .filter { it.tipo == TipoMovimiento.INGRESO }
                .sumOf { it.monto }
            val gastos = state.movimientos
                .filter { it.tipo == TipoMovimiento.GASTO }
                .sumOf { it.monto }
            val gastosPorCategoria = state.categorias
                .filter { it.tipo == TipoMovimiento.GASTO }
                .map { categoria ->
                    GastoCategoria(
                        categoria = categoria,
                        total = state.movimientos
                            .filter { it.tipo == TipoMovimiento.GASTO && it.categoriaId == categoria.id }
                            .sumOf { it.monto }
                    )
                }

            return DashboardUiState(
                saldoInicial = state.saldoInicial,
                saldoDisponible = state.saldoInicial + ingresos - gastos,
                totalIngresos = ingresos,
                totalGastos = gastos,
                movimientosRecientes = state.movimientos.sortedByDescending { it.fecha }.take(5),
                gastosPorCategoria = gastosPorCategoria,
                categorias = state.categorias
            )
        }
    }
}
