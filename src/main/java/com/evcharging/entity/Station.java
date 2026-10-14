package com.evcharging.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Station {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String area;
    private String city;
    private String state;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String contactNumber;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String operatingDays;
    private String parkingInformation;
    private String status; // ACTIVE, INACTIVE, UNDER_MAINTENANCE
    private boolean demo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional transient helper fields for display
    private Double distanceKm;
    private List<String> amenities = new ArrayList<>();
    private List<Charger> chargers = new ArrayList<>();

    public Station() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }

    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }

    public String getOperatingDays() { return operatingDays; }
    public void setOperatingDays(String operatingDays) { this.operatingDays = operatingDays; }

    public String getParkingInformation() { return parkingInformation; }
    public void setParkingInformation(String parkingInformation) { this.parkingInformation = parkingInformation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isDemo() { return demo; }
    public void setDemo(boolean demo) { this.demo = demo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public List<Charger> getChargers() { return chargers; }
    public void setChargers(List<Charger> chargers) { this.chargers = chargers; }
}
