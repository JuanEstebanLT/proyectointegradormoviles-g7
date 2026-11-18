package com.example.proyecto_integrador_moviles_g7.data.repository

import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DashboardViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DetalleCategoriaViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.SaldoInicialViewModel
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryFinanzasRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: InMemoryFinanzasRepository

    @Test
    fun `historial completo conserva movimientos fuera del resumen reciente`() {
        repeat(7) { indice ->
            assertTrue(repository.agregarMovimiento("Ingreso $indice", 100L, TipoMovimiento.INGRESO, 3))
        }
        val estado = DashboardViewModel.crearUiState(repository.state.value)
        assertEquals(7, estado.movimientos.size)
        assertEquals(5, estado.movimientosRecientes.size)
        assertEquals(700L, estado.totalIngresos)
    }

    @Test
    fun `registro desde categoria actualiza historial y saldo compartidos`() = runTest(dispatcher) {
        repository.establecerSaldoInicial(100_000L)
        val detalle = DetalleCategoriaViewModel(2, repository)
        assertNull(detalle.agregarMovimiento("Mercado", 40_000L))
        advanceUntilIdle()
        val estado = DashboardViewModel.crearUiState(repository.state.value)
        assertEquals(40_000L, detalle.uiState.value.gastoCategoria)
        assertEquals(60_000L, estado.saldoDisponible)
        assertEquals(2, estado.movimientos.single().categoriaId)
        assertNull(detalle.eliminarMovimiento(estado.movimientos.single().id))
        advanceUntilIdle()
        assertEquals(100_000L, DashboardViewModel.crearUiState(repository.state.value).saldoDisponible)
    }

    @Test
    fun `no permite gastar mas del saldo disponible`() {
        assertTrue(agregarIngreso(550_000L))

        // El caso reportado: ingreso de 550.000 y gasto de 600.000
        assertFalse(agregarGasto(600_000L, categoriaId = 1))
        val dashboard = DashboardViewModel(repository)
        val mensaje = dashboard.agregarMovimiento("Compra", 600_000L, TipoMovimiento.GASTO, 1)
        assertTrue(mensaje!!.startsWith("Saldo insuficiente"))
        assertTrue(mensaje.contains("550.000"))
        assertEquals(1, repository.state.value.movimientos.size)

        // Gastar exactamente lo disponible sí se permite
        assertNull(dashboard.agregarMovimiento("Compra", 550_000L, TipoMovimiento.GASTO, 1))
        assertEquals(0L, DashboardViewModel.crearUiState(repository.state.value).saldoDisponible)
        assertFalse(agregarGasto(1L, categoriaId = 1))
    }

    @Test
    fun `no permite editar un gasto por encima del saldo ni bajar un ingreso que lo deje negativo`() {
        assertTrue(agregarIngreso(500_000L))
        assertTrue(agregarGasto(300_000L, categoriaId = 1))
        val (ingreso, gasto) = repository.state.value.movimientos
        val dashboard = DashboardViewModel(repository)

        // Disponible: 200.000 + los 300.000 del propio gasto = máximo 500.000
        assertTrue(dashboard.actualizarMovimiento(gasto.id, "Compra", 500_001L, 1)!!.contains("500.000"))
        assertNull(dashboard.actualizarMovimiento(gasto.id, "Compra", 500_000L, 1))

        // Ahora el saldo es 0: el ingreso no puede bajar
        assertTrue(dashboard.actualizarMovimiento(ingreso.id, "Nómina", 400_000L, 3)!!.contains("al menos"))
        assertEquals(500_000L, repository.state.value.movimientos.first { it.id == ingreso.id }.monto)
    }

    @Test
    fun `no permite eliminar un ingreso si el saldo quedaria negativo`() {
        assertTrue(agregarIngreso(500_000L))
        assertTrue(agregarGasto(300_000L, categoriaId = 1))
        val ingreso = repository.state.value.movimientos.first { it.tipo == TipoMovimiento.INGRESO }

        val mensaje = DashboardViewModel(repository).eliminarMovimiento(ingreso.id)

        assertTrue(mensaje!!.startsWith("No puedes eliminar este ingreso"))
        assertEquals(2, repository.state.value.movimientos.size)
    }

    @Test
    fun `no permite bajar el saldo inicial si el saldo quedaria negativo`() {
        assertTrue(repository.establecerSaldoInicial(1_000_000L))
        assertTrue(agregarGasto(700_000L, categoriaId = 1))
        val saldoInicial = SaldoInicialViewModel(repository)

        // Los gastos suman 700.000: el saldo inicial no puede bajar de ahí
        val mensaje = saldoInicial.establecerSaldo(600_000L)
        assertTrue(mensaje!!.contains("700.000"))
        assertEquals(1_000_000L, repository.state.value.saldoInicial)
        assertFalse(repository.establecerSaldoInicial(699_999L))

        assertNull(saldoInicial.establecerSaldo(700_000L))
        assertEquals(0L, DashboardViewModel.crearUiState(repository.state.value).saldoDisponible)
        // Subirlo siempre se permite
        assertNull(saldoInicial.establecerSaldo(2_000_000L))
    }

    @Test
    fun `con saldo ya negativo permite cambios que lo mejoran`() {
        // Datos anteriores a la regla: gasto mayor que el saldo
        repository = InMemoryFinanzasRepository(
            initialState = FinanzasState(
                categorias = listOf(
                    Categoria(1, "Vivienda", TipoMovimiento.GASTO),
                    Categoria(3, "Ingresos", TipoMovimiento.INGRESO)
                ),
                movimientos = listOf(
                    Movimiento("i", "Nómina", 550_000L, TipoMovimiento.INGRESO, 3, LocalDateTime.of(2026, 11, 1, 9, 0)),
                    Movimiento("g", "Compra", 600_000L, TipoMovimiento.GASTO, 1, LocalDateTime.of(2026, 11, 2, 9, 0))
                )
            )
        )
        assertFalse(agregarGasto(1L, categoriaId = 1))
        // Reducir el gasto mejora el saldo aunque siga negativo
        assertTrue(repository.actualizarMovimiento("g", "Compra", 580_000L, 1))
        assertFalse(repository.actualizarMovimiento("g", "Compra", 590_000L, 1))
        assertTrue(repository.eliminarMovimiento("g"))
    }

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
        repository.establecerSaldoInicial(1_000_000L)
        assertTrue(agregarGasto(90_000L, categoriaId = 1))
        val id = repository.state.value.movimientos.single().id

        assertTrue(repository.eliminarMovimiento(id))
        assertTrue(repository.state.value.movimientos.isEmpty())
        assertFalse(repository.eliminarMovimiento(id))
    }

    @Test
    fun `filtra movimientos por identificador de categoria`() {
        repository.establecerSaldoInicial(1_000_000L)
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
        repository.establecerSaldoInicial(1_000_000L)
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

    @Test
    fun `edita descripcion, monto y categoria de un movimiento conservando su fecha`() {
        repository.establecerSaldoInicial(1_000_000L)
        agregarGasto(90_000L, categoriaId = 1)
        val original = repository.state.value.movimientos.single()

        assertTrue(repository.actualizarMovimiento(original.id, "  Arriendo  ", 95_000L, 2))

        val editado = repository.state.value.movimientos.single()
        assertEquals("Arriendo", editado.descripcion)
        assertEquals(95_000L, editado.monto)
        assertEquals(2, editado.categoriaId)
        assertEquals(original.fecha, editado.fecha)
        assertEquals(95_000L, DashboardViewModel.crearUiState(repository.state.value).totalGastos)
    }

    @Test
    fun `rechaza ediciones de movimiento invalidas`() {
        repository.establecerSaldoInicial(1_000_000L)
        agregarGasto(90_000L, categoriaId = 1)
        val id = repository.state.value.movimientos.single().id

        assertFalse(repository.actualizarMovimiento("no_existe", "Compra", 1_000L, 1))
        assertFalse(repository.actualizarMovimiento(id, " ", 1_000L, 1))
        assertFalse(repository.actualizarMovimiento(id, "Compra", 0L, 1))
        assertFalse(repository.actualizarMovimiento(id, "Compra", 1_000L, 99))
        // Un gasto no puede pasar a una categoría de ingresos
        assertFalse(repository.actualizarMovimiento(id, "Compra", 1_000L, 3))
        assertEquals(90_000L, repository.state.value.movimientos.single().monto)
    }

    @Test
    fun `crea, edita y elimina categorias`() {
        assertTrue(repository.crearCategoria("  Transporte ", TipoMovimiento.GASTO, 200_000L))
        val creada = repository.state.value.categorias.single { it.nombre == "Transporte" }
        assertEquals(4, creada.id)
        assertEquals(200_000L, creada.presupuesto)

        assertTrue(repository.actualizarCategoria(creada.id, "Movilidad", 250_000L))
        val editada = repository.state.value.categorias.single { it.id == creada.id }
        assertEquals("Movilidad", editada.nombre)
        assertEquals(250_000L, editada.presupuesto)
        assertEquals(TipoMovimiento.GASTO, editada.tipo)

        assertTrue(repository.eliminarCategoria(creada.id))
        assertTrue(repository.state.value.categorias.none { it.id == creada.id })
    }

    @Test
    fun `rechaza nombres de categoria vacios o repetidos`() {
        assertFalse(repository.crearCategoria(" ", TipoMovimiento.GASTO))
        assertFalse(repository.crearCategoria("vivienda", TipoMovimiento.GASTO))
        assertFalse(repository.actualizarCategoria(2, "VIVIENDA", 0L))
        assertFalse(repository.actualizarCategoria(2, "Comida", -1L))
        assertFalse(repository.actualizarCategoria(99, "Comida", 0L))
        // Cambiar solo mayúsculas del propio nombre sí está permitido
        assertTrue(repository.actualizarCategoria(2, "ALIMENTACIÓN", 500_000L))
        assertEquals(3, repository.state.value.categorias.size)
    }

    @Test
    fun `no elimina una categoria con movimientos`() {
        repository.establecerSaldoInicial(1_000_000L)
        assertTrue(agregarGasto(90_000L, categoriaId = 1))

        assertFalse(repository.eliminarCategoria(1))
        assertTrue(repository.state.value.categorias.any { it.id == 1 })
        assertFalse(repository.eliminarCategoria(99))
    }

    @Test
    fun `detalle refleja la edicion de su categoria`() = runTest(dispatcher) {
        val detalle = DetalleCategoriaViewModel(2, repository)
        advanceUntilIdle()

        assertTrue(detalle.actualizarCategoria("Mercado", 700_000L))
        advanceUntilIdle()

        assertEquals("Mercado", detalle.uiState.value.categoria?.nombre)
        assertEquals(700_000L, detalle.uiState.value.categoria?.presupuesto)
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
