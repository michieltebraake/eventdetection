package nl.michieltebraake.eventdetection;

import nl.michieltebraake.eventdetection.event.EventTime;

import java.util.ArrayList;
import java.util.List;

public class LabeledData {
    private List<EventTime> brakingTraining = new ArrayList<>();
    private List<EventTime> hardBrakingTraining = new ArrayList<>();

    private List<EventTime> brakingLabeled = new ArrayList<>();
    private List<EventTime> hardBrakingLabeled = new ArrayList<>();

    public LabeledData() {
        initBraking();
        initHardBraking();
    }

    public List<EventTime> getBrakingTraining() {
        return brakingTraining;
    }

    public List<EventTime> getHardBrakingTraining() {
        return hardBrakingTraining;
    }

    public List<EventTime> getBrakingLabeled() {
        return brakingLabeled;
    }

    public List<EventTime> getHardBrakingLabeled() {
        return hardBrakingLabeled;
    }

    private void initBraking() {
        brakingTraining.add(new EventTime(600, 650));
        brakingTraining.add(new EventTime(860, 890));
        brakingTraining.add(new EventTime(960, 980));
        brakingTraining.add(new EventTime(990, 1040));
        brakingTraining.add(new EventTime(1060, 1090));
        brakingTraining.add(new EventTime(1130, 1170));
        brakingTraining.add(new EventTime(1320, 1360));
        brakingTraining.add(new EventTime(1460, 1480));
        brakingTraining.add(new EventTime(1580, 1600));
        brakingTraining.add(new EventTime(1600, 1620));
        brakingTraining.add(new EventTime(2470, 2490));
        brakingTraining.add(new EventTime(2720, 2760));
        brakingTraining.add(new EventTime(3140, 3160));
        brakingTraining.add(new EventTime(3270, 3310));
        brakingTraining.add(new EventTime(3650, 3690));
        brakingTraining.add(new EventTime(4880, 4900));
        brakingTraining.add(new EventTime(5080, 5120));
        brakingTraining.add(new EventTime(5860, 5900));
        brakingTraining.add(new EventTime(6200, 6230));
        brakingTraining.add(new EventTime(6910, 6940));
        brakingTraining.add(new EventTime(7120, 7155));
        brakingTraining.add(new EventTime(7570, 7600));
        brakingTraining.add(new EventTime(7820, 7840));
        brakingTraining.add(new EventTime(9320, 9370));
        brakingTraining.add(new EventTime(9380, 9410));
        brakingTraining.add(new EventTime(9670, 9700));

        brakingLabeled.add(new EventTime(10720, 10740));
        brakingLabeled.add(new EventTime(10980, 11010));
        brakingLabeled.add(new EventTime(11115, 11135));
        brakingLabeled.add(new EventTime(11660, 11690));
        brakingLabeled.add(new EventTime(11930, 11950));
        brakingLabeled.add(new EventTime(12680, 12720));
        brakingLabeled.add(new EventTime(13940, 13970));
        brakingLabeled.add(new EventTime(15370, 15390));
        brakingLabeled.add(new EventTime(15390, 15410));
        brakingLabeled.add(new EventTime(16670, 16700));

    }

    private void initHardBraking() {
        hardBrakingTraining.add(new EventTime(2150, 2180));
        hardBrakingTraining.add(new EventTime(2610, 2640));
        hardBrakingTraining.add(new EventTime(2660, 2680));
        hardBrakingTraining.add(new EventTime(3510, 3550));
        hardBrakingTraining.add(new EventTime(3780, 3810));
        hardBrakingTraining.add(new EventTime(3910, 3930));
        hardBrakingTraining.add(new EventTime(4190, 4210));
        hardBrakingTraining.add(new EventTime(4380, 4400));
        hardBrakingTraining.add(new EventTime(4520, 4550));
        hardBrakingTraining.add(new EventTime(6340, 6370));
        hardBrakingTraining.add(new EventTime(7155, 7170));
        hardBrakingTraining.add(new EventTime(7730, 7750));
        hardBrakingTraining.add(new EventTime(7760, 7780));
        hardBrakingTraining.add(new EventTime(7790, 7800));
        hardBrakingTraining.add(new EventTime(8310, 8350));
        hardBrakingTraining.add(new EventTime(8730, 8750));
        hardBrakingTraining.add(new EventTime(8850, 8880));
        hardBrakingTraining.add(new EventTime(9020, 9040));
        hardBrakingTraining.add(new EventTime(9800, 9820));
        hardBrakingTraining.add(new EventTime(9920, 9960));
        hardBrakingTraining.add(new EventTime(10310, 10330));
        hardBrakingTraining.add(new EventTime(10390, 10410));
        hardBrakingTraining.add(new EventTime(10410, 10430));
        hardBrakingTraining.add(new EventTime(10640, 10675));
        hardBrakingTraining.add(new EventTime(12170, 12200));
        hardBrakingTraining.add(new EventTime(12400, 12420));

        hardBrakingLabeled.add(new EventTime(13360, 13380));
        hardBrakingLabeled.add(new EventTime(13460, 13470));
        hardBrakingLabeled.add(new EventTime(13550, 13570));
        hardBrakingLabeled.add(new EventTime(13640, 13690));
        hardBrakingLabeled.add(new EventTime(14190, 14210));
        hardBrakingLabeled.add(new EventTime(14220, 14240));
        hardBrakingLabeled.add(new EventTime(14260, 14280));
        hardBrakingLabeled.add(new EventTime(14300, 14320));
        hardBrakingLabeled.add(new EventTime(14350, 14370));
        hardBrakingLabeled.add(new EventTime(17155, 17185));
    }
}
