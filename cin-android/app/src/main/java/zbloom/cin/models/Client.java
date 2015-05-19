package zbloom.cin.models;

/**
 * Created by bloom on 3/2/15.
 */
public class Client {
    private String first, last;
    private int id;

    public Client(String first, String last, Integer id) {
        this.first = first;
        this.last = last;
        this.id = id;
    }

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

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public String getName() {
        return first + " " + last;
    }

}
