package ddwucom.mobile.distance;

public class SMSInfo {

    int _id;
    String datetime;
    String location;

    public SMSInfo(String datetime, String location) {
        this.datetime = datetime;
        this.location = location;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
