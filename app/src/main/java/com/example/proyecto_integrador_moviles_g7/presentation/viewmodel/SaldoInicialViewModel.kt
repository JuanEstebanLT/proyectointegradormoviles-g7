package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_integrador_moviles_g7.data.repository.FinanzasRepository
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

    fun establecerSaldo(monto: Long): Boolean = repository.establecerSaldoInicial(monto)

    companion object {
        fun factory(repository: FinanzasRepository): ViewModelProvider.Factory =
            simpleViewModelFactory { SaldoInicialViewModel(repository) }
    }
}
