# Finanzas G7 API

API REST del proyecto (Node.js + Express 5) con base de datos PostgreSQL en **Supabase**.

```text
api/
├── database/
│   ├── schema.sql        Tablas, triggers, función de totales y RLS
│   ├── seed.sql          Catálogo de sugerencias educativas
│   └── migraciones/      Cambios para bases ya creadas (ejecutar en orden)
└── src/
    ├── config/           Variables de entorno y cliente de Supabase
    ├── controllers/      Lógica de cada recurso
    ├── middlewares/      Autenticación JWT, validación y errores
    ├── routes/           Definición de endpoints (/api/v1)
    ├── schemas/          Validaciones con Zod
    ├── services/         Resumen financiero y reglas de sugerencias
    ├── utils/
    ├── app.js
    └── server.js
```

## 1. Crear la base de datos en Supabase

1. Crear un proyecto en <https://supabase.com>.
2. Ir a **SQL Editor**, pegar el contenido de `database/schema.sql` y ejecutarlo.
3. Luego ejecutar `database/seed.sql`.
4. Ejecutar en orden los archivos de `database/migraciones/` (la `001` agrega el rol y crea las cuentas de administrador del equipo).

Al registrarse, cada usuario recibe automáticamente su **configuración** y seis **categorías predeterminadas** (Vivienda, Alimentación, Transporte, Ocio, Salud e Ingresos), mediante un trigger.

Todas las tablas tienen RLS activado sin políticas: la `anon key` pública no puede leer nada. Solo la API, con la `service_role key`, accede a los datos.

## 2. Configurar y ejecutar la API

```bash
cd api
npm install
cp .env.example .env    # completar SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY y JWT_SECRET
npm run dev             # o: npm start
```

La URL y la `service_role key` están en Supabase > **Project Settings > API**. La `service_role key` es secreta: va solo en el `.env` del servidor, **nunca** en la app Android ni en el repositorio.

Comprobación: `GET http://localhost:3000/api/v1/salud`.

> Desde el emulador de Android, `localhost` del PC es `http://10.0.2.2:3000/api/v1`. Desde un celular físico en la misma red Wi-Fi se usa la IP local del PC, por ejemplo `http://192.168.0.113:3000/api/v1` (`ipconfig` la muestra).

### Configurar la app Android

En `local.properties` (raíz del proyecto, no versionado):

```properties
API_BASE_URL=http://<ip-del-pc>:3000/api/v1
```

Luego se recompila la app (`gradlew assembleDebug`). La URL queda disponible como `BuildConfig.API_BASE_URL`.

## 3. Autenticación

`POST /usuarios` (registro) y `POST /usuarios/sesion` (inicio de sesión) devuelven `{ usuario, token }`. El resto de endpoints exige la cabecera:

```text
Authorization: Bearer <token>
```

Cada usuario solo ve y modifica sus propios datos. El parámetro `usuario_id` del contrato original es opcional: el usuario se toma del token y, si se envía otro id, se responde `403`.

### Roles

| Rol | Permisos |
|---|---|
| `USUARIO` | Rol por defecto al registrarse. Solo accede a sus datos. |
| `ADMIN` | Puede listar todos los usuarios (`GET /usuarios`), consultar o editar cualquier `/usuarios/{id}` y actuar sobre los datos de otro usuario enviando `?usuario_id={id}` en categorías, movimientos, resumen y sugerencias. |

El rol no se puede elegir al registrarse: se asigna en la base de datos (ver `migraciones/001_rol_y_administradores.sql`).

## 4. Endpoints (`/api/v1`)

