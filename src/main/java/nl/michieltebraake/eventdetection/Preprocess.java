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
    private int n = 39;

    // Number of data points to group together into one point
    private int groupSize = 20;

    public static void main(String[] args) {
        try {
            new Preprocess().processFile("resources/data/sander-15-6");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processFile(String file) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(file + "/Acc.txt"))) {
            List<String> dataPoints = stream.collect(Collectors.toList());
            List<String> resultPoints = new ArrayList<>();

            double[] averageX = new double[n];
            double[] averageY = new double[n];
            double[] averageZ = new double[n];

            double sumX = 0;
            double sumY = 0;
            double sumZ = 0;

            AccelerometerPoint firstPoint = new AccelerometerPoint(dataPoints.get(0));
            AccelerometerPoint finalPoint = new AccelerometerPoint(dataPoints.get(dataPoints.size() - 1));
            double totalSeconds = (finalPoint.getTimestamp() - firstPoint.getTimestamp()) * (1 * Math.pow(10, -9));
            List<String> gpsPoints = Files.lines(Paths.get(file + "/Gps.txt")).collect(Collectors.toList());

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

                sumX -= averageX[i % n];
                sumY -= averageY[i % n];
                sumZ -= averageZ[i % n];

                averageX[i % n] = dataPoint.getX();
                averageY[i % n] = dataPoint.getY();
                averageZ[i % n] = dataPoint.getZ();

                sumX += averageX[i % n];
                sumY += averageY[i % n];
                sumZ += averageZ[i % n];

                if (i >= n && i % groupSize == 0) {
                    String[] gpsString = gpsPoints.get(gpsIndex).split(",");
                    String gpsTimestamp = gpsString[0];
                    String lat = gpsString[2];
                    String lng = gpsString[3];
                    resultPoints.add(new AccelerometerPoint(dataPoint.getTimestamp(), sumX / n, sumY / n, sumZ / n, gpsTimestamp, lat, lng).toString());
                }
            }

            Files.write(Paths.get(file + "/Acc_Processed.txt"), resultPoints);
        }
    }
}
