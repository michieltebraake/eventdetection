package nl.michieltebraake.eventdetection;

public class RollingStandardDeviation {
    private double mean;
    private double stdDev;
    private int index;

    public RollingStandardDeviation(double mean, double stdDev, int index) {
        this.mean = mean;
        this.stdDev = stdDev;
        this.index = index;
    }

    public void addPoint(double value) {
        double value1 = (index - 2) * Math.pow(stdDev, 2);

        double new_mean = ((mean * index) + value) / (index + 1);

        double value2 = (index - 1) * Math.pow(mean - new_mean, 2);

        double value3 = Math.pow(value - new_mean, 2);

        double variance = (value1 + value2 + value3) / index;
        stdDev = Math.sqrt(variance);
        index++;
    }

    public double getStdDev() {
        return stdDev;
    }

    @Override
    protected RollingStandardDeviation clone() {
        return new RollingStandardDeviation(mean, stdDev, index);
    }
}
