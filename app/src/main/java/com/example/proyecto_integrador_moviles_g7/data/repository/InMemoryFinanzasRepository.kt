package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.UUID

class InMemoryFinanzasRepository(
    initialState: FinanzasState = DatosFinancierosDeEjemplo.state,
    private val idProvider: () -> String = { UUID.randomUUID().toString() }
) : FinanzasRepository {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<FinanzasState> = _state.asStateFlow()

    override fun establecerSaldoInicial(monto: Long): Boolean {
        if (monto <= 0L || !ReglasFinanzas.saldoInicialPermitido(_state.value, monto)) return false
        _state.update { it.copy(saldoInicial = monto) }
        return true
    }

    override fun agregarMovimiento(
        descripcion: String,
        monto: Long,
        tipo: TipoMovimiento,
        categoriaId: Int,
        fecha: LocalDateTime,
        medioPago: String
    ): Boolean {
        val descripcionNormalizada = descripcion.trim()
        val medioPagoNormalizado = medioPago.trim()
        val categoria = _state.value.categorias.firstOrNull { it.id == categoriaId }
        if (
            descripcionNormalizada.isEmpty() ||
            medioPagoNormalizado.isEmpty() ||
            monto <= 0L ||
            categoria == null ||
            categoria.tipo != tipo ||
            !ReglasFinanzas.altaPermitida(_state.value, tipo, monto)
        ) return false

        val movimiento = Movimiento(
            id = idProvider(),
            descripcion = descripcionNormalizada,
            monto = monto,
            tipo = tipo,
            categoriaId = categoriaId,
            fecha = fecha,
            medioPago = medioPagoNormalizado
        )
        _state.update { it.copy(movimientos = it.movimientos + movimiento) }
        return true
    }

    override fun actualizarMovimiento(id: String, descripcion: String, monto: Long, categoriaId: Int): Boolean {
        val state = _state.value
        val movimiento = state.movimientos.firstOrNull { it.id == id } ?: return false
        val descripcionNormalizada = descripcion.trim()
        if (!ReglasFinanzas.edicionMovimientoValida(state, movimiento, descripcionNormalizada, monto, categoriaId)) {
            return false
        }
        val actualizado = movimiento.copy(descripcion = descripcionNormalizada, monto = monto, categoriaId = categoriaId)
        _state.update { it.copy(movimientos = it.movimientos.map { m -> if (m.id == id) actualizado else m }) }
        return true
    }

    override fun eliminarMovimiento(id: String): Boolean {
        val movimiento = _state.value.movimientos.firstOrNull { it.id == id } ?: return false
        if (!ReglasFinanzas.bajaPermitida(_state.value, movimiento)) return false
        _state.update { state ->
            state.copy(movimientos = state.movimientos.filterNot { it.id == id })
        }
        return true
    }

    override fun crearCategoria(nombre: String, tipo: TipoMovimiento, presupuesto: Long): Boolean {
        val nombreNormalizado = nombre.trim()
        val state = _state.value
        if (!ReglasFinanzas.nombreCategoriaValido(state, nombreNormalizado) || presupuesto < 0L) return false
        val id = (state.categorias.maxOfOrNull { it.id } ?: 0) + 1
        _state.update { it.copy(categorias = it.categorias + Categoria(id, nombreNormalizado, tipo, presupuesto)) }
        return true
    }

    override fun actualizarCategoria(id: Int, nombre: String, presupuesto: Long): Boolean {
        val nombreNormalizado = nombre.trim()
        val state = _state.value
        if (
            state.categorias.none { it.id == id } ||
            !ReglasFinanzas.nombreCategoriaValido(state, nombreNormalizado, excluirId = id) ||
            presupuesto < 0L
        ) return false
        _state.update {
            it.copy(categorias = it.categorias.map { c ->
                if (c.id == id) c.copy(nombre = nombreNormalizado, presupuesto = presupuesto) else c
            })
        }
        return true
    }

    override fun eliminarCategoria(id: Int): Boolean {
        val state = _state.value
        if (state.categorias.none { it.id == id } || ReglasFinanzas.tieneMovimientos(state, id)) return false
        _state.update { it.copy(categorias = it.categorias.filterNot { c -> c.id == id }) }
        return true
    }
}
