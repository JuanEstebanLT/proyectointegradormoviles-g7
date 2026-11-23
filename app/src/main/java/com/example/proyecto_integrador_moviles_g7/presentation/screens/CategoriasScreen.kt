package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoError
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoInfo
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraContextual
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraProgreso
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.presentation.components.CajaIcono
import com.example.proyecto_integrador_moviles_g7.presentation.components.CampoMonto
import com.example.proyecto_integrador_moviles_g7.presentation.components.EstadoVacio
import com.example.proyecto_integrador_moviles_g7.presentation.components.SelectorSegmentado
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.colorUsoPresupuesto
import com.example.proyecto_integrador_moviles_g7.presentation.components.coloresCampo
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.components.iconoCategoria
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DashboardViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa

private const val LARGO_MAX_NOMBRE = 60

/** Pantalla 07 — Gestión de categorías. */
@Composable
fun CategoriasScreen(
    viewModel: DashboardViewModel,
    onAtras: () -> Unit,
    onCategoria: (Int) -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var pestana by rememberSaveable { mutableStateOf(0) }
    val tipo = if (pestana == 0) TipoMovimiento.GASTO else TipoMovimiento.INGRESO
    var hojaAbierta by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Categoria?>(null) }
    var eliminando by remember { mutableStateOf<Categoria?>(null) }

    val lista = estado.categorias.filter { it.tipo == tipo }.sortedBy { it.nombre.lowercase() }
    val usos = (estado.mesActual.gastosPorCategoria + estado.mesActual.ingresosPorCategoria).associateBy { it.categoria.id }
    val cantidadMovimientos = estado.movimientos.groupingBy { it.categoriaId }.eachCount()

    Scaffold(
        containerColor = FondoClaro,
        topBar = { BarraContextual("Gestión de categorías", onAtras) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { editando = null; hojaAbierta = true },
                containerColor = TealOscuro,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text("Nueva", fontWeight = FontWeight.Bold) }
            )
        }
    ) { relleno ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SelectorSegmentado(listOf("Gastos", "Ingresos"), pestana, { pestana = it })
            }
            if (tipo == TipoMovimiento.GASTO && lista.any { it.presupuesto == 0L }) {
                item {
                    AvisoInfo("Asigna un presupuesto mensual a tus categorías de gasto para recibir alertas cuando te acerques al límite.")
                }
            }
            if (lista.isEmpty()) {
                item {
                    EstadoVacio(
                        Icons.Outlined.Category,
                        "Sin categorías de ${if (tipo == TipoMovimiento.GASTO) "gasto" else "ingreso"}",
                        "Crea una con el botón \"Nueva\"."
                    )
                }
            }
            items(lista, key = { it.id }) { categoria ->
                FilaCategoria(
                    categoria = categoria,
                    gastadoMes = usos[categoria.id]?.total ?: 0L,
                    movimientos = cantidadMovimientos[categoria.id] ?: 0,
                    onClick = { onCategoria(categoria.id) },
                    onEditar = { editando = categoria; hojaAbierta = true },
                    onEliminar = { eliminando = categoria }
                )
            }
        }
    }

    if (hojaAbierta) {
        HojaCategoria(
            categoria = editando,
            tipoInicial = tipo,
            existentes = estado.categorias,
            onCerrar = { hojaAbierta = false; editando = null },
            onGuardar = { nombre, tipoElegido, presupuesto ->
                val actual = editando
                if (actual == null) viewModel.crearCategoria(nombre, tipoElegido, presupuesto)
                else viewModel.actualizarCategoria(actual.id, nombre, presupuesto)
            }
        )
    }

    eliminando?.let { categoria ->
        ConfirmarEliminarCategoria(
            categoria = categoria,
            movimientos = cantidadMovimientos[categoria.id] ?: 0,
            onEliminar = { viewModel.eliminarCategoria(categoria.id) },
            onCerrar = { eliminando = null }
        )
    }
}

@Composable
private fun FilaCategoria(
    categoria: Categoria,
    gastadoMes: Long,
    movimientos: Int,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val esGasto = categoria.tipo == TipoMovimiento.GASTO
    TarjetaG7(radio = 18.dp, relleno = PaddingValues(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CajaIcono(
                iconoCategoria(categoria.nombre, categoria.tipo),
                fondo = if (esGasto) Turquesa.copy(alpha = 0.35f) else Esmeralda50,
                tinte = if (esGasto) TealOscuro else Esmeralda600
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(categoria.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (esGasto) {
                    Text(
                        if (categoria.presupuesto > 0L) "Límite mensual: ${formatoCop(categoria.presupuesto)} COP" else "Sin presupuesto",
                        fontSize = 12.sp, color = TealMedio, fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    "$movimientos ${if (movimientos == 1) "movimiento" else "movimientos"} • ${formatoCop(gastadoMes)} este mes",
                    fontSize = 12.sp, color = Slate500
                )
                if (esGasto && categoria.presupuesto > 0L) {
                    val porcentaje = gastadoMes * 100f / categoria.presupuesto
                    Spacer(Modifier.height(6.dp))
                    BarraProgreso(porcentaje / 100f, colorUsoPresupuesto(porcentaje), alto = 6.dp)
                }
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Outlined.Edit, contentDescription = "Editar ${categoria.nombre}", tint = TealMedio)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = "Eliminar ${categoria.nombre}", tint = Rosa600)
            }
        }
    }
}

