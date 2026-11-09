import { HttpError } from './errors.js';

function configuracion() {
  const url = (process.env.SUPABASE_URL ?? '').replace(/\/$/, '');
  const key = process.env.SUPABASE_PUBLISHABLE_KEY ?? '';
  if (!/^https:\/\/[^/]+$/.test(url) || !key || key.includes('REEMPLAZAR')) {
    throw new HttpError(503, 'Falta configurar SUPABASE_URL o SUPABASE_PUBLISHABLE_KEY');
  }
  return { url, key };
}

/** Supabase REST: apikey público + JWT del usuario preservan el RLS. */
export async function supabaseRequest(path, { method = 'GET', token, query = {}, body, prefer } = {}) {
  const { url, key } = configuracion();
  const target = new URL(url + path);
  for (const [name, value] of Object.entries(query)) {
    if (value !== undefined && value !== null) target.searchParams.set(name, String(value));
  }
  const headers = { apikey: key, Accept: 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (prefer) headers.Prefer = prefer;
  let response;
  try {
    response = await fetch(target, {
      method, headers, body: body === undefined ? undefined : JSON.stringify(body),
      signal: AbortSignal.timeout(12000),
    });
  } catch {
    throw new HttpError(502, 'No fue posible comunicarse con Supabase');
  }
  const raw = await response.text();
  let data = null;
  if (raw) {
    try { data = JSON.parse(raw); } catch { throw new HttpError(502, 'Respuesta inválida de Supabase'); }
  }
  if (!response.ok) {
    const code = data?.code;
    const status = code === '23503' || code === '23505' ? 409 :
      response.status === 401 || response.status === 403 ? response.status :
      response.status === 404 ? 404 : response.status >= 500 ? 502 : 400;
    throw new HttpError(status, status >= 500 ? 'Error del servicio de datos' :
      data?.msg || data?.message || data?.error_description || 'Operación rechazada');
  }
  return data;
}

export const datos = (tabla, options) => supabaseRequest(`/rest/v1/${tabla}`, options);

export async function obtenerUno(tabla, token, filtros) {
  const rows = await datos(tabla, { token, query: { select: '*', ...filtros, limit: 1 } });
  return rows?.[0] ?? null;
}

export async function leerTodos(tabla, token, filtros = {}) {
  const MAX_FILAS = 50000;
  const rows = [];
  for (let offset = 0; offset < MAX_FILAS; offset += 1000) {
    const batch = await datos(tabla, { token, query: { select: '*', ...filtros, limit: 1000, offset } });
    rows.push(...(batch ?? []));
    if (!batch || batch.length < 1000) return rows;
  }
  throw new HttpError(413, 'La consulta supera el máximo permitido; utiliza filtros');
}

export async function verificarUsuario(token) {
  const user = await supabaseRequest('/auth/v1/user', { token });
  if (!user?.id) throw new HttpError(401, 'Sesión inválida');
  return user;
}

/** Crea datos iniciales usando el token personal y RLS, sin service_role. */
export async function prepararPerfil(user, token) {
  let perfil = await obtenerUno('usuarios', token, { id: `eq.${user.id}` });
  if (!perfil) {
    const candidato = String(user.user_metadata?.nombre ?? user.email?.split('@')[0] ?? 'Usuario').trim();
    const nombre = candidato.length >= 2 && candidato.length <= 120 ? candidato : 'Usuario';
    const rows = await datos('usuarios', { method: 'POST', token, body: { id: user.id, nombre },
      prefer: 'return=representation' });
    perfil = rows?.[0];
  }
  const configuracion = await obtenerUno('configuraciones', token, { usuario_id: `eq.${user.id}` });
  if (!configuracion) {
    await datos('configuraciones', { method: 'POST', token,
      body: { usuario_id: user.id }, prefer: 'return=minimal' });
  }
  const categorias = await datos('categorias', { token,
    query: { select: 'id', usuario_id: `eq.${user.id}`, limit: 1 } });
  if (!categorias?.length) {
    await datos('categorias', { method: 'POST', token, prefer: 'return=minimal', body: [
      { usuario_id: user.id, nombre: 'Ingresos', tipo: 'INGRESO', es_predeterminada: true },
      { usuario_id: user.id, nombre: 'Vivienda', tipo: 'GASTO', es_predeterminada: true },
      { usuario_id: user.id, nombre: 'Alimentación', tipo: 'GASTO', es_predeterminada: true },
      { usuario_id: user.id, nombre: 'Transporte', tipo: 'GASTO', es_predeterminada: true },
      { usuario_id: user.id, nombre: 'Ocio', tipo: 'GASTO', es_predeterminada: true },
    ] });
  }
  return perfil;
}
