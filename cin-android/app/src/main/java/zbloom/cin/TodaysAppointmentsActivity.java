package zbloom.cin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import zbloom.cin.adapters.NavigationAdapter;
import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.API;
import zbloom.cin.models.Appointment;
import zbloom.cin.models.Client;
import zbloom.cin.models.Navigation;

public class TodaysAppointmentsActivity extends ActionBarActivity {

    Integer clientID = 0;
    Integer appointmentID = null;
    private SharedPreferences mPreferences;
    private static final ArrayList<Appointment> appointments = new ArrayList<Appointment>();
    private static final ArrayList<Client> clients = new ArrayList<Client>();
    private API api = new API();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private NavigationAdapter mNavigationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_client);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPreferences.contains("AuthToken")) {
            appointments.clear();
            loadAppointmentsFromAPI(api.getCLIENTS_URL());
        } else {
            Intent intent = new Intent(TodaysAppointmentsActivity.this, LoginActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    public static List getData(){
        List<Navigation> navigation = new ArrayList<>();
        for (int i = 0; i < appointments.size(); i++){
            Navigation current = new Navigation();
            current.setIconID(R.drawable.ic_action_event);
            String name = "";
            for (int j = 0; j < clients.size(); j++){
                if (appointments.get(i).getClient_id() == clients.get(j).getID()){
                    name = clients.get(j).getName();
                }
            }
            String beginning = appointments.get(i).getBeginning();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            GregorianCalendar gCal = new GregorianCalendar(appointments.get(i).getYear(beginning), appointments.get(i).getMonth(beginning) - 1, appointments.get(i).getDay(beginning), appointments.get(i).getHour(beginning), appointments.get(i).getMinute(beginning));
            //Date date = new Date(appointments.get(i).getYear(beginning), appointments.get(i).getMonth(beginning), appointments.get(i).getDay(beginning), appointments.get(i).getHour(beginning), appointments.get(i).getMinute(beginning));
            sdf.setCalendar(gCal);
            String time = sdf.format(gCal.getTime());
            current.setTitle(name + ": " + time);
            navigation.add(current);
        }
        return navigation;
    }



    public void onClickBack(View view)
    {
        Intent intent = new Intent(this, CIN.class);
        startActivity(intent);
    }

    private void deleteClientFromAPI(String url) {
        DeleteClient deleteClient = new DeleteClient(TodaysAppointmentsActivity.this);
        deleteClient.setMessageLoading("Deleting client...");
        deleteClient.setAuthToken(mPreferences.getString("AuthToken", ""));
        deleteClient.execute(url);
    }

    public void onClickNewAppointment(View view) throws InterruptedException {
        Intent intent = new Intent(this, NewAppointmentActivity.class);
        intent.putExtra("ClientID", clientID);
        startActivity(intent);
        /*
        CreateAppointment createAppointment = new CreateAppointment(ShowClientActivity.this);
        createAppointment.setMessageLoading("Creating new appointment...");
        api.setClient_id(clientID);
        api.setCREATE_APPOINTMENT_URL();
        createAppointment.execute(api.getCREATE_APPOINTMENT_URL());
        */
    }


    private class DeleteClient extends UrlJsonAsyncTask {
        public DeleteClient(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpDelete delete = new HttpDelete(urls[0]);
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
            Intent intent = new Intent(TodaysAppointmentsActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CIN.class);
        startActivity(intent);
    }

    private void loadAppointmentsFromAPI(String url) {
        GetAppointments getAppointments = new GetAppointments(TodaysAppointmentsActivity.this);
        getAppointments.setMessageLoading("Loading client...");
        getAppointments.setAuthToken(mPreferences.getString("AuthToken", ""));
        getAppointments.execute(url);
    }

    private class GetAppointments extends UrlJsonAsyncTask {
        public GetAppointments(Context context) {
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
            Integer length;
            String beginning = "";
            String end = "";
            String first = "";
            String last = "";
            String time = "";
            String note = "";
            String date = "";
            String month = "";
            String day = "";
            String year = "";
            String hour = "";
            String minute = "";
            String dateTime[];
            Integer id;
            Integer client_id;
            Integer numClients;
            try {
                JSONArray jsonAppointments = json.getJSONObject("data").getJSONArray("appointments");
                JSONArray jsonClients = json.getJSONObject("data").getJSONArray("clients");
                length = jsonAppointments.length();
                numClients = jsonClients.length();
                for (int i = 0; i < numClients; i++){
                    JSONObject jsonClient = jsonClients.getJSONObject(i).getJSONObject("client");
                    first = jsonClient.getString("first");
                    last = jsonClient.getString("last");
                    id = jsonClient.getInt("id");
                    Client client = new Client(first, last, id, "", "");
                    clients.add(client);
                }
                for (int i = 0; i < length; i++) {
                    JSONObject jsonAppointment = jsonAppointments.getJSONObject(i).getJSONObject("appointment");
                    beginning = jsonAppointment.getString("beginning");
                    end = jsonAppointment.getString("end");
                    note = jsonAppointment.getString("note");
                    id = jsonAppointment.getInt("id");
                    client_id = jsonAppointment.getInt("client_id");
                    dateTime = beginning.split("T");
                    date = dateTime[0];
                    time = dateTime[1];
                    Date Today = Calendar.getInstance().getTime();
                    String yearToday = new SimpleDateFormat("yyyy").format(Today);
                    String monthToday = new SimpleDateFormat("MM").format(Today);
                    String dayToday = new SimpleDateFormat("dd").format(Today);
                    dateTime = date.split("-");
                    month = dateTime[1];
                    day = dateTime[2];
                    year = dateTime[0];
                    dateTime = time.split(":");
                    hour = dateTime[0];
                    minute = dateTime[1];
                    if (year.equals(yearToday) && month.equals(monthToday) && day.equals(dayToday)) {
                        Appointment appointment = new Appointment(id, client_id, note, beginning, end, "", "", 0.0, "", "");
                        appointments.add(appointment);
                    }
                }
            }

            catch (JSONException e1) {
                e1.printStackTrace();
            }
            super.onPostExecute(json);
            setUp();
        }
    }

    private class CreateAppointment extends UrlJsonAsyncTask {
        public CreateAppointment(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urls[0]);
            JSONObject holder = new JSONObject();
            JSONObject taskObj = new JSONObject();
            String response = null;
            JSONObject json = new JSONObject();

            try {
                try {
                    json.put("success", false);
                    json.put("info", "Something went wrong. Retry!");
                    holder.put("appointment", taskObj);
                    taskObj.put("note", "");
                    StringEntity se = new StringEntity(holder.toString());
                    post.setEntity(se);
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("X-User-Email", mPreferences.getString("UserEmail", ""));
                    post.setHeader("X-User-Token", mPreferences.getString("AuthToken", ""));

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    response = client.execute(post, responseHandler);
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
                    JSONObject appointment = json.getJSONObject("data").getJSONObject("client").getJSONObject("appointment");
                    appointmentID = appointment.getInt("id");
                    Intent intent = new Intent(TodaysAppointmentsActivity.this, NewLocationActivity.class);
                    intent.putExtra("AppointmentID", appointmentID);
                    intent.putExtra("ClientID", clientID);
                    startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_client, menu);
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
        Logout logout = new Logout(TodaysAppointmentsActivity.this);
        logout.setMessageLoading("Logging out...");
        logout.execute(url);
    }

    private class Logout extends UrlJsonAsyncTask {
        public Logout(Context context) {
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
                if (json.getBoolean("success")) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.remove("AuthToken");
                    editor.commit();

                    Intent intent = new Intent(TodaysAppointmentsActivity.this,
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

    public void setUp(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(" Today's Appointments");
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.appointmentList);

        ButtonFloat buttonFloat1 = (ButtonFloat) findViewById(R.id.fab);
        buttonFloat1.hide();
        ButtonFloat buttonFloat2 = (ButtonFloat) findViewById(R.id.fab3);
        buttonFloat2.hide();

        mNavigationAdapter = new NavigationAdapter(TodaysAppointmentsActivity.this, getData());
        recyclerView.setAdapter(mNavigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(TodaysAppointmentsActivity.this));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(TodaysAppointmentsActivity.this, recyclerView, new TodaysAppointmentsActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(TodaysAppointmentsActivity.this, ShowAppointmentActivity.class);
                intent.putExtra("ClientID", appointments.get(position).getClient_id());
                intent.putExtra("AppointmentID", appointments.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
