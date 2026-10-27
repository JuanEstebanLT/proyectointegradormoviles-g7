package com.example.proyecto_integrador_moviles_g7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyecto_integrador_moviles_g7.presentation.navigation.NavegacionApp
import com.example.proyecto_integrador_moviles_g7.ui.theme.Proyectointegradormovilesg7Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyectointegradormovilesg7Theme {
                NavegacionApp()
            }
        }
    }
}
