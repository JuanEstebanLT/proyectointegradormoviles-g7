import { Router } from 'express';
import * as resumen from '../controllers/resumen.controller.js';
import * as sugerencias from '../controllers/sugerencias.controller.js';
import { autenticar } from '../middlewares/autenticar.js';
import { validar } from '../middlewares/validar.js';
import { rangoFechas } from '../schemas/comunes.js';
import categoriasRoutes from './categorias.routes.js';
import movimientosRoutes from './movimientos.routes.js';
import usuariosRoutes from './usuarios.routes.js';

const router = Router();

router.get('/salud', (_req, res) => res.json({ estado: 'ok', fecha: new Date().toISOString() }));

// usuarios maneja su propia autenticación (registro y sesión son públicos)
router.use('/usuarios', usuariosRoutes);

router.use('/categorias', autenticar, categoriasRoutes);
router.use('/movimientos', autenticar, movimientosRoutes);
router.get('/resumen', autenticar, validar(rangoFechas, 'query'), resumen.obtener);
router.get('/sugerencias', autenticar, sugerencias.listar);

export default router;
