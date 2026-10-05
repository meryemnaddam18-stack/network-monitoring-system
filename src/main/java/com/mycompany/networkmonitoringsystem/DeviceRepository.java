package com.mycompany.networkmonitoringsystem;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DeviceRepository {

    public List<Device> loadAllDevices() {
        List<Device> devices = new ArrayList<>();

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT device_id, hostname, device_type, ip_address, location, current_status FROM devices"
             )) {

            while (rs.next()) {
                Device d = new Device(
                        rs.getInt("device_id"),
                        rs.getString("hostname"),
                        rs.getString("device_type"),
                        rs.getString("ip_address"),
                        rs.getString("location"),
                        rs.getString("current_status")
                );

                devices.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return devices;
    }
}