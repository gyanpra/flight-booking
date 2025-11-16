# Flight Booking Platform - Architecture Documentation

## System Overview

The Flight Booking Platform is a production-ready, microservices-based system designed to handle flight search, booking, payment, and notification workflows. The system follows clean architecture principles, SOLID design patterns, and event-driven architecture.

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         API Gateway (Future)                     │
└─────────────────────────────────────────────────────────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
┌───────▼────────┐      ┌────────▼────────┐      ┌────────▼────────┐
│ Search Service │      │ Booking Service │      │ Notification    │
│   (Port 8081)  │      │   (Port 8082)   │      │   Service       │
│                │      │                 │      │   (Port 8083)   │
│ - PostgreSQL   │      │ - PostgreSQL    │      │                 │
│ - Elasticsearch│      │ - Redis         │      │                 │
│ - Redis Cache  │      │ - Kafka Producer│      │ - Kafka Consumer│
└────────────────┘      └─────────────────┘      └─────────────────┘
```

## Module Structure

### 1. flight-common
**Purpose:** Shared components across all services

**Contents:**
- Enums (BookingStatus, PaymentStatus, CabinClass, etc.)
- DTOs (PassengerDTO, SeatSelectionDTO, ErrorResponse)
- Events (BookingEvent for Kafka)
- Exceptions (ResourceNotFoundException, BusinessException, PaymentException)
- Utilities (PNRGenerator)

**Dependencies:** None (base module)

### 2. flight-search-service
**Purpose:** Flight search and inventory management

**Layers:**
```
Controller → Service → Repository → Database/Cache/Search
    ↓          ↓           ↓
  DTOs    Business     Entities
          Logic
```

**Key Components:**
- **Controllers:** FlightSearchController
- **Services:** FlightSearchService (interface + impl)
- **Repositories:** FlightRepository, SeatInventoryRepository, FlightDocumentRepository
- **Entities:** Flight, SeatInventory, SeatMap, InventoryHold, FlightDocument
- **Mappers:** FlightMapper
- **Config:** RedisConfig

**Data Flow:**
1. Client requests flight search
2. Check Redis cache for results
3. If cache miss, query PostgreSQL
4. Transform to Elasticsearch documents
5. Return results and cache in Redis (10 min TTL)

### 3. flight-booking-service
**Purpose:** Booking and payment orchestration

**Layers:**
```
Controller → Service → Repository → Database
    ↓          ↓           ↓
  DTOs    Business     Entities
          Logic          ↓
                    Kafka Events
```

**Key Components:**
- **Controllers:** BookingController, PaymentController
- **Services:** BookingService, PaymentService (interfaces + impls)
- **Repositories:** BookingRepository, PaymentRepository, UserRepository
- **Entities:** Booking, Payment, User
- **Mappers:** BookingMapper, PaymentMapper
- **Config:** KafkaProducerConfig

**Booking Flow:**
1. Create booking (status: CREATED)
2. Hold seats (15 min expiry)
3. Publish booking event to Kafka
4. Process payment
5. Update booking status to CONFIRMED
6. Publish confirmed event to Kafka

### 4. flight-notification-service
**Purpose:** Asynchronous notification handling

**Key Components:**
- **Consumers:** BookingEventConsumer
- **Services:** NotificationService (interface + impl)
- **Config:** KafkaConsumerConfig

**Event Flow:**
1. Listen to booking-events topic
2. Filter by status (CONFIRMED, CANCELLED)
3. Send notifications (email/SMS stub)
4. Log notification activity

## Data Models

### Search Service Schema

**flights**
- flight_id (UUID, PK)
- carrier (ENUM)
- flight_number (VARCHAR)
- departure_airport (VARCHAR)
- arrival_airport (VARCHAR)
- departure_time (TIMESTAMP)
- arrival_time (TIMESTAMP)
- equipment (VARCHAR)
- is_active (BOOLEAN)

**seat_inventory**
- id (BIGINT, PK)
- flight_id (UUID, FK)
- fare_class (VARCHAR)
- cabin_class (ENUM)
- total_seats (INT)
- available_seats (INT)
- price (DECIMAL)
- version (BIGINT) -- Optimistic locking

**seat_map**
- id (BIGINT, PK)
- flight_id (UUID, FK)
- seat_no (VARCHAR)
- cabin_class (ENUM)
- fare_class (VARCHAR)
- status (ENUM)
- current_hold_id (UUID)

**inventory_holds**
- hold_id (UUID, PK)
- flight_id (UUID, FK)
- user_id (UUID)
- customer_session_id (VARCHAR)
- seat_count (INT)
- seats (JSONB)
- expires_at (TIMESTAMP)
- status (ENUM)
- created_at (TIMESTAMP)

### Booking Service Schema

**users**
- user_id (UUID, PK)
- name (VARCHAR)
- email (VARCHAR, UNIQUE)
- phone (VARCHAR)

**bookings**
- booking_id (UUID, PK)
- pnr (VARCHAR, UNIQUE)
- user_id (UUID, FK)
- itinerary_id (UUID)
- booking_status (ENUM)
- amount (DECIMAL)
- currency (VARCHAR)
- passengers (JSONB)
- seats (JSONB)
- hold_id (UUID)
- payment_transaction_id (UUID)
- created_at (TIMESTAMP)
- expires_at (TIMESTAMP)
- updated_at (TIMESTAMP)

**payment_transactions**
- payment_transaction_id (UUID, PK)
- booking_id (UUID, FK)
- gateway (ENUM)
- gateway_txn_id (VARCHAR, UNIQUE)
- amount (DECIMAL)
- currency (VARCHAR)
- payment_method (ENUM)
- status (ENUM)
- failure_reason (VARCHAR)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

## Design Patterns

### 1. Repository Pattern
Abstracts data access logic from business logic.

```java
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
```

### 2. Service Layer Pattern
Encapsulates business logic in service interfaces and implementations.

```java
public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest request);
}

