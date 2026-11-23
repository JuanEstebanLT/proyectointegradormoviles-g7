package com.example.proyecto_integrador_moviles_g7.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraContextual
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraProgreso
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.presentation.components.CajaIcono
import com.example.proyecto_integrador_moviles_g7.presentation.components.CargandoPantalla
import com.example.proyecto_integrador_moviles_g7.presentation.components.EstadoVacio
import com.example.proyecto_integrador_moviles_g7.presentation.components.FilaMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.TituloSeccion
import com.example.proyecto_integrador_moviles_g7.presentation.components.colorUsoPresupuesto
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.components.iconoCategoria
import com.example.proyecto_integrador_moviles_g7.presentation.components.nombreMes
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DetalleCategoriaViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa
import java.time.YearMonth

/** Pantalla 12 — Detalle de categoría. */
@Composable
fun DetalleCategoriaScreen(
    viewModel: DetalleCategoriaViewModel,
    categorias: List<Categoria>,
    onAtras: () -> Unit,
    onRegistrar: (TipoMovimiento, Int) -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var soloEsteMes by rememberSaveable { mutableStateOf(true) }
    var editandoId by remember { mutableStateOf<String?>(null) }
    var editarCategoria by remember { mutableStateOf(false) }
    var eliminarCategoria by remember { mutableStateOf(false) }

    val categoria = estado.categoria
    if (categoria == null) {
        Scaffold(containerColor = FondoClaro, topBar = { BarraContextual("Categoría", onAtras) }) { relleno ->
            if (categorias.isEmpty()) CargandoPantalla(Modifier.padding(relleno))
            else EstadoVacio(Icons.Outlined.ReceiptLong, "Categoría no encontrada", "Es posible que se haya eliminado.", Modifier.padding(relleno))
        }
        return
    }

    val esGasto = categoria.tipo == TipoMovimiento.GASTO
    val mesActual = YearMonth.now()
    val delMes = estado.movimientos.filter { YearMonth.from(it.fecha) == mesActual }
    val totalMes = delMes.sumOf { it.monto }
    val visibles = if (soloEsteMes) delMes else estado.movimientos
    val totalVisibles = visibles.sumOf { it.monto }
    val promedio = if (visibles.isEmpty()) 0L else totalVisibles / visibles.size
    // Participación sobre el total del mismo tipo en el período visible
    val totalDelTipo = viewModel.totalDelTipo(categoria.tipo, if (soloEsteMes) mesActual else null)
    val participacion = if (totalDelTipo > 0L) totalVisibles * 100f / totalDelTipo else 0f

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            BarraContextual(categoria.nombre, onAtras) {
                IconButton(onClick = { editarCategoria = true }) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Editar categoría", tint = TealMedio)
                }
                IconButton(onClick = { eliminarCategoria = true }) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Eliminar categoría", tint = Rosa600)
                }
            }
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                BotonPrimario(
                    texto = "Registrar ${if (esGasto) "gasto" else "ingreso"} en ${categoria.nombre}",
                    icono = Icons.Outlined.Add,
                    fondo = if (esGasto) TealOscuro else Esmeralda600,
                    onClick = { onRegistrar(categoria.tipo, categoria.id) },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    ) { relleno ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(color = TealOscuro, shape = RoundedCornerShape(24.dp), shadowElevation = 4.dp) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CajaIcono(iconoCategoria(categoria.nombre, categoria.tipo), fondo = Turquesa, tinte = TealOscuro)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    if (esGasto) "CONSUMO DE ${nombreMes(mesActual).uppercase()}" else "INGRESOS DE ${nombreMes(mesActual).uppercase()}",
                                    fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Turquesa
                                )
                                Text(formatoCop(totalMes) + " COP", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                        if (esGasto && categoria.presupuesto > 0L) {
                            val porcentaje = totalMes * 100f / categoria.presupuesto
                            Spacer(Modifier.height(16.dp))
                            BarraProgreso(porcentaje / 100f, if (porcentaje >= 80f) colorUsoPresupuesto(porcentaje) else Turquesa, alto = 10.dp, fondo = Color.White.copy(alpha = 0.15f))
                            Spacer(Modifier.height(8.dp))
                            Row {
                                Text("${porcentaje.toInt()} % de ${formatoCop(categoria.presupuesto)}", fontSize = 12.sp, color = AguaClaro, modifier = Modifier.weight(1f))
                                val restante = categoria.presupuesto - totalMes
                                Text(
                                    if (restante >= 0) "Quedan ${formatoCop(restante)}" else "Excedido ${formatoCop(-restante)}",
                                    fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                    color = if (restante >= 0) Color.White else Color(0xFFFDA4AF)
                                )
                            }
                        } else if (esGasto) {
                            Spacer(Modifier.height(10.dp))
                            Text("Sin presupuesto mensual. Asígnalo con el lápiz de arriba.", fontSize = 12.sp, color = AguaClaro)
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MicroMetrica("Promedio", formatoCop(promedio), Modifier.weight(1f))
                    MicroMetrica("Movimientos", "${visibles.size}", Modifier.weight(1f))
                    MicroMetrica("% del total", "${participacion.toInt()} %", Modifier.weight(1f))
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { TituloSeccion("Historial") }
                    ChipFiltro("Este mes", soloEsteMes) { soloEsteMes = true }
                    Spacer(Modifier.width(6.dp))
                    ChipFiltro("Todo", !soloEsteMes) { soloEsteMes = false }
                }
            }
            if (visibles.isEmpty()) {
                item {
                    EstadoVacio(
                        Icons.Outlined.ReceiptLong,
                        "Sin movimientos",
                        if (soloEsteMes) "No hay movimientos en esta categoría este mes." else "Esta categoría todavía no tiene movimientos."
                    )
                }
            }
            items(visibles, key = { it.id }) { movimiento ->
                FilaMovimiento(movimiento, categoria.nombre) { editandoId = movimiento.id }
            }
        }
    }

    estado.movimientos.firstOrNull { it.id == editandoId }?.let { movimiento ->
        HojaEditarMovimiento(
            movimiento = movimiento,
            categorias = categorias,
            onCerrar = { editandoId = null },
            onGuardar = { d, m, c -> viewModel.actualizarMovimiento(movimiento.id, d, m, c) },
            onEliminar = {
                viewModel.eliminarMovimiento(movimiento.id).also { msg ->
                    if (msg != null) Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    if (editarCategoria) {
        HojaCategoria(
            categoria = categoria,
            tipoInicial = categoria.tipo,
            existentes = categorias,
            onCerrar = { editarCategoria = false },
            onGuardar = { nombre, _, presupuesto -> viewModel.actualizarCategoria(nombre, presupuesto) }
        )
    }

    if (eliminarCategoria) {
        ConfirmarEliminarCategoria(
            categoria = categoria,
            movimientos = estado.movimientos.size,
            onEliminar = {
                viewModel.eliminarCategoria().also { if (it) onAtras() }
            },
            onCerrar = { eliminarCategoria = false }
        )
    }
}

@Composable
private fun MicroMetrica(titulo: String, valor: String, modifier: Modifier) {
    TarjetaG7(modifier = modifier, radio = 16.dp, relleno = PaddingValues(12.dp)) {
        Text(titulo, fontSize = 11.sp, color = Slate500, maxLines = 1)
        Spacer(Modifier.height(4.dp))
        Text(valor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Slate900, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
