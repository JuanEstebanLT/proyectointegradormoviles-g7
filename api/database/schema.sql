-- =========================================================
-- Finanzas G7 API · Esquema de base de datos (Supabase / PostgreSQL)
-- Ejecutar en: Supabase Dashboard > SQL Editor
-- Basado en el Diagrama ER del README del proyecto.
-- =========================================================

-- Tipo compartido por CATEGORIA y MOVIMIENTO
do $$ begin
    create type tipo_movimiento as enum ('INGRESO', 'GASTO');
exception
    when duplicate_object then null;
end $$;

-- ---------------------------------------------------------
-- USUARIO
-- ---------------------------------------------------------
create table if not exists usuario (
    id               bigint generated always as identity primary key,
    nombre           varchar(100)  not null check (length(trim(nombre)) > 0),
    correo           varchar(150)  not null unique,
    telefono         varchar(20),
    contrasena_hash  text          not null,
    saldo_inicial    numeric(14,2) not null default 0 check (saldo_inicial >= 0),
    rol              varchar(10)   not null default 'USUARIO' check (rol in ('ADMIN', 'USUARIO')),
    creado_en        timestamptz   not null default now(),
    actualizado_en   timestamptz   not null default now()
);

-- ---------------------------------------------------------
-- CONFIGURACION (1:1 con USUARIO)
-- ---------------------------------------------------------
create table if not exists configuracion (
    id                     bigint generated always as identity primary key,
    usuario_id             bigint        not null unique references usuario(id) on delete cascade,
    moneda_base            char(3)       not null default 'COP',
    meta_ahorro_mensual    numeric(14,2) not null default 0 check (meta_ahorro_mensual >= 0),
    notificaciones_activas boolean       not null default true,
    actualizado_en         timestamptz   not null default now()
);

-- ---------------------------------------------------------
-- CATEGORIA (1:N con USUARIO)
-- ---------------------------------------------------------
create table if not exists categoria (
    id                  bigint generated always as identity primary key,
    usuario_id          bigint          not null references usuario(id) on delete cascade,
    nombre              varchar(60)     not null check (length(trim(nombre)) > 0),
    tipo                tipo_movimiento not null,
    color_hex           char(7)         not null default '#607D8B' check (color_hex ~ '^#[0-9A-Fa-f]{6}$'),
    presupuesto_mensual numeric(14,2)   not null default 0 check (presupuesto_mensual >= 0),
    es_predeterminada   boolean         not null default false,
    creado_en           timestamptz     not null default now(),
    unique (usuario_id, nombre)
);

create index if not exists idx_categoria_usuario on categoria(usuario_id);

-- ---------------------------------------------------------
-- MOVIMIENTO (1:N con USUARIO y con CATEGORIA)
-- on delete restrict: una categoría con movimientos no se puede eliminar
-- ---------------------------------------------------------
create table if not exists movimiento (
    id               bigint generated always as identity primary key,
    usuario_id       bigint          not null references usuario(id) on delete cascade,
    categoria_id     bigint          not null references categoria(id) on delete restrict,
    tipo             tipo_movimiento not null,
    monto            numeric(14,2)   not null check (monto > 0),
    descripcion      varchar(200)    not null check (length(trim(descripcion)) > 0),
    medio_pago       varchar(40)     not null default 'Efectivo',
    fecha_movimiento date            not null default current_date,
    creado_en        timestamptz     not null default now(),
    actualizado_en   timestamptz     not null default now()
);

create index if not exists idx_movimiento_usuario_fecha on movimiento(usuario_id, fecha_movimiento desc);
create index if not exists idx_movimiento_categoria     on movimiento(categoria_id);

-- ---------------------------------------------------------
-- SUGERENCIA (catálogo global de consejos educativos)
-- ---------------------------------------------------------
create table if not exists sugerencia (
    id             bigint generated always as identity primary key,
    titulo         varchar(100) not null unique,
    mensaje        text         not null,
    regla_asociada varchar(50)  not null,
    prioridad      int          not null default 1 check (prioridad between 1 and 5),
    activa         boolean      not null default true,
    creado_en      timestamptz  not null default now()
);

-- ---------------------------------------------------------
-- SUGERENCIA_CATEGORIA (resuelve la relación N:M)
-- ---------------------------------------------------------
create table if not exists sugerencia_categoria (
    sugerencia_id bigint not null references sugerencia(id) on delete cascade,
    categoria_id  bigint not null references categoria(id)  on delete cascade,
    primary key (sugerencia_id, categoria_id)
);

-- =========================================================
-- Consistencia: el tipo del movimiento debe coincidir con el de su categoría
-- y la categoría debe pertenecer al mismo usuario.
-- =========================================================
create or replace function validar_movimiento()
returns trigger language plpgsql as $$
declare
    cat categoria%rowtype;
begin
    select * into cat from categoria where id = new.categoria_id;
    if cat.usuario_id <> new.usuario_id then
        raise exception 'La categoría no pertenece al usuario' using errcode = 'P0001';
    end if;
    if cat.tipo <> new.tipo then
        raise exception 'El tipo del movimiento (%) no coincide con el de la categoría (%)', new.tipo, cat.tipo
            using errcode = 'P0001';
    end if;
    return new;
end $$;

drop trigger if exists trg_validar_movimiento on movimiento;
create trigger trg_validar_movimiento
    before insert or update on movimiento
    for each row execute function validar_movimiento();

-- =========================================================
-- Mantener actualizado_en
-- =========================================================
create or replace function tocar_actualizado_en()
returns trigger language plpgsql as $$
begin
    new.actualizado_en = now();
    return new;
end $$;

drop trigger if exists trg_usuario_actualizado on usuario;
create trigger trg_usuario_actualizado before update on usuario
    for each row execute function tocar_actualizado_en();

drop trigger if exists trg_configuracion_actualizado on configuracion;
create trigger trg_configuracion_actualizado before update on configuracion
    for each row execute function tocar_actualizado_en();

drop trigger if exists trg_movimiento_actualizado on movimiento;
create trigger trg_movimiento_actualizado before update on movimiento
    for each row execute function tocar_actualizado_en();

-- =========================================================
-- Al crear un usuario: crear su configuración y sus categorías predeterminadas
-- =========================================================
create or replace function inicializar_usuario()
returns trigger language plpgsql as $$
begin
    insert into configuracion (usuario_id) values (new.id);

    insert into categoria (usuario_id, nombre, tipo, color_hex, es_predeterminada) values
        (new.id, 'Vivienda',     'GASTO',   '#3F51B5', true),
        (new.id, 'Alimentación', 'GASTO',   '#4CAF50', true),
        (new.id, 'Transporte',   'GASTO',   '#FF9800', true),
        (new.id, 'Ocio',         'GASTO',   '#9C27B0', true),
        (new.id, 'Salud',        'GASTO',   '#F44336', true),
        (new.id, 'Ingresos',     'INGRESO', '#009688', true);

    return new;
end $$;

drop trigger if exists trg_inicializar_usuario on usuario;
create trigger trg_inicializar_usuario
    after insert on usuario
    for each row execute function inicializar_usuario();

-- =========================================================
-- Totales por categoría (usado por /resumen, /categorias y /sugerencias).
-- Las fechas son opcionales: null = sin límite.
-- =========================================================
create or replace function totales_por_categoria(
    p_usuario_id bigint,
    p_desde      date default null,
    p_hasta      date default null
)
returns table (categoria_id bigint, tipo tipo_movimiento, total numeric, cantidad bigint)
language sql stable as $$
    select m.categoria_id, m.tipo, sum(m.monto), count(*)
    from movimiento m
    where m.usuario_id = p_usuario_id
      and (p_desde is null or m.fecha_movimiento >= p_desde)
      and (p_hasta is null or m.fecha_movimiento <= p_hasta)
    group by m.categoria_id, m.tipo;
$$;

revoke execute on function totales_por_categoria(bigint, date, date) from public, anon, authenticated;
grant  execute on function totales_por_categoria(bigint, date, date) to service_role;

-- =========================================================
-- Seguridad: RLS activado sin políticas.
-- Solo la API (con la service_role key) puede leer/escribir;
-- la anon key pública de Supabase no tiene acceso a ninguna tabla.
-- =========================================================
alter table usuario              enable row level security;
alter table configuracion        enable row level security;
alter table categoria            enable row level security;
alter table movimiento           enable row level security;
alter table sugerencia           enable row level security;
alter table sugerencia_categoria enable row level security;
