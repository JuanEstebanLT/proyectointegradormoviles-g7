package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.HieloSuave
import com.example.proyecto_integrador_moviles_g7.ui.theme.Proyectointegradormovilesg7Theme
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.SuperficieAtenuada
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ============================================================ 01 — Splash

@Composable
fun SplashScreen(onTerminar: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1600)
        onTerminar()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TealOscuro),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Turquesa),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = TealOscuro, modifier = Modifier.size(50.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text("Finanzas G7", fontSize = 30.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text("Control total y consciente de tu dinero", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AguaClaro)
            Spacer(Modifier.height(36.dp))
            CircularProgressIndicator(color = Turquesa, strokeWidth = 3.dp, modifier = Modifier.size(28.dp))
        }
        Text(
            "Proyecto Integrador Móviles • Grupo 7",
            fontSize = 12.sp,
            color = Color(0xFF86B9C4),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
        )
    }
}

// ============================================================ 02 — Onboarding

private data class PasoBienvenida(val icono: ImageVector, val titulo: String, val descripcion: String)

private val pasosBienvenida = listOf(
    PasoBienvenida(
        Icons.Outlined.Insights,
        "Visualiza tus ingresos y gastos",
        "Registra tus movimientos en segundos, clasifícalos por categoría y mira tu saldo siempre al día."
    ),
    PasoBienvenida(
        Icons.Outlined.Savings,
        "Alcanza tus metas de ahorro",
        "Define presupuestos por categoría y una meta mensual; te avisamos cuando estés cerca del límite."
    ),
    PasoBienvenida(
        Icons.Outlined.Security,
        "Tus datos, protegidos",
        "Tu cuenta está protegida con contraseña cifrada y cada sesión usa un token seguro."
    )
)

@Composable
fun OnboardingScreen(onCrearCuenta: () -> Unit, onIniciarSesion: () -> Unit) {
    val estadoPagina = rememberPagerState(pageCount = { pasosBienvenida.size })
    val scope = rememberCoroutineScope()
    val ultima = estadoPagina.currentPage == pasosBienvenida.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onIniciarSesion) {
                Text("Saltar", color = TealOscuro, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        HorizontalPager(state = estadoPagina, modifier = Modifier.weight(1f)) { pagina ->
            val paso = pasosBienvenida[pagina]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(HieloSuave),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Turquesa),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(paso.icono, contentDescription = null, tint = TealOscuro, modifier = Modifier.size(46.dp))
                    }
                }
                Spacer(Modifier.height(36.dp))
                Text(
                    paso.titulo, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Slate900,
                    textAlign = TextAlign.Center, lineHeight = 30.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    paso.descripcion, fontSize = 14.sp, color = Slate500, textAlign = TextAlign.Center,
                    lineHeight = 20.sp, modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(pasosBienvenida.size) { indice ->
                val activo = estadoPagina.currentPage == indice
                Box(
                    Modifier
                        .height(8.dp)
                        .width(if (activo) 24.dp else 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (activo) TealOscuro else SuperficieAtenuada)
                )
            }
        }
        Spacer(Modifier.height(28.dp))
        BotonPrimario(
            texto = if (ultima) "Crear mi cuenta" else "Siguiente",
            icono = if (ultima) Icons.Outlined.Check else Icons.AutoMirrored.Outlined.ArrowForward,
            onClick = {
                if (ultima) onCrearCuenta()
                else scope.launch { estadoPagina.animateScrollToPage(estadoPagina.currentPage + 1) }
            }
        )
        TextButton(onClick = onIniciarSesion, modifier = Modifier.padding(top = 4.dp)) {
            Text("Ya tengo una cuenta", color = TealOscuro, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    Proyectointegradormovilesg7Theme { OnboardingScreen({}, {}) }
}
