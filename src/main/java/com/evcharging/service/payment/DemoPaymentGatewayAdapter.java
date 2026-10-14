package com.evcharging.service.payment;

import com.evcharging.dto.PaymentRequest;
import com.evcharging.entity.Booking;
import com.evcharging.entity.Payment;
import com.evcharging.util.ReferenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("demoPaymentAdapter")
public class DemoPaymentGatewayAdapter implements PaymentGatewayAdapter {

    private static final Logger logger = LoggerFactory.getLogger(DemoPaymentGatewayAdapter.class);

    @Override
    public String getProviderName() {
        return "DEMO";
    }

    @Override
    public Payment initiatePayment(Booking booking, PaymentRequest request) {
        Payment payment = new Payment();
        payment.setPaymentReference(ReferenceGenerator.generatePaymentReference());
        payment.setBookingId(booking.getId());
        payment.setUserId(booking.getUserId());
        payment.setAmount(booking.getEstimatedAmount());
        payment.setCurrency("INR");
        String method = request.getPaymentMethod();
        if (method == null || method.isBlank() || "DEMO".equalsIgnoreCase(method)) {
            method = "UPI";
        }
        payment.setPaymentMethod(method.toUpperCase());
        payment.setPaymentStatus("PENDING");
        payment.setGatewayOrderId("ORD-" + method.toUpperCase() + "-" + System.currentTimeMillis());
        payment.setDemo(true);
        payment.setCreatedAt(LocalDateTime.now());
        logger.info("[DEMO PAYMENT] Initiated {} payment order {} for booking {}", payment.getPaymentMethod(), payment.getPaymentReference(), booking.getBookingReference());
        return payment;
    }

    @Override
    public Payment processPaymentResult(Payment payment, PaymentRequest request) {
        String outcome = request.getDemoOutcome();
        if (outcome == null || outcome.isBlank()) {
            outcome = "SUCCESS";
        }

        switch (outcome.toUpperCase()) {
            case "SUCCESS" -> {
                payment.setPaymentStatus("SUCCESS");
                payment.setGatewayPaymentId("DEMO-PAY-" + System.currentTimeMillis());
                payment.setGatewaySignature("DEMO-SIGN-VERIFIED");
                payment.setPaidAt(LocalDateTime.now());
                payment.setFailureReason(null);
                logger.info("[DEMO PAYMENT] Simulated SUCCESS for payment {}", payment.getPaymentReference());
            }
            case "FAILED" -> {
                payment.setPaymentStatus("FAILED");
                payment.setFailureReason("Simulated card/bank transaction failure in Demo mode");
                payment.setPaidAt(null);
                logger.warn("[DEMO PAYMENT] Simulated FAILURE for payment {}", payment.getPaymentReference());
            }
            case "CANCELLED" -> {
                payment.setPaymentStatus("CANCELLED");
                payment.setFailureReason("User simulated payment cancellation");
                payment.setPaidAt(null);
                logger.info("[DEMO PAYMENT] Simulated CANCELLATION for payment {}", payment.getPaymentReference());
            }
            default -> {
                payment.setPaymentStatus("FAILED");
                payment.setFailureReason("Unknown simulation outcome: " + outcome);
            }
        }
        return payment;
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        return signature != null && signature.startsWith("DEMO-SIGN");
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        return true;
    }
}
