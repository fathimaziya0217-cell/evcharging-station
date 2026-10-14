package com.evcharging.util;

import com.evcharging.entity.Station;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("stationJsonHelper")
public class StationJsonHelper {

    /**
     * Serializes a list of stations to a compact JSON array string for Leaflet map markers.
     */
    public String toJson(List<Station> stations) {
        if (stations == null || stations.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < stations.size(); i++) {
            Station s = stations.get(i);
            sb.append("{");
            sb.append("\"id\":").append(s.getId()).append(",");
            sb.append("\"name\":").append(escapeJson(s.getName())).append(",");
            sb.append("\"address\":").append(escapeJson(s.getAddress())).append(",");
            sb.append("\"city\":").append(escapeJson(s.getCity())).append(",");
            sb.append("\"latitude\":").append(s.getLatitude() != null ? s.getLatitude().toString() : "0.0").append(",");
            sb.append("\"longitude\":").append(s.getLongitude() != null ? s.getLongitude().toString() : "0.0").append(",");
            sb.append("\"status\":").append(escapeJson(s.getStatus())).append(",");
            sb.append("\"demo\":").append(s.isDemo());
            sb.append("}");
            if (i < stations.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "\"\"";
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r") + "\"";
    }
}
