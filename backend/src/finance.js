/** Cálculos puros: los montos son enteros en pesos colombianos (COP). */
export function calcularResumen(perfil, movimientos, categorias = [], { desde, hasta } = {}) {
  const total = (items, tipo) => items.filter((m) => m.tipo === tipo)
    .reduce((sum, m) => sum + Number(m.monto), 0);
  const saldoInicial = Number(perfil.saldo_inicial ?? 0);
  const ingresosHistoricos = total(movimientos, 'INGRESO');
  const gastosHistoricos = total(movimientos, 'GASTO');
  const dentroDelPeriodo = movimientos.filter((m) =>
    (!desde || m.fecha_movimiento >= desde) && (!hasta || m.fecha_movimiento <= hasta));
  const ingresos = total(dentroDelPeriodo, 'INGRESO');
  const gastos = total(dentroDelPeriodo, 'GASTO');
  const porCategoria = categorias.filter((c) => c.tipo === 'GASTO').map((categoria) => ({
    categoria_id: categoria.id,
    nombre: categoria.nombre,
    total: dentroDelPeriodo.filter((m) => m.tipo === 'GASTO' && m.categoria_id === categoria.id)
      .reduce((s, m) => s + Number(m.monto), 0),
  }));
  return {
    saldo_inicial: saldoInicial,
    saldo_disponible: saldoInicial + ingresosHistoricos - gastosHistoricos,
    ingresos: ingresos,
    gastos: gastos,
    ingresos_historicos: ingresosHistoricos,
    gastos_historicos: gastosHistoricos,
    total_movimientos: dentroDelPeriodo.length,
    desde: desde ?? null,
    hasta: hasta ?? null,
    gastos_por_categoria: porCategoria,
  };
}

export function generarSugerencias(resumen) {
  const sugerencias = [];
  if (resumen.gastos > resumen.ingresos && resumen.gastos > 0) {
    sugerencias.push({ id: 'control-gastos', titulo: 'Revisa tus gastos',
      mensaje: 'En el período elegido, tus gastos superan tus ingresos. Revisa los gastos no esenciales.', prioridad: 1 });
  }
  const maximo = [...resumen.gastos_por_categoria].sort((a, b) => b.total - a.total)[0];
  if (maximo && resumen.gastos > 0 && maximo.total / resumen.gastos >= 0.45) {
    sugerencias.push({ id: 'concentracion-categoria', titulo: 'Diversifica tus gastos',
      mensaje: `La categoría ${maximo.nombre} concentra una parte importante de tus gastos. Revisa su presupuesto.`, prioridad: 2 });
  }
  if (resumen.ingresos > resumen.gastos && resumen.ingresos > 0) {
    sugerencias.push({ id: 'habito-ahorro', titulo: 'Considera reservar una parte',
      mensaje: 'Tienes un excedente entre ingresos y gastos. Puedes destinar una parte a tu meta de ahorro.', prioridad: 3 });
  }
  if (!sugerencias.length) {
    sugerencias.push({ id: 'registro-constante', titulo: 'Mantén tus registros',
      mensaje: 'Registra tus movimientos para obtener un panorama más claro de tus finanzas.', prioridad: 3 });
  }
  return sugerencias;
}
