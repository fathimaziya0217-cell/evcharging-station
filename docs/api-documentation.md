# REST API Specification

The EV Charging Station Management System exposes RESTful APIs alongside its server-rendered Thymeleaf views. All API endpoints return standard JSON responses and use HTTP status codes.

Base URL: `http://localhost:8080/api`

---

## 1. Authentication & Security
- Standard Web Session authentication is supported via HTTP cookies (CSRF token included for mutating requests).
- Public APIs can be queried without authentication.
- Private endpoints require active user or administrator sessions (`ROLE_USER` or `ROLE_ADMIN`).

---

## 2. Charging Station APIs

### 2.1. List All Active Stations
- **Endpoint**: `GET /api/stations`
- **Access**: Public
- **Response**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "[DEMO] EV Charge Caltex Hub",
    "description": "High-speed dual CCS2 charger situated conveniently near Caltex Junction.",
    "address": "Near Caltex Junction, NH 66, Kannur",
    "area": "Caltex",
    "city": "Kannur",
    "state": "Kerala",
    "postalCode": "670002",
    "latitude": 11.874477,
    "longitude": 75.370369,
    "contactNumber": "+914972701100",
    "openingTime": "00:00:00",
    "closingTime": "23:59:59",
    "operatingDays": "All Days",
    "parkingInformation": "Dedicated 4-bay EV parking with canopy",
    "status": "ACTIVE",
    "demo": true,
    "amenities": ["24/7 Security", "Free Wi-Fi", "Restroom", "EV Waiting Lounge"],
    "chargers": [
      {
        "id": 1,
        "stationId": 1,
        "chargerIdentifier": "KNR-CLX-DC01",
        "chargerType": "DC Fast Charging",
        "connectorType": "CCS2",
        "powerKw": 60.00,
        "pricePerKwh": 18.50,
        "availabilityStatus": "AVAILABLE",
        "operationalStatus": "OPERATIONAL"
      }
    ]
  }
]
```

### 2.2. Get Single Station Details
- **Endpoint**: `GET /api/stations/{id}`
- **Access**: Public
- **Response**: `200 OK` (or `404 Not Found` if missing)

### 2.3. Search Stations with Geolocation & Filters
- **Endpoint**: `GET /api/stations/search`
- **Access**: Public
- **Query Parameters**:
  - `city` (string, optional): e.g. "Kannur", "Kochi", "Kozhikode"
  - `area` (string, optional): e.g. "Caltex", "Edappally"
  - `chargerType` (string, optional): e.g. "DC Fast Charging", "AC Fast Charging"
  - `connectorType` (string, optional): e.g. "CCS2", "Type 2"
  - `lat` (decimal, optional): Current latitude of user
  - `lng` (decimal, optional): Current longitude of user
  - `radiusKm` (decimal, optional): Search radius (default: 50 km)
  - `onlyAvailable` (boolean, optional): Filter only stations with active available units
- **Response**: `200 OK` (list sorted by proximity if coordinates provided)

### 2.4. Get Chargers for a Station
- **Endpoint**: `GET /api/stations/{id}/chargers`
- **Access**: Public
- **Response**: `200 OK`

---

## 3. Slot Availability & Booking APIs

### 3.1. Check Slot Availability for a Charger
- **Endpoint**: `GET /api/chargers/{id}/slots`
- **Access**: Public
- **Query Parameters**:
  - `date` (format: `YYYY-MM-DD`, optional, default: today)
- **Response**: `200 OK`
```json
{
  "chargerId": 1,
  "stationId": 1,
  "chargerIdentifier": "KNR-CLX-DC01",
  "date": "2026-10-14",
  "slots": [
    {
      "startTime": "09:00:00",
      "endTime": "10:00:00",
      "available": true
    },
    {
      "startTime": "10:00:00",
      "endTime": "11:00:00",
      "available": false,
      "reason": "Reserved"
    }
  ]
}
```

### 3.2. Create Slot Booking
- **Endpoint**: `POST /api/bookings`
- **Access**: Authenticated (`ROLE_USER` or `ROLE_ADMIN`)
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "stationId": 1,
  "chargerId": 1,
  "bookingDate": "2026-10-14",
  "startTime": "14:00:00",
  "endTime": "15:00:00",
  "vehicleRegistrationNumber": "KL-13-AQ-4521",
  "notes": "Fast charge needed before highway drive"
}
```
- **Response**: `201 Created`
```json
{
  "bookingId": 6,
  "bookingReference": "EVC-20261014-98A1B2",
  "status": "PENDING_PAYMENT",
  "totalAmount": 1110.00,
  "expiresAt": "2026-10-14T14:15:00",
  "paymentUrl": "/payment/checkout?bookingId=6"
}
```
- **Error Responses**:
  - `400 Bad Request`: Validation failure (e.g. end time before start time).
  - `409 Conflict`: Slot is already booked by another driver (`SlotUnavailableException`).

