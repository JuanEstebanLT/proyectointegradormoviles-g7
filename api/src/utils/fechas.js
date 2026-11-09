import { env } from '../config/env.js';

/** Primer y último día (YYYY-MM-DD) del mes actual en la zona horaria configurada. */
export function rangoMesActual(ahora = new Date()) {
  const [anio, mes] = new Intl.DateTimeFormat('en-CA', {
    timeZone: env.zonaHoraria,
    year: 'numeric',
    month: '2-digit'
  })
    .format(ahora)
    .split('-')
    .map(Number);

  const ultimoDia = new Date(Date.UTC(anio, mes, 0)).getUTCDate();
  const mm = String(mes).padStart(2, '0');
  return { desde: `${anio}-${mm}-01`, hasta: `${anio}-${mm}-${ultimoDia}` };
}
