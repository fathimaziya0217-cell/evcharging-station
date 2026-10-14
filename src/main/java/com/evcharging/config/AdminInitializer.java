package com.evcharging.config;

import com.evcharging.entity.User;
import com.evcharging.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.name:System Administrator}")
    private String adminName;

    @Value("${app.admin.email:admin@evcharge.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@12345}")
    private String adminPassword;

    @Value("${app.admin.phone:+919876543210}")
    private String adminPhone;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ensure admin has valid encoded password
        java.util.Optional<User> adminOpt = userRepository.findByEmail(adminEmail);
        if (adminOpt.isEmpty()) {
            User admin = new User();
            admin.setFullName(adminName);
            admin.setEmail(adminEmail);
            admin.setPhone(adminPhone);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole("ROLE_ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
            logger.info("Admin created with email: {}", adminEmail);
        } else {
            User admin = adminOpt.get();
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setActive(true);
            userRepository.save(admin);
            logger.info("Administrator password verified and synchronized for: {}", adminEmail);
        }

        // Also ensure demo user rahul.ev@gmail.com has valid encoded User@12345 password
        java.util.Optional<User> demoUserOpt = userRepository.findByEmail("rahul.ev@gmail.com");
        if (demoUserOpt.isPresent()) {
            User demoUser = demoUserOpt.get();
            demoUser.setPasswordHash(passwordEncoder.encode("User@12345"));
            demoUser.setActive(true);
            userRepository.save(demoUser);
            logger.info("Demo user password verified and synchronized for: rahul.ev@gmail.com");
        }
    }
}
