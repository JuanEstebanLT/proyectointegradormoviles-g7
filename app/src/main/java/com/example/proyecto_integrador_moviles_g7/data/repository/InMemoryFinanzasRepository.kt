package com.example.proyecto_integrador_moviles_g7.data.repository

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
        if (monto <= 0L) return false
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
            categoria.tipo != tipo
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

    override fun eliminarMovimiento(id: String): Boolean {
        if (id.isBlank() || _state.value.movimientos.none { it.id == id }) return false
        _state.update { state ->
            state.copy(movimientos = state.movimientos.filterNot { it.id == id })
        }
        return true
    }
}
