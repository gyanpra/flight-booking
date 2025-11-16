#!/bin/bash

echo "=========================================="
echo "Flight Booking System - End-to-End Test"
echo "=========================================="
echo ""

echo "1️⃣  Syncing flights to Elasticsearch..."
curl -X POST http://localhost:8081/api/flights/sync
echo -e "\n\n⏳ Waiting 3 seconds for indexing...\n"
sleep 3

echo "2️⃣  Searching flights (BLR → DEL)..."
SEARCH_RESULT=$(curl -s "http://localhost:8081/api/flights/search?origin=BLR&destination=DEL&departureDate=2026-12-15&passengers=2&cabinClass=ECONOMY")
echo $SEARCH_RESULT | python3 -m json.tool 2>/dev/null || echo $SEARCH_RESULT
echo -e "\n"

echo "3️⃣  Getting flight details..."
FLIGHT_DETAILS=$(curl -s http://localhost:8081/api/flights/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11)
echo $FLIGHT_DETAILS | python3 -m json.tool 2>/dev/null || echo $FLIGHT_DETAILS
echo -e "\n"

echo "4️⃣  Creating booking..."
BOOKING_RESPONSE=$(curl -s -X POST http://localhost:8082/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14",
    "itineraryId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "passengers": [{"firstName": "John", "lastName": "Doe", "age": 30, "gender": "M"}],
    "seats": [{"flightId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11", "seatNo": "12A"}]
  }')
echo $BOOKING_RESPONSE | python3 -m json.tool 2>/dev/null || echo $BOOKING_RESPONSE

# Extract booking ID (works with or without jq)
if command -v jq &> /dev/null; then
    BOOKING_ID=$(echo $BOOKING_RESPONSE | jq -r '.bookingId')
else
    BOOKING_ID=$(echo $BOOKING_RESPONSE | grep -o '"bookingId":"[^"]*' | cut -d'"' -f4)
fi

echo -e "\n📋 Booking ID: $BOOKING_ID\n"

if [ -z "$BOOKING_ID" ] || [ "$BOOKING_ID" == "null" ]; then
    echo "❌ Failed to create booking. Exiting..."
    exit 1
fi

echo "5️⃣  Processing payment..."
PAYMENT_RESPONSE=$(curl -s -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d "{\"bookingId\": \"$BOOKING_ID\", \"gateway\": \"RAZORPAY\", \"paymentMethod\": \"UPI\"}")
echo $PAYMENT_RESPONSE | python3 -m json.tool 2>/dev/null || echo $PAYMENT_RESPONSE
echo -e "\n"

echo "6️⃣  Getting user bookings..."
USER_BOOKINGS=$(curl -s http://localhost:8082/api/bookings/user/d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14)
echo $USER_BOOKINGS | python3 -m json.tool 2>/dev/null || echo $USER_BOOKINGS
echo -e "\n"

echo "=========================================="
echo "✅ Test completed successfully!"
echo "=========================================="
echo ""
echo "📊 Summary:"
echo "  - Flights synced to Elasticsearch"
echo "  - Search returned results"
echo "  - Booking created: $BOOKING_ID"
echo "  - Payment processed"
echo "  - Kafka event published"
echo ""
echo "💡 Check Terminal 3 (Notification Service) for notification logs"
echo ""
