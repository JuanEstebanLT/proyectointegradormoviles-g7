import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import { rateLimit } from 'express-rate-limit';
import { HttpError, asyncRoute } from './errors.js';
import { uuid, texto, dinero, tipo, fecha, fechaOpcional, paginacion, colorHex, booleano, opcionales } from './validation.js';
import { supabaseRequest, datos, obtenerUno, leerTodos, verificarUsuario, prepararPerfil } from './supabase.js';
import { calcularResumen, generarSugerencias } from './finance.js';

const ruta = asyncRoute;
const hoy = () => new Date().toISOString().slice(0, 10);
const porId = (id) => `eq.${uuid(id)}`;

function exigirCampos(body, campos) {
  for (const campo of campos) if (body[campo] === undefined) {
    throw new HttpError(400, `Falta el campo ${campo}`);
  }
}
function correoValido(value) {
  const email = texto(value, 'correo', { min: 5, max: 254 }).toLowerCase();
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) throw new HttpError(400, 'Correo inválido');
  return email;
}
function claveValida(value) {
  if (typeof value !== 'string' || value.length < 8 || value.length > 128) {
    throw new HttpError(400, 'La contraseña debe tener entre 8 y 128 caracteres');
  }
  return value;
}
function registroMov(body, { parcial = false } = {}) {
  const keys = ['descripcion', 'monto', 'tipo', 'categoria_id', 'fecha_movimiento'];
  opcionales(body, keys);
  if (!parcial) exigirCampos(body, ['monto', 'tipo', 'categoria_id']);
  const data = {};
  if (body.descripcion !== undefined) data.descripcion = texto(body.descripcion, 'descripcion', { min: 1, max: 500 });
  if (body.monto !== undefined) data.monto = dinero(body.monto);
  if (body.tipo !== undefined) data.tipo = tipo(body.tipo);
  if (body.categoria_id !== undefined) data.categoria_id = uuid(body.categoria_id, 'categoria_id');
  if (body.fecha_movimiento !== undefined) data.fecha_movimiento = fecha(body.fecha_movimiento);
  if (!parcial && !data.fecha_movimiento) data.fecha_movimiento = hoy();
  if (!Object.keys(data).length) throw new HttpError(400, 'No se proporcionaron datos para modificar');
  return data;
}
function datosCategoria(body, { parcial = false } = {}) {
  opcionales(body, ['nombre', 'tipo', 'color_hex', 'presupuesto_mensual']);
  if (!parcial) exigirCampos(body, ['nombre', 'tipo']);
  const data = {};
  if (body.nombre !== undefined) data.nombre = texto(body.nombre, 'nombre', { min: 1, max: 80 });
  if (body.tipo !== undefined) data.tipo = tipo(body.tipo);
  if (body.color_hex !== undefined) data.color_hex = colorHex(body.color_hex);
  if (body.presupuesto_mensual !== undefined) data.presupuesto_mensual =
    body.presupuesto_mensual === null ? null : dinero(body.presupuesto_mensual, 'presupuesto_mensual', { permitirCero: true });
  if (parcial && !Object.keys(data).length) throw new HttpError(400, 'No se proporcionaron datos para modificar');
  return data;
}
async function categoriaPropia(req, id) {
  const categoria = await obtenerUno('categorias', req.auth.token,
    { id: porId(id), usuario_id: `eq.${req.auth.user.id}` });
  if (!categoria) throw new HttpError(404, 'Categoría no encontrada');
  return categoria;
}
async function movimientoPropio(req, id) {
  const movimiento = await obtenerUno('movimientos', req.auth.token,
    { id: porId(id), usuario_id: `eq.${req.auth.user.id}` });
  if (!movimiento) throw new HttpError(404, 'Movimiento no encontrado');
  return movimiento;
}
async function resumenPropio(req) {
  const { desde, hasta } = req.query;
  const inicio = fechaOpcional(desde);
  const fin = fechaOpcional(hasta);
  if (inicio && fin && inicio > fin) throw new HttpError(400, 'desde debe ser anterior o igual a hasta');
  const [perfil, movimientos, categorias] = await Promise.all([
    obtenerUno('usuarios', req.auth.token, { id: `eq.${req.auth.user.id}` }),
    leerTodos('movimientos', req.auth.token, { usuario_id: `eq.${req.auth.user.id}` }),
    leerTodos('categorias', req.auth.token, { usuario_id: `eq.${req.auth.user.id}` }),
  ]);
  if (!perfil) throw new HttpError(404, 'Perfil no encontrado');
  return calcularResumen(perfil, movimientos, categorias, { desde: inicio, hasta: fin });
}

