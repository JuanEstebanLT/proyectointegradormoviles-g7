import bcrypt from 'bcryptjs';
import { supabase } from '../config/supabase.js';
import { generarToken } from '../middlewares/autenticar.js';
import { HttpError, obtenerUno, verificar } from '../utils/errores.js';

// Nunca se devuelve contrasena_hash
const COLUMNAS_USUARIO = 'id, nombre, correo, telefono, saldo_inicial, rol, creado_en, actualizado_en';
const COLUMNAS_CONFIGURACION = 'id, usuario_id, moneda_base, meta_ahorro_mensual, notificaciones_activas, actualizado_en';

// POST /usuarios  (HU-01: crear usuario con su saldo inicial)
export async function registrar(req, res) {
  const { contrasena, ...datos } = req.validado.body;

  const { data: usuario, error } = await supabase
    .from('usuario')
    .insert({ ...datos, contrasena_hash: await bcrypt.hash(contrasena, 10) })
    .select(COLUMNAS_USUARIO)
    .single();

  if (error?.code === '23505') throw new HttpError(409, 'El correo ya está registrado');
  verificar({ error });

  res.status(201).json({ usuario, token: generarToken(usuario) });
}

// POST /usuarios/sesion
export async function iniciarSesion(req, res) {
  const { correo, contrasena } = req.validado.body;

  const fila = verificar(
    await supabase.from('usuario').select(`${COLUMNAS_USUARIO}, contrasena_hash`).eq('correo', correo).maybeSingle()
  );
  if (!fila || !(await bcrypt.compare(contrasena, fila.contrasena_hash))) {
    throw new HttpError(401, 'Correo o contraseña incorrectos');
  }

  const { contrasena_hash: _, ...usuario } = fila;
  res.json({ usuario, token: generarToken(usuario) });
}

// GET /usuarios   (solo ADMIN)
export async function listar(_req, res) {
  res.json(verificar(await supabase.from('usuario').select(COLUMNAS_USUARIO).order('id')));
}

// GET /usuarios/:id
export async function obtener(req, res) {
  const usuario = await obtenerUno(
    supabase.from('usuario').select(COLUMNAS_USUARIO).eq('id', req.validado.params.id),
    'Usuario'
  );
  res.json(usuario);
}

// PUT /usuarios/:id
export async function actualizar(req, res) {
  const usuario = await obtenerUno(
    supabase.from('usuario').update(req.validado.body).eq('id', req.validado.params.id).select(COLUMNAS_USUARIO),
    'Usuario'
  );
  res.json(usuario);
}

// GET /usuarios/:id/configuracion
export async function obtenerConfiguracion(req, res) {
  const configuracion = await obtenerUno(
    supabase.from('configuracion').select(COLUMNAS_CONFIGURACION).eq('usuario_id', req.validado.params.id),
    'Configuración'
  );
  res.json(configuracion);
}

// PUT /usuarios/:id/configuracion
export async function actualizarConfiguracion(req, res) {
  const configuracion = await obtenerUno(
    supabase
      .from('configuracion')
      .update(req.validado.body)
      .eq('usuario_id', req.validado.params.id)
      .select(COLUMNAS_CONFIGURACION),
    'Configuración'
  );
  res.json(configuracion);
}
