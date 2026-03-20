# 🚍 Real-Time Bus Tracking System — Backend

A Spring Boot REST API for tracking bus locations in real-time.

## Tech Stack
- Java 21
- Spring Boot 3.5
- MySQL 8.0
- Spring Data JPA
- Hibernate

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/bus-locations` | Save a bus location |
| GET | `/api/bus-locations` | Get all locations |
| GET | `/api/bus-locations/{busId}` | Get locations for a specific bus |
| GET | `/api/bus-locations/latest` | Get latest location per bus |
| GET | `/api/bus-stops` | Get all bus stops |
| POST | `/api/bus-stops` | Add a new bus stop |
| GET | `/api/bus-stops/{stopId}/eta` | Get ETA for all buses to a stop |

## Features
- GPS location storage and retrieval
- Latest location tracking per bus
- Bus stop management
- ETA calculation using Haversine formula
- Auto-cleanup of old location data
- CORS configured for frontend

## Setup
1. Install Java 21 and MySQL 8.0
2. Create database: `CREATE DATABASE bus_tracking_db;`
3. Update `application.properties` with your MySQL credentials
4. Run: `mvn spring-boot:run`
5. Server starts at `http://localhost:8080`