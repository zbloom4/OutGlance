package zbloom.cin.models;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by bloom on 9/7/15.
 */
public class OfflineData {
    private static OfflineData instance = null;

    private ArrayList<Location> Locations;

    private OfflineData(){
        Locations = null;
    }

    public static OfflineData getInstance(){
        if (instance == null){
            instance = new OfflineData();
        }
        return instance;
    }

    public ArrayList<Location> getLocations() {
        return this.Locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        Locations = locations;
    }



}
