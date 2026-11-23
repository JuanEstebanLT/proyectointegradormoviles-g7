package com.example.proyecto_integrador_moviles_g7.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LaptopMac
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar800
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.HieloSuave
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate100
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate200
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as EstiloTextoFecha
import java.util.Locale
import kotlin.math.abs

// ============================================================ Formatos

private val localeCo: Locale = Locale.forLanguageTag("es-CO")

private val formatoPesos = DecimalFormat("#,##0", DecimalFormatSymbols(localeCo).apply {
    groupingSeparator = '.'
})

/** Formatea un valor en pesos colombianos: 1200000 -> "$ 1.200.000". */
fun formatoCop(valor: Long): String = "$ ${formatoPesos.format(abs(valor))}"

/** "+ $ 1.200.000" para ingresos y "- $ 1.200.000" para gastos. */
fun formatoCopConSigno(movimiento: Movimiento): String =
    (if (movimiento.tipo == TipoMovimiento.INGRESO) "+ " else "- ") + formatoCop(movimiento.monto)

/** Valor abreviado para gráficos: 1450000 -> "$1,45M", 360000 -> "$360k". */
fun formatoCopCorto(valor: Long): String = when {
    abs(valor) >= 1_000_000 -> "$" + String.format(localeCo, "%.2f", valor / 1_000_000.0).trimEnd('0').trimEnd(',') + "M"
    abs(valor) >= 1_000 -> "$${valor / 1_000}k"
    else -> "$$valor"
}

private val formatoHora = DateTimeFormatter.ofPattern("h:mm a", localeCo)
private val formatoDiaMes = DateTimeFormatter.ofPattern("d MMM", localeCo)
private val formatoDiaMesAnio = DateTimeFormatter.ofPattern("d MMM yyyy", localeCo)

fun nombreMes(mes: YearMonth): String =
    mes.month.getDisplayName(EstiloTextoFecha.FULL, localeCo).replaceFirstChar { it.uppercase() } + " ${mes.year}"

fun nombreMesCorto(mes: YearMonth): String =
    mes.month.getDisplayName(EstiloTextoFecha.SHORT, localeCo).trimEnd('.').replaceFirstChar { it.uppercase() }

/** "Hoy", "Ayer" o "15 de noviembre". */
fun etiquetaDia(fecha: LocalDate, hoy: LocalDate = LocalDate.now()): String = when (fecha) {
    hoy -> "Hoy"
    hoy.minusDays(1) -> "Ayer"
    else -> "${fecha.dayOfMonth} de ${fecha.month.getDisplayName(EstiloTextoFecha.FULL, localeCo)}" +
        if (fecha.year != hoy.year) " de ${fecha.year}" else ""
}

fun fechaCorta(fecha: LocalDateTime): String = fecha.format(formatoDiaMes).replace(".", "")

fun fechaLarga(fecha: LocalDate): String = fecha.format(formatoDiaMesAnio).replace(".", "")

fun hora(fecha: LocalDateTime): String = fecha.format(formatoHora)

/** Deja solo los dígitos de un texto (para campos de monto). */
fun soloDigitos(texto: String, maximo: Int = 12): String = texto.filter { it.isDigit() }.take(maximo).trimStart('0')

// ============================================================ Categorías

