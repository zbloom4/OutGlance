package zbloom.cin.models;

/**
 * Created by bloom on 3/5/15.
 */
public class API {

    public String ip_address = "ec2-54-69-53-113.us-west-2.compute.amazonaws.com/";
    public Integer client_id = 0;
    public Integer appointment_id = 0;

    public String CLIENTS_URL = "http://" + ip_address + "clients.json";
    public String DESTROY_CLIENT_URL = "http://" + ip_address + "clients.json";
    public String LOGOUT_URL = "http://" + ip_address + "sessions.json";
    public String LOGIN_API_ENDPOINT_URL = "http://" + ip_address + "sessions.json";
    public String CREATE_CLIENT_ENDPOINT_URL = "http://" + ip_address + "clients.json";
    public String REGISTER_API_ENDPOINT_URL = "http://" + ip_address + "registrations";
    public String SHOW_USER_URL = "http://" + ip_address + "sessions";

    public String SHOW_CLIENT_PROFILE_URL = "";
    public String DELETE_APPOINTMENT_URL = "";
    public String SHOW_CLIENT_URL = "";
    public String CREATE_APPOINTMENT_URL = "";
    public String SHOW_APPOINTMENT_URL = "";
    public String CREATE_LOCATION_URL = "";
    public String UPDATE_APPOINTMENT_URL = "";
    public String UPDATE_CLIENT_URL = "";


    public void setUPDATE_CLIENT_URL() {
        UPDATE_CLIENT_URL = "http://" + ip_address + "clients/" + client_id.toString() + "/update.json";
    }

    public void setSHOW_CLIENT_PROFILE_URL() {
        SHOW_CLIENT_PROFILE_URL = "http://" + ip_address + "clients/" + client_id.toString() + "/show.json";
    }

    public void setSHOW_APPOINTMENT_URL() {
        SHOW_APPOINTMENT_URL = "http://" + ip_address + "clients/" + client_id.toString() + "/appointments/" + appointment_id.toString() + "/show.json";
    }

    public void setDESTROY_CLIENT_URL() {
        DESTROY_CLIENT_URL = "http://" + ip_address + "clients/" + client_id.toString() + "/destroy";
    }

    public void setCREATE_APPOINTMENT_URL() {
        CREATE_APPOINTMENT_URL = "http://" + ip_address + "clients/" + client_id.toString() + "/appointments/create.json";
    }

    public void setSHOW_CLIENT_URL() {
        SHOW_CLIENT_URL = "http://" + ip_address + "clients/" + client_id.toString() + "/appointments";
    }

    public void setCREATE_LOCATION_URL() {
        CREATE_LOCATION_URL = "http://" + ip_address + "clients/" + client_id.toString() + "/appointments/" + appointment_id.toString() + "/locations/create.json";
    }

    public void setUPDATE_APPOINTMENT_URL() {
        UPDATE_APPOINTMENT_URL = "http://" + ip_address + "clients/" + client_id.toString() + "/appointments/" + appointment_id.toString() + "/update.json";
    }

    public void setDELETE_APPOINTMENT_URL() {
        DELETE_APPOINTMENT_URL = "http://" + ip_address + "clients/" + client_id.toString() + "/appointments/" + appointment_id.toString() + "/delete.json";
    }

    public String getDELETE_APPOINTMENT_URL() {return DELETE_APPOINTMENT_URL;}

    public String getSHOW_APPOINTMENT_URL() {
        return SHOW_APPOINTMENT_URL;
    }

    public String getCREATE_APPOINTMENT_URL() {
        return CREATE_APPOINTMENT_URL;
    }

    public String getUPDATE_APPOINTMENT_URL() {
        return UPDATE_APPOINTMENT_URL;
    }

    public String getCREATE_LOCATION_URL() {
        return CREATE_LOCATION_URL;
    }

    public String getCLIENTS_URL() {
        return CLIENTS_URL;
    }

    public String getLOGOUT_URL() {
        return LOGOUT_URL;
    }

    public String getDESTROY_CLIENT_URL() {
        return DESTROY_CLIENT_URL;
    }

    public String getLOGIN_API_ENDPOINT_URL() {
        return LOGIN_API_ENDPOINT_URL;
    }

    public String getCREATE_CLIENT_ENDPOINT_URL() {
        return CREATE_CLIENT_ENDPOINT_URL;
    }

    public String getREGISTER_API_ENDPOINT_URL() {
        return REGISTER_API_ENDPOINT_URL;
    }

    public String getSHOW_CLIENT_URL() {
        return SHOW_CLIENT_URL;
    }

    public Integer getClient_id() {
        return client_id;
    }

    public Integer getAppointment_id() {
        return appointment_id;
    }

    public void setClient_id(Integer ClientID) {
        client_id = ClientID;
    }

    public void setAppointment_id(Integer AppointmentID) {
        appointment_id = AppointmentID;
    }

    public String getSHOW_USER_URL() {
        return SHOW_USER_URL;
    }

    public String getSHOW_CLIENT_PROFILE_URL() {return SHOW_CLIENT_PROFILE_URL; }

    public String getUPDATE_CLIENT_URL() {return UPDATE_CLIENT_URL; }
}
