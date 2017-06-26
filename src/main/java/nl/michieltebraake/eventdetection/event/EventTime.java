package nl.michieltebraake.eventdetection.event;

import nl.michieltebraake.eventdetection.Preprocess;

public class EventTime {
    private int start;
    private int end;

    public EventTime(int start, int end) {
        this.start = (int) Math.round(Preprocess.groupSizeFactor * start);
        this.end = (int) Math.round(Preprocess.groupSizeFactor * end);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
