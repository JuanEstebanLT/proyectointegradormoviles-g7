package com.example.proyecto_integrador_moviles_g7.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.proyecto_integrador_moviles_g7.data.dto.UsuarioDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first

private val Context.datosSesion: DataStore<Preferences> by preferencesDataStore(name = "sesion_finanzas_g7")

/**
 * Nivel 1 de persistencia: guarda la sesión (token + usuario) con DataStore para no tener
 * que iniciar sesión cada vez que se abre la app. También guarda qué notificaciones ya se leyeron.
 * Los datos viven en el almacenamiento privado de la app (otras apps no pueden leerlos).
 */
class AlmacenSesion(context: Context) {

    private val almacen = context.applicationContext.datosSesion
    private val adaptadorUsuario = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter(UsuarioDto::class.java)

    suspend fun guardar(token: String, usuario: UsuarioDto) {
        almacen.edit {
            it[TOKEN] = token
            it[USUARIO] = adaptadorUsuario.toJson(usuario)
        }
    }

    suspend fun actualizarUsuario(usuario: UsuarioDto) {
        almacen.edit { it[USUARIO] = adaptadorUsuario.toJson(usuario) }
    }

    /** Token y usuario guardados, o null si no hay sesión (o los datos están dañados). */
    suspend fun leer(): Pair<String, UsuarioDto>? {
        val datos = almacen.data.first()
        val token = datos[TOKEN] ?: return null
        val usuario = datos[USUARIO]?.let { json ->
            try {
                adaptadorUsuario.fromJson(json)
            } catch (e: Exception) {
                null
            }
        } ?: return null
        return token to usuario
    }

    /** Borra la sesión. Las notificaciones leídas se conservan (son por usuario). */
    suspend fun borrar() {
        almacen.edit {
            it.remove(TOKEN)
            it.remove(USUARIO)
        }
    }

    suspend fun notificacionesLeidas(usuarioId: String): Set<String> =
        almacen.data.first()[claveLeidas(usuarioId)].orEmpty()

    suspend fun guardarNotificacionesLeidas(usuarioId: String, claves: Set<String>) {
        almacen.edit { it[claveLeidas(usuarioId)] = claves }
    }

    private fun claveLeidas(usuarioId: String) = stringSetPreferencesKey("notificaciones_leidas_$usuarioId")

    private companion object {
        val TOKEN = stringPreferencesKey("token")
        val USUARIO = stringPreferencesKey("usuario")
    }
}
