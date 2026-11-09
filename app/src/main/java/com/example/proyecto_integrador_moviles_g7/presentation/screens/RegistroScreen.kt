package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.presentation.components.Tarjeta
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AuthViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate800
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro

/** Crea la cuenta en la API (HU-01) y continúa al Saldo Inicial. */
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

    LaunchedEffect(estado.usuario) {
        if (estado.usuario != null) onRegistrado()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onAtras) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = TealOscuro)
            }
            Text("Crear cuenta", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Slate800)
        }
        Text(
            "Regístrate para guardar tus movimientos de forma segura. Luego definirás tu saldo inicial.",
            fontSize = 12.sp,
            color = Slate500
        )

        Tarjeta(espaciado = 14.dp) {
            Campo("Nombre completo", nombre, { nombre = it; viewModel.limpiarError() },
                Icons.Outlined.Person, "Tu nombre", KeyboardType.Text)
            Campo("Correo electrónico", correo, { correo = it; viewModel.limpiarError() },
                Icons.Outlined.AlternateEmail, "ejemplo@correo.com", KeyboardType.Email)
            Campo("Teléfono (opcional)", telefono, { telefono = it; viewModel.limpiarError() },
                Icons.Outlined.Phone, "3001234567", KeyboardType.Phone)
            Campo("Contraseña", contrasena, { contrasena = it; viewModel.limpiarError() },
                Icons.Outlined.Lock, "Mínimo 6 caracteres", KeyboardType.Password, ocultar = true)

            estado.error?.let { error ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Rosa50, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.ErrorOutline, null, tint = Rosa600, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(error, fontSize = 12.sp, color = Rosa600)
                }
            }

            Button(
                onClick = { viewModel.registrar(nombre, correo, telefono, contrasena) },
                enabled = !estado.cargando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealOscuro)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text("Crear cuenta", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun Campo(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    icono: ImageVector,
    placeholder: String,
    tipoTeclado: KeyboardType,
    ocultar: Boolean = false
) {
    Column {
        Text(etiqueta, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
        Spacer(Modifier.height(6.dp))
        CampoTexto(
            valor = valor,
            onCambio = onCambio,
            icono = icono,
            placeholder = placeholder,
            tipoTeclado = tipoTeclado,
            transformacion = if (ocultar) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}
