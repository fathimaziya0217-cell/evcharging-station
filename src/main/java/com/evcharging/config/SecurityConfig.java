package com.evcharging.config;

import com.evcharging.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler handler = 
                new org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/user/dashboard");
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (roles.contains("ROLE_ADMIN")) {
                handler.setDefaultTargetUrl("/admin/dashboard");
            } else {
                handler.setDefaultTargetUrl("/user/dashboard");
            }
            handler.onAuthenticationSuccess(request, response, authentication);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Static resources & public assets
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // Public views and search endpoints
                .requestMatchers("/", "/home", "/about", "/how-it-works", "/stations/**", "/auth/**", "/login", "/register").permitAll()
                .requestMatchers("/api/auth/**", "/api/stations/**", "/api/payments/webhook").permitAll()
                // Admin secured endpoints
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                // User & customer booking/payment workflows
                .requestMatchers("/user/**", "/booking/**", "/payment/**", "/api/bookings/**", "/api/payments/**", "/api/sessions/**").hasAnyRole("USER", "ADMIN")
                // Any other request must be authenticated
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(customAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                // Keep CSRF enabled for all HTML forms, ignoring stateless webhook or test endpoints
                .ignoringRequestMatchers("/api/payments/webhook", "/api/auth/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );

        return http.build();
    }
}
