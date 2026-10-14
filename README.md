# EV Charging Station Management System (EV Charge)

> **College Mini-Project Full-Stack Web Application**  
> Developed with **Java 17**, **Spring Boot 3.x**, **Spring JDBC**, **Spring Security**, **MySQL 8.x**, **Thymeleaf**, and **Bootstrap 5**.

---

## 1. Project Overview

The **EV Charging Station Management System** is an enterprise-grade full-stack web application designed to solve the critical challenges of electric vehicle infrastructure in India: discovering charging stations, checking live connector availability, booking time-slots without conflict, and processing seamless payments.

### Key Architectural Highlights:
- **Strictly Spring JDBC**: All data persistence is performed via `NamedParameterJdbcTemplate` and `JdbcTemplate` with explicit `RowMapper` implementations. **No JPA, Hibernate, or ORM frameworks** are used.
- **Concurrency-Safe Slot Booking**: Employs MySQL row-level locking (`SELECT ... FOR UPDATE`) and interval intersection queries to mathematically prevent double-booking.
- **Payment Gateway Adapter Pattern**: Features a pluggable payment architecture supporting an offline **Demo Simulation** mode (ideal for viva evaluations) and live **Razorpay** checkout.
- **Interactive Geospatial Mapping**: Integrates Leaflet.js and OpenStreetMap with browser HTML5 Geolocation and Haversine distance calculations.
- **Complete Administrative Suite**: Real-time KPI dashboards, hardware maintenance toggles, live charging telemetry simulators, and CSV audit reports.

---

## 2. Technology Stack

| Layer | Technologies & Tools |
| :--- | :--- |
| **Backend Framework** | Java 17, Spring Boot 3.3.4, Spring Web MVC |
| **Data Access** | **Spring JDBC** (`NamedParameterJdbcTemplate`), MySQL Connector/J 8.3 |
| **Security** | Spring Security 6, BCrypt Password Hashing, Session Management |
| **Database** | MySQL Server 8.0 (InnoDB Engine, UTF-8 Multibyte) |
| **Frontend UI** | HTML5, CSS3, JavaScript (ES6+), Thymeleaf Template Engine, Bootstrap 5.3 |
| **Mapping & Icons** | Leaflet.js, OpenStreetMap CDN, Bootstrap Icons |
| **Build & Testing** | Apache Maven 3.9, JUnit 5, H2 In-Memory Database (for isolated offline tests) |

---

## 3. Pre-Configured Demonstration Accounts

| Role | Email Address | Password | Permissions & Features |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@evcharge.com` | `Admin@12345` | Full access to Admin Dashboard, Station/Charger CRUD, Session Simulators, Reports |
| **EV Driver** | `rahul.ev@gmail.com` | `User@12345` | Registered owner (Tata Nexon EV & Ather 450X), Slot Booking, Payments, History |

*Note: New drivers can also register immediately via the `/register` page.*

---

## 4. Quick Start & Execution Guide

### Prerequisites
- JDK 17 or higher
- Apache Maven 3.8+
- MySQL Server 8.0+ running on port 3306

### Step 1: Database Setup
1. Log in to your MySQL server as `root` and execute the dedicated user script:
   ```powershell
   Get-Content "database/create-app-user.sql" | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
   ```
   *(This creates user `ev_app` with password `ev_secure_password_123` and database `ev_charging_db`)*

2. Initialize the schema and demo dataset:
   ```powershell
   Get-Content "database/schema.sql" | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u ev_app -pev_secure_password_123
   Get-Content "database/demo-data.sql" | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u ev_app -pev_secure_password_123
   ```

### Step 2: Run Automated Tests
Verify all 13 unit and integration tests:
```powershell
mvn clean test
```

### Step 3: Launch Application
Run directly using Maven:
```powershell
mvn spring-boot:run
```
Or build and execute the standalone JAR:
```powershell
mvn clean package -DskipTests
java -jar target/ev-charging-station-management-1.0.0.jar
```

The application will start on: **`http://localhost:8080`**

---

## 5. College Mini-Project Walkthrough (Viva Demonstration Script)

Follow this 5-minute workflow to demonstrate the complete application during evaluation:

