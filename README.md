# Flight Booking Platform

A production-ready, multi-module Spring Boot backend for a Flight Booking System with comprehensive test coverage, clean architecture, and proper design patterns.

## Architecture

### Multi-Module Structure

```
flight-booking-platform/
├── flight-common/              # Shared models, DTOs, enums, utilities
├── flight-search-service/      # Flight search with Elasticsearch & Redis
├── flight-booking-service/     # Booking & payment management with Kafka
└── flight-notification-service/ # Notification consumer (Kafka)
```

### Technology Stack

- **Java 21** with Spring Boot 3.4.0
- **PostgreSQL** - Primary database
- **Redis** - Caching layer
- **Redisson** - Distributed locking
- **Elasticsearch** - Search engine
- **Kafka** - Event streaming
- **Resilience4j** - Circuit breakers & rate limiting
- **Spring Retry** - Retry mechanism
- **Lombok** - Boilerplate reduction
- **JUnit 5 & Mockito** - Testing

## Services

### 1. Flight Search Service (Port 8081)

**Responsibilities:**
- Search flights by route, date, passengers, cabin class
- Retrieve flight details with seat inventory
- Sync flight data to Elasticsearch
- Cache search results in Redis

**Key Endpoints:**
- `GET /api/flights/search` - Search flights
- `GET /api/flights/{flightId}` - Get flight details
- `POST /api/flights/sync` - Sync to Elasticsearch
- `POST /api/seat-holds` - Hold seats with distributed lock
- `DELETE /api/seat-holds/{holdId}` - Release seat hold
- `POST /api/seat-holds/{holdId}/confirm` - Confirm seat hold

**Database:** `flight_search_db`

### 2. Flight Booking Service (Port 8082)

**Responsibilities:**
- Create and manage bookings
- Process payments (simulated)
- Publish booking events to Kafka
- Handle booking cancellations

**Key Endpoints:**
- `POST /api/bookings` - Create booking
- `GET /api/bookings/{bookingId}` - Get booking
- `GET /api/bookings/user/{userId}` - Get user bookings
- `POST /api/bookings/{bookingId}/cancel` - Cancel booking
- `POST /api/payments` - Process payment
- `GET /api/payments/{paymentId}` - Get payment details

**Database:** `flight_booking_db`

### 3. Flight Notification Service (Port 8083)

**Responsibilities:**
- Consume booking events from Kafka
- Send notifications (email/SMS stub)
- Log notification activities

**Kafka Topics:**
- `booking-events` - Consumes booking status changes

## Data Models

### Search Service Entities

- **Flight** - Flight schedule information
- **SeatInventory** - Seat availability by fare class (with optimistic locking)
- **SeatMap** - Individual seat status
- **InventoryHold** - Temporary seat holds
- **FlightDocument** - Elasticsearch document

### Booking Service Entities

- **User** - User information
- **Booking** - Booking with passengers and seats (JSONB)
- **Payment** - Payment transactions

## Setup & Configuration

### Prerequisites

- Java 21
- Maven 3.8+
- PostgreSQL 14+
- Redis 7+
- Elasticsearch 8+
- Kafka 3+

### Database Setup

```sql
-- Create databases
CREATE DATABASE flight_search_db;
CREATE DATABASE flight_booking_db;
```

### Configuration Files

Each service has `application-dev.yml` with connection properties:

**PostgreSQL:**
```yaml
spring.datasource.url: jdbc:postgresql://localhost:5432/{db_name}
spring.datasource.username: postgres
spring.datasource.password: postgres
```

**Redis:**
```yaml
spring.data.redis.host: localhost
spring.data.redis.port: 6379
```

**Elasticsearch:**
```yaml
spring.elasticsearch.uris: http://localhost:9200
```

**Kafka:**
```yaml
spring.kafka.bootstrap-servers: localhost:9092
```

## Build & Run

### Build All Modules

```bash
cd flight-booking
mvn clean install
```

### Run Individual Services

**Search Service:**
```bash
cd flight-search-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Booking Service:**
```bash
cd flight-booking-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Notification Service:**
```bash
cd flight-notification-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Testing

### Run All Tests

```bash
mvn test
```

### Run Tests for Specific Module

```bash
cd flight-booking-service
mvn test
```

### Test Coverage

- **Unit Tests** - Service layer with mocked dependencies
- **Mapper Tests** - DTO/Entity transformations
- **Controller Tests** - REST API endpoints with MockMvc
- **Integration Tests** - Repository layer with H2

Coverage: ~75% on core business logic

## Design Patterns & Principles

### SOLID Principles

- **Single Responsibility** - Each service has focused responsibilities
- **Open/Closed** - Extensible through interfaces
- **Liskov Substitution** - Interface-based design
- **Interface Segregation** - Focused service interfaces
- **Dependency Inversion** - Dependency injection throughout

### Patterns Used

- **Repository Pattern** - Data access abstraction
- **Service Layer Pattern** - Business logic encapsulation
- **DTO Pattern** - API contract separation
- **Mapper Pattern** - Entity/DTO transformation
- **Event-Driven Architecture** - Kafka for async communication
- **Cache-Aside Pattern** - Redis caching strategy
- **Optimistic Locking** - Seat inventory concurrency control
- **Pessimistic Locking** - Critical section protection
- **Distributed Lock Pattern** - Cross-instance synchronization
- **Circuit Breaker Pattern** - Fault tolerance
- **Retry Pattern** - Transient failure handling

### Clean Architecture

```
Controller → Service Interface → Service Implementation → Repository → Database
     ↓            ↓                      ↓
   DTOs      Business Logic         Entities
