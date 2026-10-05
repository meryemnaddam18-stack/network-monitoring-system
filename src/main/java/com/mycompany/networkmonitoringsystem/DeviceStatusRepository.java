package com.mycompany.networkmonitoringsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeviceStatusRepository {

    

    public void updateStatus(int deviceId, String status) {
        try {
            Connection conn = DatabaseManager.connect();

            String sql = "UPDATE devices SET current_status = ? WHERE device_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, status);
            stmt.setInt(2, deviceId);

            stmt.executeUpdate();
            conn.close();

        } catch (Exception e) {
            System.out.println("STATUS UPDATE ERROR: " + e.getMessage());
        }
    }
}
