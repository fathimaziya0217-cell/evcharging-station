# Payment Gateway Architecture & Configuration Guide

The EV Charging Station Management System implements an enterprise **Adapter Design Pattern** to decouple the payment checkout and booking verification workflow from external vendor APIs.

---

## 1. Adapter Architecture Overview

```
                        ┌───────────────────────────────┐
                        │      PaymentService           │
                        └──────────────┬────────────────┘
                                       │ delegates via
                                       ▼
                        ┌───────────────────────────────┐
                        │    PaymentGatewayAdapter      │
                        │          (Interface)          │
                        └──────┬─────────────────┬──────┘
                               │                 │
              implements       │                 │  implements
          ┌────────────────────┴───┐         ┌───┴───────────────────────┐
          │                        │         │                           │
          ▼                        ▼         ▼                           ▼
┌─────────────────────────┐            ┌────────────────────────────────┐
│ DemoPaymentGatewayAdapter│            │ RazorpayPaymentGatewayAdapter  │
│    (Development / Lab)  │            │      (Production / Live)       │
└─────────────────────────┘            └────────────────────────────────┘
```

### Components
1. **`PaymentGatewayAdapter` (Interface)**:
   - `createOrder(Booking booking)`: Initiates transaction and generates an external/mock order token.
   - `verifyPayment(PaymentRequest request)`: Verifies signatures and confirms payment success or failure.
   - `processRefund(Payment payment, BigDecimal amount)`: Executes refund if eligible cancellation occurs.
   - `getGatewayName()`: Identifier string (`DEMO_SIMULATOR` or `RAZORPAY`).

2. **`DemoPaymentGatewayAdapter`**:
   - Zero external network dependency.
   - Ideal for offline college lab evaluations and local development.
   - Generates simulated orders (`ORD-DEMO-...`).
   - Supports failure simulation toggle for testing fallback workflows.

3. **`RazorpayPaymentGatewayAdapter`**:
   - Integrated with Razorpay Standard Checkout SDK.
   - Computes HMAC-SHA256 signature verification on backend server before saving.
   - Zero sensitive API secrets exposed to Thymeleaf client templates.

4. **`PaymentAdapterFactory`**:
   - Injected dynamically into `PaymentService` based on `app.payment.mode` in `application.properties`.

---

## 2. Configuration & Switching Modes

The application payment mode is controlled by an environment variable in `application.properties`:

```properties
# Supported Modes: DEMO | REAL
app.payment.mode=${PAYMENT_MODE:DEMO}

# Razorpay Credentials (Required only when PAYMENT_MODE=REAL)
app.payment.razorpay.key-id=${RAZORPAY_KEY_ID:rzp_test_placeholder}
app.payment.razorpay.key-secret=${RAZORPAY_KEY_SECRET:secret_placeholder}
app.payment.razorpay.webhook-secret=${RAZORPAY_WEBHOOK_SECRET:webhook_placeholder}
```

### Switching to Demo Mode (Default):
```powershell
$env:PAYMENT_MODE="DEMO"
java -jar target/ev-charging-station-management-1.0.0.jar
```

### Switching to Live Razorpay Test Mode:
```powershell
$env:PAYMENT_MODE="REAL"
$env:RAZORPAY_KEY_ID="rzp_test_your_actual_key_id"
$env:RAZORPAY_KEY_SECRET="your_actual_key_secret"
java -jar target/ev-charging-station-management-1.0.0.jar
```

---

## 3. Demo Mode Testing Credentials & Behavior

When in `DEMO` mode, the checkout UI presents interactive simulation options:

### 1. UPI Payment Simulation
- **Supported VPA Formats**: Any valid handle, e.g., `driver@okaxis`, `evuser@upi`, `test@paytm`.
- Instant confirmation.

### 2. Card Payment Simulation
- **Test Card Number**: Any 16 digits (e.g. `4111 2222 3333 4444`).
- **Expiry**: Any future date (e.g. `12/28`).
- **CVV**: Any 3 digits (`123`).

### 3. Failure Simulation Testing
- The Demo Checkout UI provides a **"Simulate Payment Failure"** checkbox.
- Checking this box instructs the adapter to simulate an upstream bank decline.
- The system properly handles the failure:
  - Records transaction status as `FAILED` in the `payments` table.
  - Keeps booking in `PENDING_PAYMENT` until expiration.
  - Redirects user to `/payment/failure` with retry options.

---

## 4. Security & Compliance Principles
- **No Secret Leakage**: The Razorpay Key Secret is NEVER sent to the client browser or included in HTML hidden inputs.
- **Server-Side Verification**: Payment verification is executed strictly on the Spring Boot server using HMAC verification before the booking status transitions to `CONFIRMED`.
- **Idempotency**: Duplicate payment attempts for an already paid booking are blocked at the repository transaction boundary.
- **Automatic Refund Simulation**: Cancelling a paid booking automatically triggers refund recording in `payments` and updates booking status to `CANCELLED`.
