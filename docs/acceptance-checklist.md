# Acceptance Testing & Verification Checklist

This checklist documents the 32 verification checkpoints required for academic evaluation and production readiness of the **EV Charging Station Management System**.

---

## Part A: Authentication & User Management
- [x] **AC-01: User Registration**: EV drivers can register with full name, unique email, phone number, and vehicle specs (plate number, make, model, connector).
- [x] **AC-02: Password Security**: Passwords are encrypted exclusively using Spring Security `BCryptPasswordEncoder` (cost factor 10). Passwords are never stored in plain text.
- [x] **AC-03: Duplicate Email Prevention**: Attempting to register with an existing email returns a validation message without crashing.
- [x] **AC-04: Session Authentication**: Successful login creates a secure HTTP session with `HttpOnly` and `SameSite=Lax` cookies.
- [x] **AC-05: Role Separation**: Role hierarchy separates `ROLE_USER` from `ROLE_ADMIN`. Users cannot access `/admin/**` routes.

---

## Part B: Station Finder & Geolocation
- [x] **AC-06: Interactive Map Display**: Stations are rendered with interactive Leaflet.js markers on OpenStreetMap.
- [x] **AC-07: HTML5 Geolocation API**: Clicking "Near Me" queries browser geolocation and calculates distance using the Haversine formula.
- [x] **AC-08: Manual City/Area Filter**: Dropdown/search input allows filtering stations by City (Kannur, Kochi, Kozhikode, etc.) and Locality.
- [x] **AC-09: Charger & Connector Filtering**: Filters stations by AC Fast, DC Rapid, and connector types (CCS2, Type 2, CHAdeMO).
- [x] **AC-10: Station Detail View**: Detailed page displays operating hours, parking information, real amenities, and attached charging guns.

---

## Part C: Slot Booking & Concurrency Control
- [x] **AC-11: Slot Availability Matrix**: Available time slots are dynamically calculated based on existing active reservations.
- [x] **AC-12: Server-Side Overlap Prevention**: The system rejects overlapping slot reservations via pessimistic locking (`SELECT ... FOR UPDATE`) and interval checks.
- [x] **AC-13: Price Computation**: Estimated booking cost is automatically calculated (`duration_hours * power_kw * price_per_kwh`).
- [x] **AC-14: Booking Reference Generation**: Creates collision-free reference numbers (e.g. `EVC-20261014-98A1B2`).
- [x] **AC-15: Auto-Release of Unpaid Slots**: Reservations in `PENDING_PAYMENT` state automatically expire after 15 minutes via a Spring `@Scheduled` worker.
- [x] **AC-16: User Booking Isolation**: Drivers can view and manage only their own reservations.

---

## Part D: Payment Processing & Invoicing
- [x] **AC-17: Adapter Pattern**: Implements `PaymentGatewayAdapter` decoupling checkout logic from payment providers.
- [x] **AC-18: Zero-Secret Frontend**: Payment credentials and secrets are managed exclusively on the backend server.
- [x] **AC-19: Demo Payment Simulation**: Supports offline UPI and Card checkout simulation for evaluation environments.
- [x] **AC-20: Payment Failure Handling**: Allows simulation of card/bank declines and provides a retry mechanism.
- [x] **AC-21: Printable Receipt**: Provides a formatted receipt view with browser print stylesheet (`@media print`).

---

## Part E: Charging Telemetry & Cancellation
- [x] **AC-22: Live Session Telemetry**: Visualizes battery percentage gauge, energy delivered (kWh), and real-time charging rate (kW).
- [x] **AC-23: Session Timeline**: Displays start, progress, and completion events with audit timestamps.
- [x] **AC-24: Eligible Cancellation**: Drivers can cancel pending or upcoming bookings before the scheduled start time.
- [x] **AC-25: Automated Refund Tracking**: Cancelling a paid reservation updates payment status and records audit history.
- [x] **AC-26: Hardware Status Sync**: Charger availability reflects `AVAILABLE`, `IN_USE`, `RESERVED`, or `MAINTENANCE`.

---

## Part F: Administration & Compliance
- [x] **AC-27: Admin KPI Dashboard**: Displays real-time counts of users, stations, chargers, bookings, and revenue metrics.
- [x] **AC-28: Station & Charger CRUD**: Administrators can add, edit, or decommission stations and charger units.
- [x] **AC-29: Administrative Audit Logging**: Tracks critical admin actions (status overrides, station creation) in `admin_audit_logs`.
- [x] **AC-30: CSV Report Export**: Streams reports in standard CSV format for offline reporting and auditing.
- [x] **AC-31: Strict Spring JDBC Architecture**: Pure Spring JDBC (`NamedParameterJdbcTemplate`) used across all 10 repositories. No JPA or Hibernate.
- [x] **AC-32: Automated Test Coverage**: 13 unit and integration tests passing (`mvn test`) covering user auth, station search, concurrency checks, and payment processing.
