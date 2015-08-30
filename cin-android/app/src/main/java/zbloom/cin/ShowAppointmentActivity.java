package zbloom.cin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
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
import java.util.GregorianCalendar;
import java.util.List;

import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.Navigation;
import zbloom.cin.adapters.NavigationAdapter;
import zbloom.cin.models.Appointment;
import zbloom.cin.models.API;


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
        toolbar.setTitle("Appointment");
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
            Intent intent = new Intent(ShowAppointmentActivity.this, LoginActivity.class);
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

    public void onClickForward(View view)
    {
        Intent intent = new Intent(ShowAppointmentActivity.this, NewLocationActivity.class);
        intent.putExtra("AppointmentID", appointmentID);
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
            /*
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
            */
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            String clientFirst = "";
            String clientLast = "";
            String beginning = "";
            Double duration= 0.0;
            String end = "";
            String clockIn = "";
            String clockOut = "";
            String note = "";
            String date = "";
            String month = "";
            String day = "";
            String year = "";
            String time = "";
            String hour = "";
            String minute = "";
            String image = "";
            int id = 0;
            //int client_id = 0;
            String dateTime[];
            try {
                JSONObject jsonAppointment = json.getJSONObject("data").getJSONObject("client");
                clientFirst = jsonAppointment.getString("first");
                clientLast = jsonAppointment.getString("last");
                image = jsonAppointment.getString("data");
                jsonAppointment = jsonAppointment.getJSONObject("appointment");
                beginning = jsonAppointment.getString("beginning");
                end = jsonAppointment.getString("end");
                clockIn = jsonAppointment.getString("clockIn");
                clockOut = jsonAppointment.getString("clockOut");
                note = jsonAppointment.getString("note");
                id = jsonAppointment.getInt("id");
                //client_id = jsonAppointment.getInt("client_id");
                duration = jsonAppointment.getDouble("hours");
                dateTime = beginning.split("T");
                date = dateTime[0];
                time = dateTime[1];
                dateTime = date.split("-");
                month = dateTime[1];
                day = dateTime[2];
                year = dateTime[0];
                dateTime = time.split(":");
                hour = dateTime[0];
                minute = dateTime[1];
                appointment = new Appointment(id, 0, note, beginning, end, clockIn, clockOut, duration/3600, clientFirst + " " + clientLast, image);
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

                    Intent intent = new Intent(ShowAppointmentActivity.this,
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

    public void setUp() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Appointment");
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.appointmentList);

        ImageView iv = (ImageView) findViewById(R.id.ReturnedImageView);

        byte[] decodedImage = Base64.decode(appointment.getImage(), Base64.DEFAULT);

        Bitmap bmp = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);

        iv.setImageBitmap(bmp);

        if (appointment.getDuration() != 0){
            ButtonFloat buttonFloat2 = (ButtonFloat) findViewById(R.id.fab2);
            ButtonFloat buttonFloat3 = (ButtonFloat) findViewById(R.id.fab3);
            ButtonFloat buttonFloat4 = (ButtonFloat) findViewById(R.id.fab4);
            buttonFloat2.setVisibility(View.GONE);
            buttonFloat3.setVisibility(View.GONE);
            buttonFloat4.setVisibility(View.GONE);
        }

        mNavigationAdapter = new NavigationAdapter(ShowAppointmentActivity.this, getData());
        recyclerView.setAdapter(mNavigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowAppointmentActivity.this));
    }

    public static List getData(){
        int[] icons = {R.drawable.ic_action_person, R.drawable.ic_action_event, R.drawable.ic_action_event, R.drawable.ic_action_time, R.drawable.ic_action_about};
        String beginning;
        String end;
        if (appointment.getDuration() == 0) {
            beginning = appointment.getBeginning();
            end = appointment.getEnd();
        }
        else{
            beginning = appointment.getClockIn();
            end = appointment.getClockOut();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, y hh:mm aa");
        //Date date = new Date(appointments.get(i).getYear(beginning), appointments.get(i).getMonth(beginning), appointments.get(i).getDay(beginning), appointments.get(i).getHour(beginning), appointments.get(i).getMinute(beginning));
        GregorianCalendar gCal = new GregorianCalendar(appointment.getYear(beginning), appointment.getMonth(beginning) - 1, appointment.getDay(beginning), appointment.getHour(beginning), appointment.getMinute(beginning));
        sdf.setCalendar(gCal);
        beginning = sdf.format(gCal.getTime());
        gCal = new GregorianCalendar(appointment.getYear(end), appointment.getMonth(end) - 1, appointment.getDay(end), appointment.getHour(end), appointment.getMinute(end));
        sdf.setCalendar(gCal);
        end = sdf.format(gCal.getTime());
        String[] titles = {appointment.getClientName(), beginning, end, String.format("%.3f", appointment.getDuration())+ " hours", appointment.getNote()};
        List<Navigation> navigation = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            Navigation current = new Navigation();
            current.setIconID(icons[i]);
            current.setTitle(titles[i]);
            navigation.add(current);
        }
        return navigation;
    }

    public void onClickEdit(View view){
        Intent intent = new Intent(ShowAppointmentActivity.this, NewAppointmentActivity.class);
        intent.putExtra("AppointmentID", appointmentID);
        intent.putExtra("isUpdate", true);
        intent.putExtra("beginning", appointment.getBeginning());
        intent.putExtra("end", appointment.getEnd());
        intent.putExtra("ClientID", clientID);
        startActivity(intent);
    }

    public void onClickDelete(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Really Delete?")
                .setMessage("Are you sure you want to delete?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteAppointment deleteAppointment = new DeleteAppointment(ShowAppointmentActivity.this);
                        deleteAppointment.setMessageLoading("Deleting appointment...");
                        deleteAppointment.setAuthToken(mPreferences.getString("AuthToken", ""));
                        api.setClient_id(clientID);
                        api.setAppointment_id(appointmentID);
                        api.setDELETE_APPOINTMENT_URL();
                        deleteAppointment.execute(api.getDELETE_APPOINTMENT_URL());
                    }
                }).create().show();
    }

    private class DeleteAppointment extends UrlJsonAsyncTask {
        public DeleteAppointment(Context context) {
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
                    Intent intent = new Intent(ShowAppointmentActivity.this,
                            HomeActivity.class);
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
}
