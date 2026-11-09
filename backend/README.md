# Finanzas G7 API — backend

API REST **propia** basada en Node.js 22+, Express 5 y las APIs HTTP de Supabase Auth y PostgREST. Se añade en la carpeta `backend/` y **no modifica el proyecto Android ni su arquitectura MVVM**.

## Ejecución

1. Instala Node.js 22 o posterior.
2. Desde `backend/`, ejecuta `npm install`.
3. Copia `.env.example` como `.env` e introduce **la publishable key del proyecto Finanzas G7** desde su panel Supabase.
4. Ejecuta `npm run dev`. API local: `http://localhost:3000/api/v1`.
5. Valida `GET /api/v1/health` y ejecuta `npm test`.

> **Seguridad:** nunca subas `.env`, claves administrativas, tokens de usuario ni datos reales al repositorio. Este backend se comunica con Supabase usando la **publishable key** (encabezado `apikey`) y el **Bearer JWT real del usuario**. Supabase verifica el token con `GET /auth/v1/user` y las políticas RLS protegen las filas. No se usa `service_role` ni conexión directa con contraseña de PostgreSQL.

**Proyecto autorizado:** `jbjadrsrdgpmronqpejv` (Finanzas G7). No usar credenciales de NexVitria.

## Endpoints (prefijo `/api/v1`)

| Método | Ruta | Detalle |
|---|---|---|
| GET | /health | Estado del servidor (no comprueba la BD) |
| POST | /auth/register | `{nombre,correo,password}`; 201 con sesión o 202 si requiere confirmación por correo |
| POST | /auth/login | `{correo,password}`; devuelve tokens |
| POST | /auth/refresh | `{refresh_token}` |
| POST | /auth/logout | Revoca sesión del token presentado |
| GET | /auth/me | Identidad del usuario autenticado |
| GET | /usuarios/me | Perfil propio |
| PATCH | /usuarios/me | Actualiza el nombre |
| PUT | /usuarios/me/saldo-inicial | `{monto}`; configura saldo inicial |
| GET, PUT | /usuarios/me/configuracion | Meta de ahorro, moneda COP y notificaciones |
| GET, POST | /categorias | Lista/crea categorías |
| GET, PUT, DELETE | /categorias/:id | Consulta, edita o elimina una categoría |
| GET, POST | /movimientos | Lista/crea movimientos |
| GET, PUT, DELETE | /movimientos/:id | Consulta, edita o elimina un movimiento |
| GET | /resumen | Saldo y totales, opcional `?desde=AAAA-MM-DD&hasta=AAAA-MM-DD` |
| GET | /sugerencias | Consejos educativos calculados a partir del resumen |

Todas las rutas salvo `/health`, `/auth/register`, `/auth/login` y `/auth/refresh` exigen `Authorization: Bearer <access_token>`.

### Ejemplos

Registro:
```http
POST /api/v1/auth/register
Content-Type: application/json

{"nombre":"Usuario Ejemplo","correo":"usuario@example.com","password":"ClaveSegura123!"}
```

Configuración del saldo:
```http
PUT /api/v1/usuarios/me/saldo-inicial
Authorization: Bearer TOKEN
Content-Type: application/json

{"monto":1000000}
```

Movimiento:
```http
POST /api/v1/movimientos
Authorization: Bearer TOKEN
Content-Type: application/json

{"descripcion":"Mercado","tipo":"GASTO","monto":50000,"categoria_id":"UUID-DE-UNA-CATEGORIA-PROPIA","fecha_movimiento":"2026-11-09"}
```

Para listar movimientos utiliza `?limit=50&offset=0&tipo=GASTO&categoria_id=UUID&desde=2026-11-01&hasta=2026-11-30`.

Los importes son **enteros en COP**, no decimales, y el saldo disponible se calcula con **todos** los movimientos históricos. El período filtra solo los totales del resumen, no altera el saldo histórico.

La cuenta nueva **no recibe movimientos simulados**. En el primer acceso confirmado se crea su perfil financiero, configuración y categorías predeterminadas. Con confirmación de correo activa, `register` responde 202 y el perfil se crea durante el primer `login`.

### Limitaciones conocidas

- No se conecta Android; `app/` sigue utilizando el repositorio en memoria.
- En PostgreSQL las categorías/movimientos tienen identificadores `UUID`, mientras los mocks Android usan `Int`. Los DTO y el adaptador Retrofit deben resolverlo en otro Issue.
- La tabla real `public.movimientos` no tiene columna `medio_pago` ni hora del movimiento (fecha es `date`); el contrato actual solo acepta `fecha_movimiento` y no guarda medio de pago. Requiere migración compatible para añadirlos si la interfaz final los necesita.
- Las sugerencias son **reglas calculadas**, no filas escritas en `public.sugerencias`.
- El cálculo de resumen es suficiente para un MVP de tamaño moderado; pagina hasta 50 000 filas por recurso y debe trasladarse a consultas agregadas/RPC si aumenta la escala.
- No se crea una sesión con credenciales simuladas. El inicio de sesión requiere un usuario de **Supabase Auth**.
- El backend aún no está desplegado; no hay URL HTTPS pública.
- CORS se configura con `CORS_ORIGINS` (separados por comas); un cliente Android nativo no depende de CORS.
- Las pruebas unitarias no reemplazan pruebas de integración reales contra Supabase con dos cuentas, tokens revocados y escenarios RLS.

## Verificación

```bash
npm test
npm run check
npm start
```

El entorno CI/de prueba debe proporcionar variables de configuración válidas para ejecutar rutas autenticadas contra un proyecto Supabase **de pruebas**, nunca contra datos reales de usuarios. Comprueba también que al crear una categoría o un movimiento solo es accesible para su dueño.

**Pendiente:** desplegar bajo HTTPS, comprobar Auth/RLS con usuarios reales de prueba, generar un lockfile con `npm install` en un entorno con acceso al registro npm, preparar la integración Android y sincronizar el README raíz, que aún contiene decisiones anteriores sobre MySQL.
