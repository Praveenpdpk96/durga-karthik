# Career Intelligence Platform

A production-oriented full-stack portfolio project for job matching and application intelligence.

The backend is built with **Java 21 + Spring Boot** and currently supports resume/job skill matching, persistent application tracking, lifecycle status management, and optional Kafka domain events.

## Architecture

```text
                         +----------------------+
                         |     Client / UI      |
                         +----------+-----------+
                                    |
                                    v
                         +----------------------+
                         | Spring Boot REST API |
                         +----+-------------+---+
                              |             |
                    +---------+--+       +--+----------------+
                    | Matching   |       | Application       |
                    | Service    |       | Tracking Service  |
                    +------------+       +----+----------+---+
                                              |          |
                                              v          v
                                        PostgreSQL     Kafka
                                                        |
                                                        v
                                              Application Events
```

Kafka publishing is feature-controlled. Local development can run without a broker, while Docker Compose enables the complete PostgreSQL + Kafka environment.

## Technology

- Java 21
- Spring Boot 3
- Spring Web / Jakarta Validation
- Spring Data JPA
- PostgreSQL / H2
- Apache Kafka
- OpenAPI / Swagger UI
- Spring Boot Actuator
- JUnit 5 / Mockito / AssertJ
- Maven
- Docker / Docker Compose
- GitHub Actions

## APIs

### Resume / Job Match

`POST /api/v1/matches`

```json
{
  "resumeText": "Java Spring Boot AWS Docker",
  "jobDescription": "Looking for Java, Spring Boot, Kafka and AWS experience"
}
```

```json
{
  "score": 75,
  "matchedSkills": ["java", "spring boot", "aws"],
  "missingSkills": ["kafka"]
}
```

### Track an Application

`POST /api/v1/applications`

```json
{
  "company": "Example Corp",
  "role": "Senior Software Engineer",
  "jobUrl": "https://example.com/jobs/123",
  "status": "APPLIED"
}
```

`GET /api/v1/applications`

`PATCH /api/v1/applications/{id}/status`

```json
{
  "status": "INTERVIEW"
}
```

Supported lifecycle states include `SAVED`, `APPLIED`, `ASSESSMENT`, `INTERVIEW`, `OFFER`, `REJECTED`, and `WITHDRAWN`.

## API Documentation

After starting the application, Swagger UI is available at:

`http://localhost:8080/swagger-ui.html`

Health endpoint:

`http://localhost:8080/actuator/health`

## Run Locally

For the lightweight H2 configuration:

```bash
mvn spring-boot:run
```

For the complete PostgreSQL + Kafka stack:

```bash
docker compose up --build
```

Run tests:

```bash
mvn test
```

## Event-Driven Workflow

When Kafka events are enabled, application creation and status changes publish domain events to `career.application-events`. This creates a foundation for independent consumers such as analytics, notifications, recommendations, and audit processing without tightly coupling those capabilities to the application service.

## Roadmap

- [x] Spring Boot service foundation
- [x] Resume/job skill matching API
- [x] Input validation and API error handling
- [x] Unit tests
- [x] GitHub Actions CI
- [x] Docker configuration
- [x] PostgreSQL persistence
- [x] Application tracking service
- [x] Kafka application lifecycle events
- [x] OpenAPI / Swagger documentation
- [ ] Resume document ingestion
- [ ] AI-assisted semantic matching
- [ ] Angular dashboard
- [ ] Authentication and authorization
- [ ] Metrics, tracing, and structured logging
- [ ] Cloud deployment

## Engineering Principles

This project favors understandable production-style engineering over unnecessary complexity: modular boundaries, automated testing, environment-based configuration, asynchronous integration, documented APIs, and incremental architecture.
