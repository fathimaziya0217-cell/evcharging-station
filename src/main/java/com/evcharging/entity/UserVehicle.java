package com.evcharging.entity;

import java.time.LocalDateTime;

public class UserVehicle {
    private Long id;
    private Long userId;
    private String registrationNumber;
    private String make;
    private String model;
    private String vehicleType;
    private String preferredConnector;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserVehicle() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getPreferredConnector() { return preferredConnector; }
    public void setPreferredConnector(String preferredConnector) { this.preferredConnector = preferredConnector; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
