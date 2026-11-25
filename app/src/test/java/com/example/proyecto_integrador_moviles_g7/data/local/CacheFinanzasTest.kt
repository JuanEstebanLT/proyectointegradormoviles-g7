package com.example.proyecto_integrador_moviles_g7.data.local

import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

/** DAO en memoria para probar la caché sin Room ni dispositivo. */
private class CacheDaoFalso : CacheDao() {
    val categoriasGuardadas = mutableListOf<CategoriaEntidad>()
    val movimientosGuardados = mutableListOf<MovimientoEntidad>()
    val cuentas = mutableMapOf<Long, CuentaEntidad>()

    override suspend fun categorias(usuarioId: Long) = categoriasGuardadas.filter { it.usuarioId == usuarioId }
    override suspend fun movimientos(usuarioId: Long) = movimientosGuardados.filter { it.usuarioId == usuarioId }
    override suspend fun cuenta(usuarioId: Long) = cuentas[usuarioId]
    override suspend fun guardarCuenta(cuenta: CuentaEntidad) { cuentas[cuenta.usuarioId] = cuenta }
    override suspend fun insertarCategorias(categorias: List<CategoriaEntidad>) { categoriasGuardadas += categorias }
    override suspend fun insertarMovimientos(movimientos: List<MovimientoEntidad>) { movimientosGuardados += movimientos }
    override suspend fun borrarCategorias(usuarioId: Long) { categoriasGuardadas.removeAll { it.usuarioId == usuarioId } }
    override suspend fun borrarMovimientos(usuarioId: Long) { movimientosGuardados.removeAll { it.usuarioId == usuarioId } }
    override suspend fun borrarCuenta(usuarioId: Long) { cuentas.remove(usuarioId) }
}

class CacheFinanzasTest {

    private val fecha = LocalDateTime.of(2026, 11, 20, 10, 30)
    private val estado = FinanzasState(
        saldoInicial = 1_000_000L,
        categorias = listOf(
            Categoria(1, "Alimentación", TipoMovimiento.GASTO, 300_000L),
            Categoria(2, "Salario", TipoMovimiento.INGRESO)
        ),
        movimientos = listOf(
            Movimiento("10", "Mercado", 85_000L, TipoMovimiento.GASTO, 1, fecha),
            Movimiento("tmp-abc", "Aún sin confirmar", 5_000L, TipoMovimiento.GASTO, 1, fecha)
        )
    )

    @Test
    fun `sin sincronizar no hay datos guardados`() = runTest {
        assertNull(CacheFinanzas(CacheDaoFalso()).leer(5L))
    }

    @Test
    fun `guarda y recupera el estado sin los movimientos temporales`() = runTest {
        val cache = CacheFinanzas(CacheDaoFalso())
        cache.guardar(5L, estado, ahora = 1234L)

        val guardados = cache.leer(5L)!!
        assertEquals(1_000_000L, guardados.state.saldoInicial)
        assertEquals(estado.categorias, guardados.state.categorias)
        assertEquals(listOf(estado.movimientos.first()), guardados.state.movimientos)
        assertEquals(1234L, guardados.sincronizadoEn)
    }

    @Test
    fun `la meta de ahorro se conserva al volver a sincronizar`() = runTest {
        val cache = CacheFinanzas(CacheDaoFalso())
        cache.guardarMeta(5L, 200_000L)
        cache.guardar(5L, estado)
        assertEquals(200_000L, cache.leer(5L)!!.metaAhorro)
    }

    @Test
    fun `los datos de cada usuario estan separados y se borran al cerrar sesion`() = runTest {
        val cache = CacheFinanzas(CacheDaoFalso())
        cache.guardar(5L, estado)
        cache.guardar(7L, FinanzasState(saldoInicial = 50_000L))

        cache.borrar(5L)
        assertNull(cache.leer(5L))
        assertEquals(50_000L, cache.leer(7L)!!.state.saldoInicial)
    }
}
