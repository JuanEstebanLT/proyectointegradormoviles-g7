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
    ('Emmanuel Villa',        'emmanuel.villa@finanzasg7.com', '$2b$10$nMYejKZ8XdDkqYBZTczNOeN5.dj61X5tfTqk/gwkIXtq3LZ5F/lp2', 'ADMIN'),
    ('Juan Esteban Lezcano',  'juan.lezcano@finanzasg7.com',   '$2b$10$cK.cyUAxGVm40pxb5IhvxOJfz46A5AEl9hNKmO0zf87OCIIWUE6hW', 'ADMIN'),
    ('Mariana Suárez',        'mariana.suarez@finanzasg7.com', '$2b$10$Ap40eLa0QGd7o7iUemw4GuxU/qbSymhLHn.Ha5FyzNIrKSzmk.Wci', 'ADMIN'),
    ('Juan David Bastidas',   'juan.bastidas@finanzasg7.com',  '$2b$10$3HJX8HvLuGbmD7vzqsWpvu4jQky4dKpBuMtHAdfIGj4InCT8V7SNO', 'ADMIN')
on conflict (correo) do update set rol = 'ADMIN';
