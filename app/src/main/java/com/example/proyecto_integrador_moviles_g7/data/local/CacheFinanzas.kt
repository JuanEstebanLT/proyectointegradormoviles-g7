package com.example.proyecto_integrador_moviles_g7.data.local

import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasState

/** Datos leídos de la caché local y cuándo se sincronizaron por última vez con la API. */
data class DatosGuardados(
    val state: FinanzasState,
    val metaAhorro: Long,
    val sincronizadoEn: Long
)

/** Lee y escribe la copia local de los datos de un usuario (Room). */
class CacheFinanzas(private val dao: CacheDao) {

    /** Datos guardados del usuario, o null si nunca se sincronizó en este teléfono. */
    suspend fun leer(usuarioId: Long): DatosGuardados? {
        val cuenta = dao.cuenta(usuarioId)?.takeIf { it.sincronizadoEn > 0L } ?: return null
        // Si alguna fila está dañada se ignora la caché (la API la vuelve a llenar)
        return try {
            DatosGuardados(
                state = FinanzasState(
                    saldoInicial = cuenta.saldoInicial,
                    categorias = dao.categorias(usuarioId).map { it.aModelo() },
                    movimientos = dao.movimientos(usuarioId).map { it.aModelo() }
                ),
                metaAhorro = cuenta.metaAhorro,
                sincronizadoEn = cuenta.sincronizadoEn
            )
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: java.time.format.DateTimeParseException) {
            null
        }
    }

    /**
     * Reemplaza la copia local con [state]. Los movimientos con id temporal ("tmp-…")
     * todavía no existen en la API, así que no se guardan.
     */
    suspend fun guardar(usuarioId: Long, state: FinanzasState, ahora: Long = System.currentTimeMillis()) {
        val meta = dao.cuenta(usuarioId)?.metaAhorro ?: 0L
        dao.reemplazar(
            cuenta = CuentaEntidad(usuarioId, state.saldoInicial, meta, ahora),
            categorias = state.categorias.map { it.aEntidad(usuarioId) },
            movimientos = state.movimientos.filter { it.id.toLongOrNull() != null }.map { it.aEntidad(usuarioId) }
        )
    }

    suspend fun metaAhorro(usuarioId: Long): Long? = dao.cuenta(usuarioId)?.metaAhorro

    suspend fun guardarMeta(usuarioId: Long, meta: Long) {
        val cuenta = dao.cuenta(usuarioId)
        dao.guardarCuenta(cuenta?.copy(metaAhorro = meta) ?: CuentaEntidad(usuarioId, 0L, meta, 0L))
    }

    /** Borra todo lo guardado del usuario (al cerrar sesión). */
    suspend fun borrar(usuarioId: Long) = dao.borrarUsuario(usuarioId)
}
