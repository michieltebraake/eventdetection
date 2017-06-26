package nl.michieltebraake.eventdetection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Preprocess accelerometer data to decrease the number of data points as well as taking a moving average
 */
public class Preprocess {
    // Number of data points to average over
    private int n = 200;

    // Number of data points to group together into one point
    private int groupSize = 20;

    public static void main(String[] args) {
        try {
            Preprocess preprocess = new Preprocess();
            String file = "resources/data/sander-15-6/";
            preprocess.fixGps(file);
            preprocess.processFile(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fixGps(String file) throws IOException {
        System.out.println("Fixing gps...");
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
        Files.write(Paths.get(file + "Gps_Modified.txt"), gpsPoints);
    }

    private void processFile(String file, boolean useExponentialAverage) throws IOException {
        int tempInt = 0;
        System.out.println("Processing accelerometer data...");
        try (Stream<String> stream = Files.lines(Paths.get(file + "Acc.txt"))) {
            List<String> dataPoints = stream.collect(Collectors.toList());
            List<String> resultPoints = new ArrayList<>();

            double[] averageX = new double[n];
            double[] averageY = new double[n];
            double[] averageZ = new double[n];

            double sumX = 0;
            double sumY = 0;
            double sumZ = 0;

            double alpha = 0.01;
            ExponentialMovingAverage expX = new ExponentialMovingAverage(alpha);
            ExponentialMovingAverage expY = new ExponentialMovingAverage(alpha);
            ExponentialMovingAverage expZ = new ExponentialMovingAverage(alpha);

            AccelerometerPoint firstPoint = new AccelerometerPoint(dataPoints.get(0));
            AccelerometerPoint finalPoint = new AccelerometerPoint(dataPoints.get(dataPoints.size() - 1));
            double totalSeconds = (finalPoint.getTimestamp() - firstPoint.getTimestamp()) * (1 * Math.pow(10, -9));
            List<String> gpsPoints = Files.lines(Paths.get(file + "Gps_Modified.txt")).collect(Collectors.toList());

            //Remove the extra GPS points
            while (gpsPoints.size() > (totalSeconds + 1)) {
                gpsPoints.remove(0);
            }

            int gpsIndex = 0;
            long startTimestamp = 0;
            for (int i = 0; i < dataPoints.size(); i++) {
                AccelerometerPoint dataPoint = new AccelerometerPoint(dataPoints.get(i));

                if (startTimestamp == 0) {
                    startTimestamp = dataPoint.getTimestamp();
                } else if (dataPoint.getTimestamp() > startTimestamp + ((gpsIndex + 1) * Math.pow(10, 9))) {
                    gpsIndex++;
                }

                if (useExponentialAverage) {
                    expX.average(dataPoint.getX());
                    expY.average(dataPoint.getY());
                    expZ.average(dataPoint.getZ());
                } else {
                    sumX -= averageX[i % n];
                    sumY -= averageY[i % n];
                    sumZ -= averageZ[i % n];

                    averageX[i % n] = dataPoint.getX();
                    averageY[i % n] = dataPoint.getY();
                    averageZ[i % n] = dataPoint.getZ();

                    sumX += averageX[i % n];
                    sumY += averageY[i % n];
                    sumZ += averageZ[i % n];
                }

                if (i >= n && i % groupSize == 0) {
                    if ((i / groupSize) >= 10280 && (i / groupSize) <= 10400) {
                        tempInt++;
                        continue;
                    } else if ((i / groupSize) >= 17976) {
                        //tempInt++;
                        continue;
                    }
                    String[] gpsString = gpsPoints.get(gpsIndex).split(",");
                    String gpsTimestamp = gpsString[0];
                    String lat = gpsString[2];
                    String lng = gpsString[3];
                    if (useExponentialAverage) {
                        //resultPoints.add(new AccelerometerPoint(dataPoint.getTimestamp(), dataPoint.getX(), dataPoint.getY(), dataPoint.getZ(), gpsTimestamp, lat, lng).toString());
                        resultPoints.add(new AccelerometerPoint(dataPoint.getTimestamp(), expX.getAverage(), expY.getAverage(), expZ.getAverage(), gpsTimestamp, lat, lng).toString());
                    } else {
                        resultPoints.add(new AccelerometerPoint(dataPoint.getTimestamp(), sumX / n, sumY / n, sumZ / n, gpsTimestamp, lat, lng).toString());
                    }
                }
            }

            //Files.write(Paths.get(file + "Acc_Processed.txt"), resultPoints);
            Files.write(Paths.get("resources/data/temp.txt"), resultPoints);
        }
    }
}
