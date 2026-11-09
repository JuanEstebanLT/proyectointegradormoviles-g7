import jwt from 'jsonwebtoken';
import { env } from '../config/env.js';
import { HttpError } from '../utils/errores.js';

export function generarToken(usuario) {
  return jwt.sign({ sub: String(usuario.id), rol: usuario.rol }, env.jwtSecret, { expiresIn: env.jwtExpiraEn });
}

/**
 * Exige "Authorization: Bearer <token>" y deja en req.usuarioId el usuario sobre el que se actúa.
 * Un ADMIN puede enviar ?usuario_id= para consultar o probar con los datos de otro usuario.
 */
export function autenticar(req, _res, next) {
  const [esquema, token] = (req.headers.authorization ?? '').split(' ');
  if (esquema !== 'Bearer' || !token) {
    throw new HttpError(401, 'Token de sesión ausente');
  }

  let payload;
  try {
    payload = jwt.verify(token, env.jwtSecret);
  } catch {
    throw new HttpError(401, 'Token de sesión inválido o expirado');
  }
  req.rol = payload.rol ?? 'USUARIO';
  req.usuarioId = Number(payload.sub);

  if (req.query.usuario_id !== undefined) {
    const solicitado = Number(req.query.usuario_id);
    if (!Number.isInteger(solicitado) || solicitado <= 0) {
      throw new HttpError(400, 'usuario_id inválido');
    }
    if (solicitado !== req.usuarioId && req.rol !== 'ADMIN') {
      throw new HttpError(403, 'No puedes consultar datos de otro usuario');
    }
    req.usuarioId = solicitado;
  }
  next();
}

/** Para rutas /usuarios/:id: solo el propio usuario o un ADMIN. */
export function soloMismoUsuario(req, _res, next) {
  if (Number(req.params.id) !== req.usuarioId && req.rol !== 'ADMIN') {
    throw new HttpError(403, 'No puedes acceder a datos de otro usuario');
  }
  next();
}

export function soloAdmin(req, _res, next) {
  if (req.rol !== 'ADMIN') throw new HttpError(403, 'Requiere rol de administrador');
  next();
}
