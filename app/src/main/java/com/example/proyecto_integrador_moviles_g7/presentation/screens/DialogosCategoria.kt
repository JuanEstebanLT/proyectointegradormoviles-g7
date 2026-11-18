package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500

/**
 * Formulario de categoría. Sin [inicial] crea una nueva (se elige el tipo);
 * con [inicial] la edita. El tipo no se puede cambiar porque rompería sus movimientos.
 */
@Composable
internal fun DialogoCategoria(
    onCerrar: () -> Unit,
    inicial: Categoria? = null,
    tipoInicial: TipoMovimiento = TipoMovimiento.GASTO,
    onGuardar: (nombre: String, tipo: TipoMovimiento, presupuesto: Long) -> Boolean
) {
    val clave = inicial?.id
    var nombre by rememberSaveable(clave) { mutableStateOf(inicial?.nombre ?: "") }
    var tipo by rememberSaveable(clave) { mutableStateOf(inicial?.tipo ?: tipoInicial) }
    var presupuestoTexto by rememberSaveable(clave) {
        mutableStateOf(inicial?.presupuesto?.takeIf { it > 0L }?.toString() ?: "")
    }
    var mostrarError by rememberSaveable(clave) { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (inicial != null) "Editar categoría" else "Nueva categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it.take(60); mostrarError = false },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                if (inicial == null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TipoMovimiento.entries.forEach { opcion ->
                            FilterChip(
                                selected = tipo == opcion,
                                onClick = { tipo = opcion; mostrarError = false },
                                label = { Text(if (opcion == TipoMovimiento.GASTO) "Gasto" else "Ingreso") }
                            )
                        }
                    }
                } else {
                    Text(
                        "Tipo: ${if (tipo == TipoMovimiento.GASTO) "Gasto" else "Ingreso"} (no se puede cambiar)",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                }
                if (tipo == TipoMovimiento.GASTO) {
                    OutlinedTextField(
                        value = presupuestoTexto,
                        onValueChange = { valor ->
                            presupuestoTexto = valor.filter(Char::isDigit).take(11)
                            mostrarError = false
                        },
                        label = { Text("Presupuesto mensual en COP (opcional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                if (mostrarError) {
                    Text(
                        "Escribe un nombre que no uses en otra categoría.",
                        color = Rosa700,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Las categorías de ingreso no manejan presupuesto
                val presupuesto = if (tipo == TipoMovimiento.GASTO) presupuestoTexto.toLongOrNull() ?: 0L else 0L
                mostrarError = !onGuardar(nombre, tipo, presupuesto)
            }) {
                Text(if (inicial != null) "Guardar cambios" else "Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}

/** Confirma la eliminación, o explica por qué no se puede si la categoría tiene movimientos. */
@Composable
internal fun DialogoEliminarCategoria(
    categoria: Categoria,
    tieneMovimientos: Boolean,
    onCerrar: () -> Unit,
    onConfirmar: () -> Unit
) {
    if (tieneMovimientos) {
        AlertDialog(
            onDismissRequest = onCerrar,
            title = { Text("No se puede eliminar") },
            text = {
                Text("\"${categoria.nombre}\" tiene movimientos registrados. Elimínalos o cámbialos de categoría primero.")
            },
            confirmButton = { TextButton(onClick = onCerrar) { Text("Entendido") } }
        )
    } else {
        AlertDialog(
            onDismissRequest = onCerrar,
            title = { Text("¿Eliminar categoría?") },
            text = { Text("Se eliminará \"${categoria.nombre}\". Esta acción no se puede deshacer.") },
            confirmButton = { TextButton(onClick = onConfirmar) { Text("Eliminar", color = Rosa700) } },
            dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
        )
    }
}
