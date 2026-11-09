export class HttpError extends Error {
  constructor(status, mensaje, detalles) {
    super(mensaje);
    this.status = status;
    this.detalles = detalles;
  }
}

// Códigos de PostgreSQL / PostgREST más comunes y su equivalente HTTP
const CODIGOS_SUPABASE = {
  '23505': [409, 'Ya existe un registro con esos datos'],
  '23503': [409, 'El registro está relacionado con otros datos'],
  '23514': [400, 'Los datos no cumplen las restricciones'],
  '22P02': [400, 'Formato de dato inválido'],
  P0001: [400, null], // excepciones lanzadas por los triggers: se usa su propio mensaje
  PGRST116: [404, 'Recurso no encontrado']
};

export function desdeSupabase(error) {
  const [status, mensaje] = CODIGOS_SUPABASE[error.code] ?? [500, 'Error al consultar la base de datos'];
  const httpError = new HttpError(status, mensaje ?? error.message, status === 500 ? undefined : error.details);
  httpError.cause = error;
  return httpError;
}

/** Devuelve `data` de una respuesta de Supabase o lanza el error traducido a HTTP. */
export function verificar({ data, error }) {
  if (error) throw desdeSupabase(error);
  return data;
}

/** Ejecuta una consulta que debe devolver una fila; lanza 404 si no existe. */
export async function obtenerUno(consulta, recurso) {
  const fila = verificar(await consulta.maybeSingle());
  if (!fila) throw new HttpError(404, `${recurso} no encontrado`);
  return fila;
}
