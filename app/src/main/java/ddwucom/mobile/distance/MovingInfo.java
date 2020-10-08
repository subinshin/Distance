package ddwucom.mobile.distance;

import java.sql.Time;
import java.util.Date;

public class MovingInfo {

    int id;
    int year;
    int month;
    int dayOfMonth;
    String startTime;
    String endTime;
    String location;
    double latitude;
    double longitude;
    String memo;
    String store;

    public MovingInfo() {
        startTime = null;
        endTime = null;
    }

    public MovingInfo(int year, int month, int dayOfMonth, String startTime, String endTime, double latitude, double longitude, String location, String memo, String store) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.memo = memo;
        this.store = store;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
