package com.example.proyecto_integrador_moviles_g7.data.model

data class Movimiento(
    val id: String,
    val descripcion: String,
    val monto: Double,
    val tipo: String,
    val categoria: String,
    val fecha: String
)