package com.example.proyecto_integrador_moviles_g7.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Sistema de diseño Finanzas G7 (Stitch): solo modo claro
private val ColoresFinanzasG7 = lightColorScheme(
    primary = TealOscuro,
    onPrimary = Color.White,
    primaryContainer = Turquesa,
    onPrimaryContainer = TealOscuro,
    secondary = TealMedio,
    onSecondary = Color.White,
    secondaryContainer = HieloSuave,
    onSecondaryContainer = TealOscuro,
    tertiary = Esmeralda600,
    onTertiary = Color.White,
    background = FondoClaro,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = HieloSuave,
    onSurfaceVariant = Slate500,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    outline = Slate200,
    outlineVariant = Slate200,
    error = Rosa600,
    onError = Color.White,
    errorContainer = Rosa50,
    onErrorContainer = Rosa700
)

private val FormasFinanzasG7 = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun Proyectointegradormovilesg7Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresFinanzasG7,
        typography = Typography,
        shapes = FormasFinanzasG7,
        content = content
    )
}
