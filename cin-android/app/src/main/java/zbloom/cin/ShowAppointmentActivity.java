package zbloom.cin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.lang.Object;

import zbloom.cin.adapters.NavigationAdapter;
import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.Appointment;
import zbloom.cin.models.Client;
import zbloom.cin.models.API;
import zbloom.cin.models.Navigation;
import zbloom.cin.models.User;


public class ShowAppointmentActivity extends ActionBarActivity {

    Integer appointmentID = 0;
    Integer clientID = 0;
    private static Appointment appointment;
    private SharedPreferences mPreferences;
    private API api = new API();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private NavigationAdapter mNavigationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_appointment);

        final Bundle extras = getIntent().getExtras();
        clientID = extras.getInt("ClientID");
        appointmentID = extras.getInt("AppointmentID");

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("My Profile");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPreferences.contains("AuthToken")) {
            api.setClient_id(clientID);
            api.setAppointment_id(appointmentID);
            api.setSHOW_APPOINTMENT_URL();
            loadAppointmentFromAPI(api.getSHOW_APPOINTMENT_URL());
        } else {
            Intent intent = new Intent(ShowAppointmentActivity.this, WelcomeActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    private void loadAppointmentFromAPI(String url) {
        ShowAppointment showAppointment = new ShowAppointment(ShowAppointmentActivity.this);
        showAppointment.setMessageLoading("Loading appointment...");
        showAppointment.setAuthToken(mPreferences.getString("AuthToken", ""));
        showAppointment.execute(url);
    }

    public void onClickBack(View view)
    {
        Intent intent = new Intent(this, ShowClientActivity.class);
        intent.putExtra("ClientID", clientID);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ShowClientActivity.class);
        intent.putExtra("ClientID", clientID);
        startActivity(intent);
    }

    private class ShowAppointment extends UrlJsonAsyncTask {
        public ShowAppointment(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(urls[0]);
            String response = null;
            JSONObject json = new JSONObject();

            try {
                try {
                    json.put("success", false);
                    json.put("info", "Something went wrong. Retry!");
                    get.setHeader("Accept", "application/json");
                    get.setHeader("Content-Type", "application/json");
                    get.setHeader("X-User-Email", mPreferences.getString("UserEmail", ""));
                    get.setHeader("X-User-Token", mPreferences.getString("AuthToken", ""));

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    response = client.execute(get, responseHandler);
                    json = new JSONObject(response);

                } catch (HttpResponseException e) {
                    e.printStackTrace();
                    Log.e("ClientProtocol", "" + e);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IO", "" + e);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON", "" + e);
            }

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            String clientFirst = "";
            String clientLast = "";
            String created_at = "";
            Double duration= 0.0;
            String note = "";
            String date = "";
            String month = "";
            String day = "";
            String year = "";
            String time = "";
            String hour = "";
            String minute = "";
            int id = 0;
            String dateTime[];
            try {
                JSONObject jsonAppointment = json.getJSONObject("data").getJSONObject("client");
                clientFirst = jsonAppointment.getString("first");
                clientLast = jsonAppointment.getString("last");
                jsonAppointment = jsonAppointment.getJSONObject("appointment");
                created_at = jsonAppointment.getString("created_at");
                note = jsonAppointment.getString("note");
                id = jsonAppointment.getInt("id");
                duration = jsonAppointment.getDouble("hours");
                dateTime = created_at.split("T");
                date = dateTime[0];
                time = dateTime[1];
                dateTime = date.split("-");
                month = dateTime[1];
                day = dateTime[2];
                year = dateTime[0];
                dateTime = time.split(":");
                hour = dateTime[0];
                minute = dateTime[1];
                appointment = new Appointment(note, month + "/" + day + "/" + year, hour + ":" + minute, duration/3600, clientFirst + " " + clientLast, id);
            }
            catch (JSONException e1) {
                e1.printStackTrace();
            }
            setUp();
            super.onPostExecute(json);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logoutFromAPI(api.getLOGOUT_URL());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logoutFromAPI(String url) {
        Logout logout = new Logout(ShowAppointmentActivity.this);
        logout.setMessageLoading("Logging out...");
        logout.execute(url);
    }

    private class Logout extends UrlJsonAsyncTask {
        public Logout(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpDelete delete = new HttpDelete(urls[0]);
            JSONObject holder = new JSONObject();
            JSONObject userObj = new JSONObject();
            String response = null;
            JSONObject json = new JSONObject();

            try {
                try {
                    json.put("success", false);
                    json.put("info", "Something went wrong. Retry!");
                    delete.setHeader("Accept", "application/json");
                    delete.setHeader("Content-Type", "application/json");
                    delete.setHeader("X-User-Email", mPreferences.getString("UserEmail", ""));
                    delete.setHeader("X-User-Token", mPreferences.getString("AuthToken", ""));

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    response = client.execute(delete, responseHandler);
                    json = new JSONObject(response);

                } catch (HttpResponseException e) {
                    e.printStackTrace();
                    Log.e("ClientProtocol", "" + e);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IO", "" + e);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON", "" + e);
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getBoolean("success")) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.remove("AuthToken");
                    editor.commit();

                    Intent intent = new Intent(ShowAppointmentActivity.this,
                            WelcomeActivity.class);
                    startActivityForResult(intent, 0);
                }
                Toast.makeText(context, json.getString("info"),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
                        .show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }

    public void setUp() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Appointment");
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.appointmentList);


        mNavigationAdapter = new NavigationAdapter(ShowAppointmentActivity.this, getData());
        recyclerView.setAdapter(mNavigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowAppointmentActivity.this));
    }

    public static List getData(){
        int[] icons = {R.drawable.ic_action_person, R.drawable.ic_action_event, R.drawable.ic_action_time, R.drawable.ic_action_time, R.drawable.ic_action_about};
        String[] titles = {appointment.getClientName(), appointment.getDate(), appointment.getCreated_at(), String.format("%.3f", appointment.getDuration())+ " hours", appointment.getNote()};
        List<Navigation> navigation = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            Navigation current = new Navigation();
            current.setIconID(icons[i]);
            current.setTitle(titles[i]);
            navigation.add(current);
        }
        return navigation;
    }
}
