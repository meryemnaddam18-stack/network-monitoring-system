package com.mycompany.networkmonitoringsystem;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class PingModule {

    private static final Pattern TIME_PATTERN = Pattern.compile("time[=<]\\s*(\\d+(?:\\.\\d+)?)\\s*ms");
    private static final Pattern LOSS_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)%");

    public PingResult pingDevice(String ipAddress) {

        String os = System.getProperty("os.name").toLowerCase();
        String countFlag = os.contains("win") ? "-n" : "-c";

        // Arguments are passed as a list (not one string) so the IP address
        // always stays a single argument and is never split apart.
        List<String> command = List.of("ping", countFlag, "4", ipAddress);

        return executePing(command);
    }

    private PingResult executePing(List<String> command) {
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            return parsePingOutput(output.toString(), exitCode);

        } catch (Exception e) {
            return new PingResult(
                    "DOWN",
                    0.0,
                    100.0,
                    0.0,
                    "ERROR: " + e.getMessage(),
                    System.currentTimeMillis()
            );
        }
    }

    // Package-private so unit tests can feed it sample ping output.
    PingResult parsePingOutput(String output, int exitCode) {

        // DOWN condition
        if (exitCode != 0 || output.contains("Request timed out")
                || output.contains("Destination host unreachable")) {
            return new PingResult(
                    "DOWN",
                    0.0,
                    100.0,
                    0.0,
                    output,
                    System.currentTimeMillis()
            );
        }

        List<Double> times = new ArrayList<>();
        double packetLoss = 0.0;

        for (String line : output.split("\n")) {

            // Latency: "time=12.3 ms" (Linux/macOS), "time=12ms" or "time<1ms" (Windows)
            Matcher time = TIME_PATTERN.matcher(line);
            if (time.find()) {
                times.add(Double.parseDouble(time.group(1)));
            }

            // Packet loss: "0% packet loss" (Linux/macOS) or "(0% loss)" (Windows)
            if (line.contains("loss")) {
                Matcher loss = LOSS_PATTERN.matcher(line);
                if (loss.find()) {
                    packetLoss = Double.parseDouble(loss.group(1));
                }
            }
        }

        double avg = times.stream().mapToDouble(a -> a).average().orElse(0);
        double jitter = times.stream().mapToDouble(a -> a).max().orElse(0)
                - times.stream().mapToDouble(a -> a).min().orElse(0);

        return new PingResult(
                "UP",
                avg,
                packetLoss,
                jitter,
                output,
                System.currentTimeMillis()
        );
    }
}
