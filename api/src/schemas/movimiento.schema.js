import { z } from 'zod';
import { fecha, id, noVacio, rangoFechas, tipoMovimiento } from './comunes.js';

const campos = {
  categoria_id: id,
  // Opcional: si no se envía se toma el tipo de la categoría.
  tipo: tipoMovimiento.optional(),
  monto: z.number({ error: 'Debe ser un número' }).positive('El monto debe ser mayor a cero').multipleOf(0.01),
  descripcion: z.string().trim().min(1, 'La descripción es obligatoria').max(200),
  medio_pago: z.string().trim().min(1).max(40).optional(),
  fecha_movimiento: fecha.optional()
};

export const crearMovimiento = z.object(campos);

export const actualizarMovimiento = noVacio(z.object(campos).partial());

export const filtrosMovimientos = rangoFechas.and(
  z.object({
    categoria_id: id.optional(),
    tipo: tipoMovimiento.optional(),
    pagina: z.coerce.number().int().min(1).default(1),
    limite: z.coerce.number().int().min(1).max(500).default(100)
  })
);
