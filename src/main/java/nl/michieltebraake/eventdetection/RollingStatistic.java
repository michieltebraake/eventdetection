package nl.michieltebraake.eventdetection;

public class RollingStatistic {
    private int window_size;
    private double average;
    private double variance;
    private double stddev = Math.sqrt(variance);

    public RollingStatistic(int window_size, double average, double variance) {
        this.window_size = window_size;
        this.average = average;
        this.variance = variance;
    }

//    private void update() {
//
//    }
//    def update(new, old):
//    oldavg = self.average
//            newavg = oldavg + (new - old)/self.N
//    self.average = newavg
//    self.variance += (new-old)*(new-newavg+old-oldavg)/(self.N-1)
//    self.stddev = sqrt(variance)
}
