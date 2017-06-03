package nl.michieltebraake.eventdetection;

import com.fastdtw.dtw.FastDTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;
import com.fastdtw.util.Distances;
import nl.michieltebraake.eventdetection.event.Event;
import nl.michieltebraake.eventdetection.event.EventTime;
import nl.michieltebraake.eventdetection.event.EventType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private String dataFile = "resources/data/smooth_Y";
    private String smallerDataFile = "resources/data/smaller_Y2";
    private List<Event> brakingEvents;

    public Main() {
        buildReferenceData();
        //compareReferenceData();
        //findBrakingEvents();
        bruteForceFind();
        //compare(dataFile, 4230, 4915, smallerDataFile, 190, 260);
    }

    public static void main(String[] args) {
        new Main();
    }

    private void buildReferenceData() {
        List<EventTime> eventTimes = new ArrayList<>();
        eventTimes.add(new EventTime(4230, 4915));
        eventTimes.add(new EventTime(6055, 6450));
        eventTimes.add(new EventTime(8940, 9350));

        brakingEvents = new ArrayList<>();
        for (EventTime time : eventTimes) {
            brakingEvents.add(new Event(dataFile, time.getStart(), time.getEnd(), EventType.BRAKING));
        }
    }

    private void compareReferenceData() {
        for (Event event1 : brakingEvents) {
            for (Event event2 : brakingEvents) {
                TimeWarpInfo info = FastDTW.compare(event1.getTimeSeries(), event2.getTimeSeries(), 10, Distances.MANHATTAN_DISTANCE);

                System.out.format("Distance between %s and %s events: %f", event1.getStart(), event2.getStart(), info.getDistance());
                System.out.println();
            }
        }
    }

    /*private void findBrakingEvents() {
        try (Stream<String> stream = Files.lines(Paths.get(dataFile))) {
            List<String> list = stream.collect(Collectors.toList());

            TimeSeriesBase.Builder seriesBuilder = TimeSeriesBase.builder();
            boolean inEvent = false;
            int seriesIndex = 0;
            int eventStartValue = 0;

            for (int i = 0; i < list.size(); i++) {
                if (i > 1000) {
                    if (Math.abs(Double.parseDouble(list.get(i)) - Double.parseDouble(list.get(i - 1000))) > 1) {
                        if (!inEvent) {
                            System.out.println(i + "has a potential event");
                            inEvent = true;
                            eventStartValue = Double.parseDouble(list.get(i - 1000));
                        }
                    } else {
                        inEvent = false;
                    }
                }


                if (inEvent) {
                    seriesBuilder.add(seriesIndex, Double.parseDouble(list.get(i)));
                    seriesIndex++;
                }
            }

            //timeSeries = seriesBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void bruteForceFind() {
        try (Stream<String> stream = Files.lines(Paths.get(smallerDataFile))) {
            List<Double> list = stream.map(Double::valueOf).collect(Collectors.toList());

            TimeSeriesBase.Builder seriesBuilder = TimeSeriesBase.builder();
            boolean inEvent = false;
            int seriesIndex = 0;
            int eventStart = 0;
            double smallestDistance = 10000;
            int eventEndSmallest = 0;

            for (int i = 0; i < list.size(); i++) {
                if (i > 10) {
                    if (!inEvent) {
                        if (i % 10 == 0) {
                            inEvent = true;
                            eventStart = i;
                        }
                    } else {
                        seriesBuilder.add(seriesIndex, list.get(i));
                        seriesIndex++;

                        //Every 100 data points, build the time series and compare it to the reference data
                        if (seriesIndex > 30 && seriesIndex % 10 == 0) {
                            TimeSeries series = seriesBuilder.build();
                            Double meanDistance = compareWithReferences(series);
                            if (meanDistance < smallestDistance) {
                                smallestDistance = meanDistance;
                                eventEndSmallest = i;
                            }
                        }

                        if (i - eventStart > 250) {
                             if (smallestDistance < 50) {
                                System.out.println("Distance less than 50: " + eventStart + ", " + eventEndSmallest + ", " + smallestDistance);
                            }
                            i = eventStart + 1;
                            //System.out.print(". " + i + " .");
                            inEvent = false;
                            seriesIndex = 0;
                            smallestDistance = 10000;
                            eventEndSmallest = 0;
                            seriesBuilder = TimeSeriesBase.builder();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compare(String dataFile1, int start1, int end1, String dataFile2, int start2, int end2) {
        TimeSeries series1 = createSeries(dataFile1, start1, end1);
        TimeSeries series2 = createSeries(dataFile2, start2, end2);

        TimeWarpInfo info = FastDTW.compare(series1, series2, Distances.EUCLIDEAN_DISTANCE);
        System.out.format("Distance between %s and %s events: %f", start1, start2, info.getDistance());
    }

    private TimeSeries createSeries(String fileName, int start, int end) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            List<Double> list = stream.map(Double::valueOf).collect(Collectors.toList());

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
                    seriesBuilder.add(seriesIndex, list.get(i));
                }
            }

            return seriesBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double compareWithReferences(TimeSeries series) {
        double distance = 0;
        for (Event event : brakingEvents) {
            TimeWarpInfo info = FastDTW.compare(brakingEvents.get(0).getTimeSeries(), series, Distances.EUCLIDEAN_DISTANCE);
            distance += info.getDistance();
        }
        return distance / brakingEvents.size();
    }
}