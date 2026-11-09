import { Router } from 'express';
import * as usuarios from '../controllers/usuarios.controller.js';
import { autenticar, soloAdmin, soloMismoUsuario } from '../middlewares/autenticar.js';
import { validar } from '../middlewares/validar.js';
import { paramsId } from '../schemas/comunes.js';
import * as esquemas from '../schemas/usuario.schema.js';

const router = Router();

// Públicas
router.post('/', validar(esquemas.registroUsuario), usuarios.registrar);
router.post('/sesion', validar(esquemas.inicioSesion), usuarios.iniciarSesion);

// Solo ADMIN: listado de todos los usuarios
router.get('/', autenticar, soloAdmin, usuarios.listar);

// Protegidas: el propio usuario o un ADMIN
router.use('/:id', validar(paramsId, 'params'), autenticar, soloMismoUsuario);
router.get('/:id', usuarios.obtener);
router.put('/:id', validar(esquemas.actualizarUsuario), usuarios.actualizar);
router.get('/:id/configuracion', usuarios.obtenerConfiguracion);
router.put('/:id/configuracion', validar(esquemas.actualizarConfiguracion), usuarios.actualizarConfiguracion);

export default router;
