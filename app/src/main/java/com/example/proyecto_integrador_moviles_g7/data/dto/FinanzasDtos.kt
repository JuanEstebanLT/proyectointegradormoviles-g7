package com.example.proyecto_integrador_moviles_g7.data.dto

import com.squareup.moshi.Json

/** Objetos de transferencia con el mismo formato JSON (snake_case) que expone la Finanzas G7 API. */

data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String?,
    @Json(name = "saldo_inicial") val saldoInicial: Double,
    val rol: String = "USUARIO",
    @Json(name = "creado_en") val creadoEn: String? = null
)

data class SesionDto(
    val usuario: UsuarioDto,
    val token: String
)

data class InicioSesionDto(
    val correo: String,
    val contrasena: String
)

data class RegistroUsuarioDto(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val telefono: String?
)

data class ActualizarUsuarioDto(
    @Json(name = "saldo_inicial") val saldoInicial: Double
)

data class CategoriaDto(
    val id: Long,
    val nombre: String,
    val tipo: String,
    @Json(name = "color_hex") val colorHex: String?,
    @Json(name = "presupuesto_mensual") val presupuestoMensual: Double
)

data class MovimientoDto(
    val id: Long,
    @Json(name = "categoria_id") val categoriaId: Long,
    val tipo: String,
    val monto: Double,
    val descripcion: String,
    @Json(name = "medio_pago") val medioPago: String,
    @Json(name = "fecha_movimiento") val fechaMovimiento: String,
    @Json(name = "creado_en") val creadoEn: String
)

data class CrearMovimientoDto(
    @Json(name = "categoria_id") val categoriaId: Long,
    val tipo: String,
    val monto: Double,
    val descripcion: String,
    @Json(name = "medio_pago") val medioPago: String,
    @Json(name = "fecha_movimiento") val fechaMovimiento: String
)

/** PUT parcial: Moshi omite los campos null, así que solo se envía lo que cambia. */
data class ActualizarMovimientoDto(
    @Json(name = "categoria_id") val categoriaId: Long? = null,
    val monto: Double? = null,
    val descripcion: String? = null
)

data class CrearCategoriaDto(
    val nombre: String,
    val tipo: String,
    @Json(name = "presupuesto_mensual") val presupuestoMensual: Double
)

/** El tipo de una categoría no se puede cambiar (la API lo rechaza). */
data class ActualizarCategoriaDto(
    val nombre: String,
    @Json(name = "presupuesto_mensual") val presupuestoMensual: Double
)

data class ErrorDto(
    val error: String
)

/** PUT /usuarios/:id con los datos del perfil (el correo no se puede cambiar). */
data class ActualizarPerfilDto(
    val nombre: String,
    val telefono: String
)

data class ConfiguracionDto(
    @Json(name = "meta_ahorro_mensual") val metaAhorroMensual: Double = 0.0,
    @Json(name = "notificaciones_activas") val notificacionesActivas: Boolean = true
)

data class ActualizarConfiguracionDto(
    @Json(name = "meta_ahorro_mensual") val metaAhorroMensual: Double
)

data class CategoriaSugerenciaDto(
    val id: Long,
    val nombre: String
)

data class SugerenciaDto(
    val id: Long,
    val titulo: String,
    val mensaje: String,
    val prioridad: Int = 0,
    val categorias: List<CategoriaSugerenciaDto> = emptyList()
)
