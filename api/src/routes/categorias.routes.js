import { Router } from 'express';
import * as categorias from '../controllers/categorias.controller.js';
import { validar } from '../middlewares/validar.js';
import { paramsId } from '../schemas/comunes.js';
import * as esquemas from '../schemas/categoria.schema.js';

const router = Router();

router.get('/', validar(esquemas.filtrosCategorias, 'query'), categorias.listar);
router.post('/', validar(esquemas.crearCategoria), categorias.crear);
router.get('/:id', validar(paramsId, 'params'), categorias.obtener);
router.put('/:id', validar(paramsId, 'params'), validar(esquemas.actualizarCategoria), categorias.actualizar);
router.delete('/:id', validar(paramsId, 'params'), categorias.eliminar);

export default router;
