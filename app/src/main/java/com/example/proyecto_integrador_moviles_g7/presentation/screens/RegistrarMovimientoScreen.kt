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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoAdvertencia
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoError
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoInfo
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraContextual
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.presentation.components.CampoMonto
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AnalisisFinanciero
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DashboardViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900

/** Pantallas 05 (Registrar ingreso) y 06 (Registrar gasto). */
@Composable
fun RegistrarMovimientoScreen(
    tipo: TipoMovimiento,
    viewModel: DashboardViewModel,
    categoriaInicial: Int?,
    onAtras: () -> Unit,
    onGuardado: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val esIngreso = tipo == TipoMovimiento.INGRESO
    val acento = if (esIngreso) Esmeralda600 else Rosa600
    // Los abonos a la meta se hacen desde Ahorro, no como un gasto normal
    val categorias = estado.categorias.filter { it.tipo == tipo && !AnalisisFinanciero.esCategoriaAhorro(it) }

    var digitos by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var categoriaId by rememberSaveable {
        mutableStateOf(categoriaInicial?.takeIf { id -> categorias.any { it.id == id } } ?: categorias.firstOrNull()?.id)
    }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    // Si las categorías llegan después (carga desde la API), se elige la primera
    androidx.compose.runtime.LaunchedEffect(categorias) {
        if (categorias.none { it.id == categoriaId }) categoriaId = categorias.firstOrNull()?.id
    }
    val monto = digitos.toLongOrNull() ?: 0L
    val saldoProyectado = estado.saldoDisponible + if (esIngreso) monto else -monto

    Scaffold(
        containerColor = FondoClaro,
        topBar = { BarraContextual(if (esIngreso) "Registrar ingreso" else "Registrar gasto", onAtras) }
    ) { relleno ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(relleno)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaG7(
                fondo = if (esIngreso) Esmeralda50 else Rosa50,
                borde = acento.copy(alpha = 0.25f),
                radio = 24.dp,
                relleno = PaddingValues(18.dp)
            ) {
                Text(
                    if (esIngreso) "MONTO A INGRESAR" else "MONTO DEL GASTO",
                    fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = acento
                )
                Spacer(Modifier.height(10.dp))
                CampoMonto(digitos, { digitos = it; error = null }, acento = acento, tamanoTexto = 28.sp)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Saldo después: ", fontSize = 13.sp, color = Slate500)
                    Text(
                        (if (saldoProyectado < 0) "- " else "") + formatoCop(saldoProyectado) + " COP",
                        fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                        color = if (saldoProyectado < 0) Rosa600 else Slate900
                    )
                }
            }

            TarjetaG7(radio = 24.dp) {
                CampoDescripcion(descripcion, { descripcion = it; error = null }, if (esIngreso) "Concepto (ej. Pago nómina)" else "Concepto o establecimiento (ej. Mercado Éxito)")
                Spacer(Modifier.height(16.dp))
                Text(if (esIngreso) "Categoría de ingreso" else "Categoría del gasto", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate500)
                Spacer(Modifier.height(8.dp))
                SelectorCategorias(categorias, categoriaId) { categoriaId = it; error = null }
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Fecha: hoy", fontSize = 13.sp, color = Slate500)
                }
            }

            // Alerta de presupuesto de la categoría elegida (solo gastos)
            if (!esIngreso) {
                estado.mesActual.gastosPorCategoria.firstOrNull { it.categoria.id == categoriaId && it.presupuesto > 0L }?.let { uso ->
                    val restante = uso.presupuesto - uso.total
                    when {
                        monto > restante && restante >= 0 -> AvisoAdvertencia(
                            "Con este gasto superarás el presupuesto de ${uso.categoria.nombre}: te quedan ${formatoCop(restante)} de ${formatoCop(uso.presupuesto)} COP."
                        )
                        restante < 0 -> AvisoAdvertencia(
                            "Ya superaste el presupuesto de ${uso.categoria.nombre} este mes por ${formatoCop(-restante)} COP."
                        )
                        else -> AvisoInfo("Te quedan ${formatoCop(restante - monto)} COP de presupuesto en ${uso.categoria.nombre} este mes.")
                    }
                }
            }

            error?.let { AvisoError(it) }

            BotonPrimario(
                texto = if (esIngreso) "Guardar ingreso" else "Registrar gasto",
                icono = Icons.Outlined.Check,
                fondo = if (esIngreso) Esmeralda600 else com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro,
                onClick = {
                    val id = categoriaId
                    error = when {
                        monto <= 0L -> "Ingresa un monto mayor a cero."
                        descripcion.isBlank() -> "Escribe un concepto para identificar el movimiento."
                        id == null -> "Elige una categoría."
                        else -> viewModel.agregarMovimiento(descripcion, monto, tipo, id)
                    }
                    if (error == null) onGuardado()
                }
            )
        }
    }
}
