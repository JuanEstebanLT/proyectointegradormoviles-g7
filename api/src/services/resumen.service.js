import { supabase } from '../config/supabase.js';
import { obtenerUno, verificar } from '../utils/errores.js';

const redondear = (valor) => Math.round(valor * 100) / 100;
const porcentaje = (parte, total) => (total > 0 ? redondear((parte / total) * 100) : 0);

async function totalesPorCategoria(usuarioId, desde = null, hasta = null) {
  const filas = verificar(
    await supabase.rpc('totales_por_categoria', { p_usuario_id: usuarioId, p_desde: desde, p_hasta: hasta })
  );
  return filas.map((fila) => ({ ...fila, total: Number(fila.total), cantidad: Number(fila.cantidad) }));
}

function sumarPorTipo(filas) {
  const suma = (tipo) => redondear(filas.filter((f) => f.tipo === tipo).reduce((acc, f) => acc + f.total, 0));
  return { ingresos: suma('INGRESO'), gastos: suma('GASTO') };
}

/**
 * Saldo disponible (histórico) + totales del período + desglose por categoría.
 * Sin desde/hasta, el período abarca todos los movimientos.
 */
export async function calcularResumen(usuarioId, { desde, hasta } = {}) {
  const [usuario, categorias, historico, periodo] = await Promise.all([
    obtenerUno(supabase.from('usuario').select('saldo_inicial').eq('id', usuarioId), 'Usuario'),
    supabase
      .from('categoria')
      .select('id, nombre, tipo, color_hex, presupuesto_mensual')
      .eq('usuario_id', usuarioId)
      .order('nombre')
      .then(verificar),
    totalesPorCategoria(usuarioId),
    desde || hasta ? totalesPorCategoria(usuarioId, desde, hasta) : null
  ]);

  const filasPeriodo = periodo ?? historico;
  const totalHistorico = sumarPorTipo(historico);
  const totalPeriodo = sumarPorTipo(filasPeriodo);
  const saldoInicial = Number(usuario.saldo_inicial);

  const porCategoria = categorias.map((categoria) => {
    const fila = filasPeriodo.find((f) => f.categoria_id === categoria.id);
    const total = fila?.total ?? 0;
    const presupuesto = Number(categoria.presupuesto_mensual);
    const totalDelTipo = categoria.tipo === 'GASTO' ? totalPeriodo.gastos : totalPeriodo.ingresos;
    return {
      categoria_id: categoria.id,
      nombre: categoria.nombre,
      tipo: categoria.tipo,
      color_hex: categoria.color_hex,
      presupuesto_mensual: presupuesto,
      total,
      cantidad_movimientos: fila?.cantidad ?? 0,
      participacion: porcentaje(total, totalDelTipo),
      uso_presupuesto: presupuesto > 0 ? porcentaje(total, presupuesto) : null
    };
  });

  return {
    saldo_inicial: saldoInicial,
    saldo_disponible: redondear(saldoInicial + totalHistorico.ingresos - totalHistorico.gastos),
    periodo: {
      desde: desde ?? null,
      hasta: hasta ?? null,
      total_ingresos: totalPeriodo.ingresos,
      total_gastos: totalPeriodo.gastos,
      balance: redondear(totalPeriodo.ingresos - totalPeriodo.gastos)
    },
    por_categoria: porCategoria
  };
}
