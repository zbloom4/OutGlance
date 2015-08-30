package zbloom.cin;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import me.tatarka.support.os.PersistableBundle;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import zbloom.cin.libs.Lazy;
import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.Appointment;
import zbloom.cin.models.Appointments;
import zbloom.cin.models.Navigation;
import zbloom.cin.adapters.NavigationAdapter;
import zbloom.cin.models.Client;
import zbloom.cin.models.API;


public class HomeActivity extends ActionBarActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;
    private static final ArrayList<Client> clients = new ArrayList<Client>();
    private API api = new API();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private NavigationAdapter mNavigationAdapter;
    Appointments appointments = Appointments.getInstance();
    ArrayList<String> appointment_ids = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        editor = mPreferences.edit();
        //editor.remove("Appointment_ids");
        //editor.commit();
        String json = mPreferences.getString("Appointment_ids", null);
        ArrayList<String> appIDs = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String id = a.optString(i);
                   appIDs.add(id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        appointment_ids = appIDs;

        // Get an instance of the JobScheduler, this will delegate to the system JobScheduler on api 21+
        // and to a custom implementataion on older api levels.
        JobScheduler jobScheduler = JobScheduler.getInstance(this);

        // Extras for your job.
        PersistableBundle extras = new PersistableBundle();
        extras.putString("key", "value");

        // Construct a new job with your service and some constraints.
        // See the javadoc for more detail.
        JobInfo job = new JobInfo.Builder(0 /*jobid*/, new ComponentName(this, JobSchedulerService.class))
                .setMinimumLatency(10000)
                .setOverrideDeadline(20000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(false)
                .setPersisted(true)
                .setExtras(extras)
                .build();

        jobScheduler.schedule(job);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPreferences.contains("AuthToken")) {
            loadClientsFromAPI(api.getCLIENTS_URL());
            //Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
            //startActivityForResult(intent, 0);
        } else {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    public static List getData(){
        List<Navigation> navigation = new ArrayList<>();
        for (int i = 0; i < clients.size(); i++){
            Navigation current = new Navigation();
            current.setIconID(R.drawable.ic_action_person);
            current.setTitle(clients.get(i).getName());
            navigation.add(current);
        }
        return navigation;
    }


    public void onClickNewClient(View view) {
        Intent intent = new Intent(HomeActivity.this, NewClientActivity.class);
        intent.putExtra("isUpdate", false);
        intent.putExtra("clientID", 0);
        startActivityForResult(intent, 0);
    }

    private void loadClientsFromAPI(String url) {
        GetClients getClients = new GetClients(HomeActivity.this);
        getClients.setMessageLoading("Loading clients...");
        getClients.setAuthToken(mPreferences.getString("AuthToken", ""));
        getClients.execute(url);
    }

    private class GetClients extends UrlJsonAsyncTask {
        public GetClients(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject json = new JSONObject();
            StringBuffer response = new StringBuffer();
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X-User-Email", mPreferences.getString("UserEmail", ""));
                connection.setRequestProperty("X-User-Token", mPreferences.getString("AuthToken", ""));
                connection.setUseCaches(false);

                //DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

                json.put("success", false);
                json.put("info", "Something went wrong. Retry!");
                // add the user email and password to
                // the params

                //wr.writeBytes(holder.toString());

                //wr.flush();
                //wr.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (connection.getInputStream())));

                String output = "";
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                json = new JSONObject(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONArray jsonClients = json.getJSONObject("data").getJSONArray("clients");
                int length = 0;
                length = jsonClients.length();
                clients.clear();
                for (int i = 0; i < length; i++) {
                    String first, last;
                    Integer id;
                    JSONObject jsonClient = jsonClients.getJSONObject(i).getJSONObject("client");
                    first = jsonClient.getString("first");
                    last = jsonClient.getString("last");
                    id = jsonClient.getInt("id");
                    Client client = new Client(first, last, id, "", "");
                    clients.add(client);
                }
                JSONArray jsonAppointments = json.getJSONObject("data").getJSONArray("appointments");
                length = jsonAppointments.length();

                for (int i = 0; i < length; i++) {
                    String note, beginning, end;
                    String first = "";
                    String last = "";
                    Integer client_id;
                    Integer id;
                    Double duration = 0.0;
                    JSONObject jsonAppointment = jsonAppointments.getJSONObject(i).getJSONObject("appointment");
                    note = jsonAppointment.getString("note");
                    beginning = jsonAppointment.getString("beginning");
                    end = jsonAppointment.getString("end");
                    client_id = jsonAppointment.getInt("client_id");
                    id = jsonAppointment.getInt("id");
                    duration = jsonAppointment.getDouble("hours");

                    if (!appointment_ids.contains(id.toString())){
                        Appointment appointment = new Appointment(id, client_id, note, beginning, end, "", "", duration, first + " " + last, "");
                        long startMillis = 0;
                        long endMillis = 0;
                        Calendar beginTime = Calendar.getInstance();
                        beginTime.set(appointment.getYear(beginning), appointment.getMonth(beginning) - 1, appointment.getDay(beginning), appointment.getHour(beginning), appointment.getMinute(beginning));
                        startMillis = beginTime.getTimeInMillis();
                        Calendar endTime = Calendar.getInstance();
                        endTime.set(appointment.getYear(end), appointment.getMonth(end) - 1, appointment.getDay(end), appointment.getHour(end), appointment.getMinute(end));
                        endMillis = endTime.getTimeInMillis();

                        ContentValues values = new ContentValues();
                        ContentResolver mContentResolver = getApplicationContext().getContentResolver();
                        values.put(CalendarContract.Events.CALENDAR_ID, 1);
                        values.put(CalendarContract.Events.ORIGINAL_ID, id);
                        values.put(CalendarContract.Events.TITLE, "CIN Appointment");
                        values.put(CalendarContract.Events.DESCRIPTION, appointment.getClientName());
                        //values.put(CalendarContract.Events.EVENT_LOCATION, pLocation);
                        values.put(CalendarContract.Events.DTSTART, startMillis);
                        values.put(CalendarContract.Events.DTEND, endMillis);
                        values.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true
                        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName()); //get the Timezone
                        Uri uri = mContentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
                        long eventID = Long.parseLong(uri.getLastPathSegment());
                        ContentValues reminder1 = new ContentValues();
                        reminder1.put(CalendarContract.Reminders.EVENT_ID, eventID);
                        reminder1.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                        reminder1.put(CalendarContract.Reminders.MINUTES, 15);
                        Uri uri1 = mContentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminder1);
                        ContentValues reminder2 = new ContentValues();
                        reminder2.put(CalendarContract.Reminders.EVENT_ID, eventID);
                        reminder2.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                        reminder2.put(CalendarContract.Reminders.MINUTES, 0);
                        Uri uri2 = mContentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminder2);
                        appointment_ids.add(String.valueOf(id));
                        appointments.add(appointment);
                    }
                }
                JSONArray a = new JSONArray();
                for (int i = 0; i < appointment_ids.size(); i++) {
                    a.put(appointment_ids.get(i));
                }
                if (!appointment_ids.isEmpty()) {
                    editor.putString("Appointment_ids", a.toString());
                } else {
                    editor.putString("Appointment_ids", null);
                }
                editor.commit();

            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(),
                        Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
            setUp();
        }
    }

    public void setUp(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("My Clients");
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.clientList);


        mNavigationAdapter = new NavigationAdapter(HomeActivity.this, getData());
        recyclerView.setAdapter(mNavigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(HomeActivity.this, recyclerView, new HomeActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(HomeActivity.this, ShowClientActivity.class);
                intent.putExtra("ClientID", clients.get(position).getID());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void logoutFromAPI(String url) {
        Logout logout = new Logout(HomeActivity.this);
        logout.setMessageLoading("Logging out...");
        logout.execute(url);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private class Logout extends UrlJsonAsyncTask {
        public Logout(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject json = new JSONObject();

            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X-User-Email", mPreferences.getString("UserEmail", ""));
                connection.setRequestProperty("X-User-Token", mPreferences.getString("AuthToken", ""));
                connection.setUseCaches(false);

                json.put("success", false);
                json.put("info", "Something went wrong. Retry!");

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (connection.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    json = new JSONObject(output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
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

                    Intent intent = new Intent(HomeActivity.this,
                            LoginActivity.class);
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

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){

            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child!= null && clickListener != null){
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }
    }

    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

}
