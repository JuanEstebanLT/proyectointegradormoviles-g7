import { Router } from 'express';
import * as movimientos from '../controllers/movimientos.controller.js';
import { validar } from '../middlewares/validar.js';
import { paramsId } from '../schemas/comunes.js';
import * as esquemas from '../schemas/movimiento.schema.js';

const router = Router();

router.get('/', validar(esquemas.filtrosMovimientos, 'query'), movimientos.listar);
router.post('/', validar(esquemas.crearMovimiento), movimientos.crear);
router.get('/:id', validar(paramsId, 'params'), movimientos.obtener);
router.put('/:id', validar(paramsId, 'params'), validar(esquemas.actualizarMovimiento), movimientos.actualizar);
router.delete('/:id', validar(paramsId, 'params'), movimientos.eliminar);

export default router;
