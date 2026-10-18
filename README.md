<div align="center">

# Proyecto Integrador Móviles G7

### Aplicación móvil para la gestión y análisis de finanzas personales

[![Android](https://img.shields.io/badge/Android-Plataforma-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-Lenguaje-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Interfaz-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=flat-square&logo=gradle&logoColor=white)](https://gradle.org/)
[![GitHub](https://img.shields.io/badge/GitHub-Repositorio-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7)

</div>

---

## Descripción

Proyecto Integrador Móviles G7 es una aplicación móvil Android planificada para apoyar el control manual de las finanzas personales. Permitirá registrar un saldo inicial, ingresos, gastos y categorías, y utilizar esa información para consultar el saldo disponible, revisar el historial financiero y analizar la distribución de los movimientos.

El producto también contempla sugerencias educativas de ahorro obtenidas mediante reglas sencillas y explicables aplicadas exclusivamente a la información registrada por la persona usuaria.

> [!IMPORTANT]
> Las funcionalidades descritas en este documento corresponden al alcance planificado del MVP. La implementación, las pruebas de ejecución y la publicación de una versión estable continúan pendientes.

## Objetivo

Permitir que una persona lleve control manual de su dinero registrando saldo inicial, ingresos y gastos, para consultar posteriormente su situación financiera, historial, distribución de gastos e informes, junto con orientaciones educativas básicas sobre hábitos de ahorro.

## Tabla de contenido

- [Funcionalidades planificadas](#funcionalidades-planificadas)
- [Alcance del MVP](#alcance-del-mvp)
- [Fuera del alcance](#fuera-del-alcance)
- [Tecnologías](#tecnologías)
- [Estado actual del proyecto](#estado-actual-del-proyecto)
- [Historias de Usuario](#historias-de-usuario)
- [Gestión del proyecto](#gestión-del-proyecto)
- [Flujo de trabajo](#flujo-de-trabajo)
- [Equipo](#equipo)
- [Estrategia de ramas](#estrategia-de-ramas)
- [Convención de commits](#convención-de-commits)
- [Estructura documental](#estructura-documental)
- [Requisitos de desarrollo](#requisitos-de-desarrollo)
- [Instalación y clonación](#instalación-y-clonación)
- [Compilación básica](#compilación-básica)
- [Colaboración](#colaboración)
- [Enlaces del proyecto](#enlaces-del-proyecto)

## Funcionalidades planificadas

El MVP contempla las siguientes capacidades:

- Registrar un saldo inicial como referencia financiera.
- Registrar ingresos y actualizar el saldo disponible.
- Registrar gastos y descontarlos del saldo.
- Clasificar movimientos mediante categorías.
- Consultar el saldo, los ingresos y los gastos acumulados.
- Consultar y filtrar el historial de movimientos.
- Visualizar informes de ingresos, gastos y categorías.
- Recibir sugerencias educativas de ahorro basadas en los movimientos registrados.
- Editar y eliminar movimientos, conservando la consistencia de los cálculos.

## Alcance del MVP

La primera versión funcional se enfoca en el registro manual y la consulta de información financiera personal dentro de la aplicación. El alcance comprende:

| Área | Alcance planificado |
|---|---|
| Registro financiero | Saldo inicial, ingresos y gastos |
| Organización | Categorías para clasificar movimientos |
| Consulta | Resumen financiero e historial |
| Análisis | Totales, períodos y distribución de gastos por categoría |
| Mantenimiento | Edición y eliminación de movimientos |
| Educación financiera | Sugerencias de ahorro simples, comprensibles y no profesionales |

## Fuera del alcance

El MVP actual no contempla:

- Integración bancaria o conexión automática con entidades financieras.
- Pagos o transferencias desde la aplicación.
- Créditos, inversiones o criptomonedas.
- Recomendación de productos financieros.
- Asesoría financiera profesional.
- Sincronización con servicios externos no aprobados.

Las sugerencias de ahorro tendrán un propósito exclusivamente educativo y se basarán en patrones simples de los movimientos registrados.

## Tecnologías

Las tecnologías confirmadas actualmente son:

| Tecnología | Uso previsto |
|---|---|
| Android Studio | Entorno de desarrollo |
| Kotlin | Lenguaje principal |
| Gradle | Construcción y gestión del proyecto |
| Jetpack Compose | Desarrollo de la interfaz Android |
| Git | Control de versiones |
| GitHub | Repositorio, Issues, Pull Requests y gestión del proyecto |

> [!NOTE]
> La arquitectura y la estrategia de persistencia definitivas se definirán en las tareas TT-01 y TT-02. Room, SQLite, Firebase, Supabase, Hilt, Retrofit, MVVM, Clean Architecture, servicios cloud, APIs externas o un backend no se consideran decisiones aprobadas en este momento.

## Estado actual del proyecto

| Elemento | Estado |
|---|---|
| Repositorio | ✅ Configurado |
| Protección de `main` | ✅ Configurada |
| Equipo | ✅ Vinculado |
| GitHub Project | ✅ Configurado |
| Historias de Usuario | ✅ Definidas |
| Tareas Técnicas | ✅ Definidas |
| Trazabilidad HU ↔ TT | ✅ Configurada |
| Documentación inicial | ✅ Creada |
| Arquitectura definitiva | ⏳ Pendiente |
| Persistencia definitiva | ⏳ Pendiente |
| Implementación | ⏳ Pendiente |
| Pruebas de ejecución | ⏳ Pendiente |
| Release MVP | ⏳ Pendiente |

## Historias de Usuario

El backlog funcional inicial está compuesto por nueve Historias de Usuario:

| ID | Issue | Historia | Estado inicial |
|---|---:|---|---|
| HU-01 | [#8](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues/8) | Registrar saldo inicial | Backlog |
| HU-02 | [#10](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues/10) | Registrar ingresos | Backlog |
| HU-03 | [#6](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues/6) | Registrar gastos | Backlog |
| HU-04 | [#5](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues/5) | Clasificar movimientos por categorías | Backlog |
| HU-05 | [#1](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues/1) | Consultar saldo y resumen financiero | Backlog |
| HU-06 | [#9](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues/9) | Consultar historial de movimientos | Backlog |
| HU-07 | [#7](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues/7) | Visualizar informes de ingresos y gastos | Backlog |
| HU-08 | [#3](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues/3) | Recibir sugerencias de ahorro | Backlog |
| HU-09 | [#11](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues/11) | Editar y eliminar movimientos financieros | Backlog |

Estas historias están relacionadas con 14 Tareas Técnicas. La tarea documental DOC-01 es adicional y no altera el conteo funcional del MVP.

## Gestión del proyecto

La planificación se administra mediante [Proyecto Integrador Móviles G7 — Project #6](https://github.com/users/JuanEstebanLT/projects/6), asociado al milestone **MVP v1.0 - Control financiero personal**.

| Elemento de planificación | Cantidad |
|---|---:|
| Historias de Usuario | 9 |
| Tareas Técnicas | 14 |
| Elementos del backlog funcional | 23 |

El tablero utiliza los siguientes estados:

`Backlog` → `Ready` → `In Progress` → `In Review` → `QA` → `Done`

## Flujo de trabajo

Cada cambio debe mantener trazabilidad con un Issue y seguir este flujo:

```text
Issue
  ↓
Rama
  ↓
Desarrollo
  ↓
Commit
  ↓
Push
  ↓
Pull Request
  ↓
In Review
  ↓
QA
  ↓
Merge
  ↓
Done
```

No se debe desarrollar directamente sobre `main`. Cada Pull Request debe estar vinculado a su Issue, presentar un alcance acotado y superar revisión y QA antes de integrarse.

## Equipo

| Integrante | Rol |
|---|---|
| Juan Esteban Lezcano Tejada | Líder de Desarrollo y QA |
| Emmanuel Villa | Desarrollador |
| Juan Bastidas | Desarrollador |
| Mariana Suarez Higuita | Desarrolladora |

## Estrategia de ramas

El repositorio mantiene `main` como línea principal protegida y cuenta con las siguientes ramas personales:

- `dev/juan-esteban-lezcano`
- `dev/emmanuel-villa`
- `dev/juan-bastidas`
- `dev/mariana-suarez`

Para trabajos concretos se deben crear ramas específicas desde la base acordada y relacionarlas con el número del Issue:

```text
feature/14-saldo-inicial
docs/<issue>-readme-profesional
fix/<issue>-descripcion
```

## Convención de commits

Se utiliza una convención breve basada en el tipo de cambio:

| Prefijo | Uso |
|---|---|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de un defecto |
| `test:` | Creación o actualización de pruebas |
| `docs:` | Cambios de documentación |
| `refactor:` | Mejora interna sin cambiar el comportamiento esperado |
| `chore:` | Mantenimiento o configuración |

Formato recomendado:

```text
tipo(alcance): descripción breve (#issue)
```

Ejemplo:

```text
feat(finanzas): implementar registro de saldo inicial (#14)
```

## Estructura documental

El proyecto cuenta con documentación formal organizada fuera del código fuente en las siguientes áreas:

```text
00_Gobierno_Documental
01_Inicio_y_Planeacion
02_Requerimientos
03_Diseño
04_Calidad
05_Manuales
06_Cierre
07_Evaluacion
08_Repositorio
```

Esta documentación reúne gobierno documental, planeación, requisitos, propuestas de diseño, matrices de calidad, manuales, plantillas de cierre, evaluación y lineamientos del repositorio.

## Requisitos de desarrollo

Para trabajar con el proyecto se requiere:

- Git.
- Android Studio.
- Android SDK configurado.
- JDK compatible con la configuración Gradle del repositorio.
- Acceso autorizado al repositorio para publicar ramas y crear Pull Requests.

## Instalación y clonación

1. Clonar el repositorio:

   ```powershell
   git clone https://github.com/JuanEstebanLT/proyectointegradormoviles-g7.git
   ```

2. Acceder al directorio del proyecto:

   ```powershell
   cd proyectointegradormoviles-g7
   ```

3. Abrir el proyecto desde Android Studio.

4. Permitir que Gradle sincronice la configuración existente.

## Compilación básica

En Windows, desde la raíz del repositorio:

```powershell
.\gradlew.bat build
```

La disponibilidad de una compilación satisfactoria dependerá del entorno Android, el JDK y las versiones configuradas en el proyecto.

## Colaboración

Antes de comenzar un cambio:

1. Seleccionar o crear el Issue correspondiente.
2. Confirmar alcance, responsable, prioridad y dependencias.
3. Crear una rama específica relacionada con el Issue.
4. Mantener commits pequeños, claros y trazables.
5. Publicar la rama y abrir un Pull Request hacia `main`.
6. Atender observaciones de revisión.
7. Completar QA antes del merge.
8. Integrar únicamente mediante un Pull Request aprobado.

## Enlaces del proyecto

- [Repositorio oficial](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7)
- [GitHub Project #6](https://github.com/users/JuanEstebanLT/projects/6)
- [Milestone MVP v1.0 - Control financiero personal](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/milestone/1)
- [Issues del proyecto](https://github.com/JuanEstebanLT/proyectointegradormoviles-g7/issues)

---

<div align="center">

**Equipo de Desarrollo Móviles G7**<br>
Proyecto Integrador Móviles G7

</div>
