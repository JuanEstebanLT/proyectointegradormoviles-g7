package com.example.proyecto_integrador_moviles_g7

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.proyecto_integrador_moviles_g7.presentation.navigation.NavegacionApp
import com.example.proyecto_integrador_moviles_g7.ui.theme.Proyectointegradormovilesg7Theme
import org.junit.Rule
import org.junit.Test

/** Navegación que no requiere la API: Acceso <-> Registro y validación local del formulario. */
class NavegacionInstrumentedTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun abreRegistroYRegresaAlAcceso() {
        compose.activity.setContent { Proyectointegradormovilesg7Theme { NavegacionApp() } }
        compose.onNodeWithText("Iniciar sesión").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Regístrate").performScrollTo().performClick()
        compose.onAllNodesWithText("Crear cuenta").onFirst().assertExists()
        compose.onNodeWithContentDescription("Volver").performClick()
        compose.onNodeWithText("Iniciar sesión").assertExists()
    }

    @Test fun validaCorreoAntesDeLlamarALaApi() {
        compose.activity.setContent { Proyectointegradormovilesg7Theme { NavegacionApp() } }
        compose.onNodeWithText("Iniciar sesión").performScrollTo().performClick()
        compose.onNodeWithText("Ingresa un correo válido").assertExists()
    }
}
