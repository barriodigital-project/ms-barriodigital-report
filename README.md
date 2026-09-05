# ms-barriodigital-report

Microservicio encargado de reportería, indicadores y analítica de **BarrioDigital**.

## Descripción

Este servicio mantiene información orientada a consultas y visualización de indicadores sin impactar directamente el procesamiento transaccional de los trámites.

Se alimenta principalmente mediante eventos publicados en Kafka.

## Responsabilidades

- Consumir eventos de trámites.
- Construir proyecciones para reportería.
- Calcular indicadores.
- Entregar información para dashboards.
- Mantener estadísticas.
- Evitar consultas analíticas pesadas sobre la base transaccional de Requests.

## Indicadores iniciales

- Trámites por hora.
- Tiempo promedio de resolución.
- Estados activos.
- Tipos de trámite más demandados.
- Cantidad de trámites ingresados.
- Cantidad de trámites resueltos.
- Cantidad de trámites rechazados.

## Stack tecnológico

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Maven
- MySQL
- Apache Kafka
- Spring Security
- Spring Boot Actuator
- Docker

## Puerto local

```text
8087
```

## Base de datos

```text
barriodigital_report_db
```

Esta base está orientada a consultas y agregaciones propias de reportería.

## Kafka

Tópico principal:

```text
requests.events
```

Eventos relevantes:

```text
REQUEST_CREATED
REQUEST_ADMITTED
REQUEST_IN_PROGRESS
REQUEST_ON_SITE
REQUEST_RESOLVED
REQUEST_REJECTED
```

Report utiliza su propio Consumer Group para procesar los eventos de manera independiente de Audit.

## API

Base:

```text
/api/v1/reports
```

Endpoints iniciales:

```http
GET /api/v1/reports/kpis
GET /api/v1/reports/requests-per-hour
GET /api/v1/reports/resolution-times
GET /api/v1/reports/active-statuses
GET /api/v1/reports/top-procedure-types
```

Filtros previstos:

```text
from
to
range
```

Ejemplo:

```http
GET /api/v1/reports/kpis?range=last24h
```

## Seguridad

Rol principal:

```text
ADMIN
```

Dependiendo de futuros requerimientos se podrán incorporar otros roles de consulta.

## Principio arquitectónico

La reportería no debe bloquear el flujo principal de Requests.

El flujo esperado es:

```text
Requests
   ↓
Kafka
   ↓
Report
   ↓
Report DB
```

De esta forma, si Report presenta problemas temporales, el core transaccional puede continuar procesando trámites.

## Buenas prácticas Kafka

- Consumer Groups.
- Idempotencia.
- Manejo de offsets.
- Reintentos.
- Dead Letter Topic.
- Métricas de consumer lag.
- Eventos versionados.

## Observabilidad

- Spring Boot Actuator.
- Health checks.
- Logs.
- Métricas.
- Kafka consumer lag.
- CloudWatch.

## Variables de entorno

```env
SERVER_PORT=8087

MYSQL_HOST=
MYSQL_PORT=3306
MYSQL_DATABASE=barriodigital_report_db
MYSQL_USER=
MYSQL_PASSWORD=

KAFKA_BOOTSTRAP_SERVERS=

EUREKA_SERVER_URL=
COGNITO_ISSUER_URI=
```

## Contratos

Repositorio:

```text
barriodigital-contracts
```

Contratos utilizados:

- OpenAPI para endpoints REST.
- AsyncAPI para Kafka.

## Estructura esperada

```text
src/
├── main/
│   ├── java/
│   │   └── cl/duoc/barriodigital/report/
│   │       ├── config/
│   │       ├── consumer/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   └── resources/
│       └── application.yml
└── test/
```

## Docker

```bash
docker build -t barriodigital/report:1.0.0 .
```

## CI/CD

Flujo previsto:

```text
GitHub
↓
GitHub Actions
↓
Maven Build / Tests
↓
SonarQube
↓
Snyk
↓
Docker Build
↓
Amazon ECR
↓
AWS EC2
```

## Estrategia Git

```text
main
develop
feature/*
fix/*
```

## Estado

🚧 Proyecto en etapa inicial de diseño y construcción.
