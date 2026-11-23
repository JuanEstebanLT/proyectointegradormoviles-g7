package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_integrador_moviles_g7.data.repository.ApiFinanzasRepository
import com.example.proyecto_integrador_moviles_g7.data.repository.AuthRepository
import com.example.proyecto_integrador_moviles_g7.data.repository.CuentaRepository
import com.example.proyecto_integrador_moviles_g7.presentation.notificaciones.NotificadorSistema
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * Conserva los repositorios de la sesión durante la vida de la Activity, incluso ante
 * cambios de configuración. Los datos viven en la Finanzas G7 API.
 * También publica en la barra de notificaciones las alertas nuevas mientras la app está abierta
 * o en segundo plano.
 */
@OptIn(FlowPreview::class)
class FinanzasSessionViewModel(application: Application) : AndroidViewModel(application) {
    val repository = ApiFinanzasRepository(viewModelScope)
    val cuenta = CuentaRepository()
    private val authRepository = AuthRepository()
    val notificador = NotificadorSistema(application)

    init {
        viewModelScope.launch {
            combine(repository.state, cuenta.metaAhorro, repository.cargando, cuenta.usuario) { state, meta, cargando, usuario ->
                // Solo con sesión iniciada y los datos ya descargados
                if (usuario == null || cargando) null
                else usuario.id to AnalisisFinanciero.notificaciones(state, meta)
            }
                // Espera a que terminen las actualizaciones optimistas antes de avisar
                .debounce(1_500L)
                .collect { resultado ->
                    resultado?.let { (usuarioId, notificaciones) -> notificador.publicarNuevas(usuarioId, notificaciones) }
                }
        }
    }

    /** Publica las alertas actuales (por ejemplo, justo después de que el usuario da el permiso). */
    fun revisarNotificaciones() {
        val usuario = cuenta.usuario.value ?: return
        notificador.publicarNuevas(usuario.id, AnalisisFinanciero.notificaciones(repository.state.value, cuenta.metaAhorro.value))
    }

    /** Descarga los datos del usuario recién autenticado. */
    fun alIniciarSesion() {
        cuenta.sincronizarUsuario()
        repository.recargar()
        viewModelScope.launch { cuenta.cargarConfiguracion() }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
        repository.limpiar()
        cuenta.limpiar()
    }
}
