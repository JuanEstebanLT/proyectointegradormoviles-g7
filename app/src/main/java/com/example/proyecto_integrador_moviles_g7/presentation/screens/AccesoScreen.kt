package com.example.proyecto_integrador_moviles_g7.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyecto_integrador_moviles_g7.presentation.components.AvisoError
import com.example.proyecto_integrador_moviles_g7.presentation.components.BotonPrimario
import com.example.proyecto_integrador_moviles_g7.presentation.components.LogoG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.TarjetaG7
import com.example.proyecto_integrador_moviles_g7.presentation.components.coloresCampo
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AuthViewModel
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Rosa600
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro

@Composable
fun AccesoScreen(
    viewModel: AuthViewModel,
    onIniciarSesion: () -> Unit,
    onRegistrarse: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    val foco = LocalFocusManager.current

    LaunchedEffect(estado.usuario) {
        if (estado.usuario != null) onIniciarSesion()
    }

    val entrar = {
        foco.clearFocus()
        viewModel.iniciarSesion(correo, contrasena)
    }

    PantallaAutenticacion(
        titulo = "Bienvenido de nuevo",
        subtitulo = "Ingresa para ver tu saldo, tus movimientos y tus metas."
    ) {
        CampoAutenticacion(
            valor = correo,
            onCambio = { correo = it; viewModel.limpiarError() },
            etiqueta = "Correo electrónico",
            icono = Icons.Outlined.Mail,
            tipoTeclado = KeyboardType.Email,
            accion = ImeAction.Next,
            onAccion = { foco.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
        )
        Spacer(Modifier.height(12.dp))
        CampoContrasena(
            valor = contrasena,
            onCambio = { contrasena = it; viewModel.limpiarError() },
            etiqueta = "Contraseña",
            accion = ImeAction.Done,
            onAccion = { entrar() }
        )
        estado.error?.let {
            Spacer(Modifier.height(12.dp))
            AvisoError(it)
        }
        Spacer(Modifier.height(20.dp))
        BotonPrimario(
            texto = "Iniciar sesión",
            icono = Icons.AutoMirrored.Outlined.Login,
            cargando = estado.cargando,
            onClick = entrar
        )
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes cuenta? ", color = Slate500, fontSize = 14.sp)
            Text(
                "Regístrate",
                color = TealOscuro,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable(onClick = onRegistrarse)
                    .padding(vertical = 6.dp)
            )
        }
    }
}

/** Cabecera petróleo con el isotipo G7 y una tarjeta blanca con el formulario. */
@Composable
internal fun PantallaAutenticacion(
    titulo: String,
    subtitulo: String,
    onAtras: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TealOscuro, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 64.dp)
        ) {
            Column {
                if (onAtras != null) {
                    IconButton(onClick = onAtras, modifier = Modifier.offset(x = (-12).dp)) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                } else {
                    Spacer(Modifier.height(24.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LogoG7(tamano = 48.dp, tamanoTexto = 18.sp, radio = 14.dp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Finanzas G7", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("Tus finanzas personales en COP", color = AguaClaro, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(28.dp))
                Text(titulo, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)
                Spacer(Modifier.height(6.dp))
                Text(subtitulo, color = AguaClaro, fontSize = 14.sp, lineHeight = 20.sp)
            }
        }
        TarjetaG7(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-40).dp),
            radio = 24.dp,
            contenido = contenido
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp)
                .navigationBarsPadding()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Shield, contentDescription = null, tint = TealMedio, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Contraseñas cifradas con bcrypt • Sesión con token JWT", color = Slate500, fontSize = 11.sp)
        }
    }
}

@Composable
internal fun CampoAutenticacion(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accion: ImeAction = ImeAction.Next,
    onAccion: () -> Unit = {},
    error: String? = null,
    prefijo: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null, tint = TealMedio) },
        prefix = prefijo?.let { { Text(it) } },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(it, color = Rosa600) } },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado, imeAction = accion),
        keyboardActions = KeyboardActions(onAny = { onAccion() }),
        shape = RoundedCornerShape(16.dp),
        colors = coloresCampo()
    )
}

@Composable
internal fun CampoContrasena(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    accion: ImeAction = ImeAction.Done,
    onAccion: () -> Unit = {},
    error: String? = null
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = TealMedio) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                    tint = Slate500
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(it, color = Rosa600) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = accion),
        keyboardActions = KeyboardActions(onAny = { onAccion() }),
        shape = RoundedCornerShape(16.dp),
        colors = coloresCampo()
    )
}
