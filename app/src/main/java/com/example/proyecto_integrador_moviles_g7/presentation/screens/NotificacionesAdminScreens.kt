package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.data.model.Usuario
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoError
import com.example.proyecto_integrador_moviles_g7.presentation.components.Avatar
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraContextual
import com.example.proyecto_integrador_moviles_g7.presentation.components.CajaIcono
import com.example.proyecto_integrador_moviles_g7.presentation.components.CargandoPantalla
import com.example.proyecto_integrador_moviles_g7.presentation.components.EstadoVacio
import com.example.proyecto_integrador_moviles_g7.presentation.components.Etiqueta
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.coloresCampo
import com.example.proyecto_integrador_moviles_g7.presentation.components.fechaLarga
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AdminViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.Notificacion
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.NotificacionesViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.TipoNotificacion
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.HieloSuave
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa

// ============================================================ 14 — Notificaciones

@Composable
fun NotificacionesScreen(
    viewModel: NotificacionesViewModel,
    onAtras: () -> Unit,
    onCategoria: (Int) -> Unit,
    onAhorro: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            BarraContextual("Notificaciones", onAtras) {
                if (estado.sinLeer > 0) {
                    TextButton(onClick = viewModel::marcarTodasLeidas) {
                        Icon(Icons.Outlined.DoneAll, contentDescription = null, tint = TealMedio, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Marcar leídas", color = TealMedio, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { relleno ->
        if (estado.notificaciones.isEmpty()) {
            EstadoVacio(
                Icons.Outlined.NotificationsNone,
                "Estás al día",
                "No tienes alertas de presupuesto ni de saldo. Te avisaremos aquí cuando algo necesite tu atención.",
                Modifier.padding(relleno).padding(top = 48.dp)
            )
            return@Scaffold
        }
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    if (estado.sinLeer > 0) "${estado.sinLeer} sin leer" else "Todas leídas",
                    fontSize = 13.sp, color = Slate500, fontWeight = FontWeight.SemiBold
                )
            }
            items(estado.notificaciones, key = { it.clave }) { notificacion ->
                val leida = notificacion.clave in estado.leidas
                FilaNotificacion(notificacion, leida) {
                    viewModel.marcarLeida(notificacion.clave)
                    when {
                        notificacion.categoriaId != null -> onCategoria(notificacion.categoriaId)
                        notificacion.tipo == TipoNotificacion.META_ALCANZADA -> onAhorro()
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaNotificacion(notificacion: Notificacion, leida: Boolean, onClick: () -> Unit) {
    val (icono: ImageVector, fondo: Color, tinte: Color) = when (notificacion.tipo) {
        TipoNotificacion.PRESUPUESTO_EXCEDIDO -> Triple(Icons.Outlined.ErrorOutline, Rosa50, Rosa600)
        TipoNotificacion.PRESUPUESTO_CERCANO -> Triple(Icons.Outlined.WarningAmber, Ambar50, Ambar700)
        TipoNotificacion.META_ALCANZADA -> Triple(Icons.Outlined.TaskAlt, Esmeralda50, Esmeralda600)
        TipoNotificacion.SALDO_BAJO -> Triple(Icons.Outlined.AccountBalanceWallet, Ambar50, Ambar700)
    }
    TarjetaG7(
        fondo = if (leida) Color.White else HieloSuave,
        borde = if (leida) androidx.compose.ui.graphics.Color(0xFFE2E8F0) else Turquesa.copy(alpha = 0.5f),
        radio = 18.dp,
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.Top) {
            CajaIcono(icono, fondo = fondo, tinte = tinte)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(notificacion.titulo, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Slate900)
                Spacer(Modifier.height(2.dp))
                Text(notificacion.mensaje, fontSize = 13.sp, color = Slate500, lineHeight = 18.sp)
            }
            if (!leida) {
                Spacer(Modifier.width(8.dp))
                Box(Modifier.padding(top = 6.dp).size(10.dp).clip(CircleShape).background(Turquesa))
            }
        }
    }
}

// ============================================================ 15 — Panel de administrador

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    idUsuarioActual: String?,
    onAtras: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var busqueda by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf(0) } // 0 todos, 1 usuarios, 2 administradores
    var seleccionado by remember { mutableStateOf<Usuario?>(null) }

    val texto = busqueda.trim()
    val visibles = estado.usuarios.filter { u ->
        (filtro == 0 || (filtro == 1 && !u.esAdmin) || (filtro == 2 && u.esAdmin)) &&
            (texto.isEmpty() || u.nombre.contains(texto, ignoreCase = true) || u.correo.contains(texto, ignoreCase = true))
    }

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            BarraContextual("Administración", onAtras) {
                IconButton(onClick = viewModel::recargar) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Recargar", tint = TealMedio)
                }
            }
        }
    ) { relleno ->
        if (estado.cargando && estado.usuarios.isEmpty()) {
            CargandoPantalla(Modifier.padding(relleno))
            return@Scaffold
        }
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Gestión de usuarios", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Slate900, modifier = Modifier.weight(1f))
                    Etiqueta("● ADMIN", Turquesa, TealOscuro)
                }
            }
            estado.error?.let { item { AvisoError(it) } }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricaAdmin(Icons.Outlined.Group, "${estado.usuarios.size}", "Usuarios", Modifier.weight(1f))
                    MetricaAdmin(Icons.Outlined.AdminPanelSettings, "${estado.administradores}", "Administradores", Modifier.weight(1f), destacada = true)
                    MetricaAdmin(Icons.Outlined.PersonAdd, "${estado.nuevosEsteMes}", "Nuevos este mes", Modifier.weight(1f))
                }
            }
            item {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar por nombre o correo…", color = Slate500) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Slate500) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo()
                )
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipFiltro("Todos (${estado.usuarios.size})", filtro == 0) { filtro = 0 }
                    ChipFiltro("Usuarios (${estado.usuarios.size - estado.administradores})", filtro == 1) { filtro = 1 }
                    ChipFiltro("Administradores (${estado.administradores})", filtro == 2) { filtro = 2 }
                }
            }
            if (visibles.isEmpty() && !estado.cargando) {
                item { EstadoVacio(Icons.Outlined.Group, "Sin resultados", "No hay usuarios que coincidan con la búsqueda.") }
            }
            items(visibles, key = { it.id }) { usuario ->
                FilaUsuario(usuario, esActual = usuario.id == idUsuarioActual) { seleccionado = usuario }
            }
        }
    }

    seleccionado?.let { usuario ->
        ModalBottomSheet(
            onDismissRequest = { seleccionado = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Detalle del usuario", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Slate900, modifier = Modifier.weight(1f))
                    IconButton(onClick = { seleccionado = null }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = Slate500)
                    }
                }
                TarjetaG7(fondo = HieloSuave, borde = Color.Transparent) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(usuario.iniciales, tamano = 56.dp, fondo = Turquesa, colorTexto = TealOscuro, tamanoTexto = 20.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(usuario.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Slate900)
                            Etiqueta(if (usuario.esAdmin) "ADMINISTRADOR" else "USUARIO", Color.White, TealOscuro)
                        }
                    }
                }
                DatoUsuario(Icons.Outlined.Mail, "Correo", usuario.correo)
                DatoUsuario(Icons.Outlined.Phone, "Teléfono", usuario.telefono.ifBlank { "Sin registrar" })
                DatoUsuario(Icons.Outlined.CalendarMonth, "Miembro desde", usuario.miembroDesde?.let { fechaLarga(it) } ?: "—")
                Text(
                    "Los roles se asignan desde la base de datos (migración 001). La app solo permite consultarlos.",
                    fontSize = 12.sp, color = Slate500
                )
            }
        }
    }
}

