package ddwucom.mobile.distance;

public class UserInfo {
    private String email;
    private String pass;
    private String name;
    private String birth;
    private String phone;

    public UserInfo() {
        email = null;
        pass = null;
        name = null;
        birth = null;
        phone = null;
    }

    public UserInfo(String email, String pass, String name, String birth, String phone) {
        this.email = email;
        this.pass = pass;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
