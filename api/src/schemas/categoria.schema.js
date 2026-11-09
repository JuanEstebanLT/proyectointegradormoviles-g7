import { z } from 'zod';
import { monto, noVacio, rangoFechas, tipoMovimiento } from './comunes.js';

const nombre = z.string().trim().min(1, 'El nombre es obligatorio').max(60);
const colorHex = z.string().regex(/^#[0-9A-Fa-f]{6}$/, 'Color esperado en formato #RRGGBB');

export const crearCategoria = z.object({
  nombre,
  tipo: tipoMovimiento,
  color_hex: colorHex.optional(),
  presupuesto_mensual: monto.optional()
});

// El tipo no se puede cambiar: rompería la consistencia con los movimientos ya registrados.
export const actualizarCategoria = noVacio(
  z.object({
    nombre: nombre.optional(),
    color_hex: colorHex.optional(),
    presupuesto_mensual: monto.optional()
  })
);

export const filtrosCategorias = rangoFechas.and(z.object({ tipo: tipoMovimiento.optional() }));
