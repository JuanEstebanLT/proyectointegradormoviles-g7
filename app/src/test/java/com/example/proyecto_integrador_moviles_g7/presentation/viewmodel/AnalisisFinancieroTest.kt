package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.YearMonth

class AnalisisFinancieroTest {

    private val mes = YearMonth.of(2026, 11)
    private val comida = Categoria(1, "Alimentación", TipoMovimiento.GASTO, presupuesto = 100_000L)
    private val transporte = Categoria(2, "Transporte", TipoMovimiento.GASTO, presupuesto = 50_000L)
    private val salario = Categoria(3, "Salario", TipoMovimiento.INGRESO)

    private fun mov(id: String, monto: Long, tipo: TipoMovimiento, categoria: Int, mesMov: YearMonth = mes) =
        Movimiento(id, "Mov $id", monto, tipo, categoria, mesMov.atDay(10).atTime(12, 0))

    private fun estado(vararg movimientos: Movimiento) = FinanzasState(
        saldoInicial = 1_000_000L,
        categorias = listOf(comida, transporte, salario),
        movimientos = movimientos.toList()
    )

    @Test
    fun `resumen del mes solo cuenta movimientos de ese mes`() {
        val state = estado(
            mov("1", 500_000L, TipoMovimiento.INGRESO, 3),
            mov("2", 90_000L, TipoMovimiento.GASTO, 1),
            mov("3", 70_000L, TipoMovimiento.GASTO, 1, mes.minusMonths(1))
        )
        val resumen = AnalisisFinanciero.resumenMes(state, mes)

        assertEquals(500_000L, resumen.ingresos)
        assertEquals(90_000L, resumen.gastos)
        // Sin abonos a la meta no hay ahorro, aunque sobre dinero
        assertEquals(0L, resumen.ahorro)
        assertEquals(410_000L, resumen.balance)
        val usoComida = resumen.gastosPorCategoria.first { it.categoria.id == 1 }
        assertEquals(90f, usoComida.usoPresupuesto!!, 0.01f)
        assertEquals(100f, usoComida.participacion, 0.01f)
    }

    @Test
    fun `historico devuelve seis meses del mas antiguo al mas reciente`() {
        val historico = AnalisisFinanciero.historico(estado(), mes)
        assertEquals(6, historico.size)
        assertEquals(mes.minusMonths(5), historico.first().mes)
        assertEquals(mes, historico.last().mes)
    }

    @Test
    fun `avisa presupuesto excedido y cercano al limite`() {
        val state = estado(
            mov("1", 120_000L, TipoMovimiento.GASTO, 1),
            mov("2", 45_000L, TipoMovimiento.GASTO, 2)
        )
        val tipos = AnalisisFinanciero.notificaciones(state, metaAhorro = 0L, mes = mes).map { it.tipo }

        assertTrue(TipoNotificacion.PRESUPUESTO_EXCEDIDO in tipos)
        assertTrue(TipoNotificacion.PRESUPUESTO_CERCANO in tipos)
    }

    @Test
    fun `los abonos a la meta descuentan el saldo pero no cuentan como gasto`() {
        val ahorro = Categoria(4, "Ahorro", TipoMovimiento.GASTO)
        val state = estado(
            mov("1", 600_000L, TipoMovimiento.INGRESO, 3),
            mov("2", 50_000L, TipoMovimiento.GASTO, 1),
            mov("3", 200_000L, TipoMovimiento.GASTO, 4)
        ).let { it.copy(categorias = it.categorias + ahorro) }

        val resumen = AnalisisFinanciero.resumenMes(state, mes)
        assertEquals(50_000L, resumen.gastos)
        assertEquals(200_000L, resumen.ahorro)
        assertTrue(resumen.gastosPorCategoria.none { it.categoria.id == 4 })
        // 1.000.000 + 600.000 - 50.000 - 200.000
        assertEquals(1_350_000L, com.example.proyecto_integrador_moviles_g7.data.repository.ReglasFinanzas.saldoDisponible(state))
    }

    @Test
    fun `avisa meta de ahorro alcanzada con los abonos`() {
        val ahorro = Categoria(4, "Ahorro", TipoMovimiento.GASTO)
        val state = estado(
            mov("1", 600_000L, TipoMovimiento.INGRESO, 3),
            mov("2", 500_000L, TipoMovimiento.GASTO, 4)
        ).let { it.copy(categorias = it.categorias + ahorro) }
        val notificaciones = AnalisisFinanciero.notificaciones(state, metaAhorro = 500_000L, mes = mes)
        assertTrue(notificaciones.any { it.tipo == TipoNotificacion.META_ALCANZADA })
    }

    @Test
    fun `avisa saldo bajo`() {
        val state = estado(mov("1", 950_000L, TipoMovimiento.GASTO, 1, mes.minusMonths(2)))
        val notificaciones = AnalisisFinanciero.notificaciones(state, metaAhorro = 0L, mes = mes)
        assertTrue(notificaciones.any { it.tipo == TipoNotificacion.SALDO_BAJO })
    }
}
