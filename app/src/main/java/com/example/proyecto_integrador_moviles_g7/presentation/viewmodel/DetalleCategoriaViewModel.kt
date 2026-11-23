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
import java.time.LocalDateTime

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

    /** Devuelve null si se registró, o el mensaje que explica por qué no (por ejemplo, saldo insuficiente). */
    fun agregarMovimiento(descripcion: String, monto: Long): String? {
        val categoria = repository.state.value.categorias.firstOrNull { it.id == categoriaId }
            ?: return "La categoría ya no existe."
        return OperacionesMovimiento.registrar(
            repository, descripcion, monto, categoria.tipo, categoriaId, LocalDateTime.now(), "Efectivo"
        )
    }

    /** Devuelve null si se guardó, o el motivo del rechazo. */
    fun actualizarMovimiento(id: String, descripcion: String, monto: Long, categoriaId: Int): String? =
        OperacionesMovimiento.editar(repository, id, descripcion, monto, categoriaId)

    /** Devuelve null si se eliminó, o el motivo del rechazo. */
    fun eliminarMovimiento(id: String): String? = OperacionesMovimiento.eliminar(repository, id)

    fun actualizarCategoria(nombre: String, presupuesto: Long): Boolean =
        repository.actualizarCategoria(categoriaId, nombre, presupuesto)

    /** Total de los movimientos de [tipo] (todas las categorías), opcionalmente solo de [mes]. */
    fun totalDelTipo(tipo: TipoMovimiento, mes: java.time.YearMonth?): Long =
        repository.state.value.movimientos
            .filter { it.tipo == tipo && (mes == null || java.time.YearMonth.from(it.fecha) == mes) }
            .sumOf { it.monto }

    /** Solo se puede eliminar si la categoría no tiene movimientos. */
    fun eliminarCategoria(): Boolean = repository.eliminarCategoria(categoriaId)

    companion object {
        fun factory(
            categoriaId: Int,
            repository: FinanzasRepository
        ): ViewModelProvider.Factory = simpleViewModelFactory {
            DetalleCategoriaViewModel(categoriaId, repository)
        }
    }
}
