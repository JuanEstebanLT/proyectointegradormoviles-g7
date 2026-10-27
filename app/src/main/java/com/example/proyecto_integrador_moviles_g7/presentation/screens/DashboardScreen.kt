package com.example.proyecto_integrador_moviles_g7.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_integrador_moviles_g7.presentation.components.FilaMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.LogoG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.Tarjeta
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Crema
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda300
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda700
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Proyectointegradormovilesg7Theme
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate100
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate800
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val localeColombia: Locale = Locale.forLanguageTag("es-CO")

/** Mes y año actuales, por ejemplo "Octubre 2026". */
fun periodoActual(fecha: LocalDate = LocalDate.now()): String =
    fecha.format(DateTimeFormatter.ofPattern("MMMM yyyy", localeColombia))
        .replaceFirstChar { it.titlecase(localeColombia) }

@Composable
fun DashboardScreen(onCategoriaClick: (categoriaId: Int) -> Unit) {
    val context = LocalContext.current
    val pendiente = { nombre: String ->
        Toast.makeText(context, "$nombre: pantalla pendiente de diseño", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        containerColor = FondoClaro,
        topBar = { BarraDashboard() },
        bottomBar = { BarraNavegacionInferior(onDestino = pendiente) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        periodoActual().uppercase(localeColombia),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = TealMedio
                    )
                    Text("Hola, Usuario 👋", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Slate800)
                }
                Text(
                    "COP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealOscuro,
                    modifier = Modifier
                        .background(Turquesa.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                )
            }

            TarjetaSaldo(saldo = 2_850_000, ingresos = 2_500_000, gastos = totalGastosEjemplo)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { pendiente("Agregar ingreso") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Turquesa, contentColor = TealOscuro),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Outlined.AddCircle, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Agregar Ingreso", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { pendiente("Registrar gasto") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Crema, contentColor = Rosa700),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Outlined.RemoveCircle, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Registrar Gasto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            GastosPorCategoria(onCategoriaClick = onCategoriaClick)

            MetaAhorro(onClick = { pendiente("Ahorro") })

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Movimientos Recientes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800, modifier = Modifier.weight(1f))
                    Text(
                        "Ver todo el historial ↗",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealMedio,
                        modifier = Modifier.clickable { pendiente("Movimientos") }
                    )
                }
                FilaMovimiento(
                    icono = Icons.Outlined.Payments,
                    fondoIcono = Turquesa.copy(alpha = 0.3f),
                    colorIcono = TealOscuro,
                    titulo = "Pago de nómina quincenal",
                    detalle = "Ingreso • Hoy, 8:30 AM",
                    monto = 1_250_000,
                    colorMonto = Esmeralda600,
                    colorDetalle = Esmeralda700,
                    fondoDetalle = Esmeralda50
                )
                val ultimoGasto = movimientosEjemplo.first { it.categoriaId == 2 }
                FilaMovimiento(
                    icono = ultimoGasto.icono,
                    fondoIcono = ultimoGasto.fondoIcono,
                    colorIcono = Rosa600,
                    titulo = ultimoGasto.titulo,
                    detalle = "Alimentación • Ayer, 6:15 PM",
                    monto = ultimoGasto.monto,
                    colorMonto = Rosa600,
                    colorDetalle = Slate500
                )
            }
        }
    }
}

