package nl.michieltebraake.eventdetection;

import java.util.List;

public class Mean {
    private boolean initialized = false;
    private double mean;
    private long pointsAdded = 0;

    public Mean(boolean initialized, double mean, long pointsAdded) {
        this.initialized = initialized;
        this.mean = mean;
        this.pointsAdded = pointsAdded;
    }

    public Mean() {

    }

    public void addPoint(double value) {
        if (!initialized) {
            initialized = true;
            mean = value;
        } else {
            mean = ((mean * pointsAdded) + value) / (pointsAdded + 1);
        }
        pointsAdded++;
    }

    public double getMean() {
        return mean;
    }

    public static double getMean(List<AccelerometerPoint> values) {
        double total = 0;

        for (AccelerometerPoint value : values) {
            total += value.getY();
        }

        return total / values.size();
    }

    @Override
    protected Mean clone() {
        return new Mean(initialized, mean, pointsAdded);
    }
}
