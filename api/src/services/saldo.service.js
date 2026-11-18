import { HttpError } from '../utils/errores.js';
import { calcularResumen } from './resumen.service.js';

const formatoCop = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 });

/**
 * El saldo disponible (saldo inicial + ingresos - gastos) no puede quedar en negativo.
 * Si ya lo estaba (datos anteriores a esta regla), solo se permiten cambios que lo mejoren.
 * @param {number} variacion cuánto cambia el saldo con la operación (negativo = lo reduce)
 * @param {string} mensaje motivo que se muestra al usuario si se rechaza
 */
export async function verificarSaldo(usuarioId, variacion, mensaje) {
  if (variacion >= 0) return;
  const { saldo_disponible: saldoActual } = await calcularResumen(usuarioId);
  const saldoNuevo = saldoActual + variacion;
  if (saldoNuevo < 0 && saldoNuevo < saldoActual) {
    throw new HttpError(
      422,
      `${mensaje} Saldo disponible: ${formatoCop.format(saldoActual)}; quedaría en ${formatoCop.format(saldoNuevo)}.`
    );
  }
}
