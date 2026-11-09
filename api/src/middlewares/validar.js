import { HttpError } from '../utils/errores.js';

/**
 * Valida req[origen] con un esquema de Zod y guarda el resultado limpio
 * en req.validado[origen] (en Express 5 req.query es de solo lectura).
 */
export const validar = (esquema, origen = 'body') => (req, _res, next) => {
  const resultado = esquema.safeParse(req[origen] ?? {});
  if (!resultado.success) {
    const detalles = resultado.error.issues.map((issue) => ({
      campo: issue.path.join('.'),
      mensaje: issue.message
    }));
    throw new HttpError(400, 'Datos inválidos', detalles);
  }
  req.validado = { ...req.validado, [origen]: resultado.data };
  next();
};
