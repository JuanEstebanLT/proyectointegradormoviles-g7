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

    /** Cambia descripción, monto y categoría (del mismo tipo). La fecha se conserva. */
    fun actualizarMovimiento(id: String, descripcion: String, monto: Long, categoriaId: Int): Boolean

    fun eliminarMovimiento(id: String): Boolean

    fun crearCategoria(nombre: String, tipo: TipoMovimiento, presupuesto: Long = 0L): Boolean

    /** El tipo no se puede cambiar: rompería la consistencia con sus movimientos. */
    fun actualizarCategoria(id: Int, nombre: String, presupuesto: Long): Boolean

    /** Solo se puede eliminar una categoría sin movimientos. */
    fun eliminarCategoria(id: Int): Boolean
}

/** Reglas compartidas por todas las implementaciones del repositorio. */
internal object ReglasFinanzas {
    const val LARGO_MAX_NOMBRE_CATEGORIA = 60

    fun nombreCategoriaValido(state: FinanzasState, nombre: String, excluirId: Int? = null): Boolean =
        nombre.isNotEmpty() &&
            nombre.length <= LARGO_MAX_NOMBRE_CATEGORIA &&
            state.categorias.none { it.id != excluirId && it.nombre.equals(nombre, ignoreCase = true) }

    /** Saldo inicial + ingresos - gastos. */
    fun saldoDisponible(state: FinanzasState): Long =
        state.saldoInicial + state.movimientos.sumOf { it.montoConSigno }

    /**
     * Saldo que quedaría al quitar [quitar] (si no es null) y agregar un movimiento
     * de [tipo] por [monto] (si [tipo] no es null).
     */
    fun saldoTrasCambio(state: FinanzasState, quitar: Movimiento?, tipo: TipoMovimiento?, monto: Long): Long {
        val base = saldoDisponible(state) - (quitar?.montoConSigno ?: 0L)
        return when (tipo) {
            null -> base
            TipoMovimiento.INGRESO -> base + monto
            TipoMovimiento.GASTO -> base - monto
        }
    }

    /**
     * No se permite que un cambio deje el saldo en negativo. Si el saldo ya era negativo
     * (datos anteriores a esta regla), se permiten los cambios que lo mejoran.
     */
    fun saldoPermitido(state: FinanzasState, saldoNuevo: Long): Boolean =
        saldoNuevo >= 0L || saldoNuevo >= saldoDisponible(state)

    fun tieneMovimientos(state: FinanzasState, categoriaId: Int): Boolean =
        state.movimientos.any { it.categoriaId == categoriaId }

    /** La nueva categoría de un movimiento debe existir y ser del mismo tipo. */
    fun edicionMovimientoValida(
        state: FinanzasState,
        movimiento: Movimiento,
        descripcion: String,
        monto: Long,
        categoriaId: Int
    ): Boolean {
        val categoria = state.categorias.firstOrNull { it.id == categoriaId }
        return descripcion.isNotEmpty() && monto > 0L && categoria != null && categoria.tipo == movimiento.tipo &&
            saldoPermitido(state, saldoTrasCambio(state, movimiento, movimiento.tipo, monto))
    }

    fun altaPermitida(state: FinanzasState, tipo: TipoMovimiento, monto: Long): Boolean =
        saldoPermitido(state, saldoTrasCambio(state, null, tipo, monto))

    fun bajaPermitida(state: FinanzasState, movimiento: Movimiento): Boolean =
        saldoPermitido(state, saldoTrasCambio(state, movimiento, null, 0L))

    /** Bajar el saldo inicial tampoco puede dejar el saldo disponible en negativo. */
    fun saldoInicialPermitido(state: FinanzasState, nuevoSaldoInicial: Long): Boolean =
        saldoPermitido(state, saldoDisponible(state) - state.saldoInicial + nuevoSaldoInicial)

    /** Saldo inicial más bajo que se acepta sin dejar el saldo disponible en negativo. */
    fun saldoInicialMinimo(state: FinanzasState): Long =
        state.saldoInicial - saldoDisponible(state).coerceAtLeast(0L)
}
