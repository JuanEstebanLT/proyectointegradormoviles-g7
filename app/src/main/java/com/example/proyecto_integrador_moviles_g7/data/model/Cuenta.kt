package com.example.proyecto_integrador_moviles_g7.data.model

/** Sugerencia de ahorro calculada por la API para el mes actual. */
data class Sugerencia(
    val id: Long,
    val titulo: String,
    val mensaje: String,
    val categorias: List<String> = emptyList()
)
