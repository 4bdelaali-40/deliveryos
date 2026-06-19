# DeliveryOS

Enterprise delivery management platform built with Java Spring Boot, React, and a Python AI engine — combining hexagonal architecture, real-time fleet tracking, and carbon intelligence.

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen) ![React](https://img.shields.io/badge/React-18-blue) ![TypeScript](https://img.shields.io/badge/TypeScript-5-blue) ![Docker](https://img.shields.io/badge/Docker-Compose-2496ED) ![Kafka](https://img.shields.io/badge/Kafka-Event%20Streaming-black) ![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Modules](#modules)
- [Features](#features)
- [Security](#security)
- [Carbon Intelligence](#carbon-intelligence)
- [Observability & Monitoring](#observability--monitoring)
- [Design Patterns](#design-patterns)
- [Database Schema](#database-schema)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Default Credentials](#default-credentials)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Environment Variables](#environment-variables)
- [Project Status](#project-status)
- [Contributing](#contributing)

---

## Overview

DeliveryOS is a full-stack logistics platform for managing deliveries, fleets, drivers, and optimized routes while tracking carbon emissions in real time. It is built following **Domain-Driven Design** and **Hexagonal Architecture** principles, with a clear separation between business logic and infrastructure.

Key highlights:

- Role-based access control (SUPER_ADMIN / ADMIN / DISPATCHER / DRIVER / VIEWER) with stateless JWT authentication, MFA, and refresh token rotation
- AI-powered route optimization using OR-Tools, with CO2 and ETA prediction via XGBoost, LSTM, and Prophet
- Real-time fleet tracking over WebSocket (STOMP), with live driver positions on an interactive map
- Carbon intelligence: per-delivery, per-vehicle, and per-tour CO2 tracking against monthly/quarterly/yearly objectives
- Asynchronous event processing with Apache Kafka
- Distributed tracing with Zipkin
- Desktop client built with Electron, React, and TypeScript

---

## Architecture

The backend follows **hexagonal architecture** (ports and adapters), isolating business logic from frameworks and external systems.

```
                     ┌──────────────────────────────────────────────┐
                     │         Electron + React Frontend :5173       │
                     │   Vite · TypeScript · TailwindCSS · Leaflet   │
                     │   React Query · React Router · Zustand        │
                     └─────────────────────┬───────────────────────┬─┘
                                            │ REST                   │ WebSocket (STOMP)
                     ┌──────────────────────▼─────────────────────┐ │
                     │           Spring Boot Backend :8080         │◀┘
                     │  ┌────────────────────────────────────────┐ │
                     │  │  JWT Auth Filter + MFA + RBAC          │ │
                     │  │  GpsSimulationService (fleet tracking) │ │
                     │  │  ScheduledTasks (CO2, notifications)   │ │
                     │  └────────────────────────────────────────┘ │
                     │  adapters/in/web   ·  application (use cases) │
                     │  adapters/out/persistence  ·  domain          │
                     └───────┬──────────────┬──────────────┬────────┘
                              │              │              │
                  ┌───────────▼───┐  ┌───────▼──────┐  ┌────▼─────┐
                  │  PostgreSQL   │  │  Redis       │  │  Kafka   │
                  │  + PostGIS    │  │  Cache/Session│ │ Zookeeper│
                  └───────────────┘  └──────────────┘  └──────────┘
                              │
                     ┌────────▼─────────┐        ┌──────────────┐
                     │  AI Engine :8000  │        │   Zipkin     │
                     │  FastAPI          │        │   :9411      │
                     │  OR-Tools · XGBoost│       │   Tracing    │
                     │  LSTM · Prophet · DBSCAN │  └──────────────┘
                     └───────────────────┘
```

### Design principles

- **Domain isolation** — business rules in `domain/` have no dependency on Spring, JPA, or HTTP.
- **Use case driven** — each application use case (`AuthUseCase`, `DeliveryUseCase`, `AnalyticsUseCase`, `TourUseCase`) orchestrates one cohesive business operation.
- **Adapters are swappable** — persistence and web layers depend on ports (interfaces), not on each other.
- **Event-driven processing** — Kafka handles asynchronous events (tour completion, notifications, CO2 calculations).
- **Enum-as-VARCHAR** — status and category columns are stored as `VARCHAR` with application-level validation, avoiding native PostgreSQL enum cast issues with Hibernate.

---

## Tech Stack

### Backend

| Layer | Technology | Purpose |
|---|---|---|
| Language | Java 21 (LTS) | Records, pattern matching, virtual threads |
| Framework | Spring Boot 3.3 | Auto-configuration, production-ready features |
| Security | Spring Security 6 + JJWT 0.12.x | Stateless auth, MFA, RBAC |
| Persistence | Spring Data JPA / Hibernate | Repository pattern, entity mapping |
| Database | PostgreSQL 15 + PostGIS | Relational storage with geospatial queries |
| Caching | Redis | Session management, caching |
| Messaging | Apache Kafka + Zookeeper | Async event streaming |
| Migrations | Flyway | Versioned schema migrations |
| Real-time | Spring WebSocket (STOMP + SockJS) | Live fleet position broadcasting |
| Tracing | Zipkin + Micrometer | Distributed request tracing |
| API Docs | springdoc-openapi | Swagger UI |
| Build | Maven | Dependency management |

### Frontend

| Layer | Technology | Purpose |
|---|---|---|
| Shell | Electron | Cross-platform desktop application |
| Language | TypeScript 5 | Type safety across the codebase |
| Framework | React 18 | Concurrent rendering, hooks |
| Build Tool | Vite | Fast HMR, dev proxy to backend |
| Styling | TailwindCSS | Utility-first, light/dark theme via CSS variables |
| Server state | React Query | Caching, pagination, background refetch |
| Routing | React Router v6 | Protected routes, role-based navigation |
| Maps | Leaflet + react-leaflet | Tour planning and live tracking maps |
| Charts | Recharts | Analytics dashboards |
| Global state | Zustand-based store | Auth state, tokens, user session |

### AI Engine

| Layer | Technology | Purpose |
|---|---|---|
| Language | Python 3.11+ | AI/ML services |
| Framework | FastAPI | REST API for optimization endpoints |
| Routing | OR-Tools | Vehicle routing problem (VRP) optimization |
| Prediction | XGBoost, LSTM | CO2 and demand prediction |
| Forecasting | Prophet | Time-series forecasting |
| Clustering | DBSCAN | Delivery zone clustering |

### Infrastructure

| Service | Port | Purpose |
|---|---|---|
| PostgreSQL/PostGIS | 5432 | Primary database |
| Redis | 6379 | Cache and sessions |
| Kafka | 9092 / 29092 | Event streaming |
| Zookeeper | 2181 | Kafka coordination |
| Zipkin | 9411 | Distributed tracing |
| pgAdmin | 5050 | Database administration |
| Kafka UI | 8090 | Kafka topic inspection |

---

## Modules

| Module | Description |
|---|---|
| **Dashboard** | Overview KPIs: total deliveries, delivery rate, in-transit count, CO2 today |
| **Deliveries** | Paginated delivery list with status/priority/city filters, detail drawer, status transitions |
| **Tours** | Vehicle/driver selection, AI route optimization, daily tour list with distance and CO2 metrics |
| **Live Tracking** | Real-time fleet positions on an interactive map via WebSocket |
| **Analytics** | KPIs by period (daily/weekly/monthly), delivery status distribution chart |
| **Carbon** | CO2 tracking by vehicle type, carbon reduction objectives |
| **Fleet** | Vehicle inventory: type, fuel, capacity, mileage, maintenance schedule |
| **Drivers** | Driver list with role, status, badges |
| **Admin** | User management: create, change role, activate/deactivate, delete |

---

## Features

### User Features

- **Authentication** — Login with JWT access/refresh tokens, MFA support, refresh token rotation
- **Dashboard** — Real-time KPIs and delivery status distribution
- **Delivery tracking** — Search by tracking code, view status history and attempt count
- **Tour optimization** — AI-assisted route planning with distance and CO2 gain estimates
- **Live fleet map** — See all active drivers' positions updated every few seconds
- **Carbon dashboard** — Visualize emissions by vehicle type against reduction targets

### Admin Features

- **User management** — Full CRUD: create users, assign roles, activate/deactivate, delete
- **Role-based access** — SUPER_ADMIN, ADMIN, DISPATCHER, DRIVER, and VIEWER roles enforced at the API layer via `@PreAuthorize`
- **Fleet oversight** — Monitor all vehicles, drivers, and their current assignments
- **Notifications** — In-app notification system per user and channel
- **Driver gamification** — Badge system rewarding delivery performance (eco-driving, perfect weeks, etc.)

---

## Security

1. **Stateless JWT Authentication**
   Access tokens are signed and short-lived. Refresh tokens are rotated on every use and stored with IP and user-agent metadata for audit purposes.

2. **Multi-Factor Authentication (MFA)**
   Optional TOTP-based MFA per user account, enforced at login.

3. **Role-Based Access Control (RBAC)**
   ```
   SUPER_ADMIN  → Full access: user management, role changes, deletions
   ADMIN        → User creation, activation/deactivation, fleet management
   DISPATCHER   → Tour planning, delivery assignment, analytics
   DRIVER       → Own deliveries, own tour, GPS reporting
   VIEWER       → Read-only access to dashboards
   ```
   Enforced via Spring Security `@PreAuthorize` annotations on every controller endpoint.

4. **Account Lockout**
   Failed login attempts are tracked per account; accounts are temporarily locked after repeated failures (`failed_attempts`, `locked_until`).

5. **Password Security**
   Passwords hashed with BCrypt. `@Valid` + Jakarta Bean Validation on all request DTOs. Centralized `GlobalExceptionHandler` prevents stack trace leakage in API responses.

6. **CORS**
   Configured at the Spring Security filter chain level, restricted to the frontend dev origin.

---

## Carbon Intelligence

DeliveryOS treats carbon tracking as a first-class feature, not an afterthought:

- **Per-delivery CO2** — calculated from vehicle type, distance, and fuel type
- **Per-vehicle CO2/km** — electric vehicles and cargo bikes tracked at 0 g/km, combustion vehicles by fuel-specific factors
- **Tour-level aggregation** — each tour records total distance and total CO2, with AI optimization gain (km and % saved vs. unoptimized route)
- **Objectives** — monthly, quarterly, and yearly carbon reduction targets with `ON_TRACK` / `AT_RISK` / `OFF_TRACK` status
- **Historical records** — the `co2_records` table stores rolling emission history for trend analysis

---

## Observability & Monitoring

### Distributed Tracing with Zipkin

All HTTP requests are traced via Micrometer Tracing, with spans visualized at `http://localhost:9411`.

### Structured Logging

All services use SLF4J + Logback. Log entries include trace and span IDs for correlation with Zipkin.

### Real-Time Fleet Monitoring

`GpsSimulationService` publishes simulated driver positions to `/topic/fleet` every 5 seconds, consumed by the Live Tracking page via STOMP over SockJS. In production, this is replaced by real GPS reports from driver mobile devices via `sendGpsUpdate`.

### Spring Boot Actuator

`/actuator/health`, `/actuator/info`, and `/actuator/metrics` are exposed for health checks and metrics scraping.

---

## Design Patterns

| Pattern | Where Applied | Purpose |
|---|---|---|
| Hexagonal Architecture | Entire backend | Decouple domain logic from infrastructure |
| Repository Pattern | All persistence adapters | Abstract persistence behind port interfaces |
| DTO Pattern | All REST controllers | Decouple API contracts from domain models |
| Use Case Pattern | `application/usecases` | One class per business operation |
| Global Exception Handler | `shared/GlobalExceptionHandler` | Centralized error handling, consistent `ApiResponse` envelope |
| Refresh Token Rotation | `AuthUseCase` | Secure long-lived sessions |
| Observer (Pub/Sub) | WebSocket `/topic/fleet` | Real-time fleet position broadcasting |
| Strategy | AI engine optimization modules | Swappable routing/prediction algorithms (OR-Tools, XGBoost, Prophet) |
| Enum-as-VARCHAR | Persistence layer | Cross-database compatibility for status/category fields |

---

## Database Schema

| Table | Purpose |
|---|---|
| `users` | Authentication, roles, MFA, account lockout |
| `refresh_tokens` | JWT refresh token storage and revocation |
| `deliveries` | Delivery records: status, recipient, address, priority, time windows |
| `vehicles` | Fleet inventory: type, fuel, capacity, mileage, maintenance |
| `tours` | Daily route assignments per driver and vehicle |
| `tour_stops` | Ordered delivery stops within a tour, with ETA tracking |
| `co2_records` | Per-tour and per-vehicle CO2 emission history |
| `carbon_objectives` | Monthly/quarterly/yearly emission reduction targets |
| `notifications` | In-app notifications by user and channel |
| `driver_badges` | Gamification badges for driver performance |
| `gps_tracks` | Real-time GPS position history |
| `audit_logs` | System audit trail |

Migrations are managed with Flyway and applied automatically on startup.

---

## Prerequisites

- [Java 21](https://adoptium.net/) (JDK)
- [Maven 3.9+](https://maven.apache.org/download.cgi)
- [Node.js 18+](https://nodejs.org/) and npm
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Python 3.11+](https://www.python.org/downloads/) (AI engine and seeding scripts)

---

## Getting Started

### 1. Clone the repository

```powershell
git clone https://github.com/4bdelaali-40/deliveryos.git
cd deliveryos
```

### 2. Start the infrastructure

```powershell
docker compose up -d
```

This starts PostgreSQL (with PostGIS), Redis, Kafka, Zookeeper, Zipkin, pgAdmin, and Kafka UI.

Verify all services are healthy:

```powershell
docker compose ps
```

### 3. Run the backend

```powershell
cd backend
.\mvnw spring-boot:run
```

The backend starts on `http://localhost:8080`. Flyway migrations run automatically.

### 4. Seed the database

Insert test users:

```powershell
python insert_test_users.py
```

Insert realistic sample data (drivers, vehicles, deliveries, tours, CO2 history):

```powershell
python insert_data.py
```

### 5. Run the frontend

```powershell
cd frontend
npm install
npm run dev
```

The frontend starts on `http://localhost:5173`.

### 6. Run the AI engine (optional)

```powershell
cd ai-engine
python -m venv .venv
.\.venv\Scripts\activate
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

### 7. Verify everything is running

| URL | Description |
|---|---|
| `http://localhost:5173` | Frontend application |
| `http://localhost:8080/swagger-ui.html` | Backend API documentation |
| `http://localhost:5050` | pgAdmin (PostgreSQL administration) |
| `http://localhost:8090` | Kafka UI |
| `http://localhost:9411` | Zipkin distributed tracing |

---

## Default Credentials

| Email | Password | Role |
|---|---|---|
| admin@deliveryos.fr | password123 | SUPER_ADMIN |
| dispatcher@deliveryos.fr | password123 | DISPATCHER |
| driver@deliveryos.fr | password123 | DRIVER |

---

## API Documentation

Once the backend is running, interactive API documentation (Swagger UI) is available at:

```
http://localhost:8080/swagger-ui.html
```

All authenticated endpoints require a Bearer token obtained from `POST /api/auth/login`.

### Authentication

```
POST   /api/auth/login                Authenticate, returns access/refresh tokens
POST   /api/auth/refresh              Rotate refresh token
POST   /api/auth/logout               Revoke refresh tokens
```

### Users (Admin)

```
GET    /api/users                     List users, filterable by role        [SUPER_ADMIN, ADMIN, DISPATCHER]
POST   /api/users                     Create a new user                     [SUPER_ADMIN, ADMIN]
PATCH  /api/users/{id}/role           Change a user's role                  [SUPER_ADMIN]
PATCH  /api/users/{id}/status         Activate/deactivate a user            [SUPER_ADMIN, ADMIN]
DELETE /api/users/{id}                Delete a user                         [SUPER_ADMIN]
```

### Deliveries

```
GET    /api/deliveries                List deliveries, filterable by status, priority, city, date
GET    /api/deliveries/{id}           Get delivery by ID
GET    /api/deliveries/tracking/{code}  Get delivery by tracking code
POST   /api/deliveries                Create a delivery
PUT    /api/deliveries/{id}           Update a delivery
PATCH  /api/deliveries/{id}/status    Update delivery status
DELETE /api/deliveries/{id}           Delete a delivery
```

### Fleet & Tours

```
GET    /api/vehicles                  List fleet vehicles
GET    /api/tours?date=YYYY-MM-DD     List tours for a given date
POST   /api/ai/optimize-routes        Trigger AI route optimization
```

### Analytics & Carbon

```
GET    /api/analytics/kpis?period=MONTHLY   Dashboard KPIs
GET    /api/carbon/objectives               Carbon reduction objectives
```

### Real-time

```
WS     /ws                            STOMP endpoint for live updates
SUB    /topic/fleet                   Live driver position broadcasts
```

---

## Project Structure

```
deliveryos/
├── backend/
│   └── src/main/java/com/deliveryos/
│       ├── domain/                  # Core business entities and rules
│       ├── application/
│       │   ├── usecases/            # AuthUseCase, DeliveryUseCase, AnalyticsUseCase...
│       │   └── services/            # ScheduledTasks, GpsSimulationService
│       ├── ports/
│       │   ├── in/                  # Use case interfaces
│       │   └── out/                 # Repository & messaging interfaces
│       ├── adapters/
│       │   ├── in/web/
│       │   │   ├── controllers/     # REST controllers
│       │   │   └── dto/             # Request/response DTOs
│       │   └── out/
│       │       ├── persistence/     # JPA entities, repositories, mappers
│       │       └── ai/              # AI engine HTTP client
│       ├── config/                  # Security, JWT, CORS, WebSocket, Kafka
│       └── shared/                  # ApiResponse, PageResponse, GlobalExceptionHandler
├── frontend/
│   ├── src/
│   │   ├── pages/                   # Dashboard, Deliveries, Tours, Tracking, Analytics, Carbon, Fleet, Drivers, Admin
│   │   ├── components/
│   │   │   ├── layout/              # AppLayout, Sidebar
│   │   │   └── map/                 # DeliveryMap, DriverMarker
│   │   ├── services/                # api.ts, websocket.ts, tourService.ts
│   │   ├── hooks/                   # useFleetTracking, useRouteOptimization
│   │   ├── store/                   # useStore (auth, global state)
│   │   └── types/                   # Shared TypeScript types
│   ├── vite.config.ts               # Dev proxy to backend
│   └── package.json
├── ai-engine/
│   ├── main.py                      # FastAPI app
│   └── models/                      # OR-Tools, XGBoost, LSTM, Prophet, DBSCAN
├── docker-compose.yml                # Infrastructure orchestration
├── insert_test_users.py              # Seed: SUPER_ADMIN, DISPATCHER, DRIVER accounts
├── insert_data.py                    # Seed: realistic drivers, vehicles, deliveries, tours
└── README.md
```

---

## Environment Variables

| Variable | Service | Description | Default |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | backend | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/deliveryos` |
| `SPRING_DATASOURCE_PASSWORD` | backend | PostgreSQL password | `deliveryos_secret` |
| `SPRING_REDIS_HOST` | backend | Redis hostname | `localhost` |
| `SECURITY_JWT_ACCESS_TOKEN_EXPIRATION_MS` | backend | Access token TTL | — |
| `SECURITY_JWT_REFRESH_TOKEN_EXPIRATION_MS` | backend | Refresh token TTL | — |
| `VITE_API_BASE_URL` | frontend | Backend base URL for REST and WebSocket | `http://localhost:8080` |

---

## Project Status

This project is under active development. Current implementation status by module:

| Module | Status |
|---|---|
| Authentication (JWT, MFA, RBAC) | Complete |
| Dashboard & Analytics | Complete |
| Fleet management | Complete |
| Driver management | Complete |
| Carbon intelligence | Complete |
| Deliveries management | Complete |
| Tours & route optimization | Complete |
| Live tracking | Complete |
| Admin panel (user management) | Complete |
| AI engine integration (OR-Tools, XGBoost) | Complete |

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit using Conventional Commits: `git commit -m "feat: add your feature"`
4. Push and open a Pull Request against `main`

Commit types: `feat` · `fix` · `docs` · `refactor` · `test` · `chore`

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

**DeliveryOS** — Java 21 · Spring Boot 3.3 · React 18 · PostgreSQL/PostGIS · Apache Kafka · FastAPI

Enterprise logistics platform: hexagonal architecture · real-time tracking · AI-powered optimization · carbon intelligence