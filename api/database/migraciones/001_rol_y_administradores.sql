-- =========================================================
-- Migración 001 · Rol de usuario y cuentas de administrador
-- Ejecutar una sola vez en: Supabase Dashboard > SQL Editor
-- (Las bases creadas desde cero con schema.sql ya incluyen la columna rol.)
-- =========================================================

alter table usuario
    add column if not exists rol varchar(10) not null default 'USUARIO'
        check (rol in ('ADMIN', 'USUARIO'));

-- Cuentas de administrador del equipo. La contraseña inicial se comparte
-- por un canal privado y debe cambiarse; aquí solo se guarda su hash bcrypt.
-- Al insertarlas, el trigger crea su configuración y categorías predeterminadas.
insert into usuario (nombre, correo, contrasena_hash, rol) values
    ('Emmanuel Villa',        'emmanuel.villa@finanzasg7.com', '$2b$10$og9941nSimVbz0w6u6d0w.MumHM6rgbJmJ4NfWmW1Jpc44RIpvdUi', 'ADMIN'),
    ('Juan Esteban Lezcano',  'juan.lezcano@finanzasg7.com',   '$2b$10$YSqH.Ha3ZCGK7uUSZReojuWFsevco91.gUJ8H/do41XKg5pe9nsWq', 'ADMIN'),
    ('Mariana Suárez',        'mariana.suarez@finanzasg7.com', '$2b$10$jmNwKEYPi/q1TtFO5MdMs.IZF0rwqywBML6AUz4UAiJ3eNE1nm/vu', 'ADMIN'),
    ('Juan David Bastidas',   'juan.bastidas@finanzasg7.com',  '$2b$10$JBaIGNKd/UGxj3e60aJRkuXQFiYPzRFZyccCDMKW9njCjDIgZ6xce', 'ADMIN')
on conflict (correo) do update set rol = 'ADMIN';
