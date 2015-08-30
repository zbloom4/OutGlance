package zbloom.cin.models;

import java.util.ArrayList;

/**
 * Created by bloom on 6/3/15.
 */
public class Appointments {

    private ArrayList<Appointment> appointments;

    private static Appointments instance;

    public Appointments(){
        appointments = new ArrayList<Appointment>();
    }

    public static Appointments getInstance() {
        if (instance == null) instance = new Appointments();
        return instance;
    }

    public void add(Appointment appointment){
        appointments.add(appointment);
    }
}