| Método | Endpoint | Descripción | HU |
|---|---|---|---|
| `POST` | `/usuarios` | Registro: `nombre, correo, contrasena, telefono?, saldo_inicial?` | HU-01 |
| `POST` | `/usuarios/sesion` | Inicio de sesión: `correo, contrasena` | — |
| `GET` | `/usuarios` | Lista de todos los usuarios (**solo ADMIN**) | — |
| `GET` | `/usuarios/{id}` | Datos del usuario | HU-01 |
| `PUT` | `/usuarios/{id}` | Actualiza `nombre`, `telefono`, `saldo_inicial` | HU-01 |
| `GET` | `/usuarios/{id}/configuracion` | Moneda base, meta de ahorro, notificaciones | HU-01 |
| `PUT` | `/usuarios/{id}/configuracion` | Actualiza la configuración | HU-01 |
| `GET` | `/categorias?tipo=&desde=&hasta=` | Categorías con `total_periodo`, `participacion` (%) y `uso_presupuesto` (%). Por defecto, el mes actual | HU-04 |
| `GET` | `/categorias/{id}` | Categoría con sus totales del mes actual | HU-04 |
| `POST` | `/categorias` | `nombre, tipo, color_hex?, presupuesto_mensual?` | HU-04 |
| `PUT` | `/categorias/{id}` | `nombre?, color_hex?, presupuesto_mensual?` (el tipo no se cambia) | HU-04 |
| `DELETE` | `/categorias/{id}` | `409` si tiene movimientos asociados | HU-04 |
| `GET` | `/movimientos?categoria_id=&tipo=&desde=&hasta=&pagina=&limite=` | Historial (más reciente primero). Total en la cabecera `X-Total-Count` | HU-06 |
| `GET` | `/movimientos/{id}` | Un movimiento (incluye `categoria`) | HU-06 |
| `POST` | `/movimientos` | `categoria_id, monto, descripcion, tipo?, medio_pago?, fecha_movimiento?` | HU-02, HU-03 |
| `PUT` | `/movimientos/{id}` | Cualquier campo del movimiento | HU-09 |
| `DELETE` | `/movimientos/{id}` | Elimina el movimiento | HU-09 |
| `GET` | `/resumen?desde=&hasta=` | `saldo_disponible`, totales del período y desglose `por_categoria` | HU-05, HU-07 |
| `GET` | `/sugerencias` | Sugerencias que aplican al usuario según su mes actual (`?todas=true` = catálogo completo) | HU-08 |

- Fechas en formato `YYYY-MM-DD`. Montos en número con hasta 2 decimales.
- En un movimiento, `tipo` es opcional: si no se envía se toma de la categoría; si se envía y no coincide, responde `400`.

### Ejemplos

```http
POST /api/v1/usuarios
Content-Type: application/json

{ "nombre": "Ana", "correo": "ana@correo.com", "contrasena": "secreta123", "saldo_inicial": 1500000 }
```

```http
POST /api/v1/movimientos
Authorization: Bearer <token>
Content-Type: application/json

{ "categoria_id": 2, "monto": 115000, "descripcion": "Supermercado", "medio_pago": "Tarjeta débito", "fecha_movimiento": "2026-11-08" }
```

## 5. Respuestas de error

Todas tienen la forma `{ "error": "mensaje", "detalles": [...]? }`.

| Código | Situación |
|---|---|
| `400` | Datos inválidos (con `detalles` por campo) o JSON mal formado |
| `401` | Token ausente, inválido o expirado; credenciales incorrectas |
| `403` | Intento de acceder a datos de otro usuario |
| `404` | Recurso inexistente |
| `409` | Correo ya registrado, categoría duplicada o con movimientos |
| `500` | Error del servidor |

## 6. Sugerencias (HU-08)

Las reglas están en `src/services/reglasSugerencias.js` y se asocian a cada fila de la tabla `sugerencia` por la columna `regla_asociada`:

| Regla | Aplica cuando (mes actual) |
|---|---|
| `GASTOS_SUPERAN_INGRESOS` | Los gastos superan los ingresos |
| `PRESUPUESTO_EXCEDIDO` | Alguna categoría de gasto superó su presupuesto |
| `PRESUPUESTO_CERCANO` | Alguna categoría usó el 80 % o más de su presupuesto |
| `META_AHORRO_NO_CUMPLIDA` | Ingresos − gastos < meta de ahorro mensual |
| `OCIO_ELEVADO` | La categoría Ocio supera el 30 % de los gastos |
| `SIN_INGRESOS_MES` | No hay ingresos registrados |
| `GENERAL` | Siempre |

Si una sugerencia tiene filas en `sugerencia_categoria` (relación N:M), solo se muestra cuando la regla aplica a alguna de esas categorías.
