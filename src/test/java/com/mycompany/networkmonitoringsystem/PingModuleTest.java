package com.mycompany.networkmonitoringsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PingModuleTest {

    private final PingModule pingModule = new PingModule();

    @Test
    void parsesLinuxOutput() {
        String output = """
                PING 8.8.8.8 (8.8.8.8) 56(84) bytes of data.
                64 bytes from 8.8.8.8: icmp_seq=1 ttl=117 time=10.0 ms
                64 bytes from 8.8.8.8: icmp_seq=2 ttl=117 time=14.0 ms

                --- 8.8.8.8 ping statistics ---
                4 packets transmitted, 2 received, 50% packet loss, time 3004ms
                rtt min/avg/max/mdev = 10.0/12.0/14.0/2.0 ms
                """;

        PingResult result = pingModule.parsePingOutput(output, 0);

        assertEquals("UP", result.status);
        assertEquals(12.0, result.latency, 0.001);
        assertEquals(50.0, result.packetLoss, 0.001);
        assertEquals(4.0, result.jitter, 0.001);
    }

    @Test
    void parsesWindowsOutputIncludingSubMillisecondReplies() {
        String output = """
                Pinging 8.8.8.8 with 32 bytes of data:
                Reply from 8.8.8.8: bytes=32 time<1ms TTL=117
                Reply from 8.8.8.8: bytes=32 time=3ms TTL=117

                Ping statistics for 8.8.8.8:
                    Packets: Sent = 4, Received = 4, Lost = 0 (0% loss),
                """;

        PingResult result = pingModule.parsePingOutput(output, 0);

        assertEquals("UP", result.status);
        assertEquals(2.0, result.latency, 0.001);
        assertEquals(0.0, result.packetLoss, 0.001);
        assertEquals(2.0, result.jitter, 0.001);
    }

    @Test
    void nonZeroExitCodeMeansDown() {
        PingResult result = pingModule.parsePingOutput("", 1);

        assertEquals("DOWN", result.status);
        assertEquals(100.0, result.packetLoss, 0.001);
    }

    @Test
    void timeoutMeansDown() {
        PingResult result = pingModule.parsePingOutput("Request timed out.\n", 0);

        assertEquals("DOWN", result.status);
    }
}
