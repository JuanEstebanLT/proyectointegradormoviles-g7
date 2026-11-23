package com.example.proyecto_integrador_moviles_g7.presentation.screens

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoError
import com.example.proyecto_integrador_moviles_g7.presentation.components.BarraProgreso
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AuthViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Esmeralda600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro

@Composable
fun RegistroScreen(
    viewModel: AuthViewModel,
    onAtras: () -> Unit,
    onRegistrado: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }
    // Los errores de cada campo se muestran después del primer intento
    var intentado by rememberSaveable { mutableStateOf(false) }
    val foco = LocalFocusManager.current

    LaunchedEffect(estado.usuario) {
        if (estado.usuario != null) onRegistrado()
    }

    val errorNombre = if (intentado && nombre.isBlank()) "Ingresa tu nombre completo" else null
    val errorCorreo = if (intentado && !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()) "Correo no válido" else null
    val errorTelefono = if (intentado && telefono.length > 20) "Máximo 20 caracteres" else null
    val errorContrasena = if (intentado && contrasena.length < 6) "Mínimo 6 caracteres" else null
    val errorConfirmacion = if (intentado && confirmacion != contrasena) "Las contraseñas no coinciden" else null

    val crear = {
        foco.clearFocus()
        intentado = true
        val valido = nombre.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() &&
            telefono.length <= 20 && contrasena.length >= 6 && confirmacion == contrasena
        if (valido) viewModel.registrar(nombre, correo, telefono, contrasena)
    }
    val limpiar = { viewModel.limpiarError() }

    PantallaAutenticacion(
        titulo = "Crea tu cuenta",
        subtitulo = "Empieza a organizar tus ingresos y gastos en menos de un minuto.",
        onAtras = onAtras
    ) {
        CampoAutenticacion(nombre, { nombre = it; limpiar() }, "Nombre completo", Icons.Outlined.Person, error = errorNombre)
        Spacer(Modifier.height(8.dp))
        CampoAutenticacion(
            correo, { correo = it; limpiar() }, "Correo electrónico", Icons.Outlined.Mail,
            tipoTeclado = KeyboardType.Email, error = errorCorreo
        )
        Spacer(Modifier.height(8.dp))
        CampoAutenticacion(
            telefono, { telefono = it.filter { c -> c.isDigit() || c == ' ' }; limpiar() },
            "Teléfono (opcional)", Icons.Outlined.Phone,
            tipoTeclado = KeyboardType.Phone, error = errorTelefono, prefijo = "+57 "
        )
        Spacer(Modifier.height(8.dp))
        CampoContrasena(
            contrasena, { contrasena = it; limpiar() }, "Contraseña",
            accion = ImeAction.Next, onAccion = { foco.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) },
            error = errorContrasena
        )
        if (contrasena.isNotEmpty()) MedidorSeguridad(contrasena)
        Spacer(Modifier.height(8.dp))
        CampoContrasena(
            confirmacion, { confirmacion = it; limpiar() }, "Confirmar contraseña",
            onAccion = { crear() }, error = errorConfirmacion
        )
        estado.error?.let {
            Spacer(Modifier.height(8.dp))
            AvisoError(it)
        }
        Spacer(Modifier.height(16.dp))
        BotonPrimario("Crear cuenta", onClick = crear, icono = Icons.Outlined.PersonAdd, cargando = estado.cargando)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("¿Ya tienes cuenta? ", color = Slate500, fontSize = 14.sp)
            Text(
                "Inicia sesión", color = TealOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                modifier = Modifier
                    .clickable(onClick = onAtras)
                    .padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun MedidorSeguridad(contrasena: String) {
    var puntos = 0
    if (contrasena.length >= 8) puntos++
    if (contrasena.any { it.isUpperCase() } && contrasena.any { it.isLowerCase() }) puntos++
    if (contrasena.any { it.isDigit() }) puntos++
    if (contrasena.any { !it.isLetterOrDigit() }) puntos++
    val (texto, color) = when {
        contrasena.length < 6 || puntos <= 1 -> "Débil" to Rosa600
        puntos <= 2 -> "Media" to Ambar500
        else -> "Fuerte" to Esmeralda600
    }
    Row(Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        BarraProgreso((puntos.coerceAtLeast(1)) / 4f, color, modifier = Modifier.weight(1f), alto = 6.dp)
        Spacer(Modifier.width(10.dp))
        Text("Seguridad: $texto", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