```

## Concurrency & Locking Features

### Implemented Mechanisms

- **Distributed Locking** - Redisson-based locks for critical operations
- **Optimistic Locking** - JPA @Version for seat inventory
- **Pessimistic Locking** - Database-level locks with SELECT FOR UPDATE
- **Retry Logic** - Exponential backoff for transient failures
- **Circuit Breakers** - Resilience4j for fault tolerance
- **Rate Limiting** - Per-user and per-service rate limits
- **Idempotency** - Payment processing with idempotency keys
- **Transaction Isolation** - REPEATABLE_READ and SERIALIZABLE levels
- **Async Processing** - Thread pools for parallel operations
- **Seat Hold Expiry** - Scheduled job to release expired holds

### Configuration

**Transaction Isolation:**
- Booking Creation: REPEATABLE_READ
- Payment Processing: SERIALIZABLE

**Retry Settings:**
- Max Attempts: 3
- Initial Delay: 100ms
- Backoff Multiplier: 2x

**Circuit Breaker:**
- Failure Threshold: 50%
- Wait Duration: 10-15s

**Rate Limiting:**
- Booking: 10 req/sec
- Per User: 10 req/min

## Quickstart Guide

### Complete Booking Flow

**Step 1: Create Flight**
```bash
curl -X POST http://localhost:8081/api/flights \
  -H "Content-Type: application/json" \
  -d '{
    "carrier": "AI",
    "flightNumber": "101",
    "departureAirport": "BLR",
    "arrivalAirport": "DEL",
    "departureTime": "2026-12-15T10:00:00",
    "arrivalTime": "2026-12-15T12:30:00",
    "equipment": "A320"
  }'
# Save flightId from response
```

**Step 2: Add Seat Inventory**
```bash
curl -X POST http://localhost:8081/api/flights/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": "<flightId>",
    "fareClass": "Y",
    "cabinClass": "ECONOMY",
    "totalSeats": 150,
    "price": 5000.00
  }'
```

**Step 3: Search Flights**
```bash
curl "http://localhost:8081/api/flights/search?origin=BLR&destination=DEL&departureDate=2026-12-15&passengers=2&cabinClass=ECONOMY"
```

**Step 4: Hold Seats (with distributed lock)**
```bash
curl -X POST "http://localhost:8081/api/seat-holds?flightId=<flightId>&sessionId=session123&durationMinutes=15" \
  -H "Content-Type: application/json" \
  -d '["12A", "12B"]'
# Save holdId from response
```

**Step 5: Create Booking (with retry & rate limiting)**
```bash
curl -X POST http://localhost:8082/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "<userId>",
    "itineraryId": "<flightId>",
    "passengers": [{
      "firstName": "John",
      "lastName": "Doe",
      "age": 30,
      "gender": "M"
    }],
    "seats": [{
      "flightId": "<flightId>",
      "seatNo": "12A"
    }]
  }'
# Save bookingId from response
```

**Step 6: Process Payment (with idempotency & circuit breaker)**
```bash
curl -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": "<bookingId>",
    "gateway": "RAZORPAY",
    "paymentMethod": "UPI"
  }'
```

**Step 7: Confirm Seat Hold**
```bash
curl -X POST http://localhost:8081/api/seat-holds/<holdId>/confirm
```

**Step 8: Get Booking Details**
```bash
curl http://localhost:8082/api/bookings/<bookingId>
```

### Testing Concurrency

**Test Rate Limiting:**
```bash
for i in {1..15}; do
  curl -X POST http://localhost:8082/api/bookings \
    -H "Content-Type: application/json" \
    -d '{"userId":"<userId>","itineraryId":"<flightId>","passengers":[{"firstName":"Test","lastName":"User","age":30,"gender":"M"}],"seats":[{"flightId":"<flightId>","seatNo":"12A"}]}'
done
# Should see rate limit errors after 10 requests
```

**Test Distributed Locking:**
```bash
# Run simultaneously in 2 terminals
curl -X POST "http://localhost:8081/api/seat-holds?flightId=<flightId>&sessionId=s1&durationMinutes=15" \
  -H "Content-Type: application/json" -d '["12A"]' &
curl -X POST "http://localhost:8081/api/seat-holds?flightId=<flightId>&sessionId=s2&durationMinutes=15" \
  -H "Content-Type: application/json" -d '["12A"]' &
# Only one should succeed
```

**Test Idempotency:**
```bash
# Send same payment request twice
curl -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d '{"bookingId":"<bookingId>","gateway":"RAZORPAY","paymentMethod":"UPI"}'
curl -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d '{"bookingId":"<bookingId>","gateway":"RAZORPAY","paymentMethod":"UPI"}'
# Should return existing payment
```

## Error Handling

Global exception handlers provide consistent error responses:

```json
{
  "timestamp": "2025-01-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Flight not found: {id}",
  "path": "/api/flights/{id}"
}
```

## Kafka Events

### Booking Event Schema

```json
{
  "bookingId": "uuid",
  "pnr": "ABC123",
  "userId": "uuid",
  "status": "CONFIRMED",
  "amount": 10000.00,
  "currency": "INR",
  "userEmail": "user@example.com",
  "userPhone": "+919876543210",
  "timestamp": "2025-01-20T10:30:00"
}
```

## Future Enhancements

- Add authentication & authorization (Spring Security + JWT)
- Add real payment gateway integration
- Implement real email/SMS service integration
- Add metrics & monitoring (Actuator/Prometheus)
- Implement distributed tracing (Sleuth/Zipkin)
- Implement saga pattern for distributed transactions

## License

Proprietary - All rights reserved
