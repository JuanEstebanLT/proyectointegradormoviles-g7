package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_integrador_moviles_g7.data.model.Categoria
import com.example.proyecto_integrador_moviles_g7.data.model.Movimiento
import com.example.proyecto_integrador_moviles_g7.data.model.Sugerencia
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withTimeoutOrNull
import java.time.LocalDateTime
import com.example.proyecto_integrador_moviles_g7.data.model.Usuario
import com.example.proyecto_integrador_moviles_g7.data.repository.CuentaRepository
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasRepository
import com.example.proyecto_integrador_moviles_g7.data.repository.ReglasFinanzas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

// ---------------------------------------------------------------- Informes

data class InformesUiState(
    val mes: YearMonth = YearMonth.now(),
    val resumen: ResumenMes = ResumenMes(YearMonth.now(), 0L, 0L, emptyList(), emptyList()),
    val historico: List<ResumenMes> = emptyList(),
    val saldoInicial: Long = 0L,
    val saldoDisponible: Long = 0L,
    val puedeAvanzar: Boolean = false
)

class InformesViewModel(repository: FinanzasRepository) : ViewModel() {
    private val mes = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<InformesUiState> = combine(repository.state, mes) { state, mesActual ->
        InformesUiState(
            mes = mesActual,
            resumen = AnalisisFinanciero.resumenMes(state, mesActual),
            historico = AnalisisFinanciero.historico(state, mesActual),
            saldoInicial = state.saldoInicial,
            saldoDisponible = ReglasFinanzas.saldoDisponible(state),
            puedeAvanzar = mesActual < YearMonth.now()
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, InformesUiState())

    fun mesAnterior() = mes.update { it.minusMonths(1) }

    fun mesSiguiente() = mes.update { if (it < YearMonth.now()) it.plusMonths(1) else it }

    companion object {
        fun factory(repository: FinanzasRepository): ViewModelProvider.Factory =
            simpleViewModelFactory { InformesViewModel(repository) }
    }
}

// ---------------------------------------------------------------- Ahorro

data class AhorroUiState(
    val metaAhorro: Long = 0L,
    val mesActual: ResumenMes = ResumenMes(YearMonth.now(), 0L, 0L, emptyList(), emptyList()),
    val sugerencias: List<Sugerencia> = emptyList(),
    val cargandoSugerencias: Boolean = false,
    val errorSugerencias: String? = null,
    val guardando: Boolean = false,
    val abonando: Boolean = false,
    val saldoDisponible: Long = 0L,
    /** Abonos a la meta hechos este mes, del más reciente al más antiguo. */
    val abonosDelMes: List<Movimiento> = emptyList(),
    /** Todo lo abonado a la meta desde el inicio. */
    val ahorroAcumulado: Long = 0L
) {
    /** Progreso de 0 a 1 hacia la meta del mes. */
    val progreso: Float
        get() = if (metaAhorro > 0L) (mesActual.ahorro.toFloat() / metaAhorro).coerceIn(0f, 1f) else 0f

    val faltante: Long get() = (metaAhorro - mesActual.ahorro).coerceAtLeast(0L)
}

class AhorroViewModel(
    private val repository: FinanzasRepository,
    private val cuenta: CuentaRepository
) : ViewModel() {
    private val local = MutableStateFlow(AhorroUiState())

    val uiState: StateFlow<AhorroUiState> = combine(
        repository.state, cuenta.metaAhorro, local
    ) { state, meta, l ->
        val abonos = AnalisisFinanciero.abonos(state)
        l.copy(
            metaAhorro = meta,
            mesActual = AnalisisFinanciero.resumenMes(state, YearMonth.now()),
            saldoDisponible = ReglasFinanzas.saldoDisponible(state),
            abonosDelMes = abonos.filter { YearMonth.from(it.fecha) == YearMonth.now() },
            ahorroAcumulado = abonos.sumOf { it.monto }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AhorroUiState())

    /**
     * Abona [monto] a la meta: se registra como un movimiento en la categoría "Ahorro"
     * y por eso se descuenta del saldo disponible. Llama a [alTerminar] con null si
     * salió bien o con el mensaje de error.
     */
    fun abonar(monto: Long, alTerminar: (String?) -> Unit) {
        val state = repository.state.value
        when {
            monto <= 0L -> return alTerminar("Ingresa un monto mayor a cero.")
            !ReglasFinanzas.altaPermitida(state, TipoMovimiento.GASTO, monto) -> {
                val disponible = ReglasFinanzas.saldoDisponible(state).coerceAtLeast(0L)
                return alTerminar("Saldo insuficiente. Puedes abonar como máximo ${formatoCop(disponible)} COP.")
            }
            local.value.abonando -> return
        }
        local.update { it.copy(abonando = true) }
        viewModelScope.launch {
            val error = try {
                val categoria = asegurarCategoriaAhorro()
                if (categoria == null) {
                    "No se pudo preparar la categoría \"${AnalisisFinanciero.NOMBRE_CATEGORIA_AHORRO}\". " +
                        "Si ya tienes una categoría de ingreso con ese nombre, cámbiale el nombre."
                } else {
                    OperacionesMovimiento.registrar(
                        repository, "Abono a meta de ahorro", monto, TipoMovimiento.GASTO,
                        categoria.id, LocalDateTime.now(), "Ahorro"
                    )
                }
            } finally {
                local.update { it.copy(abonando = false) }
            }
            alTerminar(error)
        }
    }

    /** Retira un abono: el dinero vuelve al saldo disponible. Devuelve null si se aplicó. */
    fun retirarAbono(id: String): String? = OperacionesMovimiento.eliminar(repository, id)

    /** Devuelve la categoría de ahorro; si no existe la crea y espera a que la API la confirme. */
    private suspend fun asegurarCategoriaAhorro(): Categoria? {
        AnalisisFinanciero.categoriaAhorro(repository.state.value)?.let { return it }
        if (!repository.crearCategoria(AnalisisFinanciero.NOMBRE_CATEGORIA_AHORRO, TipoMovimiento.GASTO, 0L)) return null
        return withTimeoutOrNull(10_000L) {
            repository.state.mapNotNull { AnalisisFinanciero.categoriaAhorro(it) }.first()
        }
    }

    init {
        cargarSugerencias()
        viewModelScope.launch { cuenta.cargarConfiguracion() }
    }

    fun cargarSugerencias() {
        local.update { it.copy(cargandoSugerencias = true, errorSugerencias = null) }
        viewModelScope.launch {
            cuenta.sugerencias().fold(
                onSuccess = { lista -> local.update { it.copy(cargandoSugerencias = false, sugerencias = lista) } },
                onFailure = { e -> local.update { it.copy(cargandoSugerencias = false, errorSugerencias = e.message) } }
            )
        }
    }

    /** Guarda la meta y llama a [alTerminar] con null si salió bien o con el mensaje de error. */
    fun guardarMeta(meta: Long, alTerminar: (String?) -> Unit) {
        if (meta < 0L) {
            alTerminar("La meta no puede ser negativa.")
            return
        }
        local.update { it.copy(guardando = true) }
        viewModelScope.launch {
            val resultado = cuenta.guardarMetaAhorro(meta)
            local.update { it.copy(guardando = false) }
            alTerminar(resultado.exceptionOrNull()?.message)
            // Las sugerencias dependen de la meta
            if (resultado.isSuccess) cargarSugerencias()
        }
    }

    companion object {
        fun factory(repository: FinanzasRepository, cuenta: CuentaRepository): ViewModelProvider.Factory =
            simpleViewModelFactory { AhorroViewModel(repository, cuenta) }
    }
}

// ---------------------------------------------------------------- Perfil

data class PerfilUiState(
    val usuario: Usuario? = null,
    val metaAhorro: Long = 0L,
    val saldoInicial: Long = 0L,
    val guardando: Boolean = false,
    val error: String? = null
)

class PerfilViewModel(
    repository: FinanzasRepository,
    private val cuenta: CuentaRepository
) : ViewModel() {
    private val local = MutableStateFlow(PerfilUiState())

    val uiState: StateFlow<PerfilUiState> = combine(
        cuenta.usuario, cuenta.metaAhorro, repository.state, local
    ) { usuario, meta, state, l ->
        l.copy(usuario = usuario, metaAhorro = meta, saldoInicial = state.saldoInicial)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PerfilUiState(usuario = cuenta.usuario.value))

    /** Valida y guarda nombre y teléfono; llama a [alGuardar] solo si la API lo aceptó. */
    fun guardar(nombre: String, telefono: String, alGuardar: () -> Unit) {
        val error = when {
            nombre.isBlank() -> "Ingresa tu nombre."
            nombre.trim().length > 100 -> "El nombre es demasiado largo."
            telefono.trim().length > 20 -> "El teléfono puede tener máximo 20 caracteres."
            else -> null
        }
        if (error != null) {
            local.update { it.copy(error = error) }
            return
        }
        local.update { it.copy(guardando = true, error = null) }
        viewModelScope.launch {
            cuenta.actualizarPerfil(nombre, telefono).fold(
                onSuccess = {
                    local.update { it.copy(guardando = false) }
                    alGuardar()
                },
                onFailure = { e -> local.update { it.copy(guardando = false, error = e.message) } }
            )
        }
    }

    fun limpiarError() = local.update { it.copy(error = null) }

    companion object {
        fun factory(repository: FinanzasRepository, cuenta: CuentaRepository): ViewModelProvider.Factory =
            simpleViewModelFactory { PerfilViewModel(repository, cuenta) }
    }
}

// ---------------------------------------------------------------- Notificaciones

data class NotificacionesUiState(
    val notificaciones: List<Notificacion> = emptyList(),
    val leidas: Set<String> = emptySet()
) {
    val sinLeer: Int get() = notificaciones.count { it.clave !in leidas }
}

class NotificacionesViewModel(
    repository: FinanzasRepository,
    private val cuenta: CuentaRepository
) : ViewModel() {
    val uiState: StateFlow<NotificacionesUiState> = combine(
        repository.state, cuenta.metaAhorro, cuenta.notificacionesLeidas
    ) { state, meta, leidas ->
        NotificacionesUiState(AnalisisFinanciero.notificaciones(state, meta), leidas)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, NotificacionesUiState())

    fun marcarTodasLeidas() = cuenta.marcarLeidas(uiState.value.notificaciones.map { it.clave })

    fun marcarLeida(clave: String) = cuenta.marcarLeidas(listOf(clave))

    companion object {
        fun factory(repository: FinanzasRepository, cuenta: CuentaRepository): ViewModelProvider.Factory =
            simpleViewModelFactory { NotificacionesViewModel(repository, cuenta) }
    }
}

// ---------------------------------------------------------------- Administración

data class AdminUiState(
    val usuarios: List<Usuario> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
) {
    val administradores: Int get() = usuarios.count { it.esAdmin }

    val nuevosEsteMes: Int
        get() = usuarios.count { u -> u.miembroDesde?.let { YearMonth.from(it) == YearMonth.now() } == true }
}

class AdminViewModel(private val cuenta: CuentaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        recargar()
    }

    fun recargar() {
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            cuenta.usuarios().fold(
                onSuccess = { lista -> _uiState.update { it.copy(cargando = false, usuarios = lista) } },
                onFailure = { e -> _uiState.update { it.copy(cargando = false, error = e.message) } }
            )
        }
    }

    companion object {
        fun factory(cuenta: CuentaRepository): ViewModelProvider.Factory =
            simpleViewModelFactory { AdminViewModel(cuenta) }
    }
}
