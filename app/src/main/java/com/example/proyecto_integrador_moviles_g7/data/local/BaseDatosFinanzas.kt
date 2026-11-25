package com.example.proyecto_integrador_moviles_g7.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import java.time.LocalDateTime

/*
 * Nivel 2 de persistencia: copia local (caché) de los datos de la API con Room.
 * Cada fila guarda el id del usuario, así varias cuentas en el mismo teléfono no se mezclan.
 * La API sigue siendo la fuente de verdad: la caché se reemplaza en cada sincronización.
 */

@Entity(tableName = "categorias", primaryKeys = ["usuarioId", "id"])
data class CategoriaEntidad(
    val usuarioId: Long,
    val id: Int,
    val nombre: String,
    val tipo: String,
    val presupuesto: Long
)

@Entity(tableName = "movimientos", primaryKeys = ["usuarioId", "id"])
data class MovimientoEntidad(
    val usuarioId: Long,
    val id: String,
    val descripcion: String,
    val monto: Long,
    val tipo: String,
    val categoriaId: Int,
    /** Fecha y hora en formato ISO (LocalDateTime.toString()). */
    val fecha: String,
    val medioPago: String
)

@Entity(tableName = "cuenta")
data class CuentaEntidad(
    @PrimaryKey val usuarioId: Long,
    val saldoInicial: Long,
    val metaAhorro: Long,
    /** Momento de la última sincronización con la API (epoch millis); 0 = nunca. */
    val sincronizadoEn: Long
)

@Dao
abstract class CacheDao {
    @Query("SELECT * FROM categorias WHERE usuarioId = :usuarioId")
    abstract suspend fun categorias(usuarioId: Long): List<CategoriaEntidad>

    @Query("SELECT * FROM movimientos WHERE usuarioId = :usuarioId")
    abstract suspend fun movimientos(usuarioId: Long): List<MovimientoEntidad>

    @Query("SELECT * FROM cuenta WHERE usuarioId = :usuarioId")
    abstract suspend fun cuenta(usuarioId: Long): CuentaEntidad?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun guardarCuenta(cuenta: CuentaEntidad)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertarCategorias(categorias: List<CategoriaEntidad>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertarMovimientos(movimientos: List<MovimientoEntidad>)

    @Query("DELETE FROM categorias WHERE usuarioId = :usuarioId")
    protected abstract suspend fun borrarCategorias(usuarioId: Long)

    @Query("DELETE FROM movimientos WHERE usuarioId = :usuarioId")
    protected abstract suspend fun borrarMovimientos(usuarioId: Long)

    @Query("DELETE FROM cuenta WHERE usuarioId = :usuarioId")
    protected abstract suspend fun borrarCuenta(usuarioId: Long)

    /** Reemplaza todos los datos del usuario en una sola transacción. */
    @Transaction
    open suspend fun reemplazar(
        cuenta: CuentaEntidad,
        categorias: List<CategoriaEntidad>,
        movimientos: List<MovimientoEntidad>
    ) {
        borrarCategorias(cuenta.usuarioId)
        borrarMovimientos(cuenta.usuarioId)
        insertarCategorias(categorias)
        insertarMovimientos(movimientos)
        guardarCuenta(cuenta)
    }

    @Transaction
    open suspend fun borrarUsuario(usuarioId: Long) {
        borrarCategorias(usuarioId)
        borrarMovimientos(usuarioId)
        borrarCuenta(usuarioId)
    }
}

@Database(
    entities = [CategoriaEntidad::class, MovimientoEntidad::class, CuentaEntidad::class],
    version = 1,
    exportSchema = false
)
abstract class BaseDatosFinanzas : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}

// ------------------------------------------------------------ Conversión modelo <-> entidad

fun Categoria.aEntidad(usuarioId: Long) = CategoriaEntidad(usuarioId, id, nombre, tipo.name, presupuesto)

fun CategoriaEntidad.aModelo() = Categoria(id, nombre, TipoMovimiento.valueOf(tipo), presupuesto)

fun Movimiento.aEntidad(usuarioId: Long) = MovimientoEntidad(
    usuarioId = usuarioId,
    id = id,
    descripcion = descripcion,
    monto = monto,
    tipo = tipo.name,
    categoriaId = categoriaId,
    fecha = fecha.toString(),
    medioPago = medioPago
)

fun MovimientoEntidad.aModelo() = Movimiento(
    id = id,
    descripcion = descripcion,
    monto = monto,
    tipo = TipoMovimiento.valueOf(tipo),
    categoriaId = categoriaId,
    fecha = LocalDateTime.parse(fecha),
    medioPago = medioPago
)
