package com.example.proyecto_integrador_moviles_g7.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.Sugerencia
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoError
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoInfo
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraNavegacionG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.presentation.components.CajaIcono
import com.example.proyecto_integrador_moviles_g7.presentation.components.CampoMonto
import com.example.proyecto_integrador_moviles_g7.presentation.components.EncabezadoPrincipal
import com.example.proyecto_integrador_moviles_g7.presentation.components.Etiqueta
import com.example.proyecto_integrador_moviles_g7.presentation.components.SeccionPrincipal
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.components.nombreMes
import com.example.proyecto_integrador_moviles_g7.presentation.components.etiquetaDia
import com.example.proyecto_integrador_moviles_g7.presentation.components.hora
import com.example.proyecto_integrador_moviles_g7.presentation.components.soloDigitos
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AhorroViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.HieloSuave
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa

private val atajosMeta = listOf(50_000L to "+$50k", 100_000L to "+$100k", 200_000L to "+$200k")

/** Pantalla 10 — Ahorro: meta mensual y sugerencias de la API. */
@Composable
fun AhorroScreen(
    viewModel: AhorroViewModel,
    iniciales: String,
    notificacionesSinLeer: Int,
    onSeccion: (SeccionPrincipal) -> Unit,
    onPerfil: () -> Unit,
    onNotificaciones: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var editando by rememberSaveable { mutableStateOf(false) }
    var digitos by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val mes = estado.mesActual
    var retirando by remember { mutableStateOf<Movimiento?>(null) }

    retirando?.let { abono ->
        DialogoConfirmacion(
            titulo = "¿Retirar este abono?",
            mensaje = "${formatoCop(abono.monto)} COP volverán a tu saldo disponible y saldrán de tu meta de ahorro.",
            confirmar = "Retirar",
            onConfirmar = {
                viewModel.retirarAbono(abono.id)?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                retirando = null
            },
            onCancelar = { retirando = null }
        )
    }

    val abrirEditor = {
        digitos = if (estado.metaAhorro > 0L) estado.metaAhorro.toString() else ""
        error = null
        editando = true
    }

    Scaffold(
        containerColor = FondoClaro,
        topBar = { EncabezadoPrincipal("Ahorro", "Finanzas G7", iniciales, notificacionesSinLeer, onNotificaciones, onPerfil) },
        bottomBar = { BarraNavegacionG7(SeccionPrincipal.AHORRO, onSeccion) }
    ) { relleno ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(relleno)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(color = TealOscuro, shape = RoundedCornerShape(26.dp), shadowElevation = 4.dp) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text(nombreMes(mes.mes).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Turquesa)
                                Text("Meta de ahorro mensual", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Text(
                                    if (estado.metaAhorro > 0L) "Objetivo: ${formatoCop(estado.metaAhorro)} COP" else "Aún no defines una meta",
                                    fontSize = 14.sp, color = Turquesa, fontWeight = FontWeight.SemiBold
                                )
                            }
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(50),
                                onClick = abrirEditor
                            ) {
                                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (estado.metaAhorro > 0L) "Editar meta" else "Definir meta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Surface(color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(20.dp)) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                ProgresoCircular(estado.progreso)
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text("Ahorrado este mes", fontSize = 12.sp, color = AguaClaro)
                                    Text("${formatoCop(mes.ahorro)} COP", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Turquesa)
                                    Spacer(Modifier.height(8.dp))
                                    Text(if (estado.metaAhorro > 0L) "Te faltan" else "Total ahorrado", fontSize = 12.sp, color = AguaClaro)
                                    Text(
                                        "${formatoCop(if (estado.metaAhorro > 0L) estado.faltante else estado.ahorroAcumulado)} COP",
                                        fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            when {
                                estado.metaAhorro > 0L && estado.faltante == 0L -> "¡Cumpliste tu meta de este mes!"
                                else -> "Total ahorrado desde el inicio: ${formatoCop(estado.ahorroAcumulado)} COP"
                            },
                            fontSize = 12.sp, color = AguaClaro
                        )
                    }
                }
            }

            item {
                TarjetaAbonar(
                    saldoDisponible = estado.saldoDisponible,
                    faltante = estado.faltante.takeIf { estado.metaAhorro > 0L },
                    abonando = estado.abonando,
                    onAbonar = { monto, alTerminar ->
                        viewModel.abonar(monto) { mensaje ->
                            alTerminar(mensaje)
                            if (mensaje == null) {
                                Toast.makeText(context, "Abonaste ${formatoCop(monto)} COP a tu meta", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            if (estado.abonosDelMes.isNotEmpty()) {
                item {
                    TarjetaG7(radio = 22.dp) {
                        Text("Abonos de este mes", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Slate900)
                        Text("Si retiras un abono, el dinero vuelve a tu saldo disponible.", fontSize = 12.sp, color = Slate500)
                        Spacer(Modifier.height(8.dp))
                        estado.abonosDelMes.forEach { abono ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                CajaIcono(Icons.Outlined.Savings, fondo = HieloSuave, tinte = TealOscuro, tamano = 36.dp, tamanoIcono = 18.dp, radio = 10.dp)
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("+ ${formatoCop(abono.monto)} COP", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TealOscuro)
                                    Text("${etiquetaDia(abono.fecha.toLocalDate())} • ${hora(abono.fecha)}", fontSize = 12.sp, color = Slate500)
                                }
                                TextButton(onClick = { retirando = abono }) {
                                    Text("Retirar", color = Rosa600, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            if (editando) {
                item {
                    TarjetaG7(radio = 24.dp) {
                        Text("Modificar meta mensual", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Slate900)
                        Spacer(Modifier.height(12.dp))
                        CampoMonto(digitos, { digitos = it; error = null }, tamanoTexto = 26.sp)
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            atajosMeta.forEach { (valor, etiqueta) ->
                                BotonAtajo(etiqueta, Modifier.weight(1f)) {
                                    digitos = soloDigitos(((digitos.toLongOrNull() ?: 0L) + valor).toString())
                                }
                            }
                            BotonAtajo("Limpiar", Modifier.weight(1f)) { digitos = "" }
                        }
                        error?.let {
                            Spacer(Modifier.height(10.dp))
                            AvisoError(it)
                        }
                        Spacer(Modifier.height(12.dp))
                        BotonPrimario(
                            "Guardar nueva meta",
                            icono = Icons.Outlined.TaskAlt,
                            fondo = Turquesa,
                            colorContenido = TealOscuro,
                            cargando = estado.guardando,
                            onClick = {
                                viewModel.guardarMeta(digitos.toLongOrNull() ?: 0L) { mensaje ->
                                    error = mensaje
                                    if (mensaje == null) {
                                        editando = false
                                        Toast.makeText(context, "Meta de ahorro guardada", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                        TextButton(onClick = { editando = false }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Cancelar", color = TealMedio)
                        }
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = TealOscuro)
                    Spacer(Modifier.width(8.dp))
                    Text("Sugerencias para ti", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Slate900, modifier = Modifier.weight(1f))
                    if (estado.sugerencias.isNotEmpty()) Text("${estado.sugerencias.size} consejos", fontSize = 12.sp, color = TealMedio, fontWeight = FontWeight.SemiBold)
                }
            }

            when {
                estado.cargandoSugerencias -> item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealOscuro)
                    }
                }
                estado.errorSugerencias != null -> item {
                    Column {
                        AvisoError(estado.errorSugerencias!!)
                        TextButton(onClick = viewModel::cargarSugerencias) {
                            Icon(Icons.Outlined.Refresh, contentDescription = null, tint = TealOscuro)
                            Spacer(Modifier.width(6.dp))
                            Text("Reintentar", color = TealOscuro, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                estado.sugerencias.isEmpty() -> item {
                    AvisoInfo("No hay sugerencias por ahora. ¡Sigue así!")
                }
                else -> items(estado.sugerencias, key = { it.id }) { TarjetaSugerencia(it) }
            }

            item {
                AvisoInfo("Estas recomendaciones son orientativas y no constituyen asesoría financiera.")
            }
        }
    }
}

@Composable
private fun ProgresoCircular(progreso: Float) {
    Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(96.dp)) {
            val grosor = 10.dp.toPx()
            drawArc(Color.White.copy(alpha = 0.15f), 0f, 360f, false, style = Stroke(grosor))
            drawArc(Turquesa, -90f, 360f * progreso, false, style = Stroke(grosor, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(progreso * 100).toInt()} %", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text("logrado", fontSize = 11.sp, color = AguaClaro)
        }
    }
}

@Composable
private fun TarjetaSugerencia(sugerencia: Sugerencia) {
    val texto = (sugerencia.titulo + " " + sugerencia.mensaje).lowercase()
    val (icono: ImageVector, fondo: Color, tinte: Color) = when {
        listOf("super", "exced", "más de", "cuidado", "elevado", "sin ingresos").any { texto.contains(it) } ->
            Triple(Icons.Outlined.WarningAmber, Ambar50, Ambar700)
        listOf("bien", "felicita", "logr", "cumpl").any { texto.contains(it) } ->
            Triple(Icons.AutoMirrored.Outlined.TrendingUp, Esmeralda50, Esmeralda600)
        listOf("ahorr", "meta").any { texto.contains(it) } ->
            Triple(Icons.Outlined.Savings, HieloSuave, TealOscuro)
        else -> Triple(Icons.Outlined.Lightbulb, HieloSuave, TealMedio)
    }
    TarjetaG7(radio = 20.dp) {
        Row(verticalAlignment = Alignment.Top) {
            CajaIcono(icono, fondo = fondo, tinte = tinte)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(sugerencia.titulo, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Slate900, lineHeight = 20.sp)
                Spacer(Modifier.height(4.dp))
                Text(sugerencia.mensaje, fontSize = 13.sp, color = Slate500, lineHeight = 18.sp)
                if (sugerencia.categorias.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        sugerencia.categorias.take(3).forEach { Etiqueta(it, HieloSuave, TealOscuro) }
                    }
                }
            }
        }
    }
}

/** Tarjeta para mover dinero del saldo disponible a la meta de ahorro. */
@Composable
private fun TarjetaAbonar(
    saldoDisponible: Long,
    faltante: Long?,
    abonando: Boolean,
    onAbonar: (Long, (String?) -> Unit) -> Unit
) {
    var digitos by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    TarjetaG7(radio = 24.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CajaIcono(Icons.Outlined.Savings, fondo = Turquesa, tinte = TealOscuro)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Abonar a la meta", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Slate900)
                Text("Se descuenta de tu saldo disponible (${formatoCop(saldoDisponible.coerceAtLeast(0L))} COP)", fontSize = 12.sp, color = Slate500)
            }
        }
        Spacer(Modifier.height(12.dp))
        CampoMonto(digitos, { digitos = it; error = null }, tamanoTexto = 24.sp, error = error != null)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            atajosMeta.forEach { (valor, etiqueta) ->
                BotonAtajo(etiqueta, Modifier.weight(1f)) {
                    digitos = soloDigitos(((digitos.toLongOrNull() ?: 0L) + valor).toString())
                    error = null
                }
            }
            if (faltante != null && faltante > 0L) {
                BotonAtajo("Lo que falta", Modifier.weight(1.3f)) { digitos = faltante.toString(); error = null }
            } else {
                BotonAtajo("Limpiar", Modifier.weight(1f)) { digitos = ""; error = null }
            }
        }
        error?.let {
            Spacer(Modifier.height(10.dp))
            AvisoError(it)
        }
        Spacer(Modifier.height(12.dp))
        BotonPrimario(
            "Abonar",
            icono = Icons.Outlined.Savings,
            fondo = Turquesa,
            colorContenido = TealOscuro,
            cargando = abonando,
            onClick = {
                val monto = digitos.toLongOrNull() ?: 0L
                onAbonar(monto) { mensaje ->
                    error = mensaje
                    if (mensaje == null) digitos = ""
                }
            }
        )
    }
}
