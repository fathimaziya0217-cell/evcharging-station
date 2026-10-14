package com.evcharging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // DEMO, UPI, CARD, NETBANKING

    // For Demo payment simulation: SUCCESS, FAILED, CANCELLED
    private String demoOutcome;

    // Real payment method fields
    private String upiId;
    private String cardNumber;
    private String cardHolderName;
    private String cardExpiry;
    private String cardCvv;
    private String bankName;
    private String walletName;

    // Optional gateway payment fields
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    public PaymentRequest() {}

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getDemoOutcome() { return demoOutcome; }
    public void setDemoOutcome(String demoOutcome) { this.demoOutcome = demoOutcome; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }

    public String getCardCvv() { return cardCvv; }
    public void setCardCvv(String cardCvv) { this.cardCvv = cardCvv; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getWalletName() { return walletName; }
    public void setWalletName(String walletName) { this.walletName = walletName; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }
}
