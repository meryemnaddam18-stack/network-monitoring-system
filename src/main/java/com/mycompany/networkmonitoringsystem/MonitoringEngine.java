package com.mycompany.networkmonitoringsystem;

import java.util.List;
import java.util.ArrayList;

public class MonitoringEngine {

    // Dependencies are injected through the constructor (easier to test and swap).
    private final PingModule pingModule;
    private final MetricsRepository metricsRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceStatusRepository statusRepo;
    private final IncidentRepository incidentRepo;
    private List<Device> devices;
     
    public MonitoringEngine(
        PingModule pingModule,
        MetricsRepository metricsRepository,
        DeviceRepository deviceRepository,
        DeviceStatusRepository statusRepo,
        IncidentRepository incidentRepo
) {
    this.pingModule = pingModule;
    this.metricsRepository = metricsRepository;
    this.deviceRepository = deviceRepository;
    this.statusRepo = statusRepo;
    this.incidentRepo = incidentRepo;
    this.devices = new ArrayList<>();
}

    public void loadDevices() {
        devices = deviceRepository.loadAllDevices();
        System.out.println(); 
        System.out.println("========================================"); 
        System.out.println(" NETWORK MONITORING SYSTEM");
        System.out.println("========================================"); 
        System.out.println("Loaded " + devices.size() + " devices from PostgreSQL.");
        System.out.println("========================================");
        System.out.println();
    }

    public void start() {
        loadDevices();
        int cycleNumber=1;
        while (true) {
            System.out.println();
            System.out.println("========================================");
            System.out.println(" Monitoring Cycle #" + cycleNumber); 
            System.out.println("========================================");
            for (Device d : devices) {
                try{

                PingResult result = pingModule.pingDevice(d.ipAddress);
                result.deviceId = d.deviceId;
                System.out.println( "[" + d.hostname + "] " + "IP=" + d.ipAddress + " | Status=" + result.status + " | Latency=" + result.latency + " ms" + " | Loss=" + result.packetLoss + "%" + " | Jitter=" + result.jitter + " ms" );
                metricsRepository.insertMetrics(result);
                statusRepo.updateStatus(d.deviceId, result.status);

                if (result.status.equals("DOWN")) {
                    if (!incidentRepo.hasOpenIncident(d.deviceId)) {
                        incidentRepo.createIncident(d.deviceId, "Device unreachable");
                        System.out.println( " -> INCIDENT CREATED: " + d.hostname );
                    }else { 
                            System.out.println( " -> Existing incident already OPEN" );
                    } 
                }
                

                if (result.status.equals("UP")) {
                    incidentRepo.resolveIncident(d.deviceId);
                    
                }
            }catch(Exception e){
                System.out.println( "[" + d.hostname + "] MONITORING ERROR: " + e.getMessage() );
            }
        }
        System.out.println("========================================");
        System.out.println( "Cycle #" + cycleNumber + " completed." );
        System.out.println( "Next monitoring cycle in 5 seconds..." ); 
        System.out.println("========================================"); 
        cycleNumber++;
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            System.out.println( "Monitoring interrupted." ); 
            Thread.currentThread().interrupt(); 
            break;
            }
        }
    }
}