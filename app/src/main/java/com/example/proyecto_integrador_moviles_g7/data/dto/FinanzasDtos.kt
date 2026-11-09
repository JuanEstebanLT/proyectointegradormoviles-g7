package com.example.proyecto_integrador_moviles_g7.data.dto

import com.squareup.moshi.Json

/** Objetos de transferencia con el mismo formato JSON (snake_case) que expone la Finanzas G7 API. */

data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String?,
    @Json(name = "saldo_inicial") val saldoInicial: Double,
    val rol: String = "USUARIO"
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

data class ErrorDto(
    val error: String
)
