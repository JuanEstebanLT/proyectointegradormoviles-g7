package com.example.proyecto_integrador_moviles_g7.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_integrador_moviles_g7.presentation.components.Tarjeta
import com.example.proyecto_integrador_moviles_g7.ui.theme.AguaClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar700
import com.example.proyecto_integrador_moviles_g7.ui.theme.Ambar900
import com.example.proyecto_integrador_moviles_g7.ui.theme.Crema
import com.example.proyecto_integrador_moviles_g7.ui.theme.FondoClaro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Proyectointegradormovilesg7Theme
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate200
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate400
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate50
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate500
import com.example.proyecto_integrador_moviles_g7.ui.theme.Slate700
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealMedio
import com.example.proyecto_integrador_moviles_g7.ui.theme.TealOscuro
import com.example.proyecto_integrador_moviles_g7.ui.theme.Turquesa

@Composable
fun AccesoScreen(
    onIniciarSesion: () -> Unit,
    onRegistrarse: () -> Unit
) {
    val context = LocalContext.current
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoMarca()

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset(y = (-16).dp)
        ) {
            Tarjeta(relleno = PaddingValues(24.dp), espaciado = 16.dp) {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Correo Electrónico", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Text("Banca Personal", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TealMedio)
                    }
                    Spacer(Modifier.height(6.dp))
                    CampoTexto(
                        valor = correo,
                        onCambio = { correo = it },
                        icono = Icons.Outlined.AlternateEmail,
                        placeholder = "ejemplo@correo.com",
                        tipoTeclado = KeyboardType.Email
                    )
                }

                Column {
                    Text("Contraseña", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Spacer(Modifier.height(6.dp))
                    CampoTexto(
                        valor = contrasena,
                        onCambio = { contrasena = it },
                        icono = Icons.Outlined.Lock,
                        placeholder = "••••••••",
                        tipoTeclado = KeyboardType.Password,
                        transformacion = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                        iconoFinal = {
                            IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                                Icon(
                                    if (mostrarContrasena) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = Slate400
                                )
                            }
                        }
                    )
                }

                TextButton(
                    onClick = { Toast.makeText(context, "Recuperación de contraseña pendiente", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("¿Olvidaste tu contraseña?", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TealOscuro)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, tint = TealOscuro, modifier = Modifier.size(14.dp))
                }

                Button(
                    onClick = onIniciarSesion,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealOscuro)
                ) {
                    Text("Iniciar Sesión", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Outlined.Login, null, modifier = Modifier.size(18.dp))
                }

                OutlinedButton(
                    onClick = { Toast.makeText(context, "Acceso biométrico pendiente", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Turquesa.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AguaClaro.copy(alpha = 0.3f),
                        contentColor = TealOscuro
                    )
                ) {
                    Icon(Icons.Outlined.Fingerprint, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Ingresar con huella digital", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                // El texto original mencionaba SHA-256 como cifrado; ver observación 7 del README
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Crema.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Security, null, tint = Ambar700, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Tus datos viajan protegidos mediante conexión cifrada (HTTPS).",
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        color = Ambar900
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), color = Slate200)
                Text(
                    "O CONTINÚA CON",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = Slate400,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(Modifier.weight(1f), color = Slate200)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { Toast.makeText(context, "Acceso con Google pendiente", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Slate200),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Slate700)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFF4285F4), fontWeight = FontWeight.Black)) { append("G") }
                        append("   Google")
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿No tienes una cuenta?", fontSize = 12.sp, color = Slate500)
            TextButton(onClick = onRegistrarse) {
                Text("Regístrate", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealOscuro)
            }
        }
    }
}

@Composable
private fun EncabezadoMarca() {
    val forma = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, forma)
            .background(TealOscuro, forma)
            .statusBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Turquesa, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.AccountBalanceWallet, null, tint = TealOscuro, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.VerifiedUser, null, tint = AguaClaro, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(6.dp))
            Text("Finanzas G7 • Colombia", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AguaClaro)
        }
        Spacer(Modifier.height(8.dp))
        Text("Bienvenido de nuevo", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(4.dp))
        Text(
            "Ingresa tus credenciales para acceder a tus finanzas seguras",
            fontSize = 12.sp,
            color = AguaClaro.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 260.dp)
        )
    }
}

@Composable
private fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    icono: ImageVector,
    placeholder: String,
    tipoTeclado: KeyboardType,
    transformacion: VisualTransformation = VisualTransformation.None,
    iconoFinal: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(placeholder, fontSize = 14.sp, color = Slate400) },
        leadingIcon = { Icon(icono, null, tint = Slate400, modifier = Modifier.size(20.dp)) },
        trailingIcon = iconoFinal,
        visualTransformation = transformacion,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Slate50,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Slate200,
            focusedBorderColor = TealMedio
        )
    )
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun AccesoScreenPreview() {
    Proyectointegradormovilesg7Theme {
        AccesoScreen(onIniciarSesion = {}, onRegistrarse = {})
    }
}
