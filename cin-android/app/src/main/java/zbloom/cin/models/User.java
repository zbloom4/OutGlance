package zbloom.cin.models;

/**
 * Created by bloom on 2/11/15.
 */
public class User {
    private String first, last, email, authToken;

    public User(String first, String last, String email, int ID, double hours) {
        this.first = first;
        this.last = last;
        this.email = email;
        this.ID = ID;
        this.hours = hours;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    int ID;
    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    private double hours;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getName() {
        return first + " " + last;
    }
}
