# Carrier Orchestration API

API for carriers to add flight schedules and inventory to the platform.

## Base URL
```
http://localhost:8081/api/carrier
```

## Endpoints

### 1. Create Flight

**POST** `/flights`

Add a new flight schedule.

**Request Body:**
```json
{
  "carrier": "INDIGO",
  "flightNumber": "6E-123",
  "departureAirport": "BLR",
  "arrivalAirport": "DEL",
  "departureTime": "2025-12-12T06:00:00",
  "arrivalTime": "2025-12-12T08:30:00",
  "equipment": "A320"
}
```

**Response:** `201 Created`
```json
{
  "flightId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "flightNumber": "6E-123",
  "message": "Flight created successfully"
}
```

**Carriers:**
- `INDIGO`
- `AIR_INDIA`
- `SPICEJET`
- `VISTARA`
- `GO_FIRST`

---

### 2. Add Seat Inventory

**POST** `/inventory`

Add seat inventory for a flight.

**Request Body:**
```json
{
  "flightId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "fareClass": "Y",
  "cabinClass": "ECONOMY",
  "totalSeats": 180,
  "price": 5000.00
}
```

**Response:** `201 Created`

**Cabin Classes:**
- `ECONOMY`
- `PREMIUM_ECONOMY`
- `BUSINESS`
- `FIRST`

**Fare Classes:**
- Economy: `Y`, `B`, `M`, `H`
- Business: `J`, `C`, `D`
- First: `F`, `A`

---

## Complete Example

### Step 1: Create Flight
```bash
curl -X POST http://localhost:8081/api/carrier/flights \
  -H "Content-Type: application/json" \
  -d '{
    "carrier": "INDIGO",
    "flightNumber": "6E-456",
    "departureAirport": "BOM",
    "arrivalAirport": "BLR",
    "departureTime": "2025-12-15T10:00:00",
    "arrivalTime": "2025-12-15T11:30:00",
    "equipment": "A320"
  }'
```

**Save the `flightId` from response.**

### Step 2: Add Economy Inventory
```bash
curl -X POST http://localhost:8081/api/carrier/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": "<FLIGHT_ID_FROM_STEP_1>",
    "fareClass": "Y",
    "cabinClass": "ECONOMY",
    "totalSeats": 180,
    "price": 4500.00
  }'
```

### Step 3: Add Business Inventory
```bash
curl -X POST http://localhost:8081/api/carrier/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": "<FLIGHT_ID_FROM_STEP_1>",
    "fareClass": "J",
    "cabinClass": "BUSINESS",
    "totalSeats": 20,
    "price": 15000.00
  }'
```

### Step 4: Verify Flight
```bash
curl "http://localhost:8081/api/flights/<FLIGHT_ID>"
```

---

## Bulk Load Script

Create multiple flights at once:

```bash
#!/bin/bash

# Create Flight 1
FLIGHT1=$(curl -s -X POST http://localhost:8081/api/carrier/flights \
  -H "Content-Type: application/json" \
  -d '{
    "carrier": "INDIGO",
    "flightNumber": "6E-789",
    "departureAirport": "DEL",
    "arrivalAirport": "BOM",
    "departureTime": "2025-12-20T14:00:00",
    "arrivalTime": "2025-12-20T16:00:00",
    "equipment": "A321"
  }' | jq -r '.flightId')

echo "Created Flight 1: $FLIGHT1"

# Add inventory
curl -X POST http://localhost:8081/api/carrier/inventory \
  -H "Content-Type: application/json" \
  -d "{
    \"flightId\": \"$FLIGHT1\",
    \"fareClass\": \"Y\",
    \"cabinClass\": \"ECONOMY\",
    \"totalSeats\": 180,
    \"price\": 5500.00
  }"

curl -X POST http://localhost:8081/api/carrier/inventory \
  -H "Content-Type: application/json" \
  -d "{
    \"flightId\": \"$FLIGHT1\",
    \"fareClass\": \"J\",
    \"cabinClass\": \"BUSINESS\",
    \"totalSeats\": 16,
    \"price\": 16000.00
  }"

echo "Inventory added for Flight 1"
```

---

## Notes

- Flights are automatically synced to Elasticsearch when inventory is added
- All flights are created with `isActive = true`
- Available seats initially equal total seats
- Price is in INR (Indian Rupees)
- Timestamps must be in ISO-8601 format
- Flight numbers should follow carrier conventions (e.g., 6E-XXX for IndiGo)

## Error Responses

**400 Bad Request** - Invalid input
```json
{
  "timestamp": "2025-01-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/carrier/flights"
}
```

**404 Not Found** - Flight not found
```json
{
  "timestamp": "2025-01-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Flight not found: {flightId}",
  "path": "/api/carrier/inventory"
}
```
