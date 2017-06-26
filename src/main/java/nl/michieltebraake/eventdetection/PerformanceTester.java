package nl.michieltebraake.eventdetection;

import nl.michieltebraake.eventdetection.event.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTester {
    private LabeledData labeledData = new LabeledData();
    private Main main = new Main();
    private String dataFile = "resources/data/temp.txt";

    public PerformanceTester() {
        main.setBrakingEvents(labeledData.getBrakingTraining(), dataFile);
        main.setHardBrakingEvents(labeledData.getHardBrakingTraining(), dataFile);

        //compareTimeSeries();
        runTest(0, 0);
        runTest(1, 0);
        runTest(0, 1);
        //runTest(1, 1);
    }

    public static void main(String[] args) {
        new PerformanceTester();
    }

    private void compareTimeSeries() {
        EventComparison comparison = main.compareWithReferences(new Event(dataFile, null, 12680, 12720, null).getTimeSeries());
        System.out.println("Debug here");
    }

    private void runTest(int recognitionType, int classificationType) {
        List<ClassifiedEvent> foundEvents = main.findEvents(recognitionType, classificationType);

        List<EventTime> brakingLabeled = labeledData.getBrakingLabeled();
        List<EventTime> hardBrakingLabeled = labeledData.getHardBrakingLabeled();
        int correctlyIdentifiedBraking = 0;
        int wronglyIdentifiedBraking = 0;
        int correctlyIdentifiedHardBraking = 0;
        int wronglyIdentifiedHardBraking = 0;
        for (ClassifiedEvent classifiedEvent : foundEvents) {
            if (classifiedEvent.getType() == EventType.BRAKING && classifiedEvent.getStart() > 10700) {
                boolean foundMatch = false;
                for (EventTime eventTime : brakingLabeled) {
                    if (overlap(classifiedEvent, eventTime)) {
                        foundMatch = true;
                        correctlyIdentifiedBraking++;
                        break;
                    }
                }
                if (!foundMatch) {
                    //In case no matching event was found, it was wrongly identified
                    wronglyIdentifiedBraking++;
                }
            }
        }
        for (ClassifiedEvent classifiedEvent : foundEvents) {
            if (classifiedEvent.getType() == EventType.HARD_BRAKING && classifiedEvent.getStart() > 13300) {
                boolean foundMatch = false;
                for (EventTime eventTime : hardBrakingLabeled) {
                    if (overlap(classifiedEvent, eventTime)) {
                        foundMatch = true;
                        correctlyIdentifiedHardBraking++;
                        break;
                    }
                }
                if (!foundMatch) {
                    //In case no matching event was found, it was wrongly identified
                    wronglyIdentifiedHardBraking++;
                }
            }
        }
        saveEventsCsv(foundEvents);
        int missedBraking = brakingLabeled.size() - correctlyIdentifiedBraking;
        int missedHardBraking = hardBrakingLabeled.size() - correctlyIdentifiedHardBraking;

        System.out.println("Type: " + recognitionType + ", " + classificationType);
        System.out.println("=== Braking Recognition ===");
        System.out.println("Correct: " + correctlyIdentifiedBraking + " / " + brakingLabeled.size());
        System.out.println("Incorrect: " + wronglyIdentifiedBraking);
        System.out.println("Missed: " + missedBraking);
        System.out.println("=== Hard Braking Recognition ===");
        System.out.println("Correct: " + correctlyIdentifiedHardBraking + " / " + hardBrakingLabeled.size());
        System.out.println("Incorrect: " + wronglyIdentifiedHardBraking);
        System.out.println("Missed: " + missedHardBraking);
        System.out.println("=== Done ===");
    }

    private void saveEventsCsv(List<ClassifiedEvent> foundEvents) {
        List<String> eventStrings = new ArrayList<>();
        for (ClassifiedEvent classifiedEvent : foundEvents) {
            if (classifiedEvent.getType() == EventType.HARD_BRAKING) {
                eventStrings.add(classifiedEvent.getStart() + "," + (classifiedEvent.getEnd() - classifiedEvent.getStart()) + "," + classifiedEvent.getDistance());
            }
        }
        try {
            Files.write(Paths.get("output/events.csv"), eventStrings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean overlap(ClassifiedEvent classifiedEvent, EventTime eventTime) {
        return classifiedEvent.getStart() >= eventTime.getStart() && classifiedEvent.getStart() <= eventTime.getEnd()
                || classifiedEvent.getEnd() >= eventTime.getStart() && classifiedEvent.getEnd() <= eventTime.getEnd()
                || classifiedEvent.getStart() <= eventTime.getStart() && classifiedEvent.getEnd() >= eventTime.getEnd();
    }
}
