package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraSuperior
import com.example.proyecto_integrador_moviles_g7.presentation.components.Tarjeta
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Crema
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda700
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Proyectointegradormovilesg7Theme
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate800
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro

private const val MONTO_MAXIMO = 99_999_999_999L

@Composable
fun SaldoInicialScreen(
    onAtras: () -> Unit,
    onGuardar: (saldoInicial: Long) -> Unit
) {
    var monto by rememberSaveable { mutableLongStateOf(0L) }

    fun fijarMonto(nuevo: Long) {
        monto = nuevo.coerceIn(0L, MONTO_MAXIMO)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
    ) {
        BarraSuperior(titulo = "Saldo Inicial", onAtras = onAtras) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(32.dp)
                    .background(TealOscuro, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(AguaClaro, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Flag, null, tint = TealOscuro, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(6.dp))
                Text("Paso 1 de 3 • Configuración", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TealOscuro)
            }

            Tarjeta(radio = 16.dp, fondo = Crema, borde = Crema) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Outlined.AccountBalanceWallet, null, tint = TealOscuro, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Define tu punto de partida", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                        Text(
                            "Ingresa el dinero total disponible en tus cuentas o efectivo para iniciar el seguimiento de tus finanzas en Finanzas G7.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = Slate600,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Tarjeta(
                modifier = Modifier.fillMaxWidth(),
                relleno = PaddingValues(20.dp),
                espaciado = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Monto inicial en efectivo o cuenta", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate500)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(formatoCop(monto), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TealOscuro)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "COP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TealMedio,
                            modifier = Modifier
                                .background(AguaClaro.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    if (monto > 0) {
                        EstadoMonto("Monto válido para iniciar tu gestión", Icons.Outlined.CheckCircle, Esmeralda700, Esmeralda50)
                    } else {
                        EstadoMonto("Ingresa un monto mayor a cero", Icons.Outlined.Info, Ambar700, Ambar50)
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Atajo("+ \$100k", TealOscuro, AguaClaro.copy(alpha = 0.4f)) { fijarMonto(monto + 100_000) }
                        Atajo("+ \$500k", TealOscuro, AguaClaro.copy(alpha = 0.4f)) { fijarMonto(monto + 500_000) }
                        Atajo("+ \$1M", TealOscuro, AguaClaro.copy(alpha = 0.4f)) { fijarMonto(monto + 1_000_000) }
                        Atajo("Limpiar", Rosa600, Rosa50) { monto = 0 }
                    }
                }
            }

            TecladoNumerico(
                onDigito = { digito -> if (monto <= MONTO_MAXIMO / 10) fijarMonto(monto * 10 + digito) },
                onDobleCero = { if (monto <= MONTO_MAXIMO / 100) fijarMonto(monto * 100) },
                onBorrar = { monto /= 10 }
            )
        }

        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onGuardar(monto) },
                enabled = monto > 0,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealOscuro)
            ) {
                Icon(Icons.Outlined.Check, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Guardar saldo", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Info, null, tint = Slate400, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "Podrás ajustar este saldo más adelante en los ajustes.",
                    fontSize = 11.sp,
                    color = Slate400,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EstadoMonto(texto: String, icono: ImageVector, color: Color, fondo: Color) {
    Row(
        modifier = Modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, null, tint = color, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(4.dp))
        Text(texto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
private fun Atajo(texto: String, color: Color, fondo: Color, onClick: () -> Unit) {
    Text(
        texto,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(fondo)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun TecladoNumerico(
    onDigito: (Int) -> Unit,
    onDobleCero: () -> Unit,
    onBorrar: () -> Unit
) {
    Tarjeta(modifier = Modifier.fillMaxWidth(), espaciado = 4.dp) {
        listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)).forEach { fila ->
            Row(Modifier.fillMaxWidth()) {
                fila.forEach { digito ->
                    TeclaNumerica(Modifier.weight(1f), onClick = { onDigito(digito) }) {
                        Text("$digito", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            TeclaNumerica(Modifier.weight(1f), onClick = onDobleCero) {
                Text("00", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TealOscuro)
            }
            TeclaNumerica(Modifier.weight(1f), onClick = { onDigito(0) }) {
                Text("0", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate700)
            }
            TeclaNumerica(Modifier.weight(1f), onClick = onBorrar) {
                Icon(Icons.AutoMirrored.Outlined.Backspace, contentDescription = "Borrar", tint = Slate500)
            }
        }
    }
}

@Composable
private fun TeclaNumerica(modifier: Modifier, onClick: () -> Unit, contenido: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        contenido()
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun SaldoInicialScreenPreview() {
    Proyectointegradormovilesg7Theme {
        SaldoInicialScreen(onAtras = {}, onGuardar = {})
    }
}
