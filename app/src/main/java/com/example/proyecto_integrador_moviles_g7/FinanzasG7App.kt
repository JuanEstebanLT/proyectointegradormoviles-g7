package com.example.proyecto_integrador_moviles_g7

import android.app.Application
import com.example.proyecto_integrador_moviles_g7.data.local.ProveedorLocal

/** Inicia el almacenamiento local (DataStore + Room) antes de abrir cualquier pantalla. */
class FinanzasG7App : Application() {
    override fun onCreate() {
        super.onCreate()
        ProveedorLocal.iniciar(this)
    }
}
