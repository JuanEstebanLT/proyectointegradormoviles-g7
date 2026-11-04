package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DashboardViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DetalleCategoriaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryFinanzasRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: InMemoryFinanzasRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = InMemoryFinanzasRepository(
            initialState = FinanzasState(
                categorias = listOf(
                    Categoria(1, "Vivienda", TipoMovimiento.GASTO, 600_000L),
                    Categoria(2, "Alimentación", TipoMovimiento.GASTO, 500_000L),
                    Categoria(3, "Ingresos", TipoMovimiento.INGRESO)
                )
            ),
            idProvider = idSecuencial()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `registra saldo inicial y calcula saldo disponible`() {
        assertTrue(repository.establecerSaldoInicial(1_000_000L))
        assertTrue(agregarIngreso(300_000L))
        assertTrue(agregarGasto(120_000L, categoriaId = 1))

        val dashboard = DashboardViewModel.crearUiState(repository.state.value)

        assertEquals(1_000_000L, dashboard.saldoInicial)
        assertEquals(1_180_000L, dashboard.saldoDisponible)
        assertEquals(300_000L, dashboard.totalIngresos)
        assertEquals(120_000L, dashboard.totalGastos)
    }

    @Test
    fun `registra ingresos y gastos con importes exactos en COP`() {
        assertTrue(agregarIngreso(200_001L))
        assertTrue(agregarGasto(50_001L, categoriaId = 2))

        val dashboard = DashboardViewModel.crearUiState(repository.state.value)

        assertEquals(200_001L, dashboard.totalIngresos)
        assertEquals(50_001L, dashboard.totalGastos)
        assertEquals(150_000L, dashboard.saldoDisponible)
    }

    @Test
    fun `elimina movimientos de la fuente compartida`() {
        agregarGasto(90_000L, categoriaId = 1)
        val id = repository.state.value.movimientos.single().id

        assertTrue(repository.eliminarMovimiento(id))
        assertTrue(repository.state.value.movimientos.isEmpty())
        assertFalse(repository.eliminarMovimiento(id))
    }

    @Test
    fun `filtra movimientos por identificador de categoria`() {
        agregarGasto(90_000L, categoriaId = 1)
        agregarGasto(30_000L, categoriaId = 2)

        val vivienda = repository.state.value.movimientos.filter { it.categoriaId == 1 }

        assertEquals(1, vivienda.size)
        assertEquals(90_000L, vivienda.single().monto)
    }

    @Test
    fun `stateflow emite cada actualizacion financiera`() = runTest(dispatcher) {
        val emisiones = mutableListOf<FinanzasState>()
        val recolector = launch { repository.state.take(3).toList(emisiones) }
        advanceUntilIdle()

        repository.establecerSaldoInicial(800_000L)
        advanceUntilIdle()
        agregarIngreso(100_000L)
        advanceUntilIdle()
        recolector.join()

        assertEquals(3, emisiones.size)
        assertEquals(0L, emisiones[0].saldoInicial)
        assertEquals(800_000L, emisiones[1].saldoInicial)
        assertEquals(1, emisiones[2].movimientos.size)
    }

    @Test
    fun `dashboard y detalle observan altas y bajas del mismo movimiento`() = runTest(dispatcher) {
        val dashboard = DashboardViewModel(repository)
        val detalle = DetalleCategoriaViewModel(2, repository)
        dashboard.agregarMovimiento(
            descripcion = "Mercado",
            monto = 75_000L,
            tipo = TipoMovimiento.GASTO,
            categoriaId = 2,
            fecha = LocalDateTime.of(2026, 11, 3, 10, 0)
        )
        advanceUntilIdle()

        assertEquals(75_000L, dashboard.uiState.value.totalGastos)
        assertEquals(1, detalle.uiState.value.movimientos.size)

        detalle.eliminarMovimiento(detalle.uiState.value.movimientos.single().id)
        advanceUntilIdle()

        assertEquals(0L, dashboard.uiState.value.totalGastos)
        assertTrue(detalle.uiState.value.movimientos.isEmpty())
    }

    @Test
    fun `rechaza saldos y movimientos invalidos`() {
        assertFalse(repository.establecerSaldoInicial(0L))
        assertFalse(repository.establecerSaldoInicial(-1L))
        assertFalse(repository.agregarMovimiento("", 10_000L, TipoMovimiento.GASTO, 1))
        assertFalse(repository.agregarMovimiento("Gasto", 0L, TipoMovimiento.GASTO, 1))
        assertFalse(repository.agregarMovimiento("Gasto", -1L, TipoMovimiento.GASTO, 1))
        assertFalse(repository.agregarMovimiento("Gasto", 10_000L, TipoMovimiento.GASTO, 99))
        assertFalse(repository.agregarMovimiento("Ingreso", 10_000L, TipoMovimiento.INGRESO, 1))
        assertTrue(repository.state.value.movimientos.isEmpty())
    }

    private fun agregarIngreso(monto: Long) = repository.agregarMovimiento(
        descripcion = "Nómina",
        monto = monto,
        tipo = TipoMovimiento.INGRESO,
        categoriaId = 3
    )

    private fun agregarGasto(monto: Long, categoriaId: Int) = repository.agregarMovimiento(
        descripcion = "Compra",
        monto = monto,
        tipo = TipoMovimiento.GASTO,
        categoriaId = categoriaId
    )

    private fun idSecuencial(): () -> String {
        var siguiente = 0
        return { "movimiento_${++siguiente}" }
    }
}
