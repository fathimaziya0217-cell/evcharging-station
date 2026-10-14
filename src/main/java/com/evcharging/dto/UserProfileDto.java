package com.evcharging.dto;

import com.evcharging.entity.UserVehicle;
import java.util.ArrayList;
import java.util.List;

public class UserProfileDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean active;
    private List<UserVehicle> vehicles = new ArrayList<>();

    public UserProfileDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<UserVehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<UserVehicle> vehicles) { this.vehicles = vehicles; }
}
