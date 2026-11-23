package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasState
import com.example.proyecto_integrador_moviles_g7.data.repository.ReglasFinanzas
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import java.time.YearMonth

/** Uso de una categoría en un mes: total, presupuesto y participación en el total de su tipo. */
data class UsoCategoria(
    val categoria: Categoria,
    val total: Long,
    val cantidad: Int,
    /** Porcentaje del total del mes de su mismo tipo (0–100). */
    val participacion: Float
) {
    val presupuesto: Long get() = categoria.presupuesto

    /** Porcentaje del presupuesto usado, o null si la categoría no tiene presupuesto. */
    val usoPresupuesto: Float?
        get() = if (presupuesto > 0L) total * 100f / presupuesto else null

    val excedido: Boolean get() = presupuesto > 0L && total > presupuesto
}

data class ResumenMes(
    val mes: YearMonth,
    val ingresos: Long,
    val gastos: Long,
    val gastosPorCategoria: List<UsoCategoria>,
    val ingresosPorCategoria: List<UsoCategoria>,
    /** Dinero abonado a la meta de ahorro en el mes (no cuenta como gasto). */
    val abonosAhorro: Long = 0L
) {
    /** Flujo del mes: ingresos menos gastos y abonos (lo que salió del saldo disponible). */
    val balance: Long get() = ingresos - gastos - abonosAhorro

    /** Lo que se ahorró en el mes: la suma de los abonos a la meta. */
    val ahorro: Long get() = abonosAhorro

    /** Porcentaje de los ingresos que se ahorró. */
    val tasaAhorro: Float get() = if (ingresos > 0L) ahorro * 100f / ingresos else 0f
}

enum class TipoNotificacion { PRESUPUESTO_EXCEDIDO, PRESUPUESTO_CERCANO, META_ALCANZADA, SALDO_BAJO }

data class Notificacion(
    /** Identifica la alerta para marcarla como leída. */
    val clave: String,
    val tipo: TipoNotificacion,
    val titulo: String,
    val mensaje: String,
    val categoriaId: Int? = null
)

/** Cálculos de informes, ahorro y alertas a partir del estado local (sin llamadas a la API). */
object AnalisisFinanciero {

    /** Por debajo de este saldo disponible se avisa al usuario. */
    const val SALDO_BAJO = 100_000L

    /** Desde este porcentaje de uso del presupuesto se avisa que está cerca del límite. */
    const val PORCENTAJE_ALERTA = 80f

    /**
     * Los abonos a la meta se guardan como gastos en esta categoría: así se descuentan
     * del saldo disponible sin cambiar la base de datos. Se crea sola en el primer abono.
     */
    const val NOMBRE_CATEGORIA_AHORRO = "Ahorro"

    fun esCategoriaAhorro(categoria: Categoria): Boolean =
        categoria.tipo == TipoMovimiento.GASTO && categoria.nombre.trim().equals(NOMBRE_CATEGORIA_AHORRO, ignoreCase = true)

    fun categoriaAhorro(state: FinanzasState): Categoria? = state.categorias.firstOrNull(::esCategoriaAhorro)

    /** Abonos a la meta (todos los meses), del más reciente al más antiguo. */
    fun abonos(state: FinanzasState): List<Movimiento> {
        val id = categoriaAhorro(state)?.id ?: return emptyList()
        return state.movimientos.filter { it.categoriaId == id }.sortedByDescending { it.fecha }
    }

    fun resumenMes(state: FinanzasState, mes: YearMonth): ResumenMes {
        val idAhorro = categoriaAhorro(state)?.id
        val delMesTodos = state.movimientos.filter { YearMonth.from(it.fecha) == mes }
        val abonos = delMesTodos.filter { it.categoriaId == idAhorro }.sumOf { it.monto }
        val delMes = delMesTodos.filter { it.categoriaId != idAhorro }
        val ingresos = delMes.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto }
        val gastos = delMes.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }

        fun usos(tipo: TipoMovimiento, totalTipo: Long) = state.categorias
            .filter { it.tipo == tipo && it.id != idAhorro }
            .map { categoria ->
                val movimientos = delMes.filter { it.categoriaId == categoria.id }
                val total = movimientos.sumOf { it.monto }
                UsoCategoria(
                    categoria = categoria,
                    total = total,
                    cantidad = movimientos.size,
                    participacion = if (totalTipo > 0L) total * 100f / totalTipo else 0f
                )
            }
            .sortedByDescending { it.total }

        return ResumenMes(
            mes = mes,
            ingresos = ingresos,
            gastos = gastos,
            gastosPorCategoria = usos(TipoMovimiento.GASTO, gastos),
            ingresosPorCategoria = usos(TipoMovimiento.INGRESO, ingresos),
            abonosAhorro = abonos
        )
    }

    /** Resúmenes de los [cantidad] meses que terminan en [hasta], del más antiguo al más reciente. */
    fun historico(state: FinanzasState, hasta: YearMonth, cantidad: Int = 6): List<ResumenMes> =
        (cantidad - 1 downTo 0).map { resumenMes(state, hasta.minusMonths(it.toLong())) }

    fun notificaciones(state: FinanzasState, metaAhorro: Long, mes: YearMonth = YearMonth.now()): List<Notificacion> {
        val resumen = resumenMes(state, mes)
        val lista = mutableListOf<Notificacion>()

        resumen.gastosPorCategoria.filter { it.usoPresupuesto != null }.forEach { uso ->
            val nombre = uso.categoria.nombre
            if (uso.excedido) {
                lista += Notificacion(
                    clave = "excedido-${uso.categoria.id}-$mes",
                    tipo = TipoNotificacion.PRESUPUESTO_EXCEDIDO,
                    titulo = "Presupuesto superado en $nombre",
                    mensaje = "Gastaste ${formatoCop(uso.total)} de ${formatoCop(uso.presupuesto)} COP " +
                        "(${formatoCop(uso.total - uso.presupuesto)} por encima).",
                    categoriaId = uso.categoria.id
                )
            } else if ((uso.usoPresupuesto ?: 0f) >= PORCENTAJE_ALERTA) {
                lista += Notificacion(
                    clave = "cercano-${uso.categoria.id}-$mes",
                    tipo = TipoNotificacion.PRESUPUESTO_CERCANO,
                    titulo = "Presupuesto al ${uso.usoPresupuesto!!.toInt()} % en $nombre",
                    mensaje = "Te quedan ${formatoCop(uso.presupuesto - uso.total)} COP este mes.",
                    categoriaId = uso.categoria.id
                )
            }
        }

        if (metaAhorro > 0L && resumen.ahorro >= metaAhorro) {
            lista += Notificacion(
                clave = "meta-$mes",
                tipo = TipoNotificacion.META_ALCANZADA,
                titulo = "¡Meta de ahorro alcanzada!",
                mensaje = "Este mes ahorraste ${formatoCop(resumen.ahorro)} COP; tu meta era ${formatoCop(metaAhorro)} COP."
            )
        }

        val disponible = ReglasFinanzas.saldoDisponible(state)
        if (state.movimientos.isNotEmpty() && disponible < SALDO_BAJO) {
            lista += Notificacion(
                // Una vez por mes, para no repetir el aviso con cada movimiento
                clave = "saldo-bajo-$mes",
                tipo = TipoNotificacion.SALDO_BAJO,
                titulo = "Saldo bajo",
                mensaje = "Tu saldo disponible es ${if (disponible < 0) "- " else ""}${formatoCop(disponible)} COP."
            )
        }
        return lista
    }
}
