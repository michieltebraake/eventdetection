package nl.michieltebraake.eventdetection;

public class AccelerometerPoint {
    private long timestamp;
    private double x;
    private double y;
    private double z;

    private String gpsTimestamp;
    private String lat;
    private String lng;

    public AccelerometerPoint(long timestamp, double x, double y, double z, String gpsTimestamp, String lat, String lng) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
        this.gpsTimestamp = gpsTimestamp;
        this.lat = lat;
        this.lng = lng;
    }

    public AccelerometerPoint(String accelerometerPoint) {
        String[] dataSplit = accelerometerPoint.split(",");
        timestamp = Long.parseLong(dataSplit[0]);

        //Before pre-processing GPS data has not yet been added to file
        if (dataSplit.length > 5) {
            x = Double.parseDouble(dataSplit[1]);
            y = Double.parseDouble(dataSplit[2]);
            z = Double.parseDouble(dataSplit[3]);
            this.gpsTimestamp = dataSplit[4];
            this.lat = dataSplit[5];
            this.lng = dataSplit[6];
        } else {
            x = Double.parseDouble(dataSplit[2]);
            y = Double.parseDouble(dataSplit[3]);
            z = Double.parseDouble(dataSplit[4]);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getGpsTimestamp() {
        return gpsTimestamp;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return timestamp + "," + x + "," + y + "," + z + "," + gpsTimestamp + "," + lat + "," + lng;
    }
}
