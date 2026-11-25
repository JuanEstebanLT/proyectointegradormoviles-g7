package com.example.proyecto_integrador_moviles_g7.data.local

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob

/**
 * Punto único de acceso al almacenamiento local. Se inicia en [FinanzasG7App].
 * En los tests unitarios no se inicia: los repositorios funcionan igual, solo sin persistencia.
 */
object ProveedorLocal {

    @Volatile var almacenSesion: AlmacenSesion? = null
        private set

    @Volatile var cache: CacheFinanzas? = null
        private set

    /**
     * Escrituras en segundo plano que no dependen de una pantalla. Usa un solo hilo para
     * que se apliquen en orden (por ejemplo: cerrar sesión y luego iniciar otra).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))

    fun iniciar(context: Context) {
        if (almacenSesion != null) return
        val app = context.applicationContext
        almacenSesion = AlmacenSesion(app)
        val baseDatos = Room.databaseBuilder(app, BaseDatosFinanzas::class.java, "finanzas_g7.db")
            // Es una caché: si cambia el esquema se borra y se vuelve a descargar de la API
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
        cache = CacheFinanzas(baseDatos.cacheDao())
    }
}
