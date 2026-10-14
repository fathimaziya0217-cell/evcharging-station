package com.evcharging.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;

public class StationFormDto {
    private Long id;

    @NotBlank(message = "Station name is required")
    private String name;

    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Area / Locality is required")
    private String area;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private BigDecimal longitude;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    private LocalTime openingTime = LocalTime.of(0, 0);
    private LocalTime closingTime = LocalTime.of(23, 59);
    private String operatingDays = "All Days";
    private String parkingInformation;
    private String status = "ACTIVE";
    private boolean demo = false;
    private String amenitiesCsv; // Comma-separated amenities e.g. "Restroom, Wi-Fi, Café"

    public StationFormDto() {}

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

    public String getAmenitiesCsv() { return amenitiesCsv; }
    public void setAmenitiesCsv(String amenitiesCsv) { this.amenitiesCsv = amenitiesCsv; }
}