export function crearApp() {
  const app = express();
  app.disable('x-powered-by');
  app.use(helmet());
  const permitidos = (process.env.CORS_ORIGINS ?? '').split(',').map((x) => x.trim()).filter(Boolean);
  app.use(cors({ origin: (origen, callback) =>
    callback(null, !origen || permitidos.includes(origen)) }));
  app.use(express.json({ limit: '32kb' }));
  app.use('/api/v1', rateLimit({ windowMs: 15 * 60 * 1000, limit: 150,
    standardHeaders: 'draft-8', legacyHeaders: false }));

  const api = express.Router();
  api.get('/health', (_req, res) => res.json({ status: 'ok', servicio: 'Finanzas G7 API' }));

  api.post('/auth/register', ruta(async (req, res) => {
    opcionales(req.body, ['nombre', 'correo', 'password']);
    exigirCampos(req.body, ['nombre', 'correo', 'password']);
    const nombre = texto(req.body.nombre, 'nombre', { min: 2, max: 120 });
    const email = correoValido(req.body.correo);
    const password = claveValida(req.body.password);
    const response = await supabaseRequest('/auth/v1/signup', {
      method: 'POST', body: { email, password, data: { nombre } },
    });
    if (response?.access_token && response.user) {
      await prepararPerfil(response.user, response.access_token);
      return res.status(201).json({
        requiere_confirmacion: false,
        usuario_id: response.user.id,
        access_token: response.access_token,
        refresh_token: response.refresh_token,
        expires_in: response.expires_in,
      });
    }
    return res.status(202).json({
      requiere_confirmacion: true,
      mensaje: 'Comprueba tu correo y confirma la cuenta antes de iniciar sesión',
    });
  }));
  api.post('/auth/login', ruta(async (req, res) => {
    opcionales(req.body, ['correo', 'password']);
    exigirCampos(req.body, ['correo', 'password']);
    let session;
    try {
      session = await supabaseRequest('/auth/v1/token', {
        method: 'POST', query: { grant_type: 'password' },
        body: { email: correoValido(req.body.correo), password: claveValida(req.body.password) },
      });
    } catch (error) {
      if (error instanceof HttpError && error.status === 400) throw new HttpError(401, 'Credenciales inválidas o cuenta no confirmada');
      throw error;
    }
    const user = await verificarUsuario(session.access_token);
    await prepararPerfil(user, session.access_token);
    return res.json({
      usuario_id: user.id,
      access_token: session.access_token,
      refresh_token: session.refresh_token,
      expires_in: session.expires_in,
      token_type: 'bearer',
    });
  }));
  api.post('/auth/refresh', ruta(async (req, res) => {
    opcionales(req.body, ['refresh_token']);
    const refresh_token = texto(req.body.refresh_token, 'refresh_token', { min: 10, max: 4096 });
    const session = await supabaseRequest('/auth/v1/token', {
      method: 'POST', query: { grant_type: 'refresh_token' }, body: { refresh_token },
    });
    return res.json({
      access_token: session.access_token,
      refresh_token: session.refresh_token,
      expires_in: session.expires_in,
      token_type: 'bearer',
    });
  }));

  api.use(ruta(async (req, _res, next) => {
    const match = /^Bearer (\S+)$/i.exec(req.get('Authorization') || '');
    if (!match) throw new HttpError(401, 'Se requiere un token Bearer');
    const user = await verificarUsuario(match[1]);
    req.auth = { token: match[1], user };
    next();
  }));

  api.post('/auth/logout', ruta(async (req, res) => {
    await supabaseRequest('/auth/v1/logout', { method: 'POST', token: req.auth.token });
    res.status(204).end();
  }));
  api.get('/auth/me', (req, res) =>
    res.json({ id: req.auth.user.id, correo: req.auth.user.email ?? null }));

  api.get('/usuarios/me', ruta(async (req, res) => {
    const perfil = await prepararPerfil(req.auth.user, req.auth.token);
    res.json({ ...perfil, correo: req.auth.user.email ?? null });
  }));
  api.patch('/usuarios/me', ruta(async (req, res) => {
    opcionales(req.body, ['nombre']);
    if (req.body.nombre === undefined) throw new HttpError(400, 'Falta el campo nombre');
    const rows = await datos('usuarios', { method: 'PATCH', token: req.auth.token,
      query: { id: `eq.${req.auth.user.id}` },
      body: { nombre: texto(req.body.nombre, 'nombre', { min: 2, max: 120 }) },
      prefer: 'return=representation' });
    if (!rows?.length) throw new HttpError(404, 'Perfil no encontrado');
    res.json(rows[0]);
  }));
  api.put('/usuarios/me/saldo-inicial', ruta(async (req, res) => {
    opcionales(req.body, ['monto']);
    exigirCampos(req.body, ['monto']);
    const rows = await datos('usuarios', {
      method: 'PATCH', token: req.auth.token,
      query: { id: `eq.${req.auth.user.id}` },
      body: { saldo_inicial: dinero(req.body.monto, 'monto', { permitirCero: true }), saldo_configurado: true },
      prefer: 'return=representation',
    });
    if (!rows?.length) throw new HttpError(404, 'Perfil no encontrado');
    res.json(rows[0]);
  }));

  api.get('/usuarios/me/configuracion', ruta(async (req, res) => {
    const row = await obtenerUno('configuraciones', req.auth.token,
      { usuario_id: `eq.${req.auth.user.id}` });
    if (!row) throw new HttpError(404, 'Configuración no encontrada');
    res.json(row);
  }));
  api.put('/usuarios/me/configuracion', ruta(async (req, res) => {
    opcionales(req.body, ['meta_ahorro_mensual', 'notificaciones_activas', 'moneda_base']);
    const body = {};
    if (req.body.meta_ahorro_mensual !== undefined) body.meta_ahorro_mensual =
      dinero(req.body.meta_ahorro_mensual, 'meta_ahorro_mensual', { permitirCero: true });
    if (req.body.notificaciones_activas !== undefined) body.notificaciones_activas =
      booleano(req.body.notificaciones_activas, 'notificaciones_activas');
    if (req.body.moneda_base !== undefined) {
      if (req.body.moneda_base !== 'COP') throw new HttpError(400, 'El MVP solo maneja COP');
      body.moneda_base = 'COP';
    }
    if (!Object.keys(body).length) throw new HttpError(400, 'No se proporcionaron datos para modificar');
    const rows = await datos('configuraciones', { method: 'PATCH', token: req.auth.token,
      query: { usuario_id: `eq.${req.auth.user.id}` }, body, prefer: 'return=representation' });
    if (!rows?.length) throw new HttpError(404, 'Configuración no encontrada');
    res.json(rows[0]);
  }));

  api.get('/categorias', ruta(async (req, res) => {
    const { limit, offset } = paginacion(req.query);
    const filtroTipo = req.query.tipo === undefined ? undefined : `eq.${tipo(req.query.tipo)}`;
    const rows = await datos('categorias', { token: req.auth.token, query: {
      select: '*', usuario_id: `eq.${req.auth.user.id}`, tipo: filtroTipo,
      order: 'nombre.asc', limit, offset,
    } });
    res.json(rows ?? []);
  }));
  api.get('/categorias/:id', ruta(async (req, res) => res.json(await categoriaPropia(req, req.params.id))));
  api.post('/categorias', ruta(async (req, res) => {
    const body = datosCategoria(req.body);
    const rows = await datos('categorias', { method: 'POST', token: req.auth.token,
      body: { ...body, usuario_id: req.auth.user.id, es_predeterminada: false },
      prefer: 'return=representation' });
    res.status(201).json(rows[0]);
  }));
  api.put('/categorias/:id', ruta(async (req, res) => {
    const actual = await categoriaPropia(req, req.params.id);
    const body = datosCategoria(req.body, { parcial: true });
    if (body.tipo && body.tipo !== actual.tipo) {
      const relacionados = await datos('movimientos', { token: req.auth.token,
        query: { select: 'id', categoria_id: `eq.${actual.id}`, usuario_id: `eq.${req.auth.user.id}`, limit: 1 } });
      if (relacionados?.length) throw new HttpError(409, 'No puedes cambiar el tipo de una categoría con movimientos');
    }
    const rows = await datos('categorias', { method: 'PATCH', token: req.auth.token,
      query: { id: `eq.${actual.id}`, usuario_id: `eq.${req.auth.user.id}` },
      body, prefer: 'return=representation' });
    res.json(rows[0]);
  }));
  api.delete('/categorias/:id', ruta(async (req, res) => {
    const actual = await categoriaPropia(req, req.params.id);
    const movimientos = await datos('movimientos', { token: req.auth.token,
      query: { select: 'id', categoria_id: `eq.${actual.id}`, usuario_id: `eq.${req.auth.user.id}`, limit: 1 } });
    if (movimientos?.length) throw new HttpError(409, 'No puedes eliminar una categoría con movimientos');
    await datos('categorias', { method: 'DELETE', token: req.auth.token,
      query: { id: `eq.${actual.id}`, usuario_id: `eq.${req.auth.user.id}` }, prefer: 'return=minimal' });
    res.status(204).end();
  }));

  api.get('/movimientos', ruta(async (req, res) => {
    const { limit, offset } = paginacion(req.query);
    const inicio = fechaOpcional(req.query.desde);
    const fin = fechaOpcional(req.query.hasta);
    if (inicio && fin && inicio > fin) throw new HttpError(400, 'El rango de fechas es inválido');
    const filters = {
      select: '*', usuario_id: `eq.${req.auth.user.id}`,
      categoria_id: req.query.categoria_id === undefined ? undefined : porId(req.query.categoria_id),
      tipo: req.query.tipo === undefined ? undefined : `eq.${tipo(req.query.tipo)}`,
      fecha_movimiento: inicio && fin ? `gte.${inicio}` : inicio ? `gte.${inicio}` : fin ? `lte.${fin}` : undefined,
      and: inicio && fin ? `(fecha_movimiento.gte.${inicio},fecha_movimiento.lte.${fin})` : undefined,
      order: 'fecha_movimiento.desc,creado_en.desc', limit, offset,
    };
    if (inicio && fin) delete filters.fecha_movimiento;
    const rows = await datos('movimientos', { token: req.auth.token, query: filters });
    res.json(rows ?? []);
  }));
  api.get('/movimientos/:id', ruta(async (req, res) =>
    res.json(await movimientoPropio(req, req.params.id))));
  api.post('/movimientos', ruta(async (req, res) => {
    const body = registroMov(req.body);
    const categoria = await categoriaPropia(req, body.categoria_id);
    if (body.tipo !== categoria.tipo) throw new HttpError(400, 'La categoría no corresponde al tipo de movimiento');
    const rows = await datos('movimientos', { method: 'POST', token: req.auth.token,
      body: { ...body, usuario_id: req.auth.user.id },
      prefer: 'return=representation' });
    res.status(201).json(rows[0]);
  }));
  api.put('/movimientos/:id', ruta(async (req, res) => {
    const actual = await movimientoPropio(req, req.params.id);
    const body = registroMov(req.body, { parcial: true });
    const categoria = await categoriaPropia(req, body.categoria_id ?? actual.categoria_id);
    if ((body.tipo ?? actual.tipo) !== categoria.tipo) {
      throw new HttpError(400, 'La categoría no corresponde al tipo de movimiento');
    }
    const rows = await datos('movimientos', { method: 'PATCH', token: req.auth.token,
      query: { id: `eq.${actual.id}`, usuario_id: `eq.${req.auth.user.id}` },
      body, prefer: 'return=representation' });
    res.json(rows[0]);
  }));
  api.delete('/movimientos/:id', ruta(async (req, res) => {
    const actual = await movimientoPropio(req, req.params.id);
    await datos('movimientos', { method: 'DELETE', token: req.auth.token,
      query: { id: `eq.${actual.id}`, usuario_id: `eq.${req.auth.user.id}` }, prefer: 'return=minimal' });
    res.status(204).end();
  }));

  api.get('/resumen', ruta(async (req, res) => res.json(await resumenPropio(req))));
  api.get('/sugerencias', ruta(async (req, res) =>
    res.json(generarSugerencias(await resumenPropio(req)))));

  app.use('/api/v1', api);
  app.use((_req, _res, next) => next(new HttpError(404, 'Ruta no encontrada')));
  app.use((error, _req, res, _next) => {
    if (error?.type === 'entity.parse.failed') return res.status(400).json({ error: 'JSON inválido' });
    const status = error instanceof HttpError ? error.status : 500;
    if (status >= 500) console.error('[finanzas-g7-api]', error.message);
    return res.status(status).json({
      error: status >= 500 ? 'Error interno del servidor' : error.message,
      ...(error instanceof HttpError && status < 500 && error.details ? { details: error.details } : {}),
    });
  });
  return app;
}
