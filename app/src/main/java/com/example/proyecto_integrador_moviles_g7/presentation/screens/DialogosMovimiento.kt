package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoError
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.presentation.components.CampoMonto
import com.example.proyecto_integrador_moviles_g7.presentation.components.CajaIcono
import com.example.proyecto_integrador_moviles_g7.presentation.components.coloresCampo
import com.example.proyecto_integrador_moviles_g7.presentation.components.etiquetaDia
import com.example.proyecto_integrador_moviles_g7.presentation.components.hora
import com.example.proyecto_integrador_moviles_g7.presentation.components.iconoCategoria
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AnalisisFinanciero
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.HieloSuave
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate200
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa

/**
 * Hoja inferior "Editar movimiento" (pantalla 18). La fecha no se edita y la categoría
 * solo puede cambiar a otra del mismo tipo. [onGuardar] y [onEliminar] devuelven null
 * si la operación se aplicó o el mensaje de error.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HojaEditarMovimiento(
    movimiento: Movimiento,
    categorias: List<Categoria>,
    onCerrar: () -> Unit,
    onGuardar: (descripcion: String, monto: Long, categoriaId: Int) -> String?,
    onEliminar: () -> String?
) {
    val estadoHoja = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var descripcion by remember(movimiento.id) { mutableStateOf(movimiento.descripcion) }
    var digitos by remember(movimiento.id) { mutableStateOf(movimiento.monto.toString()) }
    var categoriaId by remember(movimiento.id) { mutableStateOf(movimiento.categoriaId) }
    var error by remember(movimiento.id) { mutableStateOf<String?>(null) }
    var confirmarEliminar by remember { mutableStateOf(false) }
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    val acento = if (esIngreso) Esmeralda600 else Rosa600
    // Un abono solo puede quedarse en "Ahorro"; un gasto normal no puede pasar a "Ahorro"
    val esAbono = categorias.any { it.id == movimiento.categoriaId && AnalisisFinanciero.esCategoriaAhorro(it) }
    val delMismoTipo = categorias.filter {
        it.tipo == movimiento.tipo && AnalisisFinanciero.esCategoriaAhorro(it) == esAbono
    }

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
            Text(
                if (esIngreso) "Editar ingreso" else "Editar gasto",
                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Slate900
            )
            CampoMonto(digitos, { digitos = it; error = null }, acento = acento)
            CampoDescripcion(descripcion, { descripcion = it; error = null }, if (esIngreso) "Concepto" else "Concepto o establecimiento")
            Text("Categoría", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate500)
            SelectorCategorias(delMismoTipo, categoriaId) { categoriaId = it; error = null }

            Surface(color = Slate50, shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Slate200)) {
                Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Slate500, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${etiquetaDia(movimiento.fecha.toLocalDate())} • ${hora(movimiento.fecha)}",
                        fontSize = 13.sp, color = Slate500, modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Outlined.Lock, contentDescription = "La fecha no se puede editar", tint = Slate500, modifier = Modifier.size(16.dp))
                }
            }

            error?.let { AvisoError(it) }

            BotonPrimario(
                texto = "Guardar cambios",
                icono = Icons.Outlined.Check,
                onClick = {
                    error = onGuardar(descripcion, digitos.toLongOrNull() ?: 0L, categoriaId)
                    if (error == null) onCerrar()
                }
            )
            OutlinedButton(
                onClick = { confirmarEliminar = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Rosa600.copy(alpha = 0.4f))
            ) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = Rosa600, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Eliminar movimiento", color = Rosa600, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (confirmarEliminar) {
        DialogoConfirmacion(
            titulo = "¿Eliminar movimiento?",
            mensaje = "\"${movimiento.descripcion}\" se eliminará. Tu saldo y tus informes se actualizarán.",
            confirmar = "Eliminar",
            onConfirmar = {
                confirmarEliminar = false
                error = onEliminar()
                if (error == null) onCerrar()
            },
            onCancelar = { confirmarEliminar = false }
        )
    }
}

@Composable
internal fun CampoDescripcion(valor: String, onCambio: (String) -> Unit, etiqueta: String) {
    OutlinedTextField(
        value = valor,
        onValueChange = { onCambio(it.take(120)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        shape = RoundedCornerShape(16.dp),
        colors = coloresCampo()
    )
}

/** Chips de categorías con icono, en filas que se desplazan horizontalmente. */
@Composable
internal fun SelectorCategorias(categorias: List<Categoria>, seleccionada: Int?, onSeleccion: (Int) -> Unit) {
    if (categorias.isEmpty()) {
        Text("No tienes categorías de este tipo. Créalas en Gestionar categorías.", fontSize = 13.sp, color = Slate500)
        return
    }
    // Dos filas para que quepan más categorías sin ocupar toda la pantalla
    val filas = if (categorias.size > 3) categorias.chunked((categorias.size + 1) / 2) else listOf(categorias)
    Column(
        Modifier.horizontalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filas.forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { categoria ->
                    val activa = categoria.id == seleccionada
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSeleccion(categoria.id) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (activa) Turquesa.copy(alpha = 0.4f) else Slate50,
                        border = BorderStroke(if (activa) 1.5.dp else 1.dp, if (activa) TealOscuro else Slate200)
                    ) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(iconoCategoria(categoria.nombre, categoria.tipo), contentDescription = null, tint = TealOscuro, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(categoria.nombre, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoConfirmacion(
    titulo: String,
    mensaje: String,
    confirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    peligroso: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = Color.White,
        icon = {
            CajaIcono(
                Icons.Outlined.DeleteOutline,
                fondo = if (peligroso) Rosa50 else HieloSuave,
                tinte = if (peligroso) Rosa600 else TealOscuro
            )
        },
        title = { Text(titulo, fontWeight = FontWeight.ExtraBold, color = Slate900) },
        text = { Text(mensaje, color = Slate500, lineHeight = 20.sp) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text(confirmar, color = if (peligroso) Rosa600 else TealOscuro, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar", color = TealMedio, fontWeight = FontWeight.SemiBold) }
        }
    )
}

/** Aviso cuando una categoría no se puede eliminar por tener movimientos. */
@Composable
fun DialogoNoSePuedeEliminar(nombre: String, cantidadMovimientos: Int, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Color.White,
        icon = { CajaIcono(Icons.Outlined.Lock, fondo = Rosa50, tinte = Rosa600) },
        title = { Text("No se puede eliminar la categoría", fontWeight = FontWeight.ExtraBold, color = Slate900) },
        text = {
            Text(
                "\"$nombre\" tiene $cantidadMovimientos ${if (cantidadMovimientos == 1) "movimiento asociado" else "movimientos asociados"}. " +
                    "Muévelos a otra categoría o elimínalos primero.",
                color = Slate500, lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Entendido", color = TealOscuro, fontWeight = FontWeight.Bold) }
        }
    )
}
