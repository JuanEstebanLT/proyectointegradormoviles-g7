package com.example.proyecto_integrador_moviles_g7.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate100
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate800
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

private val formatoPesos = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.forLanguageTag("es-CO")).apply {
    groupingSeparator = '.'
})

/** Formatea un valor en pesos colombianos: 1200000 -> "$ 1.200.000". */
fun formatoCop(valor: Long): String = "$ ${formatoPesos.format(abs(valor))}"

@Composable
fun LogoG7(tamano: Dp = 24.dp, tamanoTexto: TextUnit = 11.sp, radio: Dp = 6.dp) {
    Box(
        modifier = Modifier
            .size(tamano)
            .background(Turquesa, RoundedCornerShape(radio)),
        contentAlignment = Alignment.Center
    ) {
        Text("G7", fontSize = tamanoTexto, fontWeight = FontWeight.Black, color = TealOscuro)
    }
}

/** Barra superior blanca con botón de regreso, logo G7 y título centrado. */
@Composable
fun BarraSuperior(
    titulo: String,
    onAtras: () -> Unit,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    Column(Modifier.background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAtras) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Regresar", tint = Slate700)
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LogoG7()
                Spacer(Modifier.width(8.dp))
                Text(titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate800)
            }
            Row(verticalAlignment = Alignment.CenterVertically, content = acciones)
        }
        HorizontalDivider(color = Slate100)
    }
}

/** Contenedor blanco con borde suave, equivalente a las tarjetas del prototipo. */
@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    radio: Dp = 24.dp,
    fondo: Color = Color.White,
    borde: Color = Slate100,
    relleno: PaddingValues = PaddingValues(16.dp),
    espaciado: Dp = 12.dp,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(radio)
    Column(
        modifier = modifier
            .clip(forma)
            .background(fondo)
            .border(1.dp, borde, forma)
            .padding(relleno),
        verticalArrangement = Arrangement.spacedBy(espaciado),
        content = contenido
    )
}

/** Fila de un movimiento: icono, título, detalle y monto con signo. */
@Composable
fun FilaMovimiento(
    icono: ImageVector,
    fondoIcono: Color,
    colorIcono: Color,
    titulo: String,
    detalle: String,
    monto: Long,
    colorMonto: Color,
    modifier: Modifier = Modifier,
    colorDetalle: Color = Slate400,
    fondoDetalle: Color = Color.Transparent
) {
    Tarjeta(modifier = modifier.fillMaxWidth(), radio = 16.dp, relleno = PaddingValues(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(fondoIcono, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    titulo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    detalle,
                    fontSize = 10.sp,
                    color = colorDetalle,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .background(fondoDetalle, RoundedCornerShape(4.dp))
                        .padding(horizontal = if (fondoDetalle == Color.Transparent) 0.dp else 6.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                (if (monto >= 0) "+ " else "- ") + formatoCop(monto) + " COP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorMonto
            )
        }
    }
}
