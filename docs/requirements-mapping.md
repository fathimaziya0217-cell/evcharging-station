# Requirements Traceability Matrix

This document provides a comprehensive traceability mapping between the requirements defined in the **Project Abstract PDF** and **Project Presentation PDF** and their concrete implementations in the codebase.

---

## 1. Project Requirements & Implementation Mapping

| # | Requirement Area | Abstract / Presentation Specification | Implemented Backend Components | Database Entities & Tables | Frontend Templates & UI |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | **User Authentication & Profiles** | Secure registration, login, role separation (USER & ADMIN), BCrypt encryption, vehicle details storage. | `AuthController.java`<br>`UserService.java`<br>`UserRepository.java`<br>`CustomUserDetailsService.java` | `users`<br>`user_vehicles` | `register.html`<br>`login.html`<br>`user/profile.html` |
| **2** | **Vehicle Management** | Link driver vehicles, brand, model, license plate, and preferred connector type. | `UserService.java`<br>`UserVehicleRepository.java` | `user_vehicles` | `user/profile.html`<br>`register.html` |
| **3** | **Station Finder & Search** | Interactive map view, proximity search (Haversine formula), filter by city/connector/availability. | `StationViewController.java`<br>`StationApiController.java`<br>`StationService.java`<br>`StationRepository.java`<br>`GeoLocationUtils.java` | `stations`<br>`station_amenities`<br>`chargers` | `stations.html`<br>`static/js/main.js` (Leaflet.js integration) |
| **4** | **Station Details & Pricing** | Detailed station page, operating hours, amenities, charger guns, kW power rating, price per kWh. | `StationViewController.java`<br>`ChargerService.java` | `stations`<br>`station_amenities`<br>`chargers` | `station-details.html` |
| **5** | **Slot Booking & Concurrency** | Select date & time slot, prevent double-booking, calculate estimated price, reserve slot. | `BookingViewController.java`<br>`BookingApiController.java`<br>`BookingService.java`<br>`BookingRepository.java` (Row lock with `FOR UPDATE`) | `bookings`<br>`booking_status_history` | `booking.html`<br>`user/my-bookings.html` |
| **6** | **Payment Processing** | UPI & Card checkout, Adapter Pattern for Demo simulator and Razorpay real gateway, payment receipts. | `PaymentViewController.java`<br>`PaymentApiController.java`<br>`PaymentService.java`<br>`PaymentAdapterFactory.java`<br>`DemoPaymentGatewayAdapter.java`<br>`RazorpayPaymentGatewayAdapter.java` | `payments` | `payment.html`<br>`payment-success.html`<br>`payment-failure.html` |
| **7** | **User Dashboard & Bookings** | Overview of upcoming and completed bookings, quick booking actions, printable PDF/print receipt view. | `UserDashboardController.java`<br>`BookingService.java` | `bookings`<br>`payments`<br>`user_vehicles` | `user/dashboard.html`<br>`user/my-bookings.html`<br>`user/booking-details.html`<br>`user/payment-history.html` |
| **8** | **Live Charging Session Telemetry** | Real-time battery %, energy consumed (kWh), charging speed (kW), session timeline. | `ChargingSessionService.java`<br>`ChargingSessionRepository.java` | `charging_sessions` | `user/session-details.html`<br>`admin/sessions.html` |
| **9** | **Cancellation & Refunds** | User-initiated cancellation of eligible bookings, automated refund tracking, slot release. | `BookingService.java`<br>`PaymentService.java` | `bookings`<br>`payments`<br>`booking_status_history` | `user/my-bookings.html`<br>`user/booking-details.html` |
| **10** | **Admin Dashboard & Management** | KPI metrics, station management, charger maintenance toggles, user management, audit logs, CSV exports. | `AdminController.java`<br>`AdminApiController.java`<br>`AdminReportService.java`<br>`AuditLogService.java` | `admin_audit_logs`<br>`application_settings`<br>All tables | `admin/dashboard.html`<br>`admin/stations.html`<br>`admin/station-form.html`<br>`admin/chargers.html`<br>`admin/bookings.html`<br>`admin/sessions.html`<br>`admin/payments.html`<br>`admin/users.html`<br>`admin/reports.html`<br>`admin/audit-logs.html`<br>`admin/settings.html` |
| **11** | **Kerala & National Demo Data** | Real demonstration locations in Kannur, Taliparamba, Thalassery, Kozhikode, Kochi, Bengaluru, Chennai, Coimbatore. | `database/demo-data.sql`<br>`V2__seed_demo_data.sql` | `stations`<br>`chargers` | Clearly flagged in UI with `[DEMO]` badges. |

---

## 2. Technology Constraints Verification

| Mandatory Constraint | Status | Implementation Proof |
| :--- | :--- | :--- |
| **Strictly Spring JDBC** | **COMPLIANT** | All data access in `repository/` uses `NamedParameterJdbcTemplate` and `JdbcTemplate`. |
| **No JPA / Hibernate** | **COMPLIANT** | `pom.xml` excludes `spring-boot-starter-data-jpa` and Hibernate. No `@Entity` annotations used. |
| **Java 17 & Spring Boot 3.x** | **COMPLIANT** | Spring Boot 3.3.4 compiled for Java 17 compatibility. |
| **MySQL 8.x Database** | **COMPLIANT** | `mysql-connector-j` driver configured with InnoDB tables and foreign key constraints. |
| **Leaflet & OpenStreetMap** | **COMPLIANT** | Included via CDN in `templates/fragments/layout.html` and initialized dynamically in `main.js`. |
| **Responsive UI with Bootstrap 5** | **COMPLIANT** | Bootstrap 5.3 + custom theme styles in `static/css/main.css`. Works on Mobile, Tablet, and Desktop. |
| **Pure Java Backend** | **COMPLIANT** | No Python, Node.js, Kotlin, or Flutter frameworks used. |
