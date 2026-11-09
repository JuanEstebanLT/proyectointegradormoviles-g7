import cors from 'cors';
import express from 'express';
import { env } from './config/env.js';
import { manejadorErrores, rutaNoEncontrada } from './middlewares/manejadorErrores.js';
import rutas from './routes/index.js';

const app = express();

app.disable('x-powered-by');
app.use(cors({ origin: env.corsOrigen, exposedHeaders: ['X-Total-Count'] }));
app.use(express.json({ limit: '100kb' }));

// Registro de cada petición: método, ruta, estado, duración e IP de origen
app.use((req, res, next) => {
  const inicio = Date.now();
  res.on('finish', () => {
    console.log(`${req.method} ${req.originalUrl} ${res.statusCode} ${Date.now() - inicio}ms · ${req.ip}`);
  });
  next();
});

app.get('/', (_req, res) => res.json({ nombre: 'Finanzas G7 API', version: 'v1', base: '/api/v1' }));
app.use('/api/v1', rutas);

app.use(rutaNoEncontrada);
app.use(manejadorErrores);

export default app;
