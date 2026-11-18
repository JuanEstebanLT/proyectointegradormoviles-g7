package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasRepository
import com.example.proyecto_integrador_moviles_g7.data.repository.ReglasFinanzas
import com.example.proyecto_integrador_moviles_g7.presentation.components.formatoCop
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SaldoInicialViewModel(
    private val repository: FinanzasRepository
) : ViewModel() {

    val saldoInicial: StateFlow<Long> = repository.state
        .map { it.saldoInicial }
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.state.value.saldoInicial)

    /** Devuelve null si se guardó, o el mensaje que explica por qué no. */
    fun establecerSaldo(monto: Long): String? {
        if (monto <= 0L) return "Ingresa un monto mayor a cero."
        val state = repository.state.value
        if (!ReglasFinanzas.saldoInicialPermitido(state, monto)) {
            return "Saldo insuficiente. Con tus movimientos actuales el saldo inicial debe ser de al menos " +
                "${formatoCop(ReglasFinanzas.saldoInicialMinimo(state))} COP; si no, tu saldo quedaría en negativo."
        }
        return if (repository.establecerSaldoInicial(monto)) null else "No se pudo guardar el saldo."
    }

    companion object {
        fun factory(repository: FinanzasRepository): ViewModelProvider.Factory =
            simpleViewModelFactory { SaldoInicialViewModel(repository) }
    }
}
