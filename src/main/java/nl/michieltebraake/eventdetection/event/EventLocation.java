package nl.michieltebraake.eventdetection.event;

import nl.michieltebraake.eventdetection.AccelerometerPoint;

public class EventLocation {
    protected AccelerometerPoint accelerometerPoint;
    protected int start;
    protected int end;

    public EventLocation(AccelerometerPoint accelerometerPoint, int start, int end) {
        this.accelerometerPoint = accelerometerPoint;
        this.start = start;
        this.end = end;
    }

    public AccelerometerPoint getAccelerometerPoint() {
        return accelerometerPoint;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
