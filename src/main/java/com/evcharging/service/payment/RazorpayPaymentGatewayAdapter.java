package com.evcharging.service.payment;

import com.evcharging.dto.PaymentRequest;
import com.evcharging.entity.Booking;
import com.evcharging.entity.Payment;
import com.evcharging.exception.PaymentProcessingException;
import com.evcharging.util.ReferenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

@Component("razorpayPaymentAdapter")
public class RazorpayPaymentGatewayAdapter implements PaymentGatewayAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayPaymentGatewayAdapter.class);

    @Value("${app.payment.razorpay.key-id:rzp_test_placeholder}")
    private String keyId;

    @Value("${app.payment.razorpay.key-secret:secret_placeholder}")
    private String keySecret;

    @Value("${app.payment.razorpay.webhook-secret:webhook_placeholder}")
    private String webhookSecret;

    @Override
    public String getProviderName() {
        return "RAZORPAY";
    }

    public boolean isConfigured() {
        return keyId != null && !keyId.contains("placeholder") &&
               keySecret != null && !keySecret.contains("placeholder");
    }

    @Override
    public Payment initiatePayment(Booking booking, PaymentRequest request) {
        if (!isConfigured()) {
            throw new PaymentProcessingException("Razorpay credentials are not configured in environment variables. Please set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET or switch PAYMENT_MODE=DEMO.");
        }

        Payment payment = new Payment();
        payment.setPaymentReference(ReferenceGenerator.generatePaymentReference());
        payment.setBookingId(booking.getId());
        payment.setUserId(booking.getUserId());
        payment.setAmount(booking.getEstimatedAmount());
        payment.setCurrency("INR");
        payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "RAZORPAY");
        payment.setPaymentStatus("PENDING");
        // In real integration with Razorpay SDK: order = client.orders.create(orderRequest);
        payment.setGatewayOrderId("order_" + ReferenceGenerator.generateBookingReference().replace("-", ""));
        payment.setDemo(false);
        payment.setCreatedAt(LocalDateTime.now());
        logger.info("[RAZORPAY] Created order {} for booking {}", payment.getGatewayOrderId(), booking.getBookingReference());
        return payment;
    }

    @Override
    public Payment processPaymentResult(Payment payment, PaymentRequest request) {
        String orderId = request.getRazorpayOrderId();
        String paymentId = request.getRazorpayPaymentId();
        String signature = request.getRazorpaySignature();

        if (orderId == null || paymentId == null || signature == null) {
            payment.setPaymentStatus("FAILED");
            payment.setFailureReason("Incomplete gateway response parameters from client");
            return payment;
        }

        boolean verified = verifySignature(orderId, paymentId, signature);
        if (verified) {
            payment.setGatewayOrderId(orderId);
            payment.setGatewayPaymentId(paymentId);
            payment.setGatewaySignature(signature);
            payment.setPaymentStatus("SUCCESS");
            payment.setPaidAt(LocalDateTime.now());
            payment.setFailureReason(null);
            logger.info("[RAZORPAY] Payment verified successfully for payment ref: {}", payment.getPaymentReference());
        } else {
            payment.setPaymentStatus("FAILED");
            payment.setFailureReason("Cryptographic signature verification failed - potential tampering rejected");
            logger.error("[RAZORPAY] Signature verification failed for order: {}", orderId);
        }
        return payment;
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        if (!isConfigured()) return false;
        try {
            String payload = orderId + "|" + paymentId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return MessageDigest.isEqual(hexString.toString().getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error verifying Razorpay HMAC-SHA256 signature", e);
            return false;
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        if (webhookSecret == null || webhookSecret.contains("placeholder")) return false;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return MessageDigest.isEqual(hexString.toString().getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error verifying Razorpay webhook signature", e);
            return false;
        }
    }
}
