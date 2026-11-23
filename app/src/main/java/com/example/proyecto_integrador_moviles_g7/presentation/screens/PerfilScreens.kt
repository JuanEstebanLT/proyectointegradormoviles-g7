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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoError
import com.example.proyecto_integrador_moviles_g7.presentation.components.Avatar
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraContextual
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.presentation.components.CajaIcono
import com.example.proyecto_integrador_moviles_g7.presentation.components.Etiqueta
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.coloresCampo
import com.example.proyecto_integrador_moviles_g7.presentation.components.fechaLarga
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.PerfilViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.HieloSuave
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate100
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate900
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa

/** Pantalla 11 — Perfil. */
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onAtras: () -> Unit,
    onEditarPerfil: () -> Unit,
    onCategorias: () -> Unit,
    onSaldoInicial: () -> Unit,
    onMetaAhorro: () -> Unit,
    onNotificaciones: () -> Unit,
    onAdmin: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val usuario = estado.usuario
    var confirmarSalida by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera petróleo con avatar
        Box(
            Modifier
                .fillMaxWidth()
                .background(TealOscuro, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .statusBarsPadding()
                .padding(bottom = 28.dp)
        ) {
            IconButton(onClick = onAtras, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Avatar(usuario?.iniciales ?: "?", tamano = 96.dp, fondo = Turquesa, colorTexto = TealOscuro, tamanoTexto = 34.sp)
                Spacer(Modifier.height(12.dp))
                Text(usuario?.nombre ?: "Sin sesión", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(usuario?.correo.orEmpty(), fontSize = 14.sp, color = AguaClaro)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Etiqueta(if (usuario?.esAdmin == true) "ADMINISTRADOR" else "USUARIO", Turquesa, TealOscuro)
                    Etiqueta("Cuenta activa", Color.White.copy(alpha = 0.15f), AguaClaro)
                }
            }
        }

        Column(
            Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaG7(radio = 22.dp) {
                Text("Datos de la cuenta", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Slate900)
                Spacer(Modifier.height(8.dp))
                DatoCuenta(Icons.Outlined.Smartphone, "Teléfono", usuario?.telefono?.takeIf { it.isNotBlank() } ?: "Sin registrar")
                DatoCuenta(Icons.Outlined.CalendarMonth, "Miembro desde", usuario?.miembroDesde?.let { fechaLarga(it) } ?: "—")
                DatoCuenta(Icons.Outlined.Payments, "Moneda principal", "COP (Peso colombiano)")
                DatoCuenta(Icons.Outlined.AccountBalance, "Saldo inicial", "${formatoCop(estado.saldoInicial)} COP")
                DatoCuenta(
                    Icons.Outlined.Savings, "Meta de ahorro mensual",
                    if (estado.metaAhorro > 0L) "${formatoCop(estado.metaAhorro)} COP" else "Sin definir"
                )
            }

            Text("Configuración general", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate500, modifier = Modifier.padding(start = 4.dp))
            TarjetaG7(radio = 22.dp, relleno = PaddingValues(vertical = 6.dp)) {
                OpcionPerfil(Icons.Outlined.ManageAccounts, "Editar perfil", "Actualiza tu nombre y teléfono", onClick = onEditarPerfil)
                OpcionPerfil(Icons.Outlined.Category, "Gestionar categorías", "Personaliza ingresos, gastos y presupuestos", onClick = onCategorias)
                OpcionPerfil(Icons.Outlined.AccountBalance, "Saldo inicial", "Cambia el dinero con el que empezaste", onClick = onSaldoInicial)
                OpcionPerfil(Icons.Outlined.Savings, "Meta de ahorro", "Configura tu objetivo mensual", onClick = onMetaAhorro)
                OpcionPerfil(Icons.Outlined.NotificationsActive, "Notificaciones", "Alertas de presupuesto y saldo", onClick = onNotificaciones)
                if (usuario?.esAdmin == true) {
                    OpcionPerfil(
                        Icons.Outlined.AdminPanelSettings, "Panel de administrador", "Usuarios registrados en la plataforma",
                        destacada = true, onClick = onAdmin
                    )
                }
            }

            Button(
                onClick = { confirmarSalida = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Rosa50, contentColor = Rosa600),
                border = BorderStroke(1.dp, Rosa600.copy(alpha = 0.25f))
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Text(
                "Finanzas G7 • Proyecto Integrador Móviles",
                fontSize = 12.sp, color = Slate400,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    if (confirmarSalida) {
        DialogoConfirmacion(
            titulo = "¿Cerrar sesión?",
            mensaje = "Tendrás que volver a ingresar tu correo y contraseña.",
            confirmar = "Cerrar sesión",
            onConfirmar = {
                confirmarSalida = false
                onCerrarSesion()
            },
            onCancelar = { confirmarSalida = false }
        )
    }
}

@Composable
private fun DatoCuenta(icono: ImageVector, etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        CajaIcono(icono, fondo = HieloSuave, tinte = TealOscuro, tamano = 40.dp, tamanoIcono = 20.dp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(etiqueta, fontSize = 12.sp, color = Slate500)
            Text(valor, fontSize = 15.sp, color = Slate900, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun OpcionPerfil(
    icono: ImageVector,
    titulo: String,
    detalle: String,
    destacada: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (destacada) HieloSuave else Color.Transparent)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CajaIcono(
            icono,
            fondo = if (destacada) Turquesa else Slate100,
            tinte = TealOscuro,
            tamano = 44.dp, radio = 22.dp
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (destacada) TealOscuro else Slate900)
                if (destacada) {
                    Spacer(Modifier.width(6.dp))
                    Etiqueta("Admin", Turquesa, TealOscuro)
                }
            }
            Text(detalle, fontSize = 12.sp, color = Slate500)
        }
        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = Slate400)
    }
}

/** Pantalla 16 — Editar perfil. El correo es de solo lectura. */
@Composable
fun EditarPerfilScreen(
    viewModel: PerfilViewModel,
    onAtras: () -> Unit,
    onGuardado: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val usuario = estado.usuario
    val context = LocalContext.current
    var nombre by rememberSaveable { mutableStateOf(usuario?.nombre.orEmpty()) }
    var telefono by rememberSaveable { mutableStateOf(usuario?.telefono.orEmpty()) }

    LaunchedEffect(Unit) { viewModel.limpiarError() }

    Scaffold(
        containerColor = FondoClaro,
        topBar = { BarraContextual("Editar perfil", onAtras) },
        bottomBar = {
            androidx.compose.material3.Surface(color = Color.White, shadowElevation = 8.dp) {
                BotonPrimario(
                    "Guardar cambios",
                    icono = Icons.Outlined.Check,
                    cargando = estado.guardando,
                    onClick = {
                        viewModel.guardar(nombre, telefono) {
                            Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            onGuardado()
                        }
                    },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    ) { relleno ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(relleno)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val iniciales = nombre.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.take(2)
                .joinToString("") { it.first().uppercase() }.ifEmpty { "?" }
            Avatar(iniciales, tamano = 96.dp, fondo = Turquesa, colorTexto = TealOscuro, tamanoTexto = 34.sp)
            Text("Las iniciales se actualizan con tu nombre", fontSize = 12.sp, color = Slate500)

            TarjetaG7(radio = 22.dp) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it.take(100); viewModel.limpiarError() },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = TealMedio) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it.filter { c -> c.isDigit() || c == ' ' || c == '+' }.take(20); viewModel.limpiarError() },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Teléfono") },
                    leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = TealMedio) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = usuario?.correo.orEmpty(),
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Outlined.Mail, contentDescription = null, tint = Slate400) },
                    trailingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "No editable", tint = Slate400) },
                    readOnly = true,
                    enabled = false,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo()
                )
                Spacer(Modifier.height(4.dp))
                Text("El correo se usa para iniciar sesión y no se puede cambiar.", fontSize = 12.sp, color = Slate500)
            }

            TarjetaG7(radio = 22.dp, fondo = Slate50) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Badge, contentDescription = null, tint = TealOscuro)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Rol", fontSize = 12.sp, color = Slate500)
                        Text(if (usuario?.esAdmin == true) "Administrador" else "Usuario", fontWeight = FontWeight.Bold, color = Slate900)
                    }
                }
            }

            estado.error?.let { AvisoError(it) }
        }
    }
}
