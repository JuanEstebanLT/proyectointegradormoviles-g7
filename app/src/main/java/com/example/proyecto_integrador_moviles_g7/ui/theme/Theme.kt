package com.example.proyecto_integrador_moviles_g7.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// El prototipo solo define modo claro, por eso no se usa color dinámico ni tema oscuro
private val ColoresFinanzasG7 = lightColorScheme(
    primary = TealOscuro,
    onPrimary = Color.White,
    primaryContainer = AguaClaro,
    onPrimaryContainer = TealOscuro,
    secondary = TealMedio,
    onSecondary = Color.White,
    tertiary = Turquesa,
    onTertiary = TealOscuro,
    background = FondoClaro,
    onBackground = Slate800,
    surface = Color.White,
    onSurface = Slate800,
    onSurfaceVariant = Slate500,
    outline = Slate200,
    error = Rosa600
)

@Composable
fun Proyectointegradormovilesg7Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresFinanzasG7,
        typography = Typography,
        content = content
    )
}
