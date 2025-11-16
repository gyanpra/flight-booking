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
- **Elasticsearch** - Search engine
- **Kafka** - Event streaming
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

### Clean Architecture

```
Controller → Service Interface → Service Implementation → Repository → Database
     ↓            ↓                      ↓
   DTOs      Business Logic         Entities
```

## API Examples

### Search Flights

```bash
curl "http://localhost:8081/api/flights/search?origin=BLR&destination=DEL&departureDate=2026-12-15&passengers=2&cabinClass=ECONOMY"
```

### Create Booking

```bash
curl -X POST http://localhost:8082/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "uuid",
    "itineraryId": "uuid",
    "passengers": [{
      "firstName": "John",
      "lastName": "Doe",
      "age": 30,
      "gender": "M"
    }],
    "seats": [{
      "flightId": "uuid",
      "seatNo": "12A"
    }]
  }'
```

### Process Payment

```bash
curl -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": "uuid",
    "gateway": "RAZORPAY",
    "paymentMethod": "UPI"
  }'
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
- Implement seat hold expiry job
- Add real payment gateway integration
- Implement email/SMS service integration
- Add API rate limiting
- Implement distributed tracing (Sleuth/Zipkin)
- Add metrics & monitoring (Actuator/Prometheus)
- Implement circuit breakers (Resilience4j)
- Add API documentation (Swagger/OpenAPI)

## License

Proprietary - All rights reserved