/** Icono según el nombre: la API no guarda iconos y los ids varían por usuario. */
fun iconoCategoria(nombre: String?, tipo: TipoMovimiento = TipoMovimiento.GASTO): ImageVector {
    val n = nombre.orEmpty().lowercase(localeCo)
    fun tiene(vararg claves: String) = claves.any { n.contains(it) }
    return when {
        tiene("vivienda", "arriendo", "hogar", "casa") -> Icons.Outlined.Home
        tiene("aliment", "comida", "mercado", "restaur") -> Icons.Outlined.Restaurant
        tiene("transporte", "gasolina", "bus", "taxi") -> Icons.Outlined.DirectionsBus
        tiene("ocio", "entreten", "salida", "cine", "diversi") -> Icons.Outlined.Movie
        tiene("salud", "médic", "medic", "farmac") -> Icons.Outlined.LocalHospital
        tiene("educa", "estudio", "universidad", "colegio") -> Icons.Outlined.School
        tiene("servicio", "luz", "agua", "energ") -> Icons.Outlined.Lightbulb
        tiene("ropa", "vestuario") -> Icons.Outlined.Checkroom
        tiene("gimnas", "deporte") -> Icons.Outlined.FitnessCenter
        tiene("mascota") -> Icons.Outlined.Pets
        tiene("celular", "internet", "telefon") -> Icons.Outlined.Smartphone
        tiene("salario", "nómina", "nomina", "sueldo") -> Icons.Outlined.WorkOutline
        tiene("freelance", "honorario") -> Icons.Outlined.LaptopMac
        tiene("inversi", "rendimiento", "interes") -> Icons.AutoMirrored.Outlined.TrendingUp
        tiene("venta", "negocio") -> Icons.Outlined.Storefront
        tiene("regalo", "premio", "bono") -> Icons.Outlined.CardGiftcard
        tiene("ahorro") -> Icons.Outlined.Savings
        tipo == TipoMovimiento.INGRESO -> Icons.Outlined.Payments
        else -> Icons.Outlined.ShoppingBag
    }
}

/** Color de una barra de presupuesto: verde < 80 %, ámbar 80–100 %, rosa si se excede. */
fun colorUsoPresupuesto(porcentaje: Float): Color = when {
    porcentaje > 100f -> Rosa600
    porcentaje >= 80f -> Ambar500
    else -> Esmeralda600
}

// ============================================================ Marca y barras

@Composable
fun LogoG7(tamano: Dp = 34.dp, tamanoTexto: TextUnit = 13.sp, radio: Dp = 10.dp) {
    Box(
        modifier = Modifier
            .size(tamano)
            .background(Turquesa, RoundedCornerShape(radio)),
        contentAlignment = Alignment.Center
    ) {
        Text("G7", fontSize = tamanoTexto, fontWeight = FontWeight.Black, color = TealOscuro)
    }
}

@Composable
fun Avatar(
    iniciales: String,
    tamano: Dp = 36.dp,
    fondo: Color = TealOscuro,
    colorTexto: Color = Color.White,
    tamanoTexto: TextUnit = 13.sp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(tamano)
            .clip(CircleShape)
            .background(fondo)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(iniciales, fontSize = tamanoTexto, fontWeight = FontWeight.Bold, color = colorTexto)
    }
}

