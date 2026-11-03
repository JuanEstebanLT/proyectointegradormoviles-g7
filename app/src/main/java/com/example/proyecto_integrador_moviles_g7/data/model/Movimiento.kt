package com.example.proyecto_integrador_moviles_g7.data.model

import java.time.LocalDateTime

data class Movimiento(
    val id: String,
    val descripcion: String,
    /** Importe positivo expresado en pesos colombianos enteros. */
    val monto: Long,
    val tipo: TipoMovimiento,
    val categoriaId: Int,
    val fecha: LocalDateTime,
    val medioPago: String = "Efectivo"
) {
    init {
        require(id.isNotBlank()) { "El identificador del movimiento es obligatorio" }
        require(descripcion.isNotBlank()) { "La descripción es obligatoria" }
        require(monto > 0L) { "El monto debe ser mayor a cero" }
        require(categoriaId > 0) { "La categoría es obligatoria" }
    }

    val montoConSigno: Long
        get() = if (tipo == TipoMovimiento.GASTO) -monto else monto
}
