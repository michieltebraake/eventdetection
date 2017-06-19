package nl.michieltebraake.eventdetection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileChecker {
    public static void main(String[] args) {
        try {
            new FileChecker("resources/data/david-15-6/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileChecker(String file) throws IOException {
        checkGps(file);
    }

    private void checkGps(String file) throws IOException {
        List<String> gpsPoints = Files.lines(Paths.get(file + "Gps.txt")).collect(Collectors.toList());
        long firstTimestamp = Long.parseLong(gpsPoints.get(0).split(",")[0]);
        long lastTimestamp = Long.parseLong(gpsPoints.get(gpsPoints.size() - 1).split(",")[0]);

        long diff = lastTimestamp - firstTimestamp;
        double diffSeconds = diff / 1000;
        System.out.println("Seconds difference: " + diffSeconds);
        System.out.println("Number of data points: " + gpsPoints.size());
    }
}
