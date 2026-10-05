package com.mycompany.networkmonitoringsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;


public class MonitoringReportRepository {




    // ==========================================
    // 1. DEVICE STATUS REPORT
    // ==========================================

    public void showDeviceStatusReport() {

        String sql =
                "SELECT hostname, ip_address, current_status " +
                "FROM devices " +
                "ORDER BY hostname";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println();
            System.out.println("========================================");
            System.out.println("        DEVICE STATUS REPORT");
            System.out.println("========================================");

            while (rs.next()) {

                System.out.println(
                        "Device: " + rs.getString("hostname")
                        + " | IP: " + rs.getString("ip_address")
                        + " | Status: " + rs.getString("current_status")
                );
            }

            System.out.println("========================================");

        } catch (Exception e) {

            System.out.println(
                    "DEVICE REPORT ERROR: " + e.getMessage()
            );
        }
    }


    // ==========================================
    // 2. INCIDENT REPORT
    // ==========================================

    public void showIncidentReport() {

        String sql =
                "SELECT i.incident_id, " +
                "d.hostname, " +
                "d.ip_address, " +
                "i.incident_type, " +
                "i.severity, " +
                "i.description, " +
                "i.incident_status, " +
                "i.created_at, " +
                "i.resolved_at " +
                "FROM incidents i " +
                "JOIN devices d " +
                "ON i.device_id = d.device_id " +
                "ORDER BY i.created_at DESC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println();
            System.out.println("========================================");
            System.out.println("           INCIDENT REPORT");
            System.out.println("========================================");

            while (rs.next()) {

                System.out.println(
                        "Incident ID: "
                        + rs.getInt("incident_id")
                );

                System.out.println(
                        "Device: "
                        + rs.getString("hostname")
                );

                System.out.println(
                        "IP: "
                        + rs.getString("ip_address")
                );

                System.out.println(
                        "Type: "
                        + rs.getString("incident_type")
                );

                System.out.println(
                        "Severity: "
                        + rs.getString("severity")
                );

                System.out.println(
                        "Description: "
                        + rs.getString("description")
                );

                System.out.println(
                        "Status: "
                        + rs.getString("incident_status")
                );

                System.out.println(
                        "Created: "
                        + rs.getTimestamp("created_at")
                );

                System.out.println(
                        "Resolved: "
                        + rs.getTimestamp("resolved_at")
                );

                System.out.println("----------------------------------------");
            }

        } catch (Exception e) {

            System.out.println(
                    "INCIDENT REPORT ERROR: " + e.getMessage()
            );
        }
    }


    // ==========================================
    // 3. LATEST METRICS REPORT
    // ==========================================

    public void showLatestMetrics() {

        String sql =
                "SELECT d.hostname, " +
                "d.ip_address, " +
                "m.status, " +
                "m.latency, " +
                "m.packet_loss, " +
                "m.jitter, " +
                "m.recorded_at " +
                "FROM metrics m " +
                "JOIN devices d " +
                "ON m.device_id = d.device_id " +
                "WHERE m.metric_id IN " +
                "( " +
                "SELECT MAX(metric_id) " +
                "FROM metrics " +
                "GROUP BY device_id " +
                ") " +
                "ORDER BY d.hostname";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println();
            System.out.println("========================================");
            System.out.println("        LATEST METRICS REPORT");
            System.out.println("========================================");

            while (rs.next()) {

                System.out.println(
                        "Device: "
                        + rs.getString("hostname")
                );

                System.out.println(
                        "IP: "
                        + rs.getString("ip_address")
                );

                System.out.println(
                        "Status: "
                        + rs.getString("status")
                );

                System.out.println(
                        "Latency: "
                        + rs.getDouble("latency")
                        + " ms"
                );

                System.out.println(
                        "Packet Loss: "
                        + rs.getDouble("packet_loss")
                        + "%"
                );

                System.out.println(
                        "Jitter: "
                        + rs.getDouble("jitter")
                        + " ms"
                );

                System.out.println(
                        "Recorded: "
                        + rs.getTimestamp("recorded_at")
                );

                System.out.println("----------------------------------------");
            }

        } catch (Exception e) {

            System.out.println(
                    "METRICS REPORT ERROR: " + e.getMessage()
            );
        }
    }
    public void exportDeviceStatusReport() {
    try {
        PrintWriter writer = new PrintWriter("device_status.txt");

        Connection conn = DatabaseManager.connect();

        String sql =
                "SELECT hostname, ip_address, current_status " +
                "FROM devices " +
                "ORDER BY hostname";

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {

            writer.println(
                    rs.getString("hostname") + "," +
                    rs.getString("ip_address") + "," +
                    rs.getString("current_status")
            );
        }

        writer.close();
        conn.close();

    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
}
}
