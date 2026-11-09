import app from './app.js';
import { env } from './config/env.js';

app.listen(env.puerto, () => {
  console.log(`Finanzas G7 API escuchando en http://localhost:${env.puerto}/api/v1`);
});
