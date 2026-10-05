package com.mycompany.networkmonitoringsystem;


public class PingResult {

    public int deviceId;        // MonitoringEngine will set this
    public String status;
    public double latency;
    public double packetLoss;
    public double jitter;
    public String rawOutput;
    public long timestamp;

    // Constructor WITHOUT deviceId
    public PingResult(String status, double latency, double packetLoss,
                      double jitter, String rawOutput, long timestamp) {

        this.status = status;
        this.latency = latency;
        this.packetLoss = packetLoss;
        this.jitter = jitter;
        this.rawOutput = rawOutput;
        this.timestamp = timestamp;
    }
}
