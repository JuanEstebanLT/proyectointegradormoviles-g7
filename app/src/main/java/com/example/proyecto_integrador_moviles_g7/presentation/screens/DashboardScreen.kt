package com.example.proyecto_integrador_moviles_g7.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraNavegacionG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraProgreso
import com.example.proyecto_integrador_moviles_g7.presentation.components.CajaIcono
import com.example.proyecto_integrador_moviles_g7.presentation.components.EncabezadoPrincipal
import com.example.proyecto_integrador_moviles_g7.presentation.components.Etiqueta
import com.example.proyecto_integrador_moviles_g7.presentation.components.EstadoVacio
import com.example.proyecto_integrador_moviles_g7.presentation.components.FilaMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.SeccionPrincipal
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.TituloSeccion
import com.example.proyecto_integrador_moviles_g7.presentation.components.colorUsoPresupuesto
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.components.iconoCategoria
import com.example.proyecto_integrador_moviles_g7.presentation.components.nombreMes
import com.example.proyecto_integrador_moviles_g7.presentation.components.etiquetaDia
import com.example.proyecto_integrador_moviles_g7.presentation.components.hora
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DashboardViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar800
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.HieloSuave
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa

/** Colores para distinguir categorías en las barras y gráficos. */
internal val paletaCategorias = listOf(
    Color(0xFF164D5A), Color(0xFF248397), Color(0xFF52E7D2), Color(0xFFE11D48),
    Color(0xFFF59E0B), Color(0xFF059669), Color(0xFF7C3AED), Color(0xFF94A3B8)
)

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    cargando: Boolean,
    sinConexion: Boolean,
    ultimaSincronizacion: Long?,
    onReintentar: () -> Unit,
    onSeccion: (SeccionPrincipal) -> Unit,
    onPerfil: () -> Unit,
    onNotificaciones: () -> Unit,
    onCategoriaClick: (Int) -> Unit,
    onCategorias: () -> Unit,
    onRegistrar: (TipoMovimiento) -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val usuario = viewModel.usuario
    val context = LocalContext.current
    var editandoId by remember { mutableStateOf<String?>(null) }
    val nombreCategoria = { id: Int -> estado.categorias.firstOrNull { it.id == id }?.nombre ?: "Sin categoría" }
    val mes = estado.mesActual

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            EncabezadoPrincipal(
                titulo = usuario?.nombre ?: "Finanzas G7",
                subtitulo = "Bienvenido de nuevo,",
                iniciales = usuario?.iniciales ?: "G7",
                notificacionesSinLeer = estado.notificacionesSinLeer,
                onNotificaciones = onNotificaciones,
                onPerfil = onPerfil
            )
        },
        bottomBar = { BarraNavegacionG7(SeccionPrincipal.INICIO, onSeccion) }
    ) { relleno ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (sinConexion) {
                item { AvisoSinConexion(ultimaSincronizacion, cargando, onReintentar) }
            }

            item { TarjetaSaldo(estado.saldoDisponible, mes.ingresos, mes.gastos, nombreMes(mes.mes), cargando) }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BotonAccion("Nuevo ingreso", Icons.Outlined.AddCircleOutline, Esmeralda50, Esmeralda600, Modifier.weight(1f)) {
                        onRegistrar(TipoMovimiento.INGRESO)
                    }
                    BotonAccion("Nuevo gasto", Icons.Outlined.RemoveCircleOutline, Rosa50, Rosa600, Modifier.weight(1f)) {
                        onRegistrar(TipoMovimiento.GASTO)
                    }
                }
            }

            item {
                TarjetaG7 {
                    TituloSeccion("Gastos por categoría", "Gestionar ↗", onCategorias)
                    Text("${nombreMes(mes.mes)} • ${formatoCop(mes.gastos)} COP", fontSize = 12.sp, color = Slate500)
                    Spacer(Modifier.height(12.dp))
                    val conGasto = mes.gastosPorCategoria.filter { it.total > 0L }
                    if (conGasto.isEmpty()) {
                        Text("Aún no registras gastos este mes.", fontSize = 13.sp, color = Slate500)
                    } else {
                        // Barra apilada con la participación de cada categoría
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(50))
                        ) {
                            conGasto.forEachIndexed { i, uso ->
                                Box(
                                    Modifier
                                        .weight(uso.participacion.coerceAtLeast(1f))
                                        .fillMaxHeight()
                                        .background(paletaCategorias[i % paletaCategorias.size])
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        conGasto.take(4).forEachIndexed { i, uso ->
                            FilaCategoriaResumen(
                                nombre = uso.categoria.nombre,
                                total = uso.total,
                                presupuesto = uso.presupuesto,
                                color = paletaCategorias[i % paletaCategorias.size],
                                onClick = { onCategoriaClick(uso.categoria.id) }
                            )
                        }
                    }
                }
            }

            item {
                TarjetaMetaAhorro(meta = estado.metaAhorro, ahorrado = mes.ahorro) { onSeccion(SeccionPrincipal.AHORRO) }
            }

            item { TituloSeccion("Movimientos recientes", "Ver todos") { onSeccion(SeccionPrincipal.MOVIMIENTOS) } }

            if (estado.movimientosRecientes.isEmpty()) {
                item {
                    if (cargando) {
                        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = TealOscuro)
                        }
                    } else {
                        EstadoVacio(
                            Icons.Outlined.ReceiptLong,
                            "Sin movimientos todavía",
                            "Registra tu primer ingreso o gasto con los botones de arriba."
                        )
                    }
                }
            }
            items(estado.movimientosRecientes, key = { it.id }) { movimiento ->
                FilaMovimiento(movimiento, nombreCategoria(movimiento.categoriaId)) { editandoId = movimiento.id }
            }
        }
    }

    estado.movimientos.firstOrNull { it.id == editandoId }?.let { movimiento ->
        HojaEditarMovimiento(
            movimiento = movimiento,
            categorias = estado.categorias,
            onCerrar = { editandoId = null },
            onGuardar = { d, m, c -> viewModel.actualizarMovimiento(movimiento.id, d, m, c) },
            onEliminar = {
                viewModel.eliminarMovimiento(movimiento.id).also { msg ->
                    if (msg != null) Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}

@Composable
private fun TarjetaSaldo(disponible: Long, ingresosMes: Long, gastosMes: Long, mes: String, cargando: Boolean) {
    Surface(shape = RoundedCornerShape(26.dp), color = TealOscuro, shadowElevation = 4.dp) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("SALDO DISPONIBLE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Turquesa)
                if (cargando) CircularProgressIndicator(color = Turquesa, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                else Etiqueta(mes, fondo = Color.White.copy(alpha = 0.15f), color = Color.White)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                (if (disponible < 0) "- " else "") + formatoCop(disponible) + " COP",
                fontSize = 30.sp, fontWeight = FontWeight.Black, color = Color.White
            )
            if (disponible < 0) {
                Text("Tu saldo está en negativo: registra un ingreso o reduce un gasto.", fontSize = 12.sp, color = Color(0xFFFECDD3))
            }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniTotal("Ingresos del mes", "+ ${formatoCop(ingresosMes)}", Icons.Outlined.ArrowDownward, Esmeralda50, Esmeralda600, Modifier.weight(1f))
                MiniTotal("Gastos del mes", "- ${formatoCop(gastosMes)}", Icons.Outlined.ArrowUpward, Rosa50, Rosa600, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MiniTotal(titulo: String, valor: String, icono: ImageVector, fondo: Color, tinte: Color, modifier: Modifier) {
    Surface(modifier = modifier, color = Color.White.copy(alpha = 0.10f), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(30.dp).clip(CircleShape).background(fondo), contentAlignment = Alignment.Center) {
                Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(titulo, fontSize = 11.sp, color = AguaClaro, maxLines = 1)
                Text(valor, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun BotonAccion(texto: String, icono: ImageVector, fondo: Color, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = fondo, contentColor = color),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(texto, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
internal fun FilaCategoriaResumen(nombre: String, total: Long, presupuesto: Long, color: Color, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CajaIcono(iconoCategoria(nombre), fondo = color.copy(alpha = 0.12f), tinte = color, tamano = 36.dp, tamanoIcono = 18.dp, radio = 10.dp)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(formatoCop(total), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Slate900)
            }
            if (presupuesto > 0L) {
                val porcentaje = total * 100f / presupuesto
                Spacer(Modifier.height(4.dp))
                BarraProgreso(porcentaje / 100f, colorUsoPresupuesto(porcentaje), alto = 6.dp)
                Text("${porcentaje.toInt()} % de ${formatoCop(presupuesto)}", fontSize = 11.sp, color = Slate500)
            } else {
                Text("Sin presupuesto", fontSize = 11.sp, color = Slate500)
            }
        }
    }
}

@Composable
private fun TarjetaMetaAhorro(meta: Long, ahorrado: Long, onClick: () -> Unit) {
    TarjetaG7(fondo = HieloSuave, borde = Turquesa.copy(alpha = 0.4f), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CajaIcono(Icons.Outlined.Savings, fondo = Turquesa, tinte = TealOscuro)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Meta de ahorro", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Slate900)
                if (meta > 0L) {
                    Text("${formatoCop(ahorrado)} de ${formatoCop(meta)} COP este mes", fontSize = 12.sp, color = Slate500)
                } else {
                    Text("Define cuánto quieres ahorrar cada mes →", fontSize = 12.sp, color = TealOscuro, fontWeight = FontWeight.SemiBold)
                }
            }
            if (meta > 0L) {
                Text("${(ahorrado * 100 / meta).coerceAtMost(100)} %", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TealOscuro)
            }
        }
        if (meta > 0L) {
            Spacer(Modifier.height(10.dp))
            BarraProgreso(ahorrado.toFloat() / meta, TealOscuro, fondo = Color.White)
        }
    }
}

/** Se muestra cuando la API no responde: los datos vienen de la copia guardada en el teléfono. */
@Composable
private fun AvisoSinConexion(ultimaSincronizacion: Long?, cargando: Boolean, onReintentar: () -> Unit) {
    val cuando = ultimaSincronizacion?.let {
        java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
    }
    TarjetaG7(fondo = Ambar50, borde = Ambar500.copy(alpha = 0.4f), relleno = PaddingValues(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.CloudOff, contentDescription = null, tint = Ambar800, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Sin conexión", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Ambar800)
                Text(
                    if (cuando != null) "Datos guardados del ${etiquetaDia(cuando.toLocalDate()).lowercase()} a las ${hora(cuando)}. Puedes consultarlos, pero los cambios necesitan internet."
                    else "Los cambios necesitan internet.",
                    fontSize = 12.sp, color = Ambar800, lineHeight = 16.sp
                )
            }
            if (cargando) {
                CircularProgressIndicator(color = Ambar800, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                androidx.compose.material3.TextButton(onClick = onReintentar) {
                    Text("Reintentar", color = TealOscuro, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
