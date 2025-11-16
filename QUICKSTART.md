# Quick Start Guide

> 🚀 **Quick Test:** After completing setup, run `./test-system.sh` for automated end-to-end testing!

## Prerequisites Installation

### 1. Install PostgreSQL
```bash
# macOS
brew install postgresql@14
brew services start postgresql@14

# Create databases
psql postgres
CREATE DATABASE flight_search_db;
CREATE DATABASE flight_booking_db;
\q
```

### 2. Install Redis
```bash
# macOS
brew install redis
brew services start redis

# Verify
redis-cli ping
# Should return: PONG
```

### 3. Install Elasticsearch
```bash
# macOS
brew tap elastic/tap
brew install elastic/tap/elasticsearch-full
brew services start elasticsearch-full

# Verify (wait 30 seconds for startup)
curl http://localhost:9200
```

### 4. Install Kafka
```bash
# macOS
brew install kafka
brew services start zookeeper
brew services start kafka

# Create topic
kafka-topics --create --topic booking-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

## Build & Run

### Step 1: Build All Modules
```bash
cd flight-booking
mvn clean install -DskipTests
```

### Step 2: Load Sample Data
```bash
# Load flight data into search database
psql -d flight_search_db -f flight-search-service/src/main/resources/data.sql

# Load user data into booking database
psql -d flight_booking_db -f flight-booking-service/src/main/resources/data.sql
```

**If you already have data loaded with old dates, update them:**
```bash
psql -d flight_search_db -f update-flight-dates.sql
```

### Step 3: Start Services

**Terminal 1 - Search Service:**
```bash
cd flight-search-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
Wait for: "Started FlightSearchApplication"

**Terminal 2 - Booking Service:**
```bash
cd flight-booking-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
Wait for: "Started FlightBookingApplication"

**Terminal 3 - Notification Service:**
```bash
cd flight-notification-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
Wait for: "Started FlightNotificationApplication"

**Note:** If you restart services, you don't need to reload data (Step 2) again.

## Test the System

### 1. Load Flight Data via Carrier API

**Create a flight:**
```bash
curl -X POST http://localhost:8081/api/carrier/flights \
  -H "Content-Type: application/json" \
  -d '{
    "carrier": "INDIGO",
    "flightNumber": "6E-456",
    "departureAirport": "BOM",
    "arrivalAirport": "BLR",
    "departureTime": "2026-12-15T10:00:00",
    "arrivalTime": "2026-12-15T11:30:00",
    "equipment": "A320"
  }'
```

Save the `flightId` from response.

**Add inventory:**
```bash
curl -X POST http://localhost:8081/api/carrier/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": "<FLIGHT_ID_FROM_ABOVE>",
    "fareClass": "Y",
    "cabinClass": "ECONOMY",
    "totalSeats": 180,
    "price": 4500.00
  }'
```

**Or sync existing sample data to Elasticsearch:**
```bash
curl -X POST http://localhost:8081/api/flights/sync
```

**Wait 2-3 seconds** for Elasticsearch to index the data.

### 2. Search Flights
```bash
curl "http://localhost:8081/api/flights/search?origin=BLR&destination=DEL&departureDate=2026-12-15&passengers=2&cabinClass=ECONOMY"
```

Expected response:
```json
[
  {
    "flightId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "carrier": "INDIGO",
    "flightNumber": "6E-123",
    "origin": "BLR",
    "destination": "DEL",
    "departureTime": "2026-12-15T06:00:00",
    "arrivalTime": "2026-12-15T08:30:00",
    "price": 5000.00,
    "availableSeats": 150
  }
]
```

### 3. Get Flight Details
```bash
curl http://localhost:8081/api/flights/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11
```

### 4. Create Booking
```bash
curl -X POST http://localhost:8082/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14",
    "itineraryId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "passengers": [
      {
        "firstName": "John",
        "lastName": "Doe",
        "age": 30,
        "gender": "M"
      }
    ],
    "seats": [
      {
        "flightId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
        "seatNo": "12A"
      }
    ]
  }'
```

Save the `bookingId` from response.

### 5. Process Payment

**Replace `BOOKING_ID` with the actual bookingId from Step 4 response.**

```bash
# Example with actual booking ID:
curl -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": "5d7982f1-1573-4079-a50b-14042d6c0e9e",
    "gateway": "RAZORPAY",
    "paymentMethod": "UPI"
  }'
```

**Tip:** Save the bookingId from Step 4 to a variable:
```bash
# Extract bookingId (requires jq)
BOOKING_ID=$(curl -X POST http://localhost:8082/api/bookings \
  -H "Content-Type: application/json" \
  -d '{...}' | jq -r '.bookingId')

# Then use it
curl -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d "{\"bookingId\": \"$BOOKING_ID\", \"gateway\": \"RAZORPAY\", \"paymentMethod\": \"UPI\"}"
```

### 6. Check Notification Logs
Look at Terminal 3 (Notification Service) - you should see:
```
=== SENDING BOOKING CONFIRMATION ===
PNR: ABC123
User Email: john.doe@example.com
...
```

### 7. Get User Bookings
```bash
curl http://localhost:8082/api/bookings/user/d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14
```

### 8. Cancel Booking

**Replace `BOOKING_ID` with the actual bookingId.**

```bash
# Example with actual booking ID:
curl -X POST http://localhost:8082/api/bookings/5d7982f1-1573-4079-a50b-14042d6c0e9e/cancel
```

