# Installation & Deployment Guide

This guide provides end-to-end instructions for deploying and running the **EV Charging Station Management System** on a local development workstation or demonstration environment.

---

## 1. System Requirements & Prerequisites

Ensure the following tools are installed on your machine:

| Component | Minimum Version | Verified Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Java Development Kit (JDK)** | Java 17 | JDK 17 / JDK 27 | Backend Runtime & Compilation |
| **Apache Maven** | 3.8.x | Maven 3.9.x | Build and dependency manager |
| **MySQL Server** | 8.0.x | MySQL 8.0.x | Relational database persistence |
| **Modern Web Browser** | Any modern | Chrome, Edge, Firefox | Client rendering & Leaflet maps |

Verify installations via terminal:
```powershell
java -version
mvn -version
mysql --version
```

---

## 2. Database Setup & Initialization

### Step 2.1: Start MySQL Server
Ensure the MySQL service is running:
- **Windows**: `Start-Service MySQL80`
- **Linux**: `sudo systemctl start mysql`
- **macOS**: `brew services start mysql`

### Step 2.2: Create Dedicated Application User & Database
Open a terminal and execute the user creation script using root credentials:

```powershell
# In PowerShell (replace with your MySQL root password):
Get-Content "database/create-app-user.sql" | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
```

This creates:
- Database: `ev_charging_db`
- Application User: `ev_app`
- Password: `ev_secure_password_123`
- Host privileges: `localhost` and `%`

### Step 2.3: Execute Schema and Demo Seed Data
Initialize tables and sample stations (Kannur, Taliparamba, Thalassery, Kozhikode, Kochi, Bengaluru, Chennai, Coimbatore):

```powershell
# 1. Execute schema creation
Get-Content "database/schema.sql" | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u ev_app -pev_secure_password_123

# 2. Execute demo seed data
Get-Content "database/demo-data.sql" | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u ev_app -pev_secure_password_123
```

---

## 3. Configuration & Environment Variables

Copy `.env.example` to `.env` or set environment variables if needed. Defaults are already pre-configured to work directly with the `ev_app` database user:

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `SERVER_PORT` | `8080` | Web application HTTP port |
| `DB_URL` | `jdbc:mysql://localhost:3306/ev_charging_db?...` | JDBC connection string |
| `DB_USERNAME` | `ev_app` | MySQL username |
| `DB_PASSWORD` | `ev_secure_password_123` | MySQL password |
| `ADMIN_NAME` | `System Administrator` | Default Admin Full Name |
| `ADMIN_EMAIL` | `admin@evcharge.com` | Default Admin Email |
| `ADMIN_PASSWORD` | `Admin@12345` | Default Admin Password |
| `PAYMENT_MODE` | `DEMO` | `DEMO` or `REAL` |

---

## 4. Building the Application

Compile the code and execute automated tests:

```powershell
# Run full clean and test suite (runs with H2 in-memory MySQL mode)
mvn clean test
```

Expected output:
```
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Package the executable standalone Spring Boot JAR:
```powershell
mvn clean package -DskipTests
```
The output JAR is created at `target/ev-charging-station-management-1.0.0.jar`.

---

## 5. Running the Application

### Option A: Using Executable JAR (Recommended)
```powershell
java -jar target/ev-charging-station-management-1.0.0.jar
```

### Option B: Using Maven Spring Boot Plugin
```powershell
mvn spring-boot:run
```

Once started, the console displays:
```
INFO c.evcharging.EvChargingApplication - Started EvChargingApplication in 3.286 seconds
INFO o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port 8080 (http) with context path '/'
INFO c.evcharging.config.AdminInitializer - Administrator account verified: admin@evcharge.com
```

---

## 6. Accessing the Web Application

Open your browser and navigate to:
- **Public Home Page**: `http://localhost:8080/`
- **Station Finder & Map**: `http://localhost:8080/stations`
- **User Login**: `http://localhost:8080/login`
- **User Registration**: `http://localhost:8080/register`
- **Admin Dashboard**: `http://localhost:8080/admin/dashboard`

### Default Pre-Configured Accounts

1. **System Administrator**:
   - Email: `admin@evcharge.com`
   - Password: `Admin@12345`
   - Role: Complete management of stations, chargers, sessions, audit logs, and reports.

2. **Registered EV Driver (Demo User)**:
   - Email: `rahul.ev@gmail.com`
   - Password: `User@12345`
   - Role: Vehicle owner, slot bookings, payment history, live session views.

---

## 7. Troubleshooting Common Issues

### Issue 1: Port 8080 is already in use
**Solution**: Run on another port using the environment variable or flag:
```powershell
java -jar target/ev-charging-station-management-1.0.0.jar --server.port=8085
```

### Issue 2: MySQL Access Denied
**Solution**: Verify credentials or reset password:
```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u ev_app -pev_secure_password_123 -e "SELECT 1;"
```
If connecting with root, pass your root password:
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YourRootPassword"
java -jar target/ev-charging-station-management-1.0.0.jar
```

### Issue 3: Map tiles not loading
**Solution**: The station map uses OpenStreetMap CDN tiles via Leaflet.js. Ensure your machine has active internet access or is not blocked by a corporate proxy.
