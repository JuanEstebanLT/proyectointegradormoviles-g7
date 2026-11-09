import { HttpError } from './errors.js';

export const MAX_COP = 99_999_999_999;
const UUID_RE = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

export function uuid(value, label = 'id') {
  if (typeof value !== 'string' || !UUID_RE.test(value)) {
    throw new HttpError(400, `${label} debe ser un UUID válido`);
  }
  return value;
}

export function texto(value, label, { min = 1, max = 120, optional = false } = {}) {
  if (optional && (value === undefined || value === null)) return undefined;
  if (typeof value !== 'string') throw new HttpError(400, `${label} debe ser texto`);
  const result = value.trim();
  if (result.length < min || result.length > max) {
    throw new HttpError(400, `${label} debe tener entre ${min} y ${max} caracteres`);
  }
  return result;
}

export function dinero(value, label = 'monto', { permitirCero = false } = {}) {
  if (typeof value !== 'number' || !Number.isSafeInteger(value) || value > MAX_COP ||
      (permitirCero ? value < 0 : value <= 0)) {
    throw new HttpError(400, `${label} debe ser un entero válido en COP (máximo ${MAX_COP})`);
  }
  return value;
}

export function tipo(value) {
  if (value !== 'INGRESO' && value !== 'GASTO') {
    throw new HttpError(400, 'tipo debe ser INGRESO o GASTO');
  }
  return value;
}

export function fecha(value, label = 'fecha_movimiento') {
  if (typeof value !== 'string' || !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    throw new HttpError(400, `${label} debe ser AAAA-MM-DD`);
  }
  const date = new Date(`${value}T00:00:00.000Z`);
  if (!Number.isFinite(date.getTime()) || date.toISOString().slice(0, 10) !== value) {
    throw new HttpError(400, `${label} no es una fecha válida`);
  }
  return value;
}

export function fechaOpcional(value) {
  if (value === undefined || value === null || value === '') return undefined;
  return fecha(value);
}

export function paginacion(query) {
  const limit = Number(query.limit ?? 50);
  const offset = Number(query.offset ?? 0);
  if (!Number.isSafeInteger(limit) || limit < 1 || limit > 200 ||
      !Number.isSafeInteger(offset) || offset < 0) {
    throw new HttpError(400, 'limit debe estar entre 1 y 200 y offset ser >= 0');
  }
  return { limit, offset };
}

export function colorHex(value) {
  if (value === undefined) return undefined;
  if (typeof value !== 'string' || !/^#[A-Fa-f0-9]{6}$/.test(value)) {
    throw new HttpError(400, 'color_hex debe tener formato #RRGGBB');
  }
  return value;
}

export function booleano(value, label) {
  if (typeof value !== 'boolean') throw new HttpError(400, `${label} debe ser booleano`);
  return value;
}

export function opcionales(body, permitidos) {
  if (!body || typeof body !== 'object' || Array.isArray(body)) {
    throw new HttpError(400, 'El cuerpo JSON debe ser un objeto');
  }
  for (const key of Object.keys(body)) {
    if (!permitidos.includes(key)) throw new HttpError(400, `Campo no permitido: ${key}`);
  }
  return body;
}
