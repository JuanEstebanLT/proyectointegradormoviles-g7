import { HttpError } from '../utils/errores.js';

export function rutaNoEncontrada(req, res) {
  res.status(404).json({ error: `Ruta ${req.method} ${req.originalUrl} no encontrada` });
}

export function manejadorErrores(err, _req, res, _next) {
  if (err.type === 'entity.parse.failed') {
    return res.status(400).json({ error: 'El cuerpo de la petición no es un JSON válido' });
  }

  const status = err instanceof HttpError ? err.status : 500;
  if (status >= 500) console.error(err.cause ?? err);

  res.status(status).json({
    error: status >= 500 ? 'Error interno del servidor' : err.message,
    ...(err.detalles && { detalles: err.detalles })
  });
}
