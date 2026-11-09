-- =========================================================
-- Finanzas G7 API · Datos iniciales
-- Ejecutar después de schema.sql
-- =========================================================

-- Catálogo de sugerencias educativas (HU-08).
-- regla_asociada debe coincidir con una regla implementada en
-- src/services/reglasSugerencias.js
insert into sugerencia (titulo, mensaje, regla_asociada, prioridad) values
    ('Gastas más de lo que ingresas',
     'Este mes tus gastos superan tus ingresos. Revisa los gastos no esenciales y prioriza vivienda, alimentación y transporte.',
     'GASTOS_SUPERAN_INGRESOS', 5),
    ('Presupuesto excedido',
     'Superaste el presupuesto mensual de una o más categorías. Considera ajustar el presupuesto o reducir gastos en esas categorías.',
     'PRESUPUESTO_EXCEDIDO', 4),
    ('Estás cerca de tu presupuesto',
     'Ya usaste más del 80 % del presupuesto de alguna categoría. Planifica los gastos que te quedan del mes.',
     'PRESUPUESTO_CERCANO', 3),
    ('Aún no cumples tu meta de ahorro',
     'Lo que te queda este mes (ingresos menos gastos) está por debajo de tu meta de ahorro. Intenta separar el ahorro apenas recibas tus ingresos.',
     'META_AHORRO_NO_CUMPLIDA', 3),
    ('El ocio pesa mucho en tus gastos',
     'Más del 30 % de tus gastos del mes son de ocio. Busca alternativas gratuitas o define un tope semanal.',
     'OCIO_ELEVADO', 2),
    ('Registra tus ingresos',
     'No has registrado ingresos este mes. Registrarlos te permite conocer tu saldo real.',
     'SIN_INGRESOS_MES', 2),
    ('Regla 50/30/20',
     'Una guía sencilla: destina el 50 % a necesidades, el 30 % a gustos y el 20 % al ahorro.',
     'GENERAL', 1)
on conflict (titulo) do nothing;
