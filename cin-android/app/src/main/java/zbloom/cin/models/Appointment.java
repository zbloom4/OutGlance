package zbloom.cin.models;

/**
 * Created by bloom on 3/10/15.
 */

public class Appointment {

    private String note;
    private String clientName;
    private Integer id;
    private String created_at;
    private String date;
    private Double duration;

    public Appointment(String note, String date, String created_at, double duration, String clientName, int id) {
        this.id = id;
        this.note = note;
        this.created_at = created_at;
        this.date = date;
        this.duration = duration;
        this.clientName = clientName;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        date = date;
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

}
