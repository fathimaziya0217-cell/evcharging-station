package com.evcharging.controller;

import com.evcharging.dto.PaymentRequest;
import com.evcharging.entity.Booking;
import com.evcharging.entity.Payment;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.security.SecurityUtils;
import com.evcharging.service.BookingService;
import com.evcharging.service.PaymentService;
import com.evcharging.service.payment.PaymentAdapterFactory;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentViewController {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final PaymentAdapterFactory adapterFactory;

    public PaymentViewController(PaymentService paymentService, BookingService bookingService, PaymentAdapterFactory adapterFactory) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.adapterFactory = adapterFactory;
    }

    @GetMapping("/{bookingId}")
    public String paymentCheckout(@PathVariable("bookingId") Long bookingId, Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));
        boolean isAdmin = SecurityUtils.isAdmin();

        Booking booking = bookingService.getBookingForUserOrAdmin(bookingId, currentUserId, isAdmin);

        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            return "redirect:/booking/" + booking.getId();
        }

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(booking.getId());
        paymentRequest.setPaymentMethod("UPI");
        paymentRequest.setUpiId("rahul.ev@okaxis");
        paymentRequest.setDemoOutcome("SUCCESS");

        model.addAttribute("booking", booking);
        model.addAttribute("paymentRequest", paymentRequest);
        model.addAttribute("isDemoMode", adapterFactory.isDemoMode());
        return "payment";
    }

    @PostMapping("/process")
    public String processPayment(@Valid @ModelAttribute("paymentRequest") PaymentRequest request,
                                 RedirectAttributes redirectAttributes) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));

        try {
            Payment payment = paymentService.processPayment(currentUserId, request);
            if ("SUCCESS".equalsIgnoreCase(payment.getPaymentStatus())) {
                redirectAttributes.addFlashAttribute("successMessage", "Payment completed successfully! Booking confirmed.");
                return "redirect:/payment/success/" + payment.getId();
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Payment was not successful: " + payment.getFailureReason());
                return "redirect:/payment/failure/" + payment.getId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment error: " + e.getMessage());
            return "redirect:/payment/" + request.getBookingId();
        }
    }

    @GetMapping("/success/{id}")
    public String paymentSuccess(@PathVariable("id") Long id, Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(0L);
        boolean isAdmin = SecurityUtils.isAdmin();

        Payment payment = paymentService.getPaymentForUserOrAdmin(id, currentUserId, isAdmin);
        Booking booking = bookingService.getBookingById(payment.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + payment.getBookingId()));

        model.addAttribute("payment", payment);
        model.addAttribute("booking", booking);
        return "payment-success";
    }

    @GetMapping("/failure/{id}")
    public String paymentFailure(@PathVariable("id") Long id, Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(0L);
        boolean isAdmin = SecurityUtils.isAdmin();

        Payment payment = paymentService.getPaymentForUserOrAdmin(id, currentUserId, isAdmin);
        Booking booking = bookingService.getBookingById(payment.getBookingId()).orElse(null);

        model.addAttribute("payment", payment);
        model.addAttribute("booking", booking);
        return "payment-failure";
    }
}
