package com.evcharging.entity;

public class StationAmenity {
    private Long id;
    private Long stationId;
    private String amenityName;

    public StationAmenity() {}

    public StationAmenity(Long id, Long stationId, String amenityName) {
        this.id = id;
        this.stationId = stationId;
        this.amenityName = amenityName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }

    public String getAmenityName() { return amenityName; }
    public void setAmenityName(String amenityName) { this.amenityName = amenityName; }
}