@Composable
private fun BarraDashboard() {
    Column(Modifier.background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoG7(tamano = 32.dp, tamanoTexto = 12.sp, radio = 8.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("FINANZAS G7", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = Slate400)
                Text("Dashboard", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate800)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = Slate600)
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(TealOscuro, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        HorizontalDivider(color = Slate100)
    }
}

@Composable
private fun TarjetaSaldo(saldo: Long, ingresos: Long, gastos: Long) {
    var saldoVisible by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TealOscuro, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Saldo disponible actual", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = AguaClaro.copy(alpha = 0.8f))
            Icon(
                if (saldoVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                contentDescription = if (saldoVisible) "Ocultar saldo" else "Mostrar saldo",
                tint = AguaClaro.copy(alpha = 0.8f),
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(16.dp)
                    .clickable { saldoVisible = !saldoVisible }
            )
            Spacer(Modifier.weight(1f))
            Text(
                "↗ +14.5% mes",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Esmeralda300,
                modifier = Modifier
                    .background(Esmeralda400.copy(alpha = 0.2f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                if (saldoVisible) formatoCop(saldo) else "$ ••••••",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text("COP", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Turquesa, modifier = Modifier.padding(bottom = 6.dp))
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResumenMovimiento(Modifier.weight(1f), "Ingresos", ingresos, Icons.Outlined.ArrowDownward, Esmeralda400, saldoVisible)
            ResumenMovimiento(Modifier.weight(1f), "Gastos", gastos, Icons.Outlined.ArrowUpward, Rosa400, saldoVisible)
        }
    }
}

@Composable
private fun ResumenMovimiento(
    modifier: Modifier,
    titulo: String,
    valor: Long,
    icono: ImageVector,
    colorIcono: Color,
    visible: Boolean
) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, null, tint = colorIcono, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(titulo, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AguaClaro)
        }
        Spacer(Modifier.height(4.dp))
        Text(if (visible) formatoCop(valor) else "$ ••••", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun GastosPorCategoria(onCategoriaClick: (Int) -> Unit) {
    Tarjeta(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(width = 6.dp, height = 14.dp)
                    .background(TealMedio, RoundedCornerShape(50))
            )
            Spacer(Modifier.width(8.dp))
            Text("Gastos por Categoría", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800, modifier = Modifier.weight(1f))
            Text("${formatoCop(totalGastosEjemplo)} COP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealOscuro)
        }

        // Barra de distribución porcentual
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(Slate100)
        ) {
            categoriasEjemplo.forEach { categoria ->
                Box(
                    Modifier
                        .weight(categoria.gastado.toFloat())
                        .fillMaxHeight()
                        .background(categoria.color)
                )
            }
        }

        categoriasEjemplo.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { categoria ->
                    val porcentaje = categoria.gastado * 100f / totalGastosEjemplo
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate50)
                            .clickable { onCategoriaClick(categoria.id) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(categoria.icono, null, tint = categoria.color, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                "${categoria.nombre} (${String.format(localeColombia, "%.1f", porcentaje)}%)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate500
                            )
                            Text(formatoCop(categoria.gastado), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaAhorro(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Crema.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Icon(Icons.Outlined.Savings, null, tint = Esmeralda600, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Meta de ahorro mensual", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
            Text("78% completado • ${formatoCop(1_560_000)} COP", fontSize = 11.sp, color = Slate500)
        }
        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = Slate400)
    }
}

@Composable
private fun BarraNavegacionInferior(onDestino: (String) -> Unit) {
    val destinos = listOf(
        "Inicio" to Icons.Outlined.Home,
        "Movimientos" to Icons.Outlined.SwapHoriz,
        "Informes" to Icons.Outlined.BarChart,
        "Ahorro" to Icons.Outlined.Savings
    )
    NavigationBar(containerColor = Color.White) {
        destinos.forEachIndexed { indice, (nombre, icono) ->
            NavigationBarItem(
                selected = indice == 0,
                onClick = { if (indice != 0) onDestino(nombre) },
                icon = { Icon(icono, contentDescription = nombre) },
                label = { Text(nombre, fontSize = 10.sp, fontWeight = if (indice == 0) FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealOscuro,
                    selectedTextColor = TealOscuro,
                    indicatorColor = AguaClaro,
                    unselectedIconColor = Slate400,
                    unselectedTextColor = Slate400
                )
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun DashboardScreenPreview() {
    Proyectointegradormovilesg7Theme {
        DashboardScreen(onCategoriaClick = {})
    }
}
