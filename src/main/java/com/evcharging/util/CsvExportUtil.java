package com.evcharging.util;

import java.util.List;

public class CsvExportUtil {

    public static String buildCsv(List<String> headers, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder();
        
        // Write headers
        for (int i = 0; i < headers.size(); i++) {
            sb.append(escapeSpecialCharacters(headers.get(i)));
            if (i < headers.size() - 1) sb.append(",");
        }
        sb.append("\r\n");

        // Write rows
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                sb.append(escapeSpecialCharacters(row.get(i)));
                if (i < row.size() - 1) sb.append(",");
            }
            sb.append("\r\n");
        }

        return sb.toString();
    }

    private static String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "\"\"";
        }
        String escaped = data.replaceAll("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return "\"" + escaped + "\"";
    }
}
