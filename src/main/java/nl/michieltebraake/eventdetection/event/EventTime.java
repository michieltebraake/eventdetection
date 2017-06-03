package nl.michieltebraake.eventdetection.event;

public class EventTime {
    private int start;
    private int end;

    public EventTime(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
