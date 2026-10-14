package com.evcharging.service;

import com.evcharging.dto.RegisterRequest;
import com.evcharging.dto.UserProfileDto;
import com.evcharging.entity.User;
import com.evcharging.entity.UserVehicle;
import com.evcharging.exception.DuplicateEmailException;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.repository.UserRepository;
import com.evcharging.repository.UserVehicleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserVehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserVehicleRepository vehicleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("An account with email " + request.getEmail() + " already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Save initial vehicle
        UserVehicle vehicle = new UserVehicle();
        vehicle.setUserId(savedUser.getId());
        vehicle.setRegistrationNumber(request.getRegistrationNumber().trim().toUpperCase());
        vehicle.setMake(request.getMake().trim());
        vehicle.setModel(request.getModel().trim());
        vehicle.setVehicleType(request.getVehicleType().trim());
        vehicle.setPreferredConnector(request.getPreferredConnector().trim());
        vehicleRepository.save(vehicle);

        return savedUser;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        List<UserVehicle> vehicles = vehicleRepository.findByUserId(userId);

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setVehicles(vehicles);
        return dto;
    }

    @Transactional
    public void updateProfile(Long userId, String fullName, String phone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setFullName(fullName.trim());
        user.setPhone(phone.trim());
        userRepository.save(user);
    }

    @Transactional
    public void addVehicle(Long userId, UserVehicle vehicle) {
        vehicle.setUserId(userId);
        vehicleRepository.save(vehicle);
    }

    @Transactional
    public void removeVehicle(Long userId, Long vehicleId) {
        UserVehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));
        if (!v.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to delete this vehicle");
        }
        vehicleRepository.deleteById(vehicleId);
    }

    @Transactional
    public void toggleUserActive(Long userId, boolean active) {
        userRepository.updateStatus(userId, active);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return userRepository.findAll();
        }
        return userRepository.searchUsers(query);
    }

    public long countTotalUsers() {
        return userRepository.countUsers();
    }
}
