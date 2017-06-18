package nl.michieltebraake.eventdetection;

import java.util.Arrays;
import java.util.List;

public class StandardDeviation {
    private double mean;
    private double standardDeviation;

    public StandardDeviation(double mean, double standardDeviation) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public static void main(String[] args) {
        List<Double> values = Arrays.asList(12.1, 15.2, 13.3, 19.2, 4.1);
        System.out.println(StandardDeviation.calculateStdDev(values));
    }

    /**
     * Returns the standard deviation of a list
     *
     * @param values List of values
     * @return standard deviation
     */
    public static StandardDeviation calculateStdDev(List<Double> values) {
        //Step 1: Find the mean.
        double mean = mean(values);

        double squaredDiffSum = 0;
        for (double value : values) {
            //Step 2: For each data point, find the square of its distance to the mean.
            double squaredDiff = Math.pow(value - mean, 2);

            //Step 3: Sum the values from Step 2.
            squaredDiffSum += squaredDiff;
        }

        //Step 4: Divide by the number of data points.
        double dividedSum = squaredDiffSum / values.size();

        //Step 5: Take the square root.
        return new StandardDeviation(mean, Math.sqrt(dividedSum));
    }

    /**
     * Returns the standard deviation of a list
     *
     * @param values List of values
     * @return standard deviation
     */
    public static StandardDeviation calculateStdDevAccPoint(List<AccelerometerPoint> values) {
        //Step 1: Find the mean.
        double mean = meanAccPoint(values);

        double squaredDiffSum = 0;
        for (AccelerometerPoint point : values) {
            //Step 2: For each data point, find the square of its distance to the mean.
            double squaredDiff = Math.pow(point.getY() - mean, 2);

            //Step 3: Sum the values from Step 2.
            squaredDiffSum += squaredDiff;
        }

        //Step 4: Divide by the number of data points.
        double dividedSum = squaredDiffSum / values.size();

        //Step 5: Take the square root.
        return new StandardDeviation(mean, Math.sqrt(dividedSum));
    }

    private static double mean(List<Double> values) {
        double total = 0;

        for (double value : values) {
            total += value;
        }

        return total / values.size();
    }

    private static double meanAccPoint(List<AccelerometerPoint> values) {
        double total = 0;

        for (AccelerometerPoint point : values) {
            total += point.getY();
        }

        return total / values.size();
    }
}
