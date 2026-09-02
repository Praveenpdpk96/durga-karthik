# Career Intelligence Platform

A production-oriented full-stack portfolio project for job matching and application intelligence.

**Live Demo:** https://career-intelligence-platform-pccm.onrender.com

Built with **Java 21, Spring Boot, Angular, PostgreSQL, Kafka, Docker, and GitHub Actions**, the platform analyzes technical fit between a resume and job description and tracks applications through the hiring lifecycle.

## Current Capabilities

- Resume-to-job technical match scoring
- Matched and missing skill identification
- Actionable match summaries and recommendations
- Persistent job application tracking
- Hiring lifecycle status management
- Optional Kafka domain events
- Responsive Angular dashboard
- Swagger/OpenAPI documentation
- Automated backend tests, frontend builds, and production container validation in CI
- Docker-based cloud deployment on Render with PostgreSQL

> The live demo runs on Render's free service tier, so the first request after inactivity can take longer while the service wakes up.

## Architecture

```text
Angular Dashboard
       |
       v
Spring Boot REST API
   |             |
   v             v
Match Engine   Application Service
                  |          |
                  v          v
             PostgreSQL    Kafka (optional)
```

## Technology

**Backend:** Java 21, Spring Boot 3, Spring Web, Validation, Spring Data JPA  
**Frontend:** Angular 18, TypeScript  
**Data:** PostgreSQL, H2  
**Messaging:** Apache Kafka  
**Platform:** Docker, Docker Compose, Render, GitHub Actions  
**Quality:** JUnit 5, Mockito, AssertJ, OpenAPI/Swagger, Actuator

## APIs

### Enhanced Resume / Job Analysis

`POST /api/v1/matches/semantic`

```json
{
  "resumeText": "Java Spring Boot AWS Docker",
  "jobDescription": "Looking for Java, Spring Boot, Kafka and AWS experience"
}
```

The response includes a score, matched skills, missing skills, a summary, and actionable recommendations.

> The current analysis engine is deterministic and explainable. A provider-backed LLM implementation is intentionally kept as a future integration rather than presenting rule-based matching as AI.

### Application Tracking

`POST /api/v1/applications`  
`GET /api/v1/applications`  
`PATCH /api/v1/applications/{id}/status`

Lifecycle states: `SAVED`, `APPLIED`, `ASSESSMENT`, `INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN`.

## Run the Backend

Lightweight local configuration using H2:

```bash
mvn spring-boot:run
```

Complete PostgreSQL + Kafka stack:

```bash
docker compose up --build
```

Swagger UI: `http://localhost:8080/swagger-ui.html`  
Health: `http://localhost:8080/actuator/health`

## Run the Angular Dashboard

```bash
cd frontend
npm install
npm start
```

The Angular development server proxies `/api` requests to the Spring Boot API on port `8080`.

## Testing

```bash
mvn test
cd frontend && npm run build
```

GitHub Actions runs backend tests, validates the Angular production build, and builds the production Docker image.

## Event-Driven Workflow

When Kafka events are enabled, application creation and status changes publish domain events to `career.application-events`. For the hosted demo, Kafka events are disabled and a no-op publisher preserves the same service boundary without requiring a Kafka broker. This provides a clean integration point for analytics, notifications, recommendations, and audit processing.

## Deployment

The production image uses a multi-stage Docker build: Angular is compiled first, the generated frontend is packaged into the Spring Boot application, and the final runtime image runs on Java 21. The live Render service connects to PostgreSQL through environment-provided database configuration and exposes Actuator health checks for deployment monitoring.

## Roadmap

- [x] Spring Boot service foundation
- [x] Resume/job skill matching API
- [x] Enhanced explainable match analysis
- [x] PostgreSQL persistence
- [x] Application tracking service
- [x] Kafka lifecycle events
- [x] Angular dashboard
- [x] Docker environment
- [x] GitHub Actions CI
- [x] OpenAPI / Swagger documentation
- [x] Cloud deployment
- [ ] Resume PDF/DOCX ingestion
- [ ] Provider-backed LLM semantic analysis
- [ ] Authentication and authorization
- [ ] Metrics, tracing, and structured logging

## Engineering Principles

This project favors understandable production-style engineering over unnecessary complexity: modular boundaries, automated testing, environment-based configuration, asynchronous integration, documented APIs, and incremental architecture.
