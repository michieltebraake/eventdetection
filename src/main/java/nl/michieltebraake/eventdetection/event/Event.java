package nl.michieltebraake.eventdetection.event;

import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Event extends EventLocation {
    private String dataFile;
    private EventType type;

    private TimeSeries timeSeries;

    public Event(String dataFile, int start, int end, EventType type) {
        super(start, end);
        this.dataFile = dataFile;
        this.type = type;

        buildSeries();
    }

    /**
     * Builds the time series
     */
    private void buildSeries() {
        try (Stream<String> stream = Files.lines(Paths.get(dataFile))) {
            List<String> list = stream.collect(Collectors.toList());

            TimeSeriesBase.Builder seriesBuilder = TimeSeriesBase.builder();
            boolean inEvent = false;
            int seriesIndex = 0;

            for (int i = 0; i < list.size(); i++) {
                if (i == start) {
                    inEvent = true;
                } else if (i == end) {
                    inEvent = false;
                }

                if (inEvent) {
                    seriesBuilder.add(seriesIndex, Double.parseDouble(list.get(i)));
                    seriesIndex++;
                }
            }

            timeSeries = seriesBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDataFile() {
        return dataFile;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public EventType getType() {
        return type;
    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }
}
