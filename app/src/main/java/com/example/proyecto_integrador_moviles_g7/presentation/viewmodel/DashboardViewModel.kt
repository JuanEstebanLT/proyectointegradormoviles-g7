package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.presentation.screens.categoriasEjemplo
import com.example.proyecto_integrador_moviles_g7.presentation.screens.movimientosEjemplo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class DashboardViewModel : ViewModel() {

    private val _movimientos = MutableStateFlow(
        movimientosEjemplo.mapIndexed { index, movimiento ->

            val categoria = categoriasEjemplo.firstOrNull {
                it.id == movimiento.categoriaId
            }?.nombre ?: "Sin categoría"

            Movimiento(
                id = "ejemplo_${index + 1}",
                descripcion = movimiento.titulo,
                monto = movimiento.monto.toDouble(),
                tipo = if (movimiento.monto < 0) "Gasto" else "Ingreso",
                categoria = categoria,
                fecha = movimiento.detalle
            )
        }
    )

    val movimientos: StateFlow<List<Movimiento>> =
        _movimientos.asStateFlow()

    fun agregarMovimiento(
        descripcion: String,
        monto: Double,
        tipo: String,
        categoria: String,
        fecha: String
    ) {
        if (
            descripcion.isBlank() ||
            categoria.isBlank() ||
            fecha.isBlank() ||
            monto <= 0 ||
            (tipo != "Gasto" && tipo != "Ingreso")
        ) {
            return
        }

        val nuevoMovimiento = Movimiento(
            id = UUID.randomUUID().toString(),
            descripcion = descripcion.trim(),
            monto = if (tipo == "Gasto") -monto else monto,
            tipo = tipo,
            categoria = categoria.trim(),
            fecha = fecha
        )

        _movimientos.value = _movimientos.value + nuevoMovimiento
    }

    fun eliminarMovimiento(id: String) {
        _movimientos.value = _movimientos.value.filter {
            it.id != id
        }
    }
}