### 3.3. Get Booking Details
- **Endpoint**: `GET /api/bookings/{id}`
- **Access**: Authenticated (Owner of booking or `ROLE_ADMIN`)
- **Response**: `200 OK`

### 3.4. Cancel Booking
- **Endpoint**: `POST /api/bookings/{id}/cancel`
- **Access**: Authenticated (Owner or `ROLE_ADMIN`)
- **Response**: `200 OK`
```json
{
  "success": true,
  "message": "Booking successfully cancelled",
  "bookingReference": "EVC-20261014-98A1B2",
  "refundInitiated": true
}
```

---

## 4. Payment APIs

### 4.1. Process Payment
- **Endpoint**: `POST /api/payments/process`
- **Access**: Authenticated (Booking owner)
- **Request Body**:
```json
{
  "bookingId": 6,
  "paymentMethod": "UPI",
  "upiId": "rahul@okhdfcbank",
  "simulateFailure": false
}
```
- **Response**: `200 OK`
```json
{
  "success": true,
  "paymentReference": "PAY-20261014-A19F8C",
  "bookingReference": "EVC-20261014-98A1B2",
  "amount": 1110.00,
  "status": "SUCCESS",
  "message": "Payment verified and booking confirmed"
}
```

---

## 5. Administration APIs

### 5.1. Dashboard Metrics
- **Endpoint**: `GET /api/admin/metrics`
- **Access**: Admin (`ROLE_ADMIN`)
- **Response**: `200 OK`
```json
{
  "totalUsers": 4,
  "totalStations": 16,
  "totalChargers": 37,
  "totalBookings": 5,
  "totalRevenue": 4725.50,
  "activeSessions": 1
}
```

### 5.2. Update Charger Hardware Status
- **Endpoint**: `POST /api/admin/chargers/{id}/status`
- **Access**: Admin (`ROLE_ADMIN`)
- **Request Body**:
```json
{
  "operationalStatus": "UNDER_MAINTENANCE",
  "reason": "Scheduled bi-weekly connector inspection"
}
```
- **Response**: `200 OK`

### 5.3. Simulate Live Charging Session
- **Endpoint**: `POST /api/admin/sessions/simulate`
- **Access**: Admin (`ROLE_ADMIN`)
- **Request Body**:
```json
{
  "bookingId": 6,
  "currentBatteryPct": 78.5,
  "energyConsumedKwh": 24.6,
  "chargingRateKw": 58.2,
  "status": "IN_PROGRESS"
}
```
- **Response**: `200 OK`

---

## 6. Error Handling & Standard Error Envelope
All error responses return standard format with meaningful descriptions:
```json
{
  "timestamp": "2026-10-14T14:02:11",
  "status": 409,
  "error": "Conflict",
  "message": "The selected time slot (14:00 to 15:00) is already booked for this charger.",
  "path": "/api/bookings"
}
```
