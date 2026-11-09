import 'dotenv/config';

const requeridas = ['SUPABASE_URL', 'SUPABASE_SERVICE_ROLE_KEY', 'JWT_SECRET'];
const faltantes = requeridas.filter((nombre) => !process.env[nombre]);

if (faltantes.length > 0) {
  throw new Error(`Faltan variables de entorno: ${faltantes.join(', ')}. Revisa el archivo .env`);
}

export const env = {
  puerto: Number(process.env.PORT ?? 3000),
  // Se admite también la URL copiada con "/rest/v1/" al final
  supabaseUrl: process.env.SUPABASE_URL.trim().replace(/\/rest\/v1\/?$/, '').replace(/\/$/, ''),
  supabaseServiceRoleKey: process.env.SUPABASE_SERVICE_ROLE_KEY,
  jwtSecret: process.env.JWT_SECRET,
  jwtExpiraEn: process.env.JWT_EXPIRA_EN || '7d',
  zonaHoraria: process.env.ZONA_HORARIA || 'America/Bogota',
  corsOrigen: process.env.CORS_ORIGEN
    ? process.env.CORS_ORIGEN.split(',').map((origen) => origen.trim())
    : '*'
};
