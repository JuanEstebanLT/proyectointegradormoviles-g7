package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BakeryDining
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalTaxi
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar500
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa

// Datos fijos para probar las pantallas mientras no exista la API ni los ViewModel.
// Se reemplazarán por los repositorios de la capa data.

data class CategoriaEjemplo(
    val id: Int,
    val nombre: String,
    val icono: ImageVector,
    val color: Color,
    val gastado: Long,
    val presupuesto: Long
)

data class MovimientoEjemplo(
    val categoriaId: Int,
    val titulo: String,
    val detalle: String,
    val icono: ImageVector,
    val fondoIcono: Color,
    val colorIcono: Color,
    val monto: Long
)

val categoriasEjemplo = listOf(
    CategoriaEjemplo(1, "Vivienda", Icons.Outlined.Home, TealOscuro, 450_000, 600_000),
    CategoriaEjemplo(2, "Alimentación", Icons.Outlined.Restaurant, TealMedio, 200_000, 500_000),
    CategoriaEjemplo(3, "Transporte", Icons.Outlined.DirectionsBus, Turquesa, 120_000, 200_000),
    CategoriaEjemplo(4, "Ocio", Icons.Outlined.Movie, Ambar500, 80_000, 150_000)
)

val movimientosEjemplo = listOf(
    MovimientoEjemplo(1, "Arriendo apartamento", "01 Nov, 09:00 a.m. • Transferencia", Icons.Outlined.Home, Color(0xFFECFEFF), Color(0xFF0891B2), -400_000),
    MovimientoEjemplo(1, "Servicios públicos", "03 Nov, 07:10 p.m. • Tarjeta Débito", Icons.Outlined.Bolt, Color(0xFFFFFBEB), Color(0xFFB45309), -50_000),
    MovimientoEjemplo(2, "Supermercado Éxito", "Ayer, 06:20 p.m. • Tarjeta Débito", Icons.Outlined.ShoppingCart, Color(0xFFEFF6FF), Color(0xFF2563EB), -115_000),
    MovimientoEjemplo(2, "Almuerzo ejecutivo", "18 Nov, 01:42 p.m. • Efectivo", Icons.Outlined.LunchDining, Color(0xFFF0FDFA), Color(0xFF0D9488), -25_000),
    MovimientoEjemplo(2, "Frutería y Verduras El Jardín", "12 Nov, 10:15 a.m. • Transferencia", Icons.Outlined.Eco, Color(0xFFECFEFF), Color(0xFF0891B2), -38_000),
    MovimientoEjemplo(2, "Panadería y Café La 70", "05 Nov, 08:30 a.m. • Efectivo", Icons.Outlined.BakeryDining, Color(0xFFFFFBEB), Color(0xFFB45309), -22_000),
    MovimientoEjemplo(3, "Recarga tarjeta de transporte", "10 Nov, 06:45 a.m. • Efectivo", Icons.Outlined.CreditCard, Color(0xFFF0FDFA), Color(0xFF0D9488), -80_000),
    MovimientoEjemplo(3, "Taxi al aeropuerto", "15 Nov, 04:30 a.m. • Efectivo", Icons.Outlined.LocalTaxi, Color(0xFFFFFBEB), Color(0xFFB45309), -40_000),
    MovimientoEjemplo(4, "Cine con amigos", "16 Nov, 08:00 p.m. • Tarjeta Débito", Icons.Outlined.Theaters, Color(0xFFFFF1F2), Color(0xFFE11D48), -45_000),
    MovimientoEjemplo(4, "Suscripción de música", "02 Nov, 12:00 p.m. • Tarjeta Débito", Icons.Outlined.MusicNote, Color(0xFFF5F3FF), Color(0xFF7C3AED), -35_000)
)

val totalGastosEjemplo: Long = categoriasEjemplo.sumOf { it.gastado }