1. **Public Landing Page (`http://localhost:8080/`)**:
   - Showcase the modern dark-navy and electric-green branding.
   - Highlight the quick city finder and popular charging stations.
2. **Interactive Station Finder (`http://localhost:8080/stations`)**:
   - Filter stations by city: **Kannur**, **Taliparamba**, **Thalassery**, **Kozhikode**, or **Kochi**.
   - Click "Locate Me" or click any station pin on the Leaflet map to inspect popup details.
   - Note the unverified demonstration badges (`[DEMO]`) complying with project presentation ethics.
3. **Station Details (`/stations/1`)**:
   - Review operating hours, amenities, and available charging guns (CCS2, Type 2, power ratings).
   - Click **"Book Charging Slot"**.
4. **Slot Booking & Concurrency**:
   - Log in as `rahul.ev@gmail.com` / `User@12345` (or register a new driver).
   - Select date and time slot (e.g. 14:00 to 15:00).
   - Notice the dynamic price calculation and click **"Proceed to Payment"**.
5. **Payment Processing (`/payment/checkout`)**:
   - Select UPI or Card simulation.
   - Demonstrate success flow and inspect the generated printable receipt.
   - Optional: Toggle the "Simulate Payment Failure" checkbox to demonstrate error recovery.
6. **Driver Dashboard (`/user/dashboard`)**:
   - View updated booking stats, vehicle profiles, and active charging telemetry.
7. **Admin Dashboard (`/admin/dashboard`)**:
   - Log in as `admin@evcharge.com` / `Admin@12345`.
   - Review the financial metrics, charger maintenance toggles, and audit logs.
   - Click **"Export CSV Report"** to download the booking report.

---

## 6. Comprehensive Project Documentation

Detailed design specifications and architectural manuals are available in the `docs/` folder:

- [Installation & Deployment Guide](docs/installation-guide.md)
- [Database Schema & ER Architecture](docs/database-design.md)
- [REST API Specifications](docs/api-documentation.md)
- [Payment Gateway Setup Guide](docs/payment-setup.md)
- [Requirements Traceability Matrix](docs/requirements-mapping.md)
- [32-Point Acceptance Verification Checklist](docs/acceptance-checklist.md)

---

## 7. Project File Structure

```
evcharging station/
├── database/
│   ├── create-app-user.sql      # MySQL user creation script
│   ├── schema.sql               # Pure 11-table schema DDL
│   └── demo-data.sql            # Seed dataset (16 stations, 37 chargers)
├── docs/
│   ├── installation-guide.md    # Step-by-step setup manual
│   ├── database-design.md       # ER diagram & JDBC mapping specification
│   ├── api-documentation.md     # REST endpoints & JSON schemas
│   ├── payment-setup.md         # Adapter pattern & gateway documentation
│   ├── requirements-mapping.md  # Traceability matrix for abstract & presentation
│   └── acceptance-checklist.md  # 32 acceptance criteria verification
├── src/
│   ├── main/
│   │   ├── java/com/evcharging/ # 73 Spring Boot Java source files
│   │   │   ├── config/          # Security & admin initialization
│   │   │   ├── controller/      # Web view & REST API controllers
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── entity/          # Pure POJO models
│   │   │   ├── exception/       # Global exception handling
│   │   │   ├── repository/      # Spring JDBC repositories
│   │   │   ├── security/        # CustomUserDetails & authentication
│   │   │   ├── service/         # Core business logic & payment adapters
│   │   │   └── util/            # Haversine & reference generators
│   │   └── resources/
│   │       ├── db/migration/    # Flyway versioned migrations (V1, V2)
│   │       ├── static/          # CSS, JS (Leaflet integration)
│   │       ├── templates/       # Thymeleaf HTML5 layouts and views
│   │       └── application.properties
│   └── test/
│       ├── java/com/evcharging/ # 13 automated unit & integration tests
│       └── resources/           # H2 isolated test configuration
├── pom.xml                      # Maven project configuration
├── .env.example                 # Environment variables reference
└── README.md                    # Main project overview & viva walkthrough
```

---

## 8. License & Academic Declaration
This project is authored for college mini-project academic evaluation. All demonstration charging station data for Kerala and other regions are simulated for academic testing purposes.
