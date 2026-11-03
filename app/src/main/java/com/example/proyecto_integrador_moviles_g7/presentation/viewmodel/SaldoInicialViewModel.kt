package com.example.proyecto_integrador_moviles_g7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SaldoInicialViewModel : ViewModel() {

    // Saldo inicial modificable
    private val _saldoInicial = MutableStateFlow(0.0)

    // Saldo observable desde la interfaz
    val saldoInicial: StateFlow<Double> =
        _saldoInicial.asStateFlow()

    // Establecer el saldo inicial
    fun establecerSaldo(monto: Double) {
        if (monto >= 0) {
            _saldoInicial.value = monto
        }
    }

    // Actualizar el saldo sin permitir valores negativos
    fun actualizarSaldo(monto: Double) {
        val nuevoSaldo = _saldoInicial.value + monto

        if (nuevoSaldo >= 0) {
            _saldoInicial.value = nuevoSaldo
        }
    }
}
