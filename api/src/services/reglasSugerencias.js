/**
 * Reglas sencillas y explicables (HU-08). Cada regla recibe el resumen del mes
 * y la configuración del usuario, y devuelve:
 *   - false si no aplica
 *   - true si aplica
 *   - un arreglo de ids de categoría si aplica a categorías concretas
 * La clave debe coincidir con sugerencia.regla_asociada en la base de datos.
 */
const gastosConPresupuesto = (resumen) =>
  resumen.por_categoria.filter((c) => c.tipo === 'GASTO' && c.presupuesto_mensual > 0);

const idsOFalse = (categorias) => (categorias.length > 0 ? categorias.map((c) => c.categoria_id) : false);

export const reglas = {
  GASTOS_SUPERAN_INGRESOS: ({ periodo }) =>
    periodo.total_gastos > 0 && periodo.total_gastos > periodo.total_ingresos,

  PRESUPUESTO_EXCEDIDO: (resumen) =>
    idsOFalse(gastosConPresupuesto(resumen).filter((c) => c.total > c.presupuesto_mensual)),

  PRESUPUESTO_CERCANO: (resumen) =>
    idsOFalse(
      gastosConPresupuesto(resumen).filter((c) => c.uso_presupuesto >= 80 && c.total <= c.presupuesto_mensual)
    ),

  META_AHORRO_NO_CUMPLIDA: ({ periodo }, configuracion) => {
    const meta = Number(configuracion?.meta_ahorro_mensual ?? 0);
    return meta > 0 && periodo.balance < meta;
  },

  OCIO_ELEVADO: ({ por_categoria }) =>
    idsOFalse(por_categoria.filter((c) => c.tipo === 'GASTO' && /ocio/i.test(c.nombre) && c.participacion > 30)),

  SIN_INGRESOS_MES: ({ periodo }) => periodo.total_ingresos === 0,

  GENERAL: () => true
};
