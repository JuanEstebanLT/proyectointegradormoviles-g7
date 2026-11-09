import { z } from 'zod';
import { monto, noVacio } from './comunes.js';

const nombre = z.string().trim().min(1, 'El nombre es obligatorio').max(100);
const correo = z.email('Correo inválido').trim().toLowerCase();
const telefono = z.string().trim().max(20);

export const registroUsuario = z.object({
  nombre,
  correo,
  contrasena: z.string().min(6, 'La contraseña debe tener al menos 6 caracteres').max(72),
  telefono: telefono.optional(),
  saldo_inicial: monto.default(0)
});

export const inicioSesion = z.object({
  correo,
  contrasena: z.string().min(1, 'La contraseña es obligatoria')
});

export const actualizarUsuario = noVacio(
  z.object({
    nombre: nombre.optional(),
    telefono: telefono.optional(),
    saldo_inicial: monto.optional()
  })
);

export const actualizarConfiguracion = noVacio(
  z.object({
    moneda_base: z.string().trim().toUpperCase().length(3, 'Código ISO de 3 letras (ej. COP)').optional(),
    meta_ahorro_mensual: monto.optional(),
    notificaciones_activas: z.boolean().optional()
  })
);
