package com.example.proyecto_integrador_moviles_g7.data.model

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val rol: String = "USUARIO"
) {
    val esAdmin: Boolean
        get() = rol == "ADMIN"
}
