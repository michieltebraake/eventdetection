package nl.michieltebraake.eventdetection;

import com.fastdtw.dtw.FastDTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;
import com.fastdtw.util.Distances;
import nl.michieltebraake.eventdetection.event.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private String dataFile = "resources/data/smaller_Y2";
    private String smallerDataFile = "resources/data/smaller_Y2";

    private HashMap<EventType, List<Event>> allEvents = new HashMap<>();
    private List<Event> brakingEvents;
    private List<Event> hardBrakingEvents;

    private boolean useStdDev = false;

    public Main() {
        buildNewReferenceData();
        //compareReferenceData();
        //findBrakingEvents();
        //bruteForceFind();
        //compare(dataFile, 4230, 4915, smallerDataFile, 190, 260);
        //findEvents();
        findEventsSimple();
    }

    public static void main(String[] args) {
        new Main();
    }

    private void buildReferenceData() {
        List<EventTime> brakingEventTimes = new ArrayList<>();
        brakingEventTimes.add(new EventTime(190, 250));
        brakingEventTimes.add(new EventTime(295, 325));
        brakingEventTimes.add(new EventTime(360, 410));
        brakingEventTimes.add(new EventTime(1660, 1700));

        brakingEvents = new ArrayList<>();
        for (EventTime time : brakingEventTimes) {
            brakingEvents.add(new Event(dataFile, null, time.getStart(), time.getEnd(), EventType.BRAKING));
        }
        allEvents.put(EventType.BRAKING, brakingEvents);

        List<EventTime> hardBrakingEventTimes = new ArrayList<>();
        hardBrakingEventTimes.add(new EventTime(1330, 1350));
        hardBrakingEventTimes.add(new EventTime(1375, 1390));
        hardBrakingEventTimes.add(new EventTime(1435, 1450));
        hardBrakingEventTimes.add(new EventTime(1480, 1500));

        hardBrakingEvents = new ArrayList<>();
        for (EventTime time : hardBrakingEventTimes) {
            hardBrakingEvents.add(new Event(dataFile, null, time.getStart(), time.getEnd(), EventType.HARD_BRAKING));
        }
        allEvents.put(EventType.HARD_BRAKING, hardBrakingEvents);
    }

    private void buildNewReferenceData() {
        System.out.println("Building reference data...");
        List<EventTime> brakingEventTimes = new ArrayList<>();
        brakingEventTimes.add(new EventTime(1340, 1380));
        brakingEventTimes.add(new EventTime(1460, 1510));
        brakingEventTimes.add(new EventTime(1600, 1630));
        brakingEventTimes.add(new EventTime(1850, 1900));

        brakingEvents = new ArrayList<>();
        for (EventTime time : brakingEventTimes) {
            brakingEvents.add(new Event("resources/data/michiel-15-6/Acc_Processed.txt", null, time.getStart(), time.getEnd(), EventType.BRAKING));
        }
        allEvents.put(EventType.BRAKING, brakingEvents);

        List<EventTime> hardBrakingEventTimes = new ArrayList<>();
        hardBrakingEventTimes.add(new EventTime(8780, 8810));
        hardBrakingEventTimes.add(new EventTime(8820, 8850));
        hardBrakingEventTimes.add(new EventTime(8910, 8940));
        hardBrakingEventTimes.add(new EventTime(8960, 8990));

        hardBrakingEvents = new ArrayList<>();
        for (EventTime time : hardBrakingEventTimes) {
            hardBrakingEvents.add(new Event("resources/data/michiel-15-6/Acc_Processed.txt", null, time.getStart(), time.getEnd(), EventType.HARD_BRAKING));
        }
        allEvents.put(EventType.HARD_BRAKING, hardBrakingEvents);
        System.out.println("Done building reference data.");
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

    private void findEvents() {
        List<EventLocation> events = new ArrayList<>();
        try {
            List<String> accLines = Files.lines(Paths.get("resources/data/sander-15-6/Acc_Processed.txt.")).collect(Collectors.toList());
            List<AccelerometerPoint> accelerometerPoints = new ArrayList<>();
            for (String accelerometerString : accLines) {
                accelerometerPoints.add(new AccelerometerPoint(accelerometerString));
            }

            if (useStdDev) {
                List<Double> initValues = new ArrayList<>();
                int initAmount = 3;
                for (int i = 0; i < initAmount; i++) {
                    initValues.add(accelerometerPoints.get(i).getY());
                }
                StandardDeviation initialStdDev = StandardDeviation.calculateStdDev(initValues);

                //Initialize the standard deviation
                RollingStandardDeviation rollingStdDev = new RollingStandardDeviation(initialStdDev.getMean(), initialStdDev.getStandardDeviation(), initAmount);
                for (int i = initAmount; i < accelerometerPoints.size(); i++) {
                    int blockSize = 6;
                    if (i % blockSize != 0) {
                        rollingStdDev.addPoint(accelerometerPoints.get(i).getY());
                        continue;
                    }

                    RollingStandardDeviation tempRollingStdDev = rollingStdDev.clone();
                    double maxWindowSize = 50;
                    for (int j = 0; j < maxWindowSize && i + j < accelerometerPoints.size(); j++) {
                        if (j == 0 || j % blockSize != 0) {
                            double value = accelerometerPoints.get(i + j).getY();
                            tempRollingStdDev.addPoint(value);
                            continue;
                        }

                        StandardDeviation stdDev = StandardDeviation.calculateStdDevAccPoint(accelerometerPoints.subList(i, i + j));

                        if (stdDev.getStandardDeviation() > tempRollingStdDev.getStdDev() * 1.2) {
                            System.out.println("Standard deviation is large enough: " + i + ", " + (i + j) + ", factor: " + stdDev.getStandardDeviation() / tempRollingStdDev.getStdDev());
                            events.add(new EventLocation(accelerometerPoints.get(i), i, i + j));
                        }
                    }
                }
            } else {
                Mean mean = new Mean();
                for (int i = 0; i < accelerometerPoints.size(); i++) {
                    int blockSize = 6;
                    if (i % blockSize != 0) {
                        mean.addPoint(accelerometerPoints.get(i).getY());
                        continue;
                    }

                    Mean tempMean = mean.clone();
                    double maxWindowSize = 50;
                    for (int j = 0; j < maxWindowSize && i + j < accelerometerPoints.size(); j++) {
                        if (j < 12 || j % blockSize != 0) {
                            double value = accelerometerPoints.get(i + j).getY();
                            tempMean.addPoint(value);
                            continue;
                        }

                        double currentMean = Mean.getMean(accelerometerPoints.subList(i, i + j));

                        if (tempMean.getMean() - currentMean > 0.2) {
                            System.out.println("Mean difference is large enough: " + i + ", " + (i + j) + ", difference: " + Math.abs(tempMean.getMean() - currentMean));
                            events.add(new EventLocation(accelerometerPoints.get(i), i, i + j));
                        }
                    }
                }
            }



            //Of all collected events, take the lowest matching start i and highest end y
            List<EventLocation> filteredEvents = new ArrayList<>();
            EventLocation currentEvent = null;
            int currentStart = 0;
            int currentEnd = 0;
            for (EventLocation event : events) {
                //End needs to be at least as far as current end
                if (currentEvent == null) {
                    currentEvent = event;
                }

                if (event.getEnd() >= currentEnd) {
                    if (currentStart == 0 && currentEnd == 0) {
                        currentStart = event.getStart();
                        currentEnd = event.getEnd();
                        currentEvent = event;
                    } else if (event.getStart() < currentEnd) {
                        currentEnd = event.getEnd();
                        currentEvent = event;
                    } else if (event.getStart() >= currentEnd) {
                        //Found a new event, save the previous one
                        filteredEvents.add(new EventLocation(event.getAccelerometerPoint(), currentStart, currentEnd));
                        currentStart = event.getStart();
                        currentEnd = event.getEnd();
                    }
                }
            }
            //Add last event to events as well
            if (currentEvent != null) {
                filteredEvents.add(new EventLocation(currentEvent.getAccelerometerPoint(), currentStart, currentEnd));
            }

            //Create text output with the location of the events, for debugging purposes
            List<String> filteredEventsStrings = new ArrayList<>();

            //Classify all found events
            List<ClassifiedEvent> classifiedEvents = new ArrayList<>();
            for (EventLocation eventLocation : filteredEvents) {
                System.out.println(eventLocation.getStart() + ", " + eventLocation.getEnd());
                EventSeriesBuilder seriesBuilder = new EventSeriesBuilder();

                int bestStart = -1;
                int bestEnd = -1;
                EventComparison bestComparison = null;
                int blockSize = 6;

                for (int i = eventLocation.getStart(); i <= eventLocation.getEnd(); i++) {
                    double value = accelerometerPoints.get(i).getY();
                    seriesBuilder.add(value);
                    if (i > 0 && i % blockSize == 0) {
                        EventComparison comparison = compareWithReferences(seriesBuilder.getTimeSeries());
                        if (bestComparison == null) {
                            bestComparison = comparison;
                            bestStart = eventLocation.getStart();
                            bestEnd = i;
                        } else if (comparison.getDistance() < bestComparison.getDistance()) {
                            bestComparison = comparison;
                            bestEnd = i;
                        }
                    }
                }

                if (bestComparison != null && bestComparison.getDistance() < 20) {
                    AccelerometerPoint centerLocation = accelerometerPoints.get((bestStart + bestEnd) / 2);
                    classifiedEvents.add(new ClassifiedEvent(bestComparison.getDistance(), bestComparison.getType(), bestStart, bestEnd, centerLocation.getLat(), centerLocation.getLng()));

                    //For debugging purposes:
                    if (bestComparison.getType() == EventType.HARD_BRAKING) {
                        filteredEventsStrings.add(bestStart + "," + (bestEnd - bestStart) + "," + bestComparison.getDistance());
                    }
                }
            }
            Files.write(Paths.get("output/events.csv"), filteredEventsStrings);

            //Create files of the classified events with their locations
            List<String> brakeLocations = new ArrayList<>();
            List<String> hardBrakeLocations = new ArrayList<>();
            for (ClassifiedEvent classifiedEvent : classifiedEvents) {
                System.out.println(classifiedEvent);
                if (classifiedEvent.getType() == EventType.BRAKING) {
                    brakeLocations.add(classifiedEvent.getLat() + "," + classifiedEvent.getLng());
                } else if (classifiedEvent.getType() == EventType.HARD_BRAKING) {
                    hardBrakeLocations.add(classifiedEvent.getLat() + "," + classifiedEvent.getLng());
                }
            }

            Files.write(Paths.get("output/brake_locations.csv"), brakeLocations);
            Files.write(Paths.get("output/hard_brake_locations.csv"), hardBrakeLocations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findEventsSimple() {
        List<ClassifiedEvent> events = new ArrayList<>();
        try {
            List<String> accLines = Files.lines(Paths.get("resources/data/david-15-6/Acc_Processed.txt.")).collect(Collectors.toList());
            List<AccelerometerPoint> accelerometerPoints = new ArrayList<>();
            for (String accelerometerString : accLines) {
                accelerometerPoints.add(new AccelerometerPoint(accelerometerString));
            }

            Mean mean = new Mean();
            for (int i = 0; i < accelerometerPoints.size(); i++) {
                int blockSize = 6;
                if (i % blockSize != 0) {
                    mean.addPoint(accelerometerPoints.get(i).getY());
                    continue;
                }

                Mean tempMean = mean.clone();
                double maxWindowSize = 50;

                for (int j = 0; j < maxWindowSize && i + j < accelerometerPoints.size(); j++) {
                    if (j < 12 || j % blockSize != 0) {
                        double value = accelerometerPoints.get(i + j).getY();
                        tempMean.addPoint(value);
                        continue;
                    }

                    double currentMean = Mean.getMean(accelerometerPoints.subList(i, i + j));

                    AccelerometerPoint centerLocation = accelerometerPoints.get(i + (j / 2));
                    double diff = tempMean.getMean() - currentMean;
                    if (diff > 1.2) {
                        events.add(new ClassifiedEvent(diff, EventType.HARD_BRAKING, i, i + j, centerLocation.getLat(), centerLocation.getLng()));
                    } else if (diff > 1) {
                        events.add(new ClassifiedEvent(diff, EventType.BRAKING, i, i + j, centerLocation.getLat(), centerLocation.getLng()));
                    }
                }

            }

            //Of all collected events, take the lowest matching start i and highest end y
            List<ClassifiedEvent> filteredEvents = new ArrayList<>();
            ClassifiedEvent currentEvent = null;
            int currentStart = 0;
            int currentEnd = 0;
            for (ClassifiedEvent event : events) {
                //End needs to be at least as far as current end
                if (currentEvent == null) {
                    currentEvent = event;
                }

                if (event.getEnd() >= currentEnd) {
                    if (currentStart == 0 && currentEnd == 0) {
                        currentStart = event.getStart();
                        currentEnd = event.getEnd();
                        currentEvent = event;
                    } else if (event.getStart() < currentEnd) {
                        currentEnd = event.getEnd();
                        currentEvent = event;
                    } else if (event.getStart() >= currentEnd) {
                        //Found a new event, save the previous one
                        filteredEvents.add(event);
                        currentStart = event.getStart();
                        currentEnd = event.getEnd();
                    }
                }
            }

            //Create files of the classified events with their locations
            List<String> brakeLocations = new ArrayList<>();
            List<String> hardBrakeLocations = new ArrayList<>();
            for (ClassifiedEvent classifiedEvent : filteredEvents) {
                System.out.println(classifiedEvent);
                if (classifiedEvent.getType() == EventType.BRAKING) {
                    brakeLocations.add(classifiedEvent.getLat() + "," + classifiedEvent.getLng());
                } else if (classifiedEvent.getType() == EventType.HARD_BRAKING) {
                    hardBrakeLocations.add(classifiedEvent.getLat() + "," + classifiedEvent.getLng());
                }
            }

            Files.write(Paths.get("output/brake_locations.csv"), brakeLocations);
            Files.write(Paths.get("output/hard_brake_locations.csv"), hardBrakeLocations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bruteForceFind() {
        try (Stream<String> stream = Files.lines(Paths.get(smallerDataFile))) {
            List<Double> list = stream.map(Double::valueOf).collect(Collectors.toList());

            TimeSeriesBase.Builder seriesBuilder = TimeSeriesBase.builder();
            boolean inEvent = false;
            int seriesIndex = 0;
            int eventStart = 0;
            double smallestDistance = 10000;
            int eventEndSmallest = 0;

            int currentEventStart = -1;
            int currentEventEnd = -1;
            double currentEventLowest = 10000;

            EventType type = null;

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
                        if (seriesIndex > 20 && seriesIndex % 10 == 0) {
                            TimeSeries series = seriesBuilder.build();
                            EventComparison comparison = compareWithReferences(series);
                            Double meanDistance = comparison.getDistance();
                            type = comparison.getType();
                            if (meanDistance < smallestDistance) {
                                smallestDistance = meanDistance;
                                eventEndSmallest = i;
                            }
                        }

                        if (i - eventStart > 250) {
                            if (smallestDistance < 20) {
                                //Found a sufficiently low distance
                                //Check if we're still in an event we were already checking out
                                if (currentEventStart == -1) {
                                    currentEventStart = eventStart;
                                    currentEventEnd = eventEndSmallest;
                                    currentEventLowest = smallestDistance;
                                } else if (eventStart < currentEventEnd) {
                                    if (smallestDistance < currentEventLowest) {
                                        currentEventLowest = smallestDistance;
                                    }
                                } else {
                                    //Found a new event, print data from last event
                                    System.out.println("Distance less than 20, type: " + type + ", " + currentEventStart + ", " + currentEventEnd + ", " + currentEventLowest);
                                    currentEventStart = eventStart;
                                    currentEventEnd = eventEndSmallest;
                                    currentEventLowest = smallestDistance;
                                    type = null;
                                }
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

    private EventComparison compareWithReferences(TimeSeries series) {
        double smallestDistance = 10000;
        EventType bestMatch = null;
        for (Map.Entry<EventType, List<Event>> entry : allEvents.entrySet()) {
            double distance = 0;

            for (Event event : entry.getValue()) {
                TimeWarpInfo info = FastDTW.compare(event.getTimeSeries(), series, Distances.EUCLIDEAN_DISTANCE);
                distance += info.getDistance();
            }
            distance = distance / brakingEvents.size();

            if (distance < smallestDistance) {
                smallestDistance = distance;
                bestMatch = entry.getKey();
            }
        }
        return new EventComparison(smallestDistance, bestMatch);
    }
}