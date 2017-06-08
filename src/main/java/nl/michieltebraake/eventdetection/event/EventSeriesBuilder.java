package nl.michieltebraake.eventdetection.event;

import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;

public class EventSeriesBuilder {
    private TimeSeriesBase.Builder seriesBuilder = TimeSeriesBase.builder();
    private int seriesIndex = 0;

    public EventSeriesBuilder() {

    }

    public void add(double value) {
        seriesBuilder.add(seriesIndex, value);
    }

    public void getDistance() {
        TimeSeries series = seriesBuilder.build();
    }

    public TimeSeries getTimeSeries() {
        return seriesBuilder.build();
    }
}
