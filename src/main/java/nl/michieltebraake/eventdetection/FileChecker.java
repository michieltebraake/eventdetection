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
        checkDifferences(file);
    }

    private void fixGps(String file) throws IOException {
        List<String> gpsPoints = Files.lines(Paths.get(file + "Gps.txt")).collect(Collectors.toList());
        long firstTimestamp = Long.parseLong(gpsPoints.get(0).split(",")[0]);
        long lastTimestamp = Long.parseLong(gpsPoints.get(gpsPoints.size() - 1).split(",")[0]);

        long diff = lastTimestamp - firstTimestamp;
        double diffSeconds = diff / 1000;
        System.out.println("Seconds difference: " + diffSeconds);
        System.out.println("Number of data points: " + gpsPoints.size());
    }

    private void checkDifferences(String file) throws IOException {
        List<String> gpsPoints = Files.lines(Paths.get(file + "Gps.txt")).collect(Collectors.toList());

        long timestamp = 0;
        for (int i = 0; i < gpsPoints.size(); i++) {
            String gpsPoint = gpsPoints.get(i);
            if (timestamp == 0) {
                timestamp = Long.parseLong(gpsPoint.split(",")[0]);
            } else {
                long newTimestamp = Long.parseLong(gpsPoint.split(",")[0]);
                long diff = (newTimestamp - timestamp) / 1000;
                while (diff > 1) {
                    diff--;
                    gpsPoints.add(i, gpsPoint);
                    System.out.println((newTimestamp - timestamp) / 1000);
                }
                timestamp = newTimestamp;
            }
        }
        System.out.println("Boe");
        Files.write(Paths.get(file + "Gps_Modified.txt"), gpsPoints);
    }
}
