package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar500
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa
import java.time.format.DateTimeFormatter
import java.util.Locale

data class EstiloCategoria(
    val icono: ImageVector,
    val color: Color,
    val fondoIcono: Color
)

/** El estilo se elige por nombre: los ids de categoría los asigna la API y varían por usuario. */
fun estiloCategoria(nombreCategoria: String?): EstiloCategoria = when (nombreCategoria?.lowercase()) {
    "vivienda" -> EstiloCategoria(Icons.Outlined.Home, TealOscuro, Color(0xFFECFEFF))
    "alimentación", "alimentacion" -> EstiloCategoria(Icons.Outlined.Restaurant, TealMedio, Color(0xFFEFF6FF))
    "transporte" -> EstiloCategoria(Icons.Outlined.DirectionsBus, Turquesa, Color(0xFFF0FDFA))
    "ocio" -> EstiloCategoria(Icons.Outlined.Movie, Ambar500, Color(0xFFFFF1F2))
    "salud" -> EstiloCategoria(Icons.Outlined.LocalHospital, Ambar500, Color(0xFFFFF1F2))
    else -> EstiloCategoria(Icons.Outlined.AccountBalanceWallet, TealOscuro, Color(0xFFECFEFF))
}

private val formatoFechaMovimiento = DateTimeFormatter.ofPattern(
    "dd MMM, hh:mm a",
    Locale.forLanguageTag("es-CO")
)

fun detalleMovimiento(movimiento: Movimiento, nombreCategoria: String): String =
    "$nombreCategoria • ${movimiento.fecha.format(formatoFechaMovimiento)} • ${movimiento.medioPago}"
