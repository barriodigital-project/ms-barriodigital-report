# ms-barriodigital-report

Microservicio encargado de indicadores y reportería de **BarrioDigital**.

## Responsabilidades

- Consultar indicadores y reportes de trámites.
- Mantener información de reportería.
- Generar KPIs.
- Consultar trámites agrupados por estado.
- Consultar tipos de trámite más solicitados.
- Evitar consultas analíticas sobre la base de Requests.
- Consumir eventos de solicitudes para mantener el modelo de lectura.

## Stack tecnológico

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Maven
- MySQL
- Lombok
- Spring Boot Actuator
- Docker

## Puerto

```text
8087
```

## Base de datos

```text
report_db
```

Al ejecutar desde esta carpeta, Spring carga `DB_PASSWORD` desde el archivo
compartido `../.env` del workspace. Ese archivo local no se versiona.
`DB_PASSWORD` es obligatoria; no existe una contraseña fallback.

## Modelo

```text
RequestReport
├── id
├── requestId
├── procedureTypeId
├── status
└── updatedAt
```

## API

```http
GET /api/v1/reports/kpis
GET /api/v1/reports/requests-by-status
GET /api/v1/reports/top-procedure-types
POST /api/v1/internal/request-events
```

El endpoint de ingesta es interno y solo debe aceptar tráfico desde Lambda;
no se expone por BFF ni por API Gateway.

La versión mayor de la API se expresa en la ruta (`/api/v1`). Los reportes
usan el estado actual de cada trámite: incluyen todos los estados con su total
(incluido cero), y el ranking completo de tipos se ordena por total descendente.
Los empates se ordenan por UUID ascendente.

## Fuente de datos pendiente

```text
Requests → Outbox → SQS → Lambda → Report
                               ↓
                     RequestReport + eventId
```

## Seguridad

La autenticación y autorización del rol `ADMIN` quedan a cargo del BFF/gateway.
Report no valida tokens y debe aceptar únicamente tráfico interno; no se debe
exponer directamente el puerto `8087`.

## Observabilidad

- Spring Boot Actuator.
- Logs.
- Métricas.
- Amazon CloudWatch.

## Estructura esperada

```text
src/
├── main/
│   ├── java/
│   │   └── cl/brarriodigital/report/
│   │       ├── controller/
│   │       ├── dto/
│   │       │   └── response/
│   │       ├── exception/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       └── ReportMsApplication.java
│   └── resources/
│       └── application.properties
└── test/
```

## Alcance de la primera etapa

La carga de `RequestReport` se realiza mediante los eventos de Requests
consumidos por Lambda. `eventId` se registra transaccionalmente junto al cambio
de reportería para que las entregas repetidas no dupliquen ni reviertan el
estado. Eventos antiguos no sobrescriben estados más recientes.

## Configuración local

El servicio escucha en `localhost:8087` y usa MySQL en `localhost:3306` por
defecto. Se pueden sobrescribir `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`
y `DB_PASSWORD`. `JPA_DDL_AUTO` controla el DDL de Hibernate (por defecto
`update`; usar una estrategia controlada por migraciones en producción).
Actuator expone `health`, `info` y `metrics` bajo `/actuator`.

## Service Discovery

El servicio se registra en Eureka. Por defecto usa
`http://localhost:8761/eureka/`; se puede cambiar mediante `EUREKA_SERVER_URL`.

El endpoint `POST /api/v1/internal/request-events` responde 204 al procesar un
evento nuevo o repetido, y 400 si el contrato no es válido. Los errores de
persistencia responden con error para que Lambda solicite el reintento de SQS.

## Contratos

```text
barriodigital-contracts/openapi/report.openapi.yaml
```

## Ejecución

```bash
./mvnw spring-boot:run
```

Para iniciar la base local junto con los demás servicios, usar
[`compose-up.ps1`](../barriodigital-infra/scripts/compose-up.ps1) desde la raíz
del workspace.

## Docker

```bash
docker build -t barriodigital/report:1.0.0 .
```

## Estrategia Git

```text
main
develop
feature/*
fix/*
```

## Estado

🚧 Proyecto en etapa inicial de diseño e implementación.