@Composable
private fun MetricaAdmin(icono: ImageVector, valor: String, etiqueta: String, modifier: Modifier, destacada: Boolean = false) {
    TarjetaG7(modifier = modifier, fondo = if (destacada) HieloSuave else Color.White, radio = 18.dp, relleno = PaddingValues(12.dp)) {
        CajaIcono(icono, fondo = if (destacada) Turquesa.copy(alpha = 0.5f) else HieloSuave, tinte = TealOscuro, tamano = 34.dp, tamanoIcono = 18.dp, radio = 10.dp)
        Spacer(Modifier.height(8.dp))
        Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Slate900)
        Text(etiqueta, fontSize = 11.sp, color = TealMedio, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun FilaUsuario(usuario: Usuario, esActual: Boolean, onClick: () -> Unit) {
    TarjetaG7(radio = 18.dp, relleno = PaddingValues(12.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(
                usuario.iniciales, tamano = 46.dp,
                fondo = if (usuario.esAdmin) Turquesa else HieloSuave,
                colorTexto = TealOscuro, tamanoTexto = 16.sp
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    if (esActual) {
                        Spacer(Modifier.width(6.dp))
                        Etiqueta("TÚ", Turquesa, TealOscuro)
                    }
                }
                Text(usuario.correo, fontSize = 12.sp, color = Slate500, maxLines = 1, overflow = TextOverflow.Ellipsis)
                usuario.miembroDesde?.let { Text("Desde ${fechaLarga(it)}", fontSize = 11.sp, color = Slate500) }
            }
            Spacer(Modifier.width(8.dp))
            Etiqueta(if (usuario.esAdmin) "ADMIN" else "USUARIO", if (usuario.esAdmin) Turquesa else HieloSuave, TealOscuro)
        }
    }
}

@Composable
private fun DatoUsuario(icono: ImageVector, etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CajaIcono(icono, fondo = HieloSuave, tinte = TealOscuro, tamano = 38.dp, tamanoIcono = 18.dp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(etiqueta, fontSize = 12.sp, color = Slate500)
            Text(valor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
        }
    }
}
