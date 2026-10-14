package com.evcharging.dto;

import java.math.BigDecimal;

public class StationSearchCriteria {
    private String city;
    private String area;
    private String query; // search name/address/locality
    private String chargerType;
    private String connectorType;
    private BigDecimal minPowerKw;
    private BigDecimal maxPricePerKwh;
    private String availabilityStatus; // AVAILABLE, etc.
    private Double userLat;
    private Double userLng;
    private Double radiusKm;

    public StationSearchCriteria() {}

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getChargerType() { return chargerType; }
    public void setChargerType(String chargerType) { this.chargerType = chargerType; }

    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }

    public BigDecimal getMinPowerKw() { return minPowerKw; }
    public void setMinPowerKw(BigDecimal minPowerKw) { this.minPowerKw = minPowerKw; }

    public BigDecimal getMaxPricePerKwh() { return maxPricePerKwh; }
    public void setMaxPricePerKwh(BigDecimal maxPricePerKwh) { this.maxPricePerKwh = maxPricePerKwh; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public Double getUserLat() { return userLat; }
    public void setUserLat(Double userLat) { this.userLat = userLat; }

    public Double getUserLng() { return userLng; }
    public void setUserLng(Double userLng) { this.userLng = userLng; }

    public Double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Double radiusKm) { this.radiusKm = radiusKm; }
}
