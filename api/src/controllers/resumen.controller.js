import { calcularResumen } from '../services/resumen.service.js';

// GET /resumen?desde=&hasta=   (HU-05 saldo disponible, HU-07 informes por período)
export async function obtener(req, res) {
  const { desde, hasta } = req.validado.query;
  res.json(await calcularResumen(req.usuarioId, { desde, hasta }));
}
