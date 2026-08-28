# Career Intelligence Platform

A production-oriented portfolio project for analyzing how a candidate's technical skills align with a job description.

The first milestone provides a **Java 21 + Spring Boot REST API** that extracts recognized technical requirements from a job description, compares them with resume text, and returns a match score plus matched and missing skills.

## Why This Project

The goal is to build this incrementally into a full career intelligence platform while demonstrating practical backend engineering: clean APIs, validation, tests, containerization, CI/CD, persistence, event-driven processing, and eventually an Angular frontend and AI-assisted analysis.

## Current Architecture

```text
Client
  |
  v
Spring Boot REST API
  |
  v
Matching Service
  |
  +--> Required skill extraction
  +--> Resume skill matching
  +--> Match score
  +--> Missing skill analysis
```

## Technology

- Java 21
- Spring Boot
- Spring Web
- Jakarta Validation
- Spring Boot Actuator
- JUnit 5 / AssertJ
- Maven
- Docker
- GitHub Actions

## API

### Analyze Resume / Job Match

`POST /api/v1/matches`

Example request:

```json
{
  "resumeText": "Java Spring Boot AWS Docker",
  "jobDescription": "Looking for Java, Spring Boot, Kafka and AWS experience"
}
```

Example response:

```json
{
  "score": 75,
  "matchedSkills": ["java", "spring boot", "aws"],
  "missingSkills": ["kafka"]
}
```

## Run Locally

Requirements: Java 21 and Maven.

```bash
mvn spring-boot:run
```

Run tests:

```bash
mvn test
```

## Roadmap

- [x] Spring Boot service foundation
- [x] Resume/job skill matching API
- [x] Input validation
- [x] Unit tests
- [x] GitHub Actions CI
- [x] Docker configuration
- [ ] PostgreSQL persistence
- [ ] Application tracking service
- [ ] Kafka event processing
- [ ] Resume document ingestion
- [ ] AI-assisted semantic matching
- [ ] Angular dashboard
- [ ] Authentication and authorization
- [ ] Observability and cloud deployment

## Engineering Principles

The project will favor understandable production-style engineering over unnecessary complexity: modular boundaries, automated testing, secure configuration, observable services, and documented architectural decisions.
