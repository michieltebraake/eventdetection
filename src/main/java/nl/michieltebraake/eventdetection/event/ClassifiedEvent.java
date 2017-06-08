package nl.michieltebraake.eventdetection.event;

public class ClassifiedEvent {
    private double distance;
    private EventType type;
    private int start;
    private int end;

    public ClassifiedEvent(double distance, EventType type, int start, int end) {
        this.distance = distance;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    public double getDistance() {
        return distance;
    }

    public EventType getType() {
        return type;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "ClassifiedEvent{" +
                "distance=" + distance +
                ", type=" + type +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