/** Barra superior de las pestañas principales: logo, título, campana con contador y avatar. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncabezadoPrincipal(
    titulo: String,
    subtitulo: String,
    iniciales: String,
    notificacionesSinLeer: Int,
    onNotificaciones: () -> Unit,
    onPerfil: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LogoG7()
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(subtitulo, fontSize = 11.sp, color = TealMedio, fontWeight = FontWeight.SemiBold)
                    Text(
                        titulo, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Slate900,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onNotificaciones) {
                BadgedBox(badge = {
                    if (notificacionesSinLeer > 0) Badge(containerColor = Rosa600) { Text("$notificacionesSinLeer") }
                }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = TealOscuro)
                }
            }
            Box(Modifier.padding(end = 12.dp)) {
                Avatar(iniciales, tamano = 34.dp, tamanoTexto = 12.sp, onClick = onPerfil)
            }
        }
    )
}

/** Barra superior de pantallas secundarias, con flecha de regreso. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraContextual(
    titulo: String,
    onAtras: () -> Unit,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        navigationIcon = {
            IconButton(onClick = onAtras) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = TealOscuro)
            }
        },
        title = {
            Text(
                titulo, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Slate900,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        },
        actions = acciones
    )
}

enum class SeccionPrincipal(val etiqueta: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Outlined.Home),
    MOVIMIENTOS("Movimientos", Icons.Outlined.SwapHoriz),
    INFORMES("Informes", Icons.Outlined.BarChart),
    AHORRO("Ahorro", Icons.Outlined.Savings)
}

@Composable
fun BarraNavegacionG7(seleccionada: SeccionPrincipal, onSeleccion: (SeccionPrincipal) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        SeccionPrincipal.entries.forEach { seccion ->
            val activa = seccion == seleccionada
            NavigationBarItem(
                selected = activa,
                onClick = { if (!activa) onSeleccion(seccion) },
                icon = { Icon(seccion.icono, contentDescription = null) },
                label = {
                    Text(seccion.etiqueta, fontSize = 11.sp, fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealOscuro,
                    selectedTextColor = TealOscuro,
                    indicatorColor = Turquesa.copy(alpha = 0.35f),
                    unselectedIconColor = Slate400,
                    unselectedTextColor = Slate400
                )
            )
        }
    }
}

// ============================================================ Tarjetas y filas

@Composable
fun TarjetaG7(
    modifier: Modifier = Modifier,
    fondo: Color = Color.White,
    borde: Color = Slate200,
    radio: Dp = 20.dp,
    relleno: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radio))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(radio),
        color = fondo,
        border = BorderStroke(1.dp, borde)
    ) {
        Column(Modifier.padding(relleno), content = contenido)
    }
}

@Composable
fun CajaIcono(
    icono: ImageVector,
    fondo: Color,
    tinte: Color,
    tamano: Dp = 42.dp,
    tamanoIcono: Dp = 22.dp,
    radio: Dp = 12.dp
) {
    Box(
        modifier = Modifier
            .size(tamano)
            .clip(RoundedCornerShape(radio))
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(tamanoIcono))
    }
}

@Composable
fun FilaMovimiento(
    movimiento: Movimiento,
    nombreCategoria: String,
    modifier: Modifier = Modifier,
    mostrarFecha: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    TarjetaG7(modifier = modifier, radio = 18.dp, relleno = PaddingValues(12.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CajaIcono(
                iconoCategoria(nombreCategoria, movimiento.tipo),
                fondo = if (esIngreso) Esmeralda50 else Rosa50,
                tinte = if (esIngreso) Esmeralda600 else Rosa600,
                tamano = 40.dp, tamanoIcono = 20.dp
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    movimiento.descripcion, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                val detalle = if (mostrarFecha) "$nombreCategoria • ${fechaCorta(movimiento.fecha)}"
                else "$nombreCategoria • ${hora(movimiento.fecha)}"
                Text(detalle, fontSize = 11.sp, color = Slate500, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                formatoCopConSigno(movimiento), fontWeight = FontWeight.Black, fontSize = 14.sp,
                color = if (esIngreso) Esmeralda600 else Rosa600
            )
        }
    }
}

/** Barra de progreso redondeada (0..1). */
@Composable
fun BarraProgreso(progreso: Float, color: Color, modifier: Modifier = Modifier, alto: Dp = 8.dp, fondo: Color = Slate100) {
    Box(
        modifier
            .fillMaxWidth()
            .height(alto)
            .clip(RoundedCornerShape(50))
            .background(fondo)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progreso.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

@Composable
fun Etiqueta(texto: String, fondo: Color, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = fondo, shape = RoundedCornerShape(50)) {
        Text(
            texto, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun TituloSeccion(titulo: String, accion: String? = null, onAccion: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(titulo, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Slate900)
        if (accion != null) {
            Text(
                accion, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TealMedio,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAccion)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun EstadoVacio(icono: ImageVector, titulo: String, mensaje: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(HieloSuave),
            contentAlignment = Alignment.Center
        ) {
            CajaIcono(icono, fondo = Turquesa, tinte = TealOscuro, tamano = 60.dp, tamanoIcono = 32.dp, radio = 20.dp)
        }
        Spacer(Modifier.height(16.dp))
        Text(titulo, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Slate900, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(mensaje, fontSize = 13.sp, color = Slate500, textAlign = TextAlign.Center, lineHeight = 18.sp)
    }
}

@Composable
fun CargandoPantalla(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = TealOscuro)
    }
}

// ============================================================ Avisos

@Composable
fun AvisoError(mensaje: String, modifier: Modifier = Modifier) {
    Aviso(mensaje, Icons.Outlined.ErrorOutline, fondo = Rosa50, color = Rosa700, borde = Rosa600.copy(alpha = 0.3f), modifier = modifier)
}

@Composable
fun AvisoAdvertencia(mensaje: String, modifier: Modifier = Modifier) {
    Aviso(mensaje, Icons.Outlined.Info, fondo = Ambar50, color = Ambar800, borde = Ambar500.copy(alpha = 0.4f), modifier = modifier)
}

@Composable
fun AvisoInfo(mensaje: String, modifier: Modifier = Modifier) {
    Aviso(mensaje, Icons.Outlined.Info, fondo = HieloSuave, color = TealOscuro, borde = TealMedio.copy(alpha = 0.25f), modifier = modifier)
}

@Composable
private fun Aviso(mensaje: String, icono: ImageVector, fondo: Color, color: Color, borde: Color, modifier: Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = fondo,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, borde)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(mensaje, color = color, fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ============================================================ Campos y botones

/** Muestra dígitos con separador de miles: "1200000" -> "1.200.000". */
object SeparadorMiles : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digitos = text.text
        if (digitos.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        val formateado = formatoPesos.format(digitos.toLong())
        // Posición en el texto formateado de cada posición del original
        val haciaFormateado = IntArray(digitos.length + 1)
        var o = 0
        formateado.forEachIndexed { i, c ->
            if (c != '.') {
                haciaFormateado[o] = i
                o++
            }
        }
        haciaFormateado[digitos.length] = formateado.length
        val mapeo = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = haciaFormateado[offset.coerceIn(0, digitos.length)]
            override fun transformedToOriginal(offset: Int) =
                formateado.take(offset.coerceIn(0, formateado.length)).count { it != '.' }
        }
        return TransformedText(AnnotatedString(formateado), mapeo)
    }
}

@Composable
fun coloresCampo(acento: Color = TealOscuro) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = acento,
    unfocusedBorderColor = Slate200,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Slate50,
    focusedLabelColor = acento,
    cursorColor = acento
)

/**
 * Campo de monto grande en COP. [digitos] guarda solo números (sin puntos);
 * el separador de miles se muestra con [SeparadorMiles].
 */
@Composable
fun CampoMonto(
    digitos: String,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier,
    acento: Color = TealOscuro,
    tamanoTexto: TextUnit = 26.sp,
    error: Boolean = false
) {
    OutlinedTextField(
        value = digitos,
        onValueChange = { onCambio(soloDigitos(it)) },
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "0", fontSize = tamanoTexto, fontWeight = FontWeight.Black, color = Slate400,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
        },
        leadingIcon = {
            Text("$", fontSize = tamanoTexto, fontWeight = FontWeight.Black, color = acento, modifier = Modifier.padding(start = 12.dp))
        },
        trailingIcon = {
            Text("COP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate500, modifier = Modifier.padding(end = 12.dp))
        },
        textStyle = TextStyle(fontSize = tamanoTexto, fontWeight = FontWeight.Black, color = Slate900, textAlign = TextAlign.Center),
        visualTransformation = SeparadorMiles,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = error,
        shape = RoundedCornerShape(16.dp),
        colors = coloresCampo(acento)
    )
}

@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    cargando: Boolean = false,
    habilitado: Boolean = true,
    fondo: Color = TealOscuro,
    colorContenido: Color = Color.White
) {
    Button(
        onClick = onClick,
        enabled = habilitado && !cargando,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = fondo,
            contentColor = colorContenido,
            disabledContainerColor = fondo.copy(alpha = 0.5f),
            disabledContentColor = colorContenido.copy(alpha = 0.8f)
        )
    ) {
        if (cargando) {
            CircularProgressIndicator(color = colorContenido, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            if (icono != null) {
                Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(texto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

/** Selector de dos opciones tipo "píldora" (por ejemplo Gastos / Ingresos). */
@Composable
fun SelectorSegmentado(
    opciones: List<String>,
    seleccion: Int,
    onSeleccion: (Int) -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        color = Color.White,
        border = BorderStroke(1.dp, Slate200)
    ) {
        Row(Modifier.padding(4.dp)) {
            opciones.forEachIndexed { indice, opcion ->
                val activa = indice == seleccion
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .clickable(enabled = habilitado) { onSeleccion(indice) },
                    shape = RoundedCornerShape(50),
                    color = if (activa) TealOscuro else Color.Transparent
                ) {
                    Text(
                        opcion,
                        color = when {
                            activa -> Color.White
                            habilitado -> Slate900
                            else -> Slate400
                        },
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 9.dp)
                    )
                }
            }
        }
    }
}
