# ms-barriodigital-report

Microservicio encargado de indicadores y reportería de **BarrioDigital**.

## Responsabilidades

- Consultar indicadores y reportes de trámites.
- Mantener información de reportería.
- Generar KPIs.
- Consultar trámites agrupados por estado.
- Consultar tipos de trámite más solicitados.
- Evitar consultas analíticas sobre la base de Requests.
- La ingesta de datos desde Kafka se incorporará en una etapa posterior.

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

## Integración futura con Kafka

Tópico:

```text
requests.events
```

Consumer Group:

```text
barriodigital-report-group
```

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
```

La versión mayor de la API se expresa en la ruta (`/api/v1`). Los reportes
usan el estado actual de cada trámite: incluyen todos los estados con su total
(incluido cero), y el ranking completo de tipos se ordena por total descendente.
Los empates se ordenan por UUID ascendente.

### Ejemplos HTTP

```bash
curl http://localhost:8087/api/v1/reports/kpis
```

```json
{
  "totalRequests": 3,
  "ingresados": 1,
  "admitidos": 1,
  "enGestion": 0,
  "enTerreno": 0,
  "resueltos": 1,
  "rechazados": 0
}
```

```bash
curl http://localhost:8087/api/v1/reports/requests-by-status
```

```json
[
  { "status": "INGRESADO", "total": 1 },
  { "status": "ADMITIDO", "total": 1 },
  { "status": "EN_GESTION", "total": 0 },
  { "status": "EN_TERRENO", "total": 0 },
  { "status": "RESUELTO", "total": 1 },
  { "status": "RECHAZADO", "total": 0 }
]
```

```bash
curl http://localhost:8087/api/v1/reports/top-procedure-types
```

```json
[
  { "procedureTypeId": "00000000-0000-0000-0000-000000000001", "total": 2 },
  { "procedureTypeId": "00000000-0000-0000-0000-000000000002", "total": 1 }
]
```

## Flujo futuro

```text
Requests
   ↓
Kafka
   ↓
Report
   ↓
MySQL
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

La API de lectura y su modelo local quedan implementados, pero la carga de
`RequestReport` está pendiente hasta integrar Kafka u otra fuente de escritura.
Mientras la tabla esté vacía, los KPIs serán cero y las listas estarán vacías.
Kafka no forma parte de esta etapa.

## Configuración local

El servicio escucha en `localhost:8087` y usa MySQL en `localhost:3306` por
defecto. Se pueden sobrescribir `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`
y `DB_PASSWORD`. `JPA_DDL_AUTO` controla el DDL de Hibernate (por defecto
`update`; usar una estrategia controlada por migraciones en producción).
Actuator expone `health`, `info` y `metrics` bajo `/actuator`.

## Service Discovery

El servicio se registra en Eureka. Por defecto usa
`http://localhost:8761/eureka/`; se puede cambiar mediante `EUREKA_SERVER_URL`.

## Contratos

```text
barriodigital-contracts/openapi/report.openapi.yaml
barriodigital-contracts/asyncapi/kafka.asyncapi.yaml
```

## Ejecución

```bash
./mvnw spring-boot:run
```

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
