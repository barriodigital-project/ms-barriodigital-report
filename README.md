# ms-barriodigital-report

Microservicio encargado de indicadores y reportería de **BarrioDigital**.

## Responsabilidades

- Consumir eventos desde Kafka.
- Mantener información de reportería.
- Generar KPIs.
- Consultar trámites agrupados por estado.
- Consultar tipos de trámite más solicitados.
- Evitar consultas analíticas sobre la base de Requests.

## Stack tecnológico

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Maven
- MySQL
- Kafka
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

## Kafka

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

## Flujo

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
│   │   └── cl/duoc/barriodigital/report/
│   │       ├── config/
│   │       ├── consumer/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── repository/
│   │       ├── service/
│   │       ├── security/
│   │       └── exception/
│   └── resources/
│       └── application.yml
└── test/
```

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
