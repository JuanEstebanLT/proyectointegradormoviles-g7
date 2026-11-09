package com.example.proyecto_integrador_moviles_g7.data.api

import com.example.proyecto_integrador_moviles_g7.data.dto.ActualizarUsuarioDto
import com.example.proyecto_integrador_moviles_g7.data.dto.CategoriaDto
import com.example.proyecto_integrador_moviles_g7.data.dto.CrearMovimientoDto
import com.example.proyecto_integrador_moviles_g7.data.dto.InicioSesionDto
import com.example.proyecto_integrador_moviles_g7.data.dto.MovimientoDto
import com.example.proyecto_integrador_moviles_g7.data.dto.RegistroUsuarioDto
import com.example.proyecto_integrador_moviles_g7.data.dto.SesionDto
import com.example.proyecto_integrador_moviles_g7.data.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Endpoints de la Finanzas G7 API (/api/v1). El token se agrega en el interceptor. */
interface FinanzasApiService {

    @POST("usuarios/sesion")
    suspend fun iniciarSesion(@Body datos: InicioSesionDto): SesionDto

    @POST("usuarios")
    suspend fun registrar(@Body datos: RegistroUsuarioDto): SesionDto

    @GET("usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Long): UsuarioDto

    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body datos: ActualizarUsuarioDto): UsuarioDto

    @GET("categorias")
    suspend fun listarCategorias(): List<CategoriaDto>

    @GET("movimientos")
    suspend fun listarMovimientos(@Query("limite") limite: Int = 500): List<MovimientoDto>

    @POST("movimientos")
    suspend fun crearMovimiento(@Body datos: CrearMovimientoDto): MovimientoDto

    @DELETE("movimientos/{id}")
    suspend fun eliminarMovimiento(@Path("id") id: Long): Response<Unit>
}
