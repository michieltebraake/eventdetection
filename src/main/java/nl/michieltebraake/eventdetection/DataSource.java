package nl.michieltebraake.eventdetection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataSource {
    private String file;

    private String gpsFile = "/Gps.txt";
    private String gpsModifiedFile = "/GpsModified.txt";
    private String accFile = "/Acc_processed.txt";

    private long accelerometerStart;
    private long accelerometerEnd;
    private long gpsStart;
    private long gpsEnd;

    private List<String> gpsLines;
    private List<String> accLines;

    private double scaleRatio;

    public DataSource(String file) throws IOException {
        this.file = file;

        try (Stream<String> stream = Files.lines(Paths.get(file + accFile))) {
            accLines = stream.collect(Collectors.toList());
            accelerometerStart = getTimestamp(accLines.get(0));
            accelerometerEnd = getTimestamp(accLines.get(accLines.size() - 1));
        }

        boolean gpsModifiedFileExists = Files.exists(Paths.get(file + gpsModifiedFile));
        if (gpsModifiedFileExists) {
            gpsLines = loadGpsData(gpsModifiedFile);
        } else {
            gpsLines = loadGpsData(gpsFile);
        }

        LocalDateTime gpsStartDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(gpsStart), ZoneId.systemDefault());
        LocalDateTime gpsEndDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(gpsEnd), ZoneId.systemDefault());

        //Convert ns to s
        double accelerometerTotal = (accelerometerEnd - accelerometerStart);
        System.out.println("Acc minutes: " + accelerometerTotal / 60);

        double gpsTotal = gpsStartDate.until(gpsEndDate, ChronoUnit.SECONDS);
        System.out.println("Gps minutes: " + gpsTotal / 60);
        System.out.println("");

        if (!gpsModifiedFileExists) {
            if (accelerometerTotal < gpsTotal) {
                double secondsDifference = Math.floor(gpsTotal - accelerometerTotal);
                System.out.println("Seconds difference: " + secondsDifference);

                //Discard the first x seconds of GPS data
                for (int i = 0; i < secondsDifference; i++) {
                    gpsLines.remove(0);
                }
                try {
                    Files.write(Paths.get(file + gpsModifiedFile), gpsLines);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        scaleRatio = (double) accLines.size() / (double) gpsLines.size();
    }

    public static void main(String[] args) {
        //new DataSource("resources/data/michiel-15-6");
        try {
            new DataSource("resources/data/sander-15-6");
            new DataSource("resources/data/david-15-6");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadGpsData(String dataFile) throws IOException {
        List<String> gpsLines;
        try (Stream<String> stream = Files.lines(Paths.get(file + dataFile))) {
            gpsLines = stream.collect(Collectors.toList());
            gpsStart = getTimestamp(gpsLines.get(0));
            gpsEnd = getTimestamp(gpsLines.get(gpsLines.size() - 1));
            return gpsLines;
        }
    }

    private long getTimestamp(String line) {
        return Long.parseLong(line.split(",")[0]);
    }

    public String getFile() {
        return file;
    }

    public long getAccelerometerStart() {
        return accelerometerStart;
    }

    public long getAccelerometerEnd() {
        return accelerometerEnd;
    }

    public long getGpsStart() {
        return gpsStart;
    }

    public long getGpsEnd() {
        return gpsEnd;
    }

    public List<String> getGpsLines() {
        return gpsLines;
    }

    public List<String> getAccLines() {
        return accLines;
    }
}
