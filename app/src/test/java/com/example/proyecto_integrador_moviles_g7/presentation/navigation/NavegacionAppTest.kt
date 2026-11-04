package com.example.proyecto_integrador_moviles_g7.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class NavegacionAppTest {

    @Test
    fun `construye ruta de detalle con identificador de categoria`() {
        assertEquals("detalle_categoria/2", Rutas.detalleCategoria(2))
    }
}
