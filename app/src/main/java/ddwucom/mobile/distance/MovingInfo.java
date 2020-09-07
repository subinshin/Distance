package ddwucom.mobile.distance;

import java.sql.Time;
import java.util.Date;

public class MovingInfo {

    int id;
//    int year;
//    int month;
//    int dayOfMonth;
    String startTime;
    String endTime;
    String location;
    double latitude;
    double longitude;

    public MovingInfo() {
        startTime = null;
        endTime = null;
    }

    public MovingInfo(String startTime, String endTime, double latitude, double longitude) {
        this.startTime = startTime;
        this.endTime = endTime;
//        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public int getYear() {
//        return year;
//    }
//
//    public void setYear(int year) {
//        this.year = year;
//    }
//
//    public int getMonth() {
//        return month;
//    }
//
//    public void setMonth(int month) {
//        this.month = month;
//    }
//
//    public int getDayOfMonth() {
//        return dayOfMonth;
//    }
//
//    public void setDayOfMonth(int dayOfMonth) {
//        this.dayOfMonth = dayOfMonth;
//    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}
