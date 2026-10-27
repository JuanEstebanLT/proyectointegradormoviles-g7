package com.example.proyecto_integrador_moviles_g7.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraSuperior
import com.example.proyecto_integrador_moviles_g7.presentation.components.FilaMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.Tarjeta
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar800
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar900
import com.example.proyecto_integrador_moviles_g7.ui.theme.Crema
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda300
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda400
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Proyectointegradormovilesg7Theme
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate200
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate800
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa
import java.time.LocalDate
import java.util.Locale

@Composable
fun DetalleCategoriaScreen(
    categoriaId: Int,
    onAtras: () -> Unit
) {
    val context = LocalContext.current
    val pendiente = { nombre: String ->
        Toast.makeText(context, "$nombre: pendiente de implementar", Toast.LENGTH_SHORT).show()
    }
    val categoria = categoriasEjemplo.firstOrNull { it.id == categoriaId } ?: categoriasEjemplo.first()
    val movimientos = movimientosEjemplo.filter { it.categoriaId == categoria.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
    ) {
        BarraSuperior(titulo = categoria.nombre, onAtras = onAtras) {
            IconButton(onClick = { pendiente("Editar categoría") }) {
                Icon(Icons.Outlined.Edit, contentDescription = "Editar categoría", tint = Slate600)
            }
            IconButton(onClick = { pendiente("Más opciones") }) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "Más opciones", tint = Slate600)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(categoria.nombre, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Slate900)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "ACTIVA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TealOscuro,
                        modifier = Modifier
                            .background(Turquesa.copy(alpha = 0.4f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text("Detalle de Categoría • Gastos", fontSize = 12.sp, color = Slate500, modifier = Modifier.padding(top = 2.dp))
            }

            TarjetaPresupuesto(categoria)

            val promedio = if (movimientos.isEmpty()) 0L else categoria.gastado / movimientos.size
            val porcentajeTotal = categoria.gastado * 100f / totalGastosEjemplo
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Metrica(Modifier.weight(1f), Icons.Outlined.Calculate, "Promedio/gasto", formatoCop(promedio))
                Metrica(Modifier.weight(1f), Icons.AutoMirrored.Outlined.ReceiptLong, "Movimientos", "${movimientos.size} compras")
                Metrica(
                    Modifier.weight(1f),
                    Icons.Outlined.PieChart,
                    "% del total",
                    String.format(Locale.forLanguageTag("es-CO"), "%.1f%%", porcentajeTotal),
                    fondo = Crema,
                    colorIcono = Ambar800,
                    colorTitulo = Ambar800.copy(alpha = 0.8f),
                    colorValor = Ambar900
                )
            }

            SelectorPeriodo()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Transacciones en ${categoria.nombre}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.AutoMirrored.Outlined.Sort, null, tint = TealMedio, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(2.dp))
                    Text("Por fecha", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TealMedio)
                }
                if (movimientos.isEmpty()) {
                    Text(
                        "No hay transacciones en este período",
                        fontSize = 12.sp,
                        color = Slate400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
                movimientos.forEach { movimiento ->
                    FilaMovimiento(
                        icono = movimiento.icono,
                        fondoIcono = movimiento.fondoIcono,
                        colorIcono = movimiento.colorIcono,
                        titulo = movimiento.titulo,
                        detalle = movimiento.detalle,
                        monto = movimiento.monto,
                        colorMonto = Rosa600
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { pendiente("Registrar gasto") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Turquesa, contentColor = TealOscuro)
            ) {
                Icon(Icons.Outlined.AddCircle, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Registrar gasto en ${categoria.nombre}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { pendiente("Ajustar presupuesto") }) {
                Text("Ajustar presupuesto mensual", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TealMedio)
            }
        }
    }
}

@Composable
private fun TarjetaPresupuesto(categoria: CategoriaEjemplo) {
    val avance = (categoria.gastado.toFloat() / categoria.presupuesto).coerceIn(0f, 1f)
    val restante = categoria.presupuesto - categoria.gastado
    val saludable = avance < 0.8f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TealOscuro, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("Consumo Mensual", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = AguaClaro.copy(alpha = 0.8f))
                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 4.dp)) {
                    Text(formatoCop(categoria.gastado), fontSize = 30.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("COP", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Turquesa, modifier = Modifier.padding(bottom = 6.dp))
                }
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Turquesa, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(categoria.icono, null, tint = TealOscuro, modifier = Modifier.size(26.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth()) {
            Text("Presupuesto: ${formatoCop(categoria.presupuesto)} COP", fontSize = 12.sp, color = AguaClaro, modifier = Modifier.weight(1f))
            Text("${(avance * 100).toInt()}% consumido", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Turquesa)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(avance)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(if (saludable) Turquesa else Rosa400)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (saludable) Icons.Outlined.CheckCircle else Icons.Outlined.Warning,
                null,
                tint = if (saludable) Esmeralda300 else Rosa400,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "${formatoCop(restante)} COP restantes",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (saludable) Esmeralda300 else Rosa400,
                modifier = Modifier.weight(1f)
            )
            Text(
                if (saludable) "En límite saludable" else "Cerca del límite",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (saludable) Esmeralda300 else Rosa400,
                modifier = Modifier
                    .background((if (saludable) Esmeralda400 else Rosa400).copy(alpha = 0.2f), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun Metrica(
    modifier: Modifier,
    icono: ImageVector,
    titulo: String,
    valor: String,
    fondo: Color = Color.White,
    colorIcono: Color = TealMedio,
    colorTitulo: Color = Slate400,
    colorValor: Color = Slate800
) {
    Tarjeta(modifier = modifier, radio = 16.dp, fondo = fondo, borde = if (fondo == Color.White) Slate200.copy(alpha = 0.5f) else fondo, espaciado = 4.dp) {
        Icon(icono, null, tint = colorIcono, modifier = Modifier.size(18.dp))
        Text(titulo, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = colorTitulo)
        Text(valor, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorValor)
    }
}

@Composable
private fun SelectorPeriodo() {
    val hoy = LocalDate.now()
    val periodos = listOf(periodoActual(hoy), periodoActual(hoy.minusMonths(1)), "Últimos 3 meses")
    var seleccionado by rememberSaveable { mutableIntStateOf(0) }

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periodos.forEachIndexed { indice, periodo ->
            val activo = indice == seleccionado
            FilterChip(
                selected = activo,
                onClick = { seleccionado = indice },
                label = { Text(periodo, fontSize = 12.sp, fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium) },
                leadingIcon = if (activo) {
                    { Icon(Icons.Outlined.Check, null, modifier = Modifier.size(14.dp)) }
                } else null,
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = Slate600,
                    selectedContainerColor = Turquesa,
                    selectedLabelColor = TealOscuro,
                    selectedLeadingIconColor = TealOscuro
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = activo,
                    borderColor = Slate200,
                    selectedBorderColor = Turquesa
                )
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun DetalleCategoriaScreenPreview() {
    Proyectointegradormovilesg7Theme {
        DetalleCategoriaScreen(categoriaId = 2, onAtras = {})
    }
}
