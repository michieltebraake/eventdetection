package nl.michieltebraake.eventdetection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileChecker {
    public static void main(String[] args) {
        try {
            new FileChecker("resources/data/michiel-15-6/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileChecker(String file) throws IOException {
        checkGps(file);
        checkAcc(file);
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

    private void checkAcc(String file) throws IOException {
        List<String> accPoints = Files.lines(Paths.get(file + "Acc.txt")).collect(Collectors.toList());
        long firstTimestamp = Long.parseLong(accPoints.get(0).split(",")[0]);
        long lastTimestamp = Long.parseLong(accPoints.get(accPoints.size() - 1).split(",")[0]);

        long diff = lastTimestamp - firstTimestamp;
        double diffSeconds = diff / Math.pow(10, 9);
        System.out.println("Seconds difference: " + diffSeconds);
        System.out.println("Number of data points: " + accPoints.size());
        System.out.println("Data points per second: " + accPoints.size() / diffSeconds);
        System.out.println("Average gap time: " + diffSeconds / accPoints.size());

        int largestDiffIndex = 0;
        long largestDiff = 0;
        long previousTimestamp = 0;
        for (int i = 0; i < accPoints.size(); i++) {
            String accPoint = accPoints.get(i);
            long timestamp = Long.parseLong(accPoint.split(",")[0]);
            if (previousTimestamp != 0) {
                if (timestamp - previousTimestamp > largestDiff) {
                    largestDiff = timestamp - previousTimestamp;
                    largestDiffIndex = i;
                }
            }
            previousTimestamp = timestamp;
        }

        double largestDiffSeconds = largestDiff / Math.pow(10, 9);
        System.out.println("Largest gap: " + largestDiffSeconds + "s at index " + largestDiffIndex);
    }
}
