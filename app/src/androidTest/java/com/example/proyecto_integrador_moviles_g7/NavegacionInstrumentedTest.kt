package com.example.proyecto_integrador_moviles_g7

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.proyecto_integrador_moviles_g7.presentation.navigation.NavegacionApp
import com.example.proyecto_integrador_moviles_g7.ui.theme.Proyectointegradormovilesg7Theme
import org.junit.Rule
import org.junit.Test

class NavegacionInstrumentedTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun recorreSeccionesYRegresaAlAcceso() {
        compose.activity.setContent { Proyectointegradormovilesg7Theme { NavegacionApp() } }
        compose.onNodeWithText("Entrar en modo demostración").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Movimientos", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Todos").assertIsDisplayed()
        compose.onNodeWithContentDescription("Informes", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Resumen de todos los movimientos de esta sesión").assertIsDisplayed()
        compose.onNodeWithContentDescription("Ahorro", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Orientación educativa").assertIsDisplayed()
        compose.onNodeWithContentDescription("Inicio", useUnmergedTree = true).performClick()
        compose.onNodeWithContentDescription("Notificaciones", useUnmergedTree = true).performClick()
        compose.onNodeWithText("No hay notificaciones. El servicio de notificaciones aún no está implementado.").assertIsDisplayed()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithContentDescription("Perfil", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Volver al acceso").performClick()
        compose.onNodeWithText("Entrar en modo demostración").assertExists()
    }
}