@Service
public class BookingServiceImpl implements BookingService {
    // Implementation
}
```

### 3. DTO Pattern
Separates API contracts from internal entities.

```java
// API Layer
public class BookingResponse { ... }

// Domain Layer
@Entity
public class Booking { ... }
```

### 4. Mapper Pattern
Transforms between DTOs and entities.

```java
@Component
public class BookingMapper {
    public BookingResponse toResponse(Booking booking) { ... }
}
```

### 5. Event-Driven Architecture
Decouples services using Kafka events.

```java
// Producer
kafkaTemplate.send("booking-events", bookingId, event);

// Consumer
@KafkaListener(topics = "booking-events")
public void consumeBookingEvent(BookingEvent event) { ... }
```

### 6. Cache-Aside Pattern
Improves read performance with Redis caching.

```java
@Cacheable(value = "flightSearch", key = "...")
public List<FlightSearchResponse> searchFlights(...) { ... }
```

### 7. Optimistic Locking
Handles concurrent seat inventory updates.

```java
@Entity
public class SeatInventory {
    @Version
    private Long version;
}
```

## SOLID Principles Implementation

### Single Responsibility Principle
Each class has one reason to change:
- Controllers handle HTTP requests
- Services contain business logic
- Repositories manage data access
- Mappers transform data

### Open/Closed Principle
Services are open for extension via interfaces:
```java
public interface NotificationService {
    void sendBookingConfirmation(BookingEvent event);
}
// Can add EmailNotificationService, SMSNotificationService, etc.
```

### Liskov Substitution Principle
Implementations can replace interfaces without breaking functionality.

### Interface Segregation Principle
Focused interfaces instead of fat interfaces:
- BookingService (booking operations)
- PaymentService (payment operations)
- NotificationService (notification operations)

### Dependency Inversion Principle
High-level modules depend on abstractions:
```java
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository; // Abstraction
}
```

## Error Handling Strategy

### Global Exception Handler
Centralized error handling with consistent responses:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(...) { ... }
}
```

### Custom Exceptions
- ResourceNotFoundException (404)
- BusinessException (400)
- PaymentException (402)

### Error Response Format
```json
{
  "timestamp": "2025-01-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Flight not found: {id}",
  "path": "/api/flights/{id}"
}
```

## Testing Strategy

### Unit Tests
- Mock external dependencies
- Test business logic in isolation
- Coverage: Service layer, Mappers

### Integration Tests
- Test repository layer with H2
- Test controller layer with MockMvc
- Coverage: Data access, API endpoints

### Test Structure
```
src/test/java/
├── service/
│   ├── BookingServiceTest.java
│   └── PaymentServiceTest.java
├── controller/
│   └── BookingControllerTest.java
└── mapper/
    └── BookingMapperTest.java
```

## Performance Considerations

### Caching Strategy
- Redis cache for flight search results (10 min TTL)
- Cache key: origin-destination-date-passengers

### Database Optimization
- Indexes on frequently queried columns
- JSONB for flexible passenger/seat data
- Optimistic locking for inventory

### Async Processing
- Kafka for non-blocking notifications
- Event-driven booking confirmations

## Security Considerations (Future)

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (RBAC)
- API key for service-to-service communication

### Data Protection
- Encrypt sensitive data (PII, payment info)
- HTTPS for all communications
- Input validation on all endpoints

### Rate Limiting
- Prevent abuse with rate limiters
- Per-user and per-IP limits

## Scalability

### Horizontal Scaling
- Stateless services (can run multiple instances)
- Load balancer for traffic distribution

### Database Scaling
- Read replicas for search service
- Partitioning by date for bookings

### Cache Scaling
- Redis cluster for high availability
- Distributed caching

### Message Queue Scaling
- Kafka partitions for parallel processing
- Consumer groups for load distribution

## Monitoring & Observability (Future)

### Metrics
- Spring Boot Actuator endpoints
- Prometheus for metrics collection
- Grafana for visualization

### Logging
- Structured logging (JSON format)
- Centralized logging (ELK stack)
- Correlation IDs for request tracing

### Tracing
- Spring Cloud Sleuth for distributed tracing
- Zipkin for trace visualization

## Deployment

### Containerization
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Orchestration
- Kubernetes for container orchestration
- Helm charts for deployment
- ConfigMaps for configuration

### CI/CD Pipeline
1. Build (Maven)
2. Test (JUnit)
3. Package (Docker)
4. Deploy (Kubernetes)

## Conclusion

This architecture provides a solid foundation for a production-ready flight booking system with:
- Clean separation of concerns
- Scalable microservices design
- Event-driven communication
- Comprehensive error handling
- High test coverage
- Performance optimization
- Future-proof extensibility
