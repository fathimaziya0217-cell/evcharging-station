package com.evcharging;

import com.evcharging.dto.RegisterRequest;
import com.evcharging.dto.UserProfileDto;
import com.evcharging.entity.User;
import com.evcharging.exception.DuplicateEmailException;
import com.evcharging.repository.UserRepository;
import com.evcharging.repository.UserVehicleRepository;
import com.evcharging.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserVehicleRepository vehicleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new RegisterRequest();
        sampleRequest.setFullName("Arun Kumar");
        sampleRequest.setEmail("arun.test" + System.currentTimeMillis() + "@gmail.com");
        sampleRequest.setPhone("+919876500000");
        sampleRequest.setPassword("Secret@123");
        sampleRequest.setConfirmPassword("Secret@123");
        sampleRequest.setRegistrationNumber("KL-11-AA-" + (System.currentTimeMillis() % 10000));
        sampleRequest.setMake("Tata");
        sampleRequest.setModel("Tiago EV");
        sampleRequest.setVehicleType("4-Wheeler Hatchback");
        sampleRequest.setPreferredConnector("CCS2");
    }

    @Test
    @DisplayName("Should successfully register a new user and vehicle with BCrypt hashed password")
    void testRegisterUserSuccess() {
        User registered = userService.registerUser(sampleRequest);

        assertNotNull(registered.getId());
        assertEquals(sampleRequest.getFullName(), registered.getFullName());
        assertEquals(sampleRequest.getEmail().toLowerCase(), registered.getEmail());
        assertEquals("ROLE_USER", registered.getRole());
        assertTrue(registered.isActive());

        // Verify password is encrypted with BCrypt
        assertNotEquals("Secret@123", registered.getPasswordHash());
        assertTrue(passwordEncoder.matches("Secret@123", registered.getPasswordHash()));

        // Verify vehicle is associated
        UserProfileDto profile = userService.getProfile(registered.getId());
        assertEquals(1, profile.getVehicles().size());
        assertEquals(sampleRequest.getRegistrationNumber(), profile.getVehicles().get(0).getRegistrationNumber());
    }

    @Test
    @DisplayName("Should reject registration when duplicate email is supplied")
    void testDuplicateEmailRejection() {
        userService.registerUser(sampleRequest);

        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setFullName("Duplicate User");
        duplicate.setEmail(sampleRequest.getEmail());
        duplicate.setPhone("+919876511111");
        duplicate.setPassword("Pass@123");
        duplicate.setConfirmPassword("Pass@123");
        duplicate.setRegistrationNumber("KL-07-ZZ-9999");
        duplicate.setMake("Ather");
        duplicate.setModel("450X");
        duplicate.setVehicleType("2-Wheeler");
        duplicate.setPreferredConnector("Type 2");

        assertThrows(DuplicateEmailException.class, () -> userService.registerUser(duplicate));
    }

    @Test
    @DisplayName("Should reject registration when passwords do not match")
    void testPasswordMismatchRejection() {
        sampleRequest.setConfirmPassword("DifferentPassword@123");
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(sampleRequest));
    }
}
