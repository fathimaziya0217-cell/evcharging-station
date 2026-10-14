package com.evcharging.service.payment;

import com.evcharging.dto.PaymentRequest;
import com.evcharging.entity.Booking;
import com.evcharging.entity.Payment;

public interface PaymentGatewayAdapter {

    String getProviderName();

    Payment initiatePayment(Booking booking, PaymentRequest request);

    Payment processPaymentResult(Payment payment, PaymentRequest request);

    boolean verifySignature(String orderId, String paymentId, String signature);

    boolean verifyWebhookSignature(String payload, String signature);
}
