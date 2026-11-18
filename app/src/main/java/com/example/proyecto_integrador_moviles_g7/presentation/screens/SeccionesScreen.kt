package com.example.proyecto_integrador_moviles_g7.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DashboardViewModel

/** Destinos de la sesión local; todos observan el mismo repositorio mediante MVVM. */
@Composable
fun SeccionesScreen(
    titulo: String,
    viewModel: DashboardViewModel,
    onDestino: (String) -> Unit,
    onAtras: () -> Unit,
    onCategoria: (Int) -> Unit,
    onSalir: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var tipo by rememberSaveable { mutableStateOf<TipoMovimiento?>(null) }
    var filtro by rememberSaveable { mutableStateOf<TipoMovimiento?>(null) }
    var eliminarId by rememberSaveable { mutableStateOf<String?>(null) }
    var editandoId by rememberSaveable { mutableStateOf<String?>(null) }
    var nuevaCategoria by rememberSaveable { mutableStateOf(false) }
    var editarCategoriaId by rememberSaveable { mutableStateOf<Int?>(null) }
    var eliminarCategoriaId by rememberSaveable { mutableStateOf<Int?>(null) }
    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(onDestino, titulo)
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onAtras) { Text("Volver") }
            Text(titulo, style = MaterialTheme.typography.headlineMedium)
            when (titulo) {
                "Movimientos" -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { tipo = TipoMovimiento.INGRESO }) { Text("Ingreso") }
                        Button(onClick = { tipo = TipoMovimiento.GASTO }) { Text("Gasto") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(null, TipoMovimiento.INGRESO, TipoMovimiento.GASTO).forEach { opcion ->
                            FilterChip(selected = filtro == opcion, onClick = { filtro = opcion },
                                label = { Text(opcion?.name?.lowercase() ?: "Todos") })
                        }
                    }
                    val movimientos = estado.movimientos.filter { filtro == null || it.tipo == filtro }
                    if (movimientos.isEmpty()) Text("No hay movimientos para este filtro.")
                    movimientos.forEach { movimiento ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(movimiento.descripcion, style = MaterialTheme.typography.titleMedium)
                                Text("${formatoCop(movimiento.montoConSigno)} COP · ${movimiento.fecha.toLocalDate()}")
                                TextButton(onClick = { onCategoria(movimiento.categoriaId) }) {
                                    Text(estado.categorias.firstOrNull { it.id == movimiento.categoriaId }?.nombre ?: "Categoría")
                                }
                                Row {
                                    TextButton(onClick = { editandoId = movimiento.id }) { Text("Editar") }
                                    TextButton(onClick = { eliminarId = movimiento.id }) { Text("Eliminar") }
                                }
                            }
                        }
                    }
                }
                "Categorías" -> {
                    Button(onClick = { nuevaCategoria = true }) { Text("Nueva categoría") }
                    listOf(TipoMovimiento.GASTO to "Gastos", TipoMovimiento.INGRESO to "Ingresos").forEach { (tipoLista, nombreLista) ->
                        Text(nombreLista, style = MaterialTheme.typography.titleMedium)
                        val categorias = estado.categorias.filter { it.tipo == tipoLista }
                        if (categorias.isEmpty()) Text("No hay categorías de ${nombreLista.lowercase()}.")
                        categorias.forEach { categoria ->
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(
                                        categoria.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.clickable { onCategoria(categoria.id) }
                                    )
                                    if (tipoLista == TipoMovimiento.GASTO) {
                                        Text(
                                            if (categoria.presupuesto > 0L) "Presupuesto: ${formatoCop(categoria.presupuesto)} COP"
                                            else "Sin presupuesto"
                                        )
                                    }
                                    Row {
                                        TextButton(onClick = { editarCategoriaId = categoria.id }) { Text("Editar") }
                                        TextButton(onClick = { eliminarCategoriaId = categoria.id }) { Text("Eliminar") }
                                    }
                                }
                            }
                        }
                    }
                }
                "Informes" -> {
                    Text("Resumen de todos los movimientos de esta sesión")
                    Text("Saldo inicial: ${formatoCop(estado.saldoInicial)} COP")
                    Text("Ingresos: ${formatoCop(estado.totalIngresos)} COP")
                    Text("Gastos: ${formatoCop(estado.totalGastos)} COP")
                    Text("Disponible: ${formatoCop(estado.saldoDisponible)} COP")
                    estado.gastosPorCategoria.forEach { gasto ->
                        Text("${gasto.categoria.nombre}: ${formatoCop(gasto.total)} COP",
                            Modifier.fillMaxWidth().clickable { onCategoria(gasto.categoria.id) }.padding(vertical = 12.dp))
                    }
                }
                "Ahorro" -> {
                    Text("Orientación educativa")
                    Text("Revisa tus gastos por categoría y define una cantidad de ahorro acorde con tus ingresos y obligaciones.")
                    Text("Consulta tus informes para identificar qué gastos puedes reducir. Estas recomendaciones no constituyen asesoría financiera.")
                    Button(onClick = { onDestino("Informes") }) { Text("Consultar informes") }
                }
                "Perfil" -> {
                    val usuario = viewModel.usuario
                    Text(usuario?.nombre ?: "Sin sesión", style = MaterialTheme.typography.titleLarge)
                    usuario?.let {
                        Text(it.correo)
                        if (it.telefono.isNotBlank()) Text(it.telefono)
                        Text(if (it.esAdmin) "Rol: Administrador" else "Rol: Usuario",
                            style = MaterialTheme.typography.labelLarge)
                    }
                    Text("Tus datos se guardan en la Finanzas G7 API.")
                    OutlinedButton(onClick = { onDestino("Categorías") }) { Text("Gestionar categorías") }
                    Button(onClick = onSalir) { Text("Cerrar sesión") }
                }
                "Notificaciones" -> Text("No hay notificaciones. El servicio de notificaciones aún no está implementado.")
            }
        }
    }
    tipo?.let { seleccionado ->
        DialogoMovimiento(seleccionado, estado.categorias.filter { it.tipo == seleccionado },
            onCerrar = { tipo = null }, onGuardar = { descripcion, monto, categoria ->
                viewModel.agregarMovimiento(descripcion, monto, seleccionado, categoria).also {
                    if (it == null) tipo = null
                }
            })
    }
    eliminarId?.let { id ->
        AlertDialog(onDismissRequest = { eliminarId = null },
            title = { Text("¿Eliminar movimiento?") },
            text = { Text("El saldo y los informes se actualizarán.") },
            confirmButton = { TextButton(onClick = {
                viewModel.eliminarMovimiento(id)?.let { mensaje ->
                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                }
                eliminarId = null
            }) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { eliminarId = null }) { Text("Cancelar") } })
    }
    editandoId?.let { id -> estado.movimientos.firstOrNull { it.id == id } }?.let { movimiento ->
        DialogoMovimiento(
            tipo = movimiento.tipo,
            categorias = estado.categorias.filter { it.tipo == movimiento.tipo },
            inicial = movimiento,
            onCerrar = { editandoId = null },
            onGuardar = { descripcion, monto, categoriaId ->
                viewModel.actualizarMovimiento(movimiento.id, descripcion, monto, categoriaId)
                    .also { if (it == null) editandoId = null }
            }
        )
    }
    if (nuevaCategoria) {
        DialogoCategoria(
            onCerrar = { nuevaCategoria = false },
            onGuardar = { nombre, tipoCategoria, presupuesto ->
                viewModel.crearCategoria(nombre, tipoCategoria, presupuesto).also { if (it) nuevaCategoria = false }
            }
        )
    }
    editarCategoriaId?.let { id -> estado.categorias.firstOrNull { it.id == id } }?.let { categoria ->
        DialogoCategoria(
            inicial = categoria,
            onCerrar = { editarCategoriaId = null },
            onGuardar = { nombre, _, presupuesto ->
                viewModel.actualizarCategoria(categoria.id, nombre, presupuesto).also { if (it) editarCategoriaId = null }
            }
        )
    }
    eliminarCategoriaId?.let { id -> estado.categorias.firstOrNull { it.id == id } }?.let { categoria ->
        DialogoEliminarCategoria(
            categoria = categoria,
            tieneMovimientos = viewModel.categoriaTieneMovimientos(categoria.id),
            onCerrar = { eliminarCategoriaId = null },
            onConfirmar = {
                viewModel.eliminarCategoria(categoria.id)
                eliminarCategoriaId = null
            }
        )
    }
}
