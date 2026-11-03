package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import java.time.LocalDateTime

object DatosFinancierosDeEjemplo {
    val categorias = listOf(
        Categoria(1, "Vivienda", TipoMovimiento.GASTO, 600_000L),
        Categoria(2, "Alimentación", TipoMovimiento.GASTO, 500_000L),
        Categoria(3, "Transporte", TipoMovimiento.GASTO, 200_000L),
        Categoria(4, "Ocio", TipoMovimiento.GASTO, 150_000L),
        Categoria(5, "Ingresos", TipoMovimiento.INGRESO)
    )

    private val fechaBase = LocalDateTime.of(2026, 11, 20, 12, 0)

    val movimientos = listOf(
        movimiento("ejemplo_1", "Arriendo apartamento", 400_000L, 1, fechaBase.minusDays(19), "Transferencia"),
        movimiento("ejemplo_2", "Servicios públicos", 50_000L, 1, fechaBase.minusDays(17), "Tarjeta débito"),
        movimiento("ejemplo_3", "Supermercado Éxito", 115_000L, 2, fechaBase.minusDays(1), "Tarjeta débito"),
        movimiento("ejemplo_4", "Almuerzo ejecutivo", 25_000L, 2, fechaBase.minusDays(2), "Efectivo"),
        movimiento("ejemplo_5", "Frutería y Verduras El Jardín", 38_000L, 2, fechaBase.minusDays(8), "Transferencia"),
        movimiento("ejemplo_6", "Panadería y Café La 70", 22_000L, 2, fechaBase.minusDays(15), "Efectivo"),
        movimiento("ejemplo_7", "Recarga tarjeta de transporte", 80_000L, 3, fechaBase.minusDays(10), "Efectivo"),
        movimiento("ejemplo_8", "Taxi al aeropuerto", 40_000L, 3, fechaBase.minusDays(5), "Efectivo"),
        movimiento("ejemplo_9", "Cine con amigos", 45_000L, 4, fechaBase.minusDays(4), "Tarjeta débito"),
        movimiento("ejemplo_10", "Suscripción de música", 35_000L, 4, fechaBase.minusDays(18), "Tarjeta débito"),
        movimiento("ejemplo_11", "Pago de nómina quincenal", 1_250_000L, 5, fechaBase.minusHours(3), "Transferencia", TipoMovimiento.INGRESO),
        movimiento("ejemplo_12", "Pago de nómina quincenal", 1_250_000L, 5, fechaBase.minusDays(15), "Transferencia", TipoMovimiento.INGRESO)
    )

    val state = FinanzasState(categorias = categorias, movimientos = movimientos)

    private fun movimiento(
        id: String,
        descripcion: String,
        monto: Long,
        categoriaId: Int,
        fecha: LocalDateTime,
        medioPago: String,
        tipo: TipoMovimiento = TipoMovimiento.GASTO
    ) = Movimiento(id, descripcion, monto, tipo, categoriaId, fecha, medioPago)
}