/** Confirma la eliminación o explica por qué no se puede (la categoría tiene movimientos). */
@Composable
internal fun ConfirmarEliminarCategoria(
    categoria: Categoria,
    movimientos: Int,
    onEliminar: () -> Boolean,
    onCerrar: () -> Unit
) {
    if (movimientos > 0) {
        DialogoNoSePuedeEliminar(categoria.nombre, movimientos, onCerrar)
    } else {
        DialogoConfirmacion(
            titulo = "¿Eliminar categoría?",
            mensaje = "\"${categoria.nombre}\" se eliminará de forma permanente.",
            confirmar = "Eliminar",
            onConfirmar = {
                onEliminar()
                onCerrar()
            },
            onCancelar = onCerrar
        )
    }
}

/**
 * Pantalla 17 — Hoja para crear o editar una categoría. En edición el tipo queda bloqueado.
 * [onGuardar] devuelve false si el repositorio rechazó los datos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HojaCategoria(
    categoria: Categoria?,
    tipoInicial: TipoMovimiento,
    existentes: List<Categoria>,
    onCerrar: () -> Unit,
    onGuardar: (nombre: String, tipo: TipoMovimiento, presupuesto: Long) -> Boolean
) {
    val estadoHoja = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var nombre by remember { mutableStateOf(categoria?.nombre.orEmpty()) }
    var tipo by remember { mutableStateOf(categoria?.tipo ?: tipoInicial) }
    var presupuesto by remember { mutableStateOf(categoria?.presupuesto?.takeIf { it > 0L }?.toString().orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    val editando = categoria != null

    ModalBottomSheet(onDismissRequest = onCerrar, sheetState = estadoHoja, containerColor = Color.White) {
        Column(
            Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(if (editando) "Editar categoría" else "Nueva categoría", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Slate900)

            SelectorSegmentado(
                listOf("Gasto", "Ingreso"),
                if (tipo == TipoMovimiento.GASTO) 0 else 1,
                { tipo = if (it == 0) TipoMovimiento.GASTO else TipoMovimiento.INGRESO },
                habilitado = !editando
            )
            if (editando) Text("El tipo no se puede cambiar.", fontSize = 12.sp, color = Slate500)

            Row(verticalAlignment = Alignment.CenterVertically) {
                CajaIcono(iconoCategoria(nombre, tipo), fondo = Turquesa.copy(alpha = 0.35f), tinte = TealOscuro, tamano = 52.dp, tamanoIcono = 26.dp, radio = 16.dp)
                Spacer(Modifier.width(12.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it.take(LARGO_MAX_NOMBRE); error = null },
                    modifier = Modifier.weight(1f),
                    label = { Text("Nombre") },
                    singleLine = true,
                    supportingText = { Text("${nombre.length}/$LARGO_MAX_NOMBRE") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo()
                )
            }
            Text("El icono se elige automáticamente según el nombre.", fontSize = 12.sp, color = Slate500)

            if (tipo == TipoMovimiento.GASTO) {
                Text("Presupuesto mensual (opcional)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate500)
                CampoMonto(presupuesto, { presupuesto = it; error = null }, tamanoTexto = 22.sp)
            } else {
                AvisoInfo("Las categorías de ingreso no manejan presupuesto.")
            }

            error?.let { AvisoError(it) }

            BotonPrimario(
                texto = if (editando) "Guardar cambios" else "Crear categoría",
                icono = Icons.Outlined.Check,
                onClick = {
                    val limpio = nombre.trim()
                    val monto = if (tipo == TipoMovimiento.GASTO) presupuesto.toLongOrNull() ?: 0L else 0L
                    error = when {
                        limpio.isEmpty() -> "El nombre es obligatorio."
                        existentes.any { it.id != categoria?.id && it.nombre.equals(limpio, ignoreCase = true) } ->
                            "Ya existe una categoría con ese nombre."
                        !onGuardar(limpio, tipo, monto) -> "No se pudo guardar la categoría. Revisa los datos."
                        else -> null
                    }
                    if (error == null) onCerrar()
                }
            )
        }
    }
}
