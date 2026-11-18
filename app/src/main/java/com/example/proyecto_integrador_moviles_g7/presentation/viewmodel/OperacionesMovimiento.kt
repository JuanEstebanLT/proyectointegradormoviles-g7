package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasRepository
import com.example.proyecto_integrador_moviles_g7.data.repository.ReglasFinanzas
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import java.time.LocalDateTime

/**
 * Altas, ediciones y bajas de movimientos con un mensaje claro cuando no se pueden hacer.
 * Cada función devuelve null si la operación se aplicó, o el motivo por el que se rechazó.
 * La regla principal: el saldo disponible nunca puede quedar en negativo.
 */
internal object OperacionesMovimiento {

    const val DATOS_INVALIDOS = "Completa una descripción, un monto positivo y una categoría válida."

    fun registrar(
        repository: FinanzasRepository,
        descripcion: String,
        monto: Long,
        tipo: TipoMovimiento,
        categoriaId: Int,
        fecha: LocalDateTime,
        medioPago: String
    ): String? {
        val state = repository.state.value
        if (monto > 0L && !ReglasFinanzas.altaPermitida(state, tipo, monto)) {
            return mensajeGasto(maximo = ReglasFinanzas.saldoDisponible(state), esEdicion = false)
        }
        return if (repository.agregarMovimiento(descripcion, monto, tipo, categoriaId, fecha, medioPago)) null
        else DATOS_INVALIDOS
    }

    fun editar(
        repository: FinanzasRepository,
        id: String,
        descripcion: String,
        monto: Long,
        categoriaId: Int
    ): String? {
        val state = repository.state.value
        val anterior = state.movimientos.firstOrNull { it.id == id } ?: return "Este movimiento ya no existe."
        if (monto > 0L) {
            val saldoNuevo = ReglasFinanzas.saldoTrasCambio(state, anterior, anterior.tipo, monto)
            if (!ReglasFinanzas.saldoPermitido(state, saldoNuevo)) {
                // Si el saldo ya era negativo, solo se permite no empeorarlo
                val disponible = ReglasFinanzas.saldoDisponible(state).coerceAtLeast(0L)
                return if (anterior.tipo == TipoMovimiento.GASTO) {
                    // Al editar un gasto, su monto anterior vuelve a estar disponible
                    mensajeGasto(maximo = anterior.monto + disponible, esEdicion = true)
                } else {
                    "Tu saldo quedaría en ${cop(saldoNuevo)}. " +
                        "Este ingreso debe ser de al menos ${cop(anterior.monto - disponible)}."
                }
            }
        }
        return if (repository.actualizarMovimiento(id, descripcion, monto, categoriaId)) null
        else "No se pudo guardar. Revisa la descripción, el monto y la categoría."
    }

    fun eliminar(repository: FinanzasRepository, id: String): String? {
        val state = repository.state.value
        val movimiento = state.movimientos.firstOrNull { it.id == id } ?: return null
        if (!ReglasFinanzas.bajaPermitida(state, movimiento)) {
            val saldoNuevo = ReglasFinanzas.saldoTrasCambio(state, movimiento, null, 0L)
            return "No puedes eliminar este ingreso: tu saldo quedaría en ${cop(saldoNuevo)}. " +
                "Primero elimina o reduce algún gasto."
        }
        return if (repository.eliminarMovimiento(id)) null
        else "Espera a que el movimiento termine de guardarse para eliminarlo."
    }

    private fun mensajeGasto(maximo: Long, esEdicion: Boolean): String = when {
        maximo <= 0L ->
            "Saldo insuficiente. Tu saldo disponible es ${cop(maximo)}; registra un ingreso antes de gastar."
        esEdicion ->
            "Saldo insuficiente. Este gasto puede ser como máximo de ${cop(maximo)}."
        else ->
            "Saldo insuficiente. Solo tienes ${cop(maximo)} disponibles; no puedes gastar más de eso."
    }

    private fun cop(valor: Long): String = (if (valor < 0L) "- " else "") + formatoCop(valor) + " COP"
}
