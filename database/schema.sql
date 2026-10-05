-- Network Monitoring System - PostgreSQL schema
-- Usage:
--   createdb network_monitoring
--   psql -d network_monitoring -f database/schema.sql

CREATE TABLE IF NOT EXISTS devices (
    device_id      SERIAL PRIMARY KEY,
    hostname       VARCHAR(100) NOT NULL,
    device_type    VARCHAR(50),
    ip_address     VARCHAR(45)  NOT NULL,
    location       VARCHAR(100),
    current_status VARCHAR(10)  DEFAULT 'UNKNOWN'
);

CREATE TABLE IF NOT EXISTS metrics (
    metric_id   SERIAL PRIMARY KEY,
    device_id   INT REFERENCES devices(device_id),
    recorded_at TIMESTAMP NOT NULL,
    status      VARCHAR(10),
    latency     DOUBLE PRECISION,
    packet_loss DOUBLE PRECISION,
    jitter      DOUBLE PRECISION,
    raw_output  TEXT,
    timestamp   BIGINT
);

CREATE TABLE IF NOT EXISTS incidents (
    incident_id     SERIAL PRIMARY KEY,
    device_id       INT REFERENCES devices(device_id),
    incident_type   VARCHAR(50),
    severity        VARCHAR(20),
    description     TEXT,
    created_at      TIMESTAMP NOT NULL,
    resolved_at     TIMESTAMP,
    incident_status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS incident_history (
    history_id  SERIAL PRIMARY KEY,
    incident_id INT REFERENCES incidents(incident_id),
    action      VARCHAR(255),
    action_at   TIMESTAMP NOT NULL
);

-- Sample devices so the monitor has something to ping.
-- 192.0.2.1 is a reserved documentation address, so it will always show as DOWN
-- (useful for demoing incident creation).
INSERT INTO devices (hostname, device_type, ip_address, location, current_status) VALUES
    ('localhost',      'Server', '127.0.0.1', 'Local machine', 'UNKNOWN'),
    ('google-dns',     'DNS',    '8.8.8.8',   'Internet',      'UNKNOWN'),
    ('offline-device', 'Router', '192.0.2.1', 'Test network',  'UNKNOWN');
