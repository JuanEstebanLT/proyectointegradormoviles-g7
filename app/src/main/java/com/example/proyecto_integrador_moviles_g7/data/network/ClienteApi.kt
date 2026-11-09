package com.example.proyecto_integrador_moviles_g7.data.network

import com.example.proyecto_integrador_moviles_g7.BuildConfig
import com.example.proyecto_integrador_moviles_g7.data.api.FinanzasApiService
import com.example.proyecto_integrador_moviles_g7.data.dto.ErrorDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Construye una única instancia de Retrofit apuntando a BuildConfig.API_BASE_URL. */
object ClienteApi {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttp: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            val peticion = cadena.request()
            val token = SesionActual.token
            cadena.proceed(
                if (token == null) peticion
                else peticion.newBuilder().header("Authorization", "Bearer $token").build()
            )
        }
        .apply {
            // El cuerpo (y el token) solo se registra en compilaciones de depuración
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            }
        }
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val servicio: FinanzasApiService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttp)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(FinanzasApiService::class.java)

    /** Convierte cualquier fallo de red o de la API en un mensaje comprensible. */
    fun mensajeDeError(error: Throwable): String = when (error) {
        is HttpException -> {
            val cuerpo = error.response()?.errorBody()?.string()
            val mensaje = cuerpo?.let {
                runCatching { moshi.adapter(ErrorDto::class.java).fromJson(it)?.error }.getOrNull()
            }
            when {
                mensaje != null -> mensaje
                error.code() == 401 -> "Tu sesión expiró. Vuelve a iniciar sesión."
                else -> "Error del servidor (${error.code()})"
            }
        }
        is IOException -> "No se pudo conectar con el servidor. Verifica que la API esté encendida y que estés en la misma red."
        else -> error.message ?: "Error inesperado"
    }
}
