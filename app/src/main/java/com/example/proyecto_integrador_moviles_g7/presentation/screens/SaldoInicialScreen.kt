package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoError
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoInfo
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraContextual
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.presentation.components.CampoMonto
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.components.soloDigitos
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.SaldoInicialViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate200
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa

/** Atajos que suman al monto escrito. */
internal val atajosMonto = listOf(100_000L to "+$100k", 500_000L to "+$500k", 1_000_000L to "+$1M")

@Composable
fun SaldoInicialScreen(
    viewModel: SaldoInicialViewModel,
    onAtras: () -> Unit,
    onGuardado: () -> Unit
) {
    val saldoActual by viewModel.saldoInicial.collectAsStateWithLifecycle()
    var digitos by rememberSaveable { mutableStateOf(if (saldoActual > 0L) saldoActual.toString() else "") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val editando = saldoActual > 0L

    Scaffold(
        containerColor = FondoClaro,
        topBar = { BarraContextual(if (editando) "Editar saldo inicial" else "Configuración inicial", onAtras = onAtras) }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Turquesa),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.AccountBalance, contentDescription = null, tint = TealOscuro, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("¿Con cuánto dinero empiezas?", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Slate900, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text(
                "Ingresa el dinero que tienes hoy en tu cuenta o en efectivo. A partir de él calculamos tu saldo disponible.",
                fontSize = 14.sp, color = Slate500, textAlign = TextAlign.Center, lineHeight = 20.sp
            )
            Spacer(Modifier.height(24.dp))

            TarjetaG7(radio = 24.dp, relleno = androidx.compose.foundation.layout.PaddingValues(20.dp)) {
                Text(
                    "SALDO INICIAL", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = TealMedio,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))
                CampoMonto(digitos, { digitos = it; error = null }, tamanoTexto = 28.sp, error = error != null)
                Spacer(Modifier.height(16.dp))
                Text("Atajos rápidos", fontSize = 12.sp, color = Slate500, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    atajosMonto.forEach { (valor, etiqueta) ->
                        BotonAtajo(etiqueta, Modifier.weight(1f)) {
                            digitos = soloDigitos(((digitos.toLongOrNull() ?: 0L) + valor).toString())
                            error = null
                        }
                    }
                    BotonAtajo("Limpiar", Modifier.weight(1f)) { digitos = ""; error = null }
                }
            }

            Spacer(Modifier.height(16.dp))
            if (error != null) AvisoError(error!!)
            else if (editando) AvisoInfo("Tu saldo inicial actual es ${formatoCop(saldoActual)} COP. Cambiarlo recalcula tu saldo disponible.")
            else AvisoInfo("Podrás cambiarlo cuando quieras desde tu perfil.")

            Spacer(Modifier.height(24.dp))
            BotonPrimario(
                texto = if (editando) "Guardar cambios" else "Guardar y continuar",
                icono = Icons.Outlined.Check,
                onClick = {
                    error = viewModel.establecerSaldo(digitos.toLongOrNull() ?: 0L)
                    if (error == null) onGuardado()
                }
            )
        }
    }
}

@Composable
internal fun BotonAtajo(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Slate200),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Text(texto, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealOscuro, maxLines = 1)
    }
}
