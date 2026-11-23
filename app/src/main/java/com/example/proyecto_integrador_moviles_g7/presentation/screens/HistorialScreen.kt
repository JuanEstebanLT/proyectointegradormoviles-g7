package com.example.proyecto_integrador_moviles_g7.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraNavegacionG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.EncabezadoPrincipal
import com.example.proyecto_integrador_moviles_g7.presentation.components.EstadoVacio
import com.example.proyecto_integrador_moviles_g7.presentation.components.FilaMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.SeccionPrincipal
import com.example.proyecto_integrador_moviles_g7.presentation.components.coloresCampo
import com.example.proyecto_integrador_moviles_g7.presentation.components.etiquetaDia
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DashboardViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate200
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa
import java.time.YearMonth

/** Pantalla 08 — Historial de movimientos. */
@Composable
fun HistorialScreen(
    viewModel: DashboardViewModel,
    onSeccion: (SeccionPrincipal) -> Unit,
    onPerfil: () -> Unit,
    onNotificaciones: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val usuario = viewModel.usuario
    val context = LocalContext.current
    var busqueda by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf<TipoMovimiento?>(null) }
    var soloEsteMes by rememberSaveable { mutableStateOf(false) }
    var editandoId by remember { mutableStateOf<String?>(null) }

    val nombres = estado.categorias.associate { it.id to it.nombre }
    val texto = busqueda.trim()
    val filtrados = estado.movimientos.filter { m ->
        (filtro == null || m.tipo == filtro) &&
            (!soloEsteMes || YearMonth.from(m.fecha) == YearMonth.now()) &&
            (texto.isEmpty() || m.descripcion.contains(texto, ignoreCase = true) ||
                nombres[m.categoriaId].orEmpty().contains(texto, ignoreCase = true))
    }
    val ingresos = filtrados.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto }
    val gastos = filtrados.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }
    val porDia = filtrados.groupBy { it.fecha.toLocalDate() }.toSortedMap(compareByDescending { it })

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            EncabezadoPrincipal(
                titulo = "Movimientos",
                subtitulo = "Historial",
                iniciales = usuario?.iniciales ?: "G7",
                notificacionesSinLeer = estado.notificacionesSinLeer,
                onNotificaciones = onNotificaciones,
                onPerfil = onPerfil
            )
        },
        bottomBar = { BarraNavegacionG7(SeccionPrincipal.MOVIMIENTOS, onSeccion) }
    ) { relleno ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar concepto o categoría…", color = Slate500) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Slate500) },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { busqueda = "" }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Borrar búsqueda", tint = Slate500)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo()
                )
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipFiltro("Todos", filtro == null) { filtro = null }
                    ChipFiltro("Ingresos", filtro == TipoMovimiento.INGRESO) { filtro = TipoMovimiento.INGRESO }
                    ChipFiltro("Gastos", filtro == TipoMovimiento.GASTO) { filtro = TipoMovimiento.GASTO }
                    ChipFiltro("Este mes", soloEsteMes) { soloEsteMes = !soloEsteMes }
                }
            }
            item {
                Surface(color = TealOscuro, shape = RoundedCornerShape(20.dp)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("FLUJO NETO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Turquesa)
                            val neto = ingresos - gastos
                            Text(
                                (if (neto >= 0) "+ " else "- ") + formatoCop(neto) + " COP",
                                fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White
                            )
                            Text("${filtrados.size} movimientos", fontSize = 12.sp, color = AguaClaro)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("+ ${formatoCop(ingresos)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7))
                            Text("- ${formatoCop(gastos)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDA4AF))
                        }
                    }
                }
            }
            if (filtrados.isEmpty()) {
                item {
                    EstadoVacio(
                        Icons.Outlined.ReceiptLong,
                        if (estado.movimientos.isEmpty()) "Aún no tienes movimientos" else "Sin resultados",
                        if (estado.movimientos.isEmpty()) "Registra ingresos y gastos desde Inicio."
                        else "Prueba con otra búsqueda o cambia los filtros."
                    )
                }
            }
            porDia.forEach { (dia, movimientos) ->
                item(key = "dia-$dia") {
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(etiquetaDia(dia), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Slate900, modifier = Modifier.weight(1f))
                        val netoDia = movimientos.sumOf { it.montoConSigno }
                        Text(
                            (if (netoDia >= 0) "+ " else "- ") + formatoCop(netoDia),
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate500
                        )
                    }
                }
                items(movimientos.sortedByDescending { it.fecha }, key = { it.id }) { movimiento ->
                    FilaMovimiento(movimiento, nombres[movimiento.categoriaId] ?: "Sin categoría", mostrarFecha = false) {
                        editandoId = movimiento.id
                    }
                }
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
internal fun ChipFiltro(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(texto, fontWeight = FontWeight.SemiBold) },
        leadingIcon = if (seleccionado) {
            { Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
        } else null,
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            selectedContainerColor = TealOscuro,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White,
            labelColor = Slate900
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true, selected = seleccionado,
            borderColor = Slate200, selectedBorderColor = TealOscuro
        )
    )
}
