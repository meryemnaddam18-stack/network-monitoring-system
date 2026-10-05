package com.mycompany.networkmonitoringsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class MetricsRepository {


    public void insertMetrics(PingResult r) {
        try {
            Connection conn = DatabaseManager.connect();

            String sql = "INSERT INTO metrics " +
             "(device_id, recorded_at, status, latency, packet_loss, jitter, raw_output, timestamp) " +
             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, r.deviceId);
stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));  // recorded_at
stmt.setString(3, r.status);
stmt.setDouble(4, r.latency);
stmt.setDouble(5, r.packetLoss);
stmt.setDouble(6, r.jitter);
stmt.setString(7, r.rawOutput);
stmt.setLong(8, r.timestamp);


            stmt.executeUpdate();
            conn.close();

        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
        }
    }
}