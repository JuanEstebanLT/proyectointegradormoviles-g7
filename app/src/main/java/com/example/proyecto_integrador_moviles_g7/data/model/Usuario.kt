package com.example.proyecto_integrador_moviles_g7.data.model

import java.time.LocalDate

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val rol: String = "USUARIO",
    /** Fecha de registro en la plataforma, si la API la envía. */
    val miembroDesde: LocalDate? = null
) {
    val esAdmin: Boolean
        get() = rol == "ADMIN"

    /** "Emmanuel Villa" -> "EV". */
    val iniciales: String
        get() = nombre.trim().split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifEmpty { "?" }
}
