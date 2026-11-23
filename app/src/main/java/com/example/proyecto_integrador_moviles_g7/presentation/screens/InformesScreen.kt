package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraNavegacionG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraProgreso
import com.example.proyecto_integrador_moviles_g7.presentation.components.CajaIcono
import com.example.proyecto_integrador_moviles_g7.presentation.components.EncabezadoPrincipal
import com.example.proyecto_integrador_moviles_g7.presentation.components.Etiqueta
import com.example.proyecto_integrador_moviles_g7.presentation.components.SeccionPrincipal
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.colorUsoPresupuesto
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCopCorto
import com.example.proyecto_integrador_moviles_g7.presentation.components.iconoCategoria
import com.example.proyecto_integrador_moviles_g7.presentation.components.nombreMes
import com.example.proyecto_integrador_moviles_g7.presentation.components.nombreMesCorto
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.InformesViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.ResumenMes
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.UsoCategoria
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar800
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.HieloSuave
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate100
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate200
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro

/** Pantalla 09 — Informes. */
@Composable
fun InformesScreen(
    viewModel: InformesViewModel,
    iniciales: String,
    notificacionesSinLeer: Int,
    onSeccion: (SeccionPrincipal) -> Unit,
    onPerfil: () -> Unit,
    onNotificaciones: () -> Unit,
    onCategoria: (Int) -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val resumen = estado.resumen

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            EncabezadoPrincipal("Informes", "Finanzas G7", iniciales, notificacionesSinLeer, onNotificaciones, onPerfil)
        },
        bottomBar = { BarraNavegacionG7(SeccionPrincipal.INFORMES, onSeccion) }
    ) { relleno ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = viewModel::mesAnterior) {
                        Icon(Icons.Outlined.ChevronLeft, contentDescription = "Mes anterior", tint = TealOscuro)
                    }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Surface(color = HieloSuave, shape = RoundedCornerShape(50)) {
                            Row(Modifier.padding(horizontal = 14.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = TealOscuro, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(nombreMes(estado.mes), fontWeight = FontWeight.Bold, color = Slate900)
                            }
                        }
                    }
                    IconButton(onClick = viewModel::mesSiguiente, enabled = estado.puedeAvanzar) {
                        Icon(Icons.Outlined.ChevronRight, contentDescription = "Mes siguiente", tint = if (estado.puedeAvanzar) TealOscuro else Slate200)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TarjetaMetrica("Saldo inicial", formatoCop(estado.saldoInicial), "Base de tus cuentas", Icons.Outlined.AccountBalanceWallet, Color.White, TealOscuro, Modifier.weight(1f))
                        TarjetaMetrica("Ingresos", "+ ${formatoCop(resumen.ingresos)}", "En ${nombreMesCorto(estado.mes).lowercase()}", Icons.AutoMirrored.Outlined.TrendingUp, Esmeralda50, Esmeralda600, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TarjetaMetrica("Gastos", "- ${formatoCop(resumen.gastos)}", "En ${nombreMesCorto(estado.mes).lowercase()}", Icons.AutoMirrored.Outlined.TrendingDown, Rosa50, Rosa600, Modifier.weight(1f))
                        TarjetaMetrica(
                            "Disponible", (if (estado.saldoDisponible < 0) "- " else "") + formatoCop(estado.saldoDisponible),
                            if (resumen.balance >= 0) "Flujo del mes positivo" else "Flujo del mes negativo",
                            Icons.Outlined.Savings, HieloSuave, TealOscuro, Modifier.weight(1f)
                        )
                    }
                }
            }

            item { GraficoIngresosGastos(estado.historico, resumen) }

            item { GraficoDistribucion(resumen) }

            item {
                TarjetaG7 {
                    Text("Control presupuestario", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Slate900)
                    Text("Límites asignados y ejecución del mes", fontSize = 12.sp, color = Slate500)
                    Spacer(Modifier.height(12.dp))
                    val conPresupuesto = resumen.gastosPorCategoria.filter { it.presupuesto > 0L }
                    if (conPresupuesto.isEmpty()) {
                        Text("No tienes categorías con presupuesto. Asígnalo desde Gestionar categorías.", fontSize = 13.sp, color = Slate500)
                    }
                    conPresupuesto.sortedByDescending { it.usoPresupuesto }.forEach { uso ->
                        FilaPresupuesto(uso) { onCategoria(uso.categoria.id) }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaMetrica(titulo: String, valor: String, detalle: String, icono: ImageVector, fondo: Color, color: Color, modifier: Modifier) {
    TarjetaG7(modifier = modifier, fondo = fondo, borde = if (fondo == Color.White) Slate200 else color.copy(alpha = 0.2f), radio = 18.dp, relleno = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(titulo, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            CajaIcono(icono, fondo = color.copy(alpha = 0.12f), tinte = color, tamano = 28.dp, tamanoIcono = 16.dp, radio = 14.dp)
        }
        Spacer(Modifier.height(8.dp))
        Text(valor, fontSize = 16.sp, fontWeight = FontWeight.Black, color = if (fondo == Color.White) Slate900 else color, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(detalle, fontSize = 11.sp, color = Slate500, maxLines = 1)
    }
}

@Composable
private fun GraficoIngresosGastos(historico: List<ResumenMes>, actual: ResumenMes) {
    TarjetaG7 {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Ingresos vs. gastos", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Slate900)
                Text("Últimos 6 meses", fontSize = 12.sp, color = Slate500)
            }
            Leyenda("Ing.", Esmeralda600)
            Spacer(Modifier.width(8.dp))
            Leyenda("Gas.", Rosa600)
        }
        Spacer(Modifier.height(16.dp))
        val maximo = historico.maxOfOrNull { maxOf(it.ingresos, it.gastos) }?.coerceAtLeast(1L) ?: 1L
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val columnas = historico.size.coerceAtLeast(1)
            val anchoGrupo = size.width / columnas
            val anchoBarra = anchoGrupo * 0.26f
            // Líneas guía
            for (i in 0..3) {
                val y = size.height * i / 3f
                drawLine(Slate100, Offset(0f, y), Offset(size.width, y), strokeWidth = 2f)
            }
            historico.forEachIndexed { i, mes ->
                val centro = anchoGrupo * i + anchoGrupo / 2
                val altoIngreso = size.height * mes.ingresos / maximo
                val altoGasto = size.height * mes.gastos / maximo
                drawRoundRect(
                    Esmeralda600,
                    topLeft = Offset(centro - anchoBarra - 3f, size.height - altoIngreso),
                    size = Size(anchoBarra, altoIngreso),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    Rosa600,
                    topLeft = Offset(centro + 3f, size.height - altoGasto),
                    size = Size(anchoBarra, altoGasto),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            historico.forEach { mes ->
                val esActual = mes.mes == actual.mes
                Text(
                    nombreMesCorto(mes.mes), fontSize = 11.sp,
                    fontWeight = if (esActual) FontWeight.ExtraBold else FontWeight.Normal,
                    color = if (esActual) Slate900 else Slate500,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Surface(color = HieloSuave, shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.TaskAlt, contentDescription = null, tint = TealOscuro, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Tasa de ahorro del mes", fontSize = 13.sp, color = Slate900, modifier = Modifier.weight(1f))
                Text("${String.format(java.util.Locale.forLanguageTag("es-CO"), "%.1f", actual.tasaAhorro)} %", fontWeight = FontWeight.ExtraBold, color = Esmeralda600)
            }
        }
    }
}

@Composable
private fun Leyenda(texto: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(4.dp))
        Text(texto, fontSize = 11.sp, color = Slate500)
    }
}

@Composable
private fun GraficoDistribucion(resumen: ResumenMes) {
    val conGasto = resumen.gastosPorCategoria.filter { it.total > 0L }
    TarjetaG7 {
        Text("Distribución del gasto", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Slate900)
        Text("Composición porcentual de ${nombreMes(resumen.mes).lowercase()}", fontSize = 12.sp, color = Slate500)
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(170.dp)) {
                val grosor = 30.dp.toPx()
                val arco = Size(size.width - grosor, size.height - grosor)
                val origen = Offset(grosor / 2, grosor / 2)
                if (conGasto.isEmpty()) {
                    drawArc(Slate100, 0f, 360f, false, origen, arco, style = Stroke(grosor))
                } else {
                    var inicio = -90f
                    conGasto.forEachIndexed { i, uso ->
                        val barrido = 360f * uso.participacion / 100f
                        drawArc(paletaCategorias[i % paletaCategorias.size], inicio, barrido - 1.5f, false, origen, arco, style = Stroke(grosor))
                        inicio += barrido
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total gastado", fontSize = 11.sp, color = Slate500)
                Text(formatoCopCorto(resumen.gastos), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Slate900)
                Text("COP", fontSize = 11.sp, color = TealMedio, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(16.dp))
        if (conGasto.isEmpty()) {
            Text("Sin gastos registrados en este mes.", fontSize = 13.sp, color = Slate500)
        }
        conGasto.forEachIndexed { i, uso ->
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(paletaCategorias[i % paletaCategorias.size]))
                Spacer(Modifier.width(8.dp))
                Text(uso.categoria.nombre, fontSize = 13.sp, color = Slate900, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${uso.participacion.toInt()} %", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Slate900)
                Spacer(Modifier.width(6.dp))
                Text("(${formatoCopCorto(uso.total)})", fontSize = 11.sp, color = Slate400)
            }
        }
    }
}

@Composable
private fun FilaPresupuesto(uso: UsoCategoria, onClick: () -> Unit) {
    val porcentaje = uso.usoPresupuesto ?: 0f
    val color = colorUsoPresupuesto(porcentaje)
    TarjetaG7(
        fondo = if (uso.excedido) Rosa50 else Color(0xFFF7FDFF),
        borde = if (uso.excedido) Rosa600.copy(alpha = 0.25f) else Slate100,
        radio = 16.dp, relleno = PaddingValues(12.dp), onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CajaIcono(iconoCategoria(uso.categoria.nombre), fondo = color.copy(alpha = 0.12f), tinte = color, tamano = 36.dp, tamanoIcono = 18.dp, radio = 10.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(uso.categoria.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${formatoCop(uso.total)} / ${formatoCop(uso.presupuesto)} COP", fontSize = 11.sp, color = Slate500)
            }
            if (uso.excedido) Etiqueta("Excedido +${formatoCopCorto(uso.total - uso.presupuesto)}", Rosa50, Rosa600)
            else Etiqueta("${porcentaje.toInt()} %", if (porcentaje >= 80f) Ambar50 else Esmeralda50, if (porcentaje >= 80f) Ambar800 else Esmeralda600)
        }
        Spacer(Modifier.height(8.dp))
        BarraProgreso(porcentaje / 100f, color, alto = 7.dp)
    }
}
