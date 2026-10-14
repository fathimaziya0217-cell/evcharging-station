package com.evcharging.config;

import com.evcharging.service.payment.PaymentAdapterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Year;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @ControllerAdvice
    public static class GlobalModelAttributes {

        private final PaymentAdapterFactory paymentAdapterFactory;

        public GlobalModelAttributes(PaymentAdapterFactory paymentAdapterFactory) {
            this.paymentAdapterFactory = paymentAdapterFactory;
        }

        @ModelAttribute("appName")
        public String appName() {
            return "EV Charge";
        }

        @ModelAttribute("currentYear")
        public int currentYear() {
            return Year.now().getValue();
        }

        @ModelAttribute("paymentMode")
        public String paymentMode() {
            return paymentAdapterFactory.getConfiguredMode();
        }

        @ModelAttribute("isDemoPaymentMode")
        public boolean isDemoPaymentMode() {
            return paymentAdapterFactory.isDemoMode();
        }
    }
}
