package com.evcharging.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentAdapterFactory {

    private final DemoPaymentGatewayAdapter demoAdapter;
    private final RazorpayPaymentGatewayAdapter razorpayAdapter;

    @Value("${app.payment.mode:DEMO}")
    private String configuredMode;

    public PaymentAdapterFactory(DemoPaymentGatewayAdapter demoAdapter, RazorpayPaymentGatewayAdapter razorpayAdapter) {
        this.demoAdapter = demoAdapter;
        this.razorpayAdapter = razorpayAdapter;
    }

    public PaymentGatewayAdapter getAdapter() {
        if ("REAL".equalsIgnoreCase(configuredMode) || "RAZORPAY".equalsIgnoreCase(configuredMode)) {
            return razorpayAdapter;
        }
        return demoAdapter;
    }

    public String getConfiguredMode() {
        return configuredMode != null ? configuredMode.toUpperCase() : "DEMO";
    }

    public boolean isDemoMode() {
        return "DEMO".equalsIgnoreCase(configuredMode);
    }
}
