import 'dotenv/config';
import { crearApp } from './app.js';

const port = Number(process.env.PORT ?? 3000);
if (!Number.isSafeInteger(port) || port < 1 || port > 65535) {
  throw new Error('PORT debe ser un puerto válido');
}
const app = crearApp();
app.listen(port, () => console.log(`Finanzas G7 API escuchando en el puerto ${port}`));
