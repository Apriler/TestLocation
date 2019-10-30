package cn.com.test.testlocation;

public class LonLat {

    private double latitude;
    private double longitude;
    private float accuracy;
    private String time;
    private String type;

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

    @Override
    public String toString() {
        return time +
                "  latitude:" + latitude +
                ", longitude:" + longitude +
                ", accuracy:" + accuracy +
                ", time='" + time + '\'' +
                ", type='" + type + '\'';
    }
}
