package zbloom.cin.models;

import org.json.JSONObject;

/**
 * Created by bloom on 3/10/15.
 */

public class Appointment {

    private String note;
    private String clientName;
    private Integer id;
    private String beginning;
    private String end;
    private String clockIn;
    private String clockOut;
    private Double duration;
    private String image;
    private Integer client_id;

    public Appointment(Integer id, Integer client_id, String note, String beginning, String end, String clockIn, String clockOut, double duration, String clientName, String image) {
        this.id = id;
        this.note = note;
        this.client_id = client_id;
        this.beginning = beginning;
        this.end = end;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
        this.duration = duration;
        this.clientName = clientName;
        this.image = image;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getEnd() {
        return end;
    }

    public void setEnd(String End) {
        end = end;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getBeginning() {
        return beginning;
    }

    public void setBeginning(String beginning) {
        this.beginning = beginning;
    }

    public Integer getYear(String appointment) {
        String dateTime[];
        String date = "";
        dateTime = appointment.split("T");
        date = dateTime[0];
        dateTime = date.split("-");
        return Integer.parseInt(dateTime[0]);
    }

    public Integer getMonth(String appointment) {
        String dateTime[];
        String date = "";
        dateTime = appointment.split("T");
        date = dateTime[0];
        dateTime = date.split("-");
        return Integer.parseInt(dateTime[1]);
    }

    public Integer getDay(String appointment) {
        String dateTime[];
        String date = "";
        dateTime = appointment.split("T");
        date = dateTime[0];
        dateTime = date.split("-");
        return Integer.parseInt(dateTime[2]);
    }

    public Integer getHour(String appointment) {
        String dateTime[];
        String time = "";
        dateTime = appointment.split("T");
        time = dateTime[1];
        dateTime = time.split(":");
        return Integer.parseInt(dateTime[0]);
    }

    public Integer getMinute(String appointment) {
        String dateTime[];
        String time = "";
        dateTime = appointment.split("T");
        time = dateTime[1];
        dateTime = time.split(":");
        return Integer.parseInt(dateTime[1]);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getClient_id() {
        return client_id;
    }

    public void setClient_id(Integer client_id) {
        this.client_id = client_id;
    }

    public String getClockIn() {
        return clockIn;
    }

    public void setClockIn(String clockIn) {
        this.clockIn = clockIn;
    }

    public String getClockOut() {
        return clockOut;
    }

    public void setClockOut(String clockOut) {
        this.clockOut = clockOut;
    }
}
