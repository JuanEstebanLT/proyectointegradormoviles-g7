import { z } from 'zod';

export const id = z.coerce.number().int().positive();
export const paramsId = z.object({ id });

export const monto = z.number({ error: 'Debe ser un número' }).nonnegative().multipleOf(0.01);
export const fecha = z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'Formato esperado: YYYY-MM-DD');
export const tipoMovimiento = z.enum(['INGRESO', 'GASTO']);

export const rangoFechas = z
  .object({
    usuario_id: id.optional(),
    desde: fecha.optional(),
    hasta: fecha.optional()
  })
  .refine((q) => !q.desde || !q.hasta || q.desde <= q.hasta, {
    message: '"desde" no puede ser posterior a "hasta"',
    path: ['desde']
  });

/** Para PUT parciales: exige al menos un campo. */
export const noVacio = (esquema) =>
  esquema.refine((datos) => Object.keys(datos).length > 0, {
    message: 'Debes enviar al menos un campo para actualizar'
  });
