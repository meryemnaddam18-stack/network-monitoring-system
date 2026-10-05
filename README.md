# Network Monitoring System

A Java application that continuously pings network devices, records performance metrics in PostgreSQL, and automatically opens and resolves incidents when devices go down or come back up.

## Features

- **Continuous monitoring**: pings every registered device every 5 seconds
- **Performance metrics**: records latency, packet loss and jitter for each check
- **Automatic incident management**: opens an incident when a device goes DOWN (without duplicating open ones) and resolves it when the device is back UP
- **Incident history**: logs every create/resolve action for an audit trail
- **Reports**: device status, latest metrics per device, and full incident report
- **Cross-platform**: parses `ping` output on Windows, Linux and macOS

## Tech Stack

- Java 25
- PostgreSQL + JDBC
- Maven
- JUnit 5

## Architecture

```
NetworkMonitoringSystem (entry point)
        │
        ▼
MonitoringEngine ──► PingModule ──► system `ping` command
        │
        ├──► MetricsRepository        (metrics table)
        ├──► DeviceRepository         (devices table)
        ├──► DeviceStatusRepository   (devices.current_status)
        └──► IncidentRepository       (incidents + incident_history)

MonitoringReportRepository ──► console reports
DatabaseManager            ──► shared JDBC connection (env-based config)
```

The engine receives its dependencies through its constructor, and all database access goes through repository classes (Repository pattern), so the monitoring logic is separate from the SQL.

## Database Schema

| Table              | Purpose                                              |
|--------------------|------------------------------------------------------|
| `devices`          | Monitored devices and their current status           |
| `metrics`          | Latency / packet loss / jitter for every ping cycle  |
| `incidents`        | Outages: when opened, when resolved, severity        |
| `incident_history` | Audit log of actions taken on each incident          |

The full schema with sample devices is in [`database/schema.sql`](database/schema.sql).

## Getting Started

### Prerequisites

- JDK 25
- Maven
- PostgreSQL

### 1. Create the database

```bash
createdb network_monitoring
psql -d network_monitoring -f database/schema.sql
```

### 2. Set your database credentials

Credentials are read from environment variables and are never stored in the code.

```bash
# macOS / Linux
export DB_PASSWORD=your_password
# optional: DB_URL (default jdbc:postgresql://localhost:5432/network_monitoring), DB_USER (default postgres)
```

```powershell
# Windows PowerShell
$env:DB_PASSWORD="your_password"
```

### 3. Build and run

```bash
mvn compile

# Start continuous monitoring (Ctrl+C to stop)
mvn exec:java -Dexec.args="monitor"

# Print the status, metrics and incident reports
mvn exec:java
```

### Example output

```
========================================
 Monitoring Cycle #1
========================================
[localhost] IP=127.0.0.1 | Status=UP | Latency=0.05 ms | Loss=0.0% | Jitter=0.02 ms
[google-dns] IP=8.8.8.8 | Status=UP | Latency=12.4 ms | Loss=0.0% | Jitter=3.1 ms
[offline-device] IP=192.0.2.1 | Status=DOWN | Latency=0.0 ms | Loss=100.0% | Jitter=0.0 ms
 -> INCIDENT CREATED: offline-device
```

## Testing

```bash
mvn test
```

Current coverage:

- ✅ **Ping output parsing**: Linux and Windows formats, sub-millisecond replies, packet loss, timeouts and unreachable hosts

## Roadmap

- [ ] Tests for the API / dashboard layer
- [ ] Repository integration tests against a test database
- [ ] Configurable ping interval and alert thresholds
- [ ] Email/SMS alerts when an incident opens
