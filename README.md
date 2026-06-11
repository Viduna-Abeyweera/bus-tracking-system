# 🚌 Bus Tracker SL — Real-Time Bus Tracking System

> A full-stack real-time bus tracking platform for Sri Lanka, built with **Spring Boot** and **React**.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue?logo=react)](https://react.dev/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## 📖 Overview

**Bus Tracker SL** enables real-time GPS tracking of buses across Sri Lanka. Passengers see live bus positions on an interactive map, drivers share their location via GPS, and admins manage the entire fleet.

### Key Features

- 🗺️ **Live Map Tracking** — Watch buses move in real-time via WebSocket (no polling!)
- ⏱️ **Smart ETA** — Adaptive arrival predictions using Strategy Pattern (urban vs highway)
- 🔍 **Route Search** — Browse routes with stop timelines (Colombo→Kandy, Colombo→Galle, etc.)
- 📅 **Schedule Viewer** — Departure times filtered by day-of-week
- 🔐 **JWT Authentication** — Secure role-based access (Passenger, Driver, Admin)
- ⚙️ **Admin Dashboard** — Full CRUD for routes, buses, and schedules
- 📡 **WebSocket** — STOMP over SockJS for sub-second location updates

---

## 🏗️ Architecture

```
┌──────────────────┐     STOMP/SockJS     ┌────────────────────┐
│    React SPA     │◄────────────────────►│  Spring Boot API   │
│  (Vite + Leaflet)│     REST + JWT       │  (Java 21 + JPA)   │
└──────────────────┘                      └────────┬───────────┘
                                                   │
                                          ┌────────▼───────────┐
                                          │   PostgreSQL DB    │
                                          └────────────────────┘
```

### Design Patterns

| Pattern | Usage |
|---------|-------|
| **Strategy** | ETA calculation — swap between Simple and Route-Based algorithms |
| **Facade** | WebSocket broadcast service — hides STOMP internals |
| **Observer** | STOMP pub/sub — passengers auto-notified of bus movement |
| **Repository** | Spring Data JPA — data access abstraction |
| **DIP** | Services depend on interfaces, not implementations |

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- PostgreSQL (or MySQL)
- Maven (included via `mvnw`)

### Backend Setup

```bash
cd bus-tracking-backend

# Configure database
cp .env.example .env
# Edit .env with your DB credentials

# Run
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`. Swagger UI at `http://localhost:8080/swagger-ui.html`.

### Frontend Setup

```bash
cd bus-tracking-frontend

# Install dependencies
npm install

# Run dev server
npm run dev
```

The app opens at `http://localhost:5173`.

---

## 🔑 Default Users (Seeded)

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@bustracker.lk` | `admin123` |
| Driver | `driver1@bustracker.lk` | `driver123` |
| Passenger | `passenger@bustracker.lk` | `passenger123` |

---

## 📡 API Endpoints

### Auth (Public)
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, get JWT |

### Routes (Public read, Admin write)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/routes` | All routes |
| GET | `/api/routes/search?query=` | Search routes |
| POST | `/api/routes` | Create route (ADMIN) |
| PUT | `/api/routes/{id}` | Update route (ADMIN) |
| DELETE | `/api/routes/{id}` | Delete route (ADMIN) |

### Buses (Public read, Admin write)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/buses` | All buses |
| POST | `/api/buses` | Register bus (ADMIN) |
| PATCH | `/api/buses/{id}/status` | Change status (DRIVER/ADMIN) |

### Bus Locations (Public read, Driver write)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/bus-locations/latest` | Latest location per bus |
| POST | `/api/bus-locations` | Save GPS location (DRIVER) |

### WebSocket
| Endpoint | Description |
|----------|-------------|
| `/ws?token=JWT` | WebSocket handshake |
| `/topic/bus-locations` | Subscribe: all bus updates |
| `/topic/bus-locations/{routeId}` | Subscribe: route-specific |
| `/topic/bus-status` | Subscribe: status changes |

> Full API docs: `http://localhost:8080/swagger-ui.html`

---

## 🧪 Testing

```bash
cd bus-tracking-backend
./mvnw test
```

**80 tests** across 15 test classes — all passing ✅

| Layer | Tests |
|-------|-------|
| Service (unit) | Auth, Bus, BusLocation, BusStop, Route, Schedule, ETA, WebSocket |
| Controller (integration) | Auth, Route (MockMvc) |
| Strategy | ETA strategies |
| Utility | GeoUtils |
| Config | WebSocket event listener |

---

## 🗺️ Seeded Routes (Sri Lanka)

| # | Route | Stops |
|---|-------|-------|
| 1 | Colombo Fort → Kandy | 7 |
| 2 | Colombo Fort → Galle | 6 |
| 3 | Colombo → Negombo | 4 |
| 4 | Colombo Fort → Jaffna | 5 |
| 48 | Kandy → Nuwara Eliya | 5 |

---

## 🛠️ Tech Stack

**Backend:** Spring Boot 3.x, Java 21, Spring Security, Spring Data JPA, Spring WebSocket (STOMP), JWT, PostgreSQL, SpringDoc OpenAPI

**Frontend:** React 19, Vite 8, React Router 7, Axios, @stomp/stompjs, SockJS, Leaflet, React Icons, React Hot Toast

---

## 📁 Project Structure

```
bus-tracking-backend/
├── src/main/java/com/bustracker/
│   ├── config/          # Security, CORS, WebSocket, OpenAPI
│   ├── controller/      # REST + STOMP controllers
│   ├── dto/             # Request/Response DTOs
│   ├── entity/          # JPA entities
│   ├── enums/           # BusStatus, UserRole
│   ├── exception/       # Custom exceptions + global handler
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT provider + auth filter
│   ├── service/         # Service interfaces + implementations
│   ├── strategy/        # ETA calculation strategies
│   └── util/            # GeoUtils (Haversine)
└── src/test/            # Unit + integration tests

bus-tracking-frontend/
├── src/
│   ├── components/      # Navbar, ProtectedRoute, LoadingSpinner
│   ├── context/         # AuthContext (JWT state)
│   ├── services/        # API (Axios) + WebSocket (STOMP)
│   └── pages/           # Landing, Auth, Passenger, Driver, Admin
```

---

## 🚀 Deployment

**Backend** → Railway / Render  
**Frontend** → Vercel

Set environment variables:
```env
# Backend
DB_URL=jdbc:postgresql://host:port/db
JWT_SECRET=your-256-bit-secret

# Frontend
VITE_API_URL=https://your-backend.railway.app
VITE_WS_URL=https://your-backend.railway.app/ws
```

---

## 📄 License

MIT License — Built by Viduna Abeyweera