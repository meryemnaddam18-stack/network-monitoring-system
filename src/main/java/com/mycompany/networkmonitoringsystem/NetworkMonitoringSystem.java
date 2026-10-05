package com.mycompany.networkmonitoringsystem;

/**
 * Entry point.
 *   No argument / "report" : print device status, latest metrics and incident reports.
 *   "monitor"              : start the continuous ping monitoring loop.
 */
public class NetworkMonitoringSystem {

    public static void main(String[] args) {

        String mode = args.length > 0 ? args[0].toLowerCase() : "report";

        if (mode.equals("monitor")) {
            MonitoringEngine engine = new MonitoringEngine(
                    new PingModule(),
                    new MetricsRepository(),
                    new DeviceRepository(),
                    new DeviceStatusRepository(),
                    new IncidentRepository()
            );
            engine.start();
            return;
        }

        MonitoringReportRepository reports = new MonitoringReportRepository();

        reports.showDeviceStatusReport();
        reports.showLatestMetrics();
        reports.showIncidentReport();
    }
}
