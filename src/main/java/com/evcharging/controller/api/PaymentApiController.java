package com.evcharging.controller.api;

import com.evcharging.dto.PaymentRequest;
import com.evcharging.entity.Payment;
import com.evcharging.security.SecurityUtils;
import com.evcharging.service.PaymentService;
import com.evcharging.service.payment.PaymentAdapterFactory;
import com.evcharging.service.payment.PaymentGatewayAdapter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentApiController {

    private final PaymentService paymentService;
    private final PaymentAdapterFactory adapterFactory;

    public PaymentApiController(PaymentService paymentService, PaymentAdapterFactory adapterFactory) {
        this.paymentService = paymentService;
        this.adapterFactory = adapterFactory;
    }

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody PaymentRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));
        Payment payment = paymentService.processPayment(currentUserId, request);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Payment>> getMyPayments() {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));
        return ResponseEntity.ok(paymentService.getUserPayments(currentUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable("id") Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(0L);
        boolean isAdmin = SecurityUtils.isAdmin();
        return ResponseEntity.ok(paymentService.getPaymentForUserOrAdmin(id, currentUserId, isAdmin));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        PaymentGatewayAdapter adapter = adapterFactory.getAdapter();
        if (adapter.verifyWebhookSignature(payload, signature)) {
            return ResponseEntity.ok("Webhook received and verified");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
    }
}
