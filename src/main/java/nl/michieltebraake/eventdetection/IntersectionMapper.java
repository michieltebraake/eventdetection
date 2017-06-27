package nl.michieltebraake.eventdetection;

import nl.michieltebraake.eventdetection.event.ClassifiedEvent;
import nl.michieltebraake.eventdetection.event.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntersectionMapper {
    private HashMap<String, Coordinate> intersections = new HashMap<>();
    private HashMap<String, Integer> brakeCount = new HashMap<>();

    private Coordinate pedestrianTopRight = new Coordinate(52.222465, 6.896042);
    private Coordinate pedestrianBottomLeft = new Coordinate(52.220844, 6.892577);
    private String pedestrianAreaName = "Voetgangersgebied";

    public IntersectionMapper() {
        intersections.put("Station", new Coordinate(52.223182, 6.892320));
        intersections.put("Wellinkgaarde", new Coordinate(52.223578, 6.895530));
        intersections.put("Brakmanstraat", new Coordinate(52.224621, 6.895026));
        intersections.put("Deurningerstraat", new Coordinate(52.224486, 6.89163));
        intersections.put("Wilminktheater", new Coordinate(52.222417, 6.892828));
    }

    public static void main(String[] args) {
        IntersectionMapper intersectionMapper = new IntersectionMapper();
        List<ClassifiedEvent> events = new ArrayList<>();
        events.add(new ClassifiedEvent(0, EventType.HARD_BRAKING, 0, 0, "52.221420", "6.892922"));
        //events.add(new ClassifiedEvent(0, EventType.HARD_BRAKING, 0, 0, "52.221420", "6.892922"));
        intersectionMapper.mapLocations(events);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    public static double distance(Coordinate coordinate1, Coordinate coordinate2) {
        double lat1 = coordinate1.getLat();
        double lat2 = coordinate2.getLat();
        double lng1 = coordinate1.getLng();
        double lng2 = coordinate2.getLng();
        double el1 = 0;
        double el2 = 0;

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public void mapLocations(List<ClassifiedEvent> events) {
        for (String intersection : intersections.keySet()) {
            brakeCount.put(intersection, 0);
        }
        brakeCount.put("Voetgangersgebied", 0);

        for (ClassifiedEvent event : events) {
            if (event.getType() == EventType.BRAKING) {
                continue;
            }

            double lowestDistance = 99999;
            String matchingIntersection = "";
            for (Map.Entry<String, Coordinate> entry : intersections.entrySet()) {
                double distance = distance(event.getCoordinate(), entry.getValue());

                if (distance < lowestDistance) {
                    lowestDistance = distance;
                    matchingIntersection = entry.getKey();
                }
            }

            if (lowestDistance < 40) {
                brakeCount.put(matchingIntersection, brakeCount.get(matchingIntersection) + 1);
            } else {
                //Check if coordinate is inside pedestrian zone
                if (event.getCoordinate().getLat() < pedestrianTopRight.getLat() && event.getCoordinate().getLng() < pedestrianTopRight.getLng()
                        && event.getCoordinate().getLat() > pedestrianBottomLeft.getLat() && event.getCoordinate().getLng() > pedestrianBottomLeft.getLng()) {
                    brakeCount.put(pedestrianAreaName, brakeCount.get(pedestrianAreaName) + 1);
                }
            }
        }

        System.out.println("=== Intersection brake count ===");
        for (Map.Entry<String, Integer> brakeEntry : brakeCount.entrySet()) {
            System.out.println(brakeEntry.getKey() + ", " + brakeEntry.getValue());
        }
    }
}
