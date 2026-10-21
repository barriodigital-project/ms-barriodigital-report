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
- Spring Security
- Eureka Client
- Spring Boot Actuator
- Docker

## Puerto

```text
8087
```

## Base de datos

```text
barriodigital_report_db
```

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

Rol principal:

```text
ADMIN
```

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
│   │       │   ├── request/
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

Este primer incremento establece la estructura por capas para preparar la API
de reportes. No incorpora Kafka ni una fuente de escritura de datos; por lo
tanto, la ingesta necesaria para generar reportes queda pendiente.

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