## Verify Infrastructure

### PostgreSQL
```bash
# Search DB
psql -d flight_search_db -c "SELECT COUNT(*) FROM flights;"

# Booking DB
psql -d flight_booking_db -c "SELECT COUNT(*) FROM bookings;"
```

### Redis
```bash
redis-cli
KEYS *
GET "flightSearch::BLR-DEL-2026-12-15-2"
exit
```

### Elasticsearch
```bash
curl "http://localhost:9200/itineraries/_search?pretty"

# Or search for specific route
curl -X GET "http://localhost:9200/itineraries/_search?pretty" \
  -H 'Content-Type: application/json' \
  -d '{
    "query": {
      "bool": {
        "must": [
          {"term": {"origin": "BLR"}},
          {"term": {"destination": "DEL"}}
        ]
      }
    }
  }'
```

### Kafka
```bash
# List topics
kafka-topics --list --bootstrap-server localhost:9092

# Consume messages
kafka-console-consumer --topic booking-events --from-beginning --bootstrap-server localhost:9092
```

## Run Tests

### All Tests
```bash
mvn test
```

### Specific Module
```bash
cd flight-booking-service
mvn test
```

### With Coverage
```bash
mvn test jacoco:report
# Reports in: target/site/jacoco/index.html
```

## Troubleshooting

### Port Already in Use
```bash
# Find process
lsof -i :8081
lsof -i :8082
lsof -i :8083

# Kill process
kill -9 <PID>
```

### PostgreSQL Connection Error
```bash
# Check if running
brew services list | grep postgresql

# Restart
brew services restart postgresql@14
```

### Redis Connection Error
```bash
# Check if running
redis-cli ping

# Restart
brew services restart redis
```

### Elasticsearch Not Starting
```bash
# Check logs
tail -f /usr/local/var/log/elasticsearch.log

# Restart
brew services restart elasticsearch-full
```

### Kafka Connection Error
```bash
# Restart Kafka and Zookeeper
brew services restart zookeeper
brew services restart kafka
```

### Jackson Date/Time Serialization Error
If you see "Java 8 date/time type not supported" error:

```bash
# Rebuild all services
cd flight-booking
mvn clean install -DskipTests

# Restart all services
```

### Kafka Deserialization Error
If notification service shows "Can't deserialize data from topic" error:

```bash
# Delete and recreate the Kafka topic
./fix-kafka-topic.sh

# Then rebuild and restart all services
mvn clean install -DskipTests
```

## Stop Services

### Stop Spring Boot Services
Press `Ctrl+C` in each terminal

### Stop Infrastructure
```bash
brew services stop postgresql@14
brew services stop redis
brew services stop elasticsearch-full
brew services stop kafka
brew services stop zookeeper
```

## Next Steps

1. Review [README.md](README.md) for detailed API documentation
2. Review [ARCHITECTURE.md](ARCHITECTURE.md) for system design
3. Explore the codebase starting with controllers
4. Add custom business logic to services
5. Extend with additional features

## Quick Test Script

Run this complete end-to-end test:

```bash
#!/bin/bash

echo "1. Syncing flights to Elasticsearch..."
curl -X POST http://localhost:8081/api/flights/sync
echo -e "\n\nWaiting 3 seconds for indexing...\n"
sleep 3

echo "2. Searching flights..."
curl "http://localhost:8081/api/flights/search?origin=BLR&destination=DEL&departureDate=2026-12-15&passengers=2&cabinClass=ECONOMY"
echo -e "\n\n"

echo "3. Getting flight details..."
curl http://localhost:8081/api/flights/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11
echo -e "\n\n"

echo "4. Creating booking..."
BOOKING_RESPONSE=$(curl -s -X POST http://localhost:8082/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14",
    "itineraryId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "passengers": [{"firstName": "John", "lastName": "Doe", "age": 30, "gender": "M"}],
    "seats": [{"flightId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11", "seatNo": "12A"}]
  }')
echo $BOOKING_RESPONSE
BOOKING_ID=$(echo $BOOKING_RESPONSE | grep -o '"bookingId":"[^"]*' | cut -d'"' -f4)
echo -e "\n\nBooking ID: $BOOKING_ID\n"

echo "5. Processing payment..."
curl -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d "{\"bookingId\": \"$BOOKING_ID\", \"gateway\": \"RAZORPAY\", \"paymentMethod\": \"UPI\"}"
echo -e "\n\n"

echo "6. Getting user bookings..."
curl http://localhost:8082/api/bookings/user/d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14
echo -e "\n\n"

echo "✅ Test completed! Check Terminal 3 for notification logs."
```

Save as `test-system.sh`, make executable with `chmod +x test-system.sh`, and run with `./test-system.sh`

## Common Issues

**Issue:** Tests fail with "Connection refused"
**Solution:** Tests use H2 in-memory database, no infrastructure needed

**Issue:** Elasticsearch queries return empty
**Solution:** Run sync endpoint first and wait 2-3 seconds: `POST /api/flights/sync`

**Issue:** Search returns empty array `[]`
**Solution:** Check the date in your search query matches the sample data (2026-12-15)

**Issue:** Kafka consumer not receiving messages
**Solution:** Check topic exists and consumer group is running

**Issue:** Redis cache not working
**Solution:** Verify Redis is running and accessible on port 6379

**Issue:** Payment fails with JSON parse error
**Solution:** Replace `<BOOKING_ID>` placeholder with actual UUID from booking response
