package zbloom.cin.models;

import android.graphics.Bitmap;

/**
 * Created by bloom on 3/2/15.
 */
public class Client {
    private String first, last, address, image;
    private int id;

    public Client(String first, String last, Integer id, String address, String image) {
        this.first = first;
        this.last = last;
        this.id = id;
        this.address = address;
        this.image = image;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
