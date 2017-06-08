package nl.michieltebraake.eventdetection.event;

public class EventComparison {
    private double distance;
    private EventType type;

    public EventComparison(double distance, EventType type) {
        this.distance = distance;
        this.type = type;
    }

    public double getDistance() {
        return distance;
    }

    public EventType getType() {
        return type;
    }
}
