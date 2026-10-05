package com.mycompany.networkmonitoringsystem;

public class Device {
    public int deviceId;
    public String hostname;
    public String deviceType;
    public String ipAddress;
    public String location;
    public String currentStatus;

    public Device(int deviceId, String hostname, String deviceType,
                  String ipAddress, String location, String currentStatus) {

        this.deviceId = deviceId;
        this.hostname = hostname;
        this.deviceType = deviceType;
        this.ipAddress = ipAddress;
        this.location = location;
        this.currentStatus = currentStatus;
    }
}

