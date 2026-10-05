package com.mycompany.networkmonitoringsystem;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class IncidentRepository {


    public void createIncident(int deviceId, String description) {
    try {
        Connection conn = DatabaseManager.connect();

        String sql = "INSERT INTO incidents " +
                "(device_id, incident_type, severity, description, created_at, incident_status) " +
                "VALUES (?, 'Network', 'HIGH', ?, NOW(), 'OPEN')";

        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setInt(1, deviceId);
        stmt.setString(2, description);

        stmt.executeUpdate();

        // Get incident_id
        ResultSet keys = stmt.getGeneratedKeys();
        int incidentId = -1;
        if (keys.next()) {
            incidentId = keys.getInt(1);
        }

        conn.close();

        // ⭐ Log history
        addHistory(incidentId, "Incident created");

    } catch (Exception e) {
        System.out.println("INCIDENT ERROR: " + e.getMessage());
    }
}

    public boolean hasOpenIncident(int deviceId) {
    try {
        Connection conn = DatabaseManager.connect();

        String sql = "SELECT incident_id FROM incidents " +
                     "WHERE device_id = ? AND incident_status = 'OPEN' LIMIT 1";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, deviceId);

        ResultSet rs = stmt.executeQuery();

        boolean exists = rs.next();
        conn.close();
        return exists;

    } catch (Exception e) {
        System.out.println("INCIDENT CHECK ERROR: " + e.getMessage());
        return false;
    }
}
    public void addHistory(int incidentId, String action) {
    try {
        Connection conn = DatabaseManager.connect();

        String sql = "INSERT INTO incident_history (incident_id, action, action_at) " +
                     "VALUES (?, ?, NOW())";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, incidentId);
        stmt.setString(2, action);

        stmt.executeUpdate();
        conn.close();

    } catch (Exception e) {
        System.out.println("HISTORY ERROR: " + e.getMessage());
    }
}
public void resolveIncident(int deviceId) {
    String sql = "UPDATE incidents " +
            "SET incident_status = 'RESOLVED', resolved_at = NOW() " +
            "WHERE device_id = ? AND incident_status = 'OPEN' " +
            "RETURNING incident_id";

    try (Connection conn = DatabaseManager.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, deviceId);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int incidentId = rs.getInt("incident_id");
                addHistory(incidentId, "Incident resolved");
                System.out.println(" -> INCIDENT RESOLVED: " + incidentId);
            }
        }

    } catch (Exception e) {
        System.out.println("INCIDENT RESOLUTION ERROR: " + e.getMessage());
    }
}
}