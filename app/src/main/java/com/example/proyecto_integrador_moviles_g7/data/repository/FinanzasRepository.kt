package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

data class FinanzasState(
    val saldoInicial: Long = 0L,
    val categorias: List<Categoria> = emptyList(),
    val movimientos: List<Movimiento> = emptyList()
)

interface FinanzasRepository {
    val state: StateFlow<FinanzasState>

    fun establecerSaldoInicial(monto: Long): Boolean

    fun agregarMovimiento(
        descripcion: String,
        monto: Long,
        tipo: TipoMovimiento,
        categoriaId: Int,
        fecha: LocalDateTime = LocalDateTime.now(),
        medioPago: String = "Efectivo"
    ): Boolean

    fun eliminarMovimiento(id: String): Boolean
}
