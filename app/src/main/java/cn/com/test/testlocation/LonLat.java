package cn.com.test.testlocation;

public class LonLat {

    private double latitude;
    private double longitude;
    private float accuracy;
    private String time;
    private String type;
    private boolean isGetLocation;

    public LonLat() {
    }

    public LonLat(double latitude, double longitude, float accuracy, String time, String type, boolean isGetLocation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.time = time;
        this.type = type;
        this.isGetLocation = isGetLocation;
    }

    public LonLat(String time, String type, boolean isGetLocation) {
        this.time = time;
        this.type = type;
        this.isGetLocation = isGetLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isGetLocation() {
        return isGetLocation;
    }

    public void setGetLocation(boolean getLocation) {
        isGetLocation = getLocation;
    }


    @Override
    public String toString() {
        if (isGetLocation)
            return time +
                "  latitude:" + latitude +
                ", longitude:" + longitude +
                ", accuracy:" + accuracy +
                ", type:" + type + '\n';
        else
            return time +
                " Location information not obtained" +
                ", type:'" + type + '\n';
    }

    public String tofileString(int electricQuantity) {

        return longitude +"  "+latitude+"  "+accuracy+"  "+type+"  "+time+"  "+electricQuantity+ '\n';

    }
}
