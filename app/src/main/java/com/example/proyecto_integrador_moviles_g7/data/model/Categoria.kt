package com.example.proyecto_integrador_moviles_g7.data.model

data class Categoria(
    val id: Int,
    val nombre: String,
    val tipo: TipoMovimiento,
    val presupuesto: Long = 0L
) {
    init {
        require(id > 0) { "El identificador de la categoría debe ser positivo" }
        require(nombre.isNotBlank()) { "El nombre de la categoría es obligatorio" }
        require(presupuesto >= 0L) { "El presupuesto no puede ser negativo" }
    }
}
