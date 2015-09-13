package zbloom.cin;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.API;
import zbloom.cin.models.Appointment;

import static com.wdullaer.materialdatetimepicker.time.TimePickerDialog.*;


public class NewAppointmentActivity extends ActionBarActivity implements OnTimeSetListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    private SharedPreferences mPreferences;
    private Integer clientID = 0;
    private Integer appointmentID = 0;
    private String date = "";
    private String beginning = "";
    private String end = "";
    private String beginningHour = "";
    private String beginningMinute = "";
    private String endHour = "";
    private String endMinute = "";
    private Boolean isEnd = false;
    private Integer mYear = 0;
    private Integer mMonth = 0;
    private Integer mDay = 0;
    EditText appointmentTime;
    private API api = new API();
    private Boolean isUpdate = false;
    EditText appointmentBeginning;
    EditText appointmentEnd;
    EditText appointmentDate;
    Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        final Bundle extras = getIntent().getExtras();
        isUpdate = extras.getBoolean("isUpdate");
        clientID = extras.getInt("ClientID");
        appointmentID = extras.getInt("AppointmentID");


        appointmentBeginning = (EditText) findViewById(R.id.appointmentTimeBeginning);
        appointmentEnd = (EditText) findViewById(R.id.appointmentTimeEnd);
        appointmentDate = (EditText) findViewById(R.id.appointmentDate);


        if (isUpdate){
            appointmentID = extras.getInt("AppointmentID");
            beginning = extras.getString("beginning");
            end = extras.getString("end");
            appointment = new Appointment(appointmentID, 0, "", beginning, end, "", "", 0, "", "");
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            GregorianCalendar gCal = new GregorianCalendar(appointment.getYear(beginning), appointment.getMonth(beginning) - 1, appointment.getDay(beginning), appointment.getHour(beginning), appointment.getMinute(beginning));
            sdf.setCalendar(gCal);
            String time = sdf.format(gCal.getTime());
            String prompt = "You picked the following time: " + time.toString();
            appointmentBeginning.setText(prompt, TextView.BufferType.EDITABLE);
            gCal = new GregorianCalendar(appointment.getYear(end), appointment.getMonth(end) - 1, appointment.getDay(end), appointment.getHour(end), appointment.getMinute(end));
            sdf.setCalendar(gCal);
            time = sdf.format(gCal.getTime());
            prompt = "You picked the following time: " + time.toString();
            appointmentEnd.setText(prompt, TextView.BufferType.EDITABLE);
            sdf = new SimpleDateFormat("MM/d/y");
            gCal = new GregorianCalendar(appointment.getYear(beginning), appointment.getMonth(beginning) - 1, appointment.getDay(beginning), appointment.getHour(beginning), appointment.getMinute(beginning));
            sdf.setCalendar(gCal);
            time = sdf.format(gCal.getTime());
            prompt = "You picked the following date: " + time.toString();
            appointmentDate.setText(prompt, TextView.BufferType.EDITABLE);
            beginningHour = appointment.getHour(beginning).toString();
            beginningMinute = appointment.getMinute(beginning).toString();
            endHour = appointment.getHour(end).toString();
            endMinute = appointment.getMinute(end).toString();
            date = appointment.getYear(beginning) + "-" + (appointment.getMonth(beginning)) + "-" + appointment.getDay(beginning);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newDate(View button) {
        Calendar now = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                NewAppointmentActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void newBeginningTime(View button) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                NewAppointmentActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.show(getFragmentManager(), "TimePickerdialog");
        appointmentTime = (EditText) findViewById(R.id.appointmentTimeBeginning);
        isEnd = false;
    }

    public void newEndTime(View button) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                NewAppointmentActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.show(getFragmentManager(), "TimePickerdialog");
        appointmentTime = (EditText) findViewById(R.id.appointmentTimeEnd);
        isEnd = true;
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        Date date = new Date(mYear, mMonth, mDay, hour, minute);
        String time = sdf.format(date);
        String prompt = "You picked the following time: " + time.toString();
        appointmentTime.setText(prompt);
        if (isEnd) {
            endHour = hour + "";
            endMinute = minute + "";
        }
        else {
            beginningHour = hour + "";
            beginningMinute = minute + "";
        }
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog, int year, int month, int day) {
        String prompt = "You picked the following date: "+(month+1)+"/"+day+"/"+year;
        date = year + "-" + (month+1) + "-" + day;
        EditText appointmentDate = (EditText) findViewById(R.id.appointmentDate);
        appointmentDate.setText(prompt);
        mYear = year;
        mMonth = month;
        mDay = day;
    }

    public void onClickNewAppointment(View view) throws InterruptedException {
        CreateAppointment createAppointment = new CreateAppointment(NewAppointmentActivity.this);
        createAppointment.setMessageLoading("Creating new appointment...");
        if (beginningHour.isEmpty() || beginningMinute.isEmpty() || endHour.isEmpty() || endMinute.isEmpty() || date.isEmpty()){
            Toast.makeText(this,
                    "Please enter date, beginning time and end time of appointment",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (isUpdate == true) {
            api.setClient_id(clientID);
            api.setAppointment_id(appointmentID);
            api.setUPDATE_APPOINTMENT_URL();
            createAppointment.execute(api.getUPDATE_APPOINTMENT_URL());
        }
        else {
            api.setClient_id(clientID);
            api.setCREATE_APPOINTMENT_URL();
            createAppointment.execute(api.getCREATE_APPOINTMENT_URL());
        }
    }

    private class CreateAppointment extends UrlJsonAsyncTask {
        public CreateAppointment(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            //DefaultHttpClient client = new DefaultHttpClient();
            JSONObject holder = new JSONObject();
            JSONObject taskObj = new JSONObject();
            JSONObject userObj = new JSONObject();
            String response = null;
            JSONObject json = new JSONObject();

            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X-User-Email", mPreferences.getString("UserEmail", ""));
                connection.setRequestProperty("X-User-Token", mPreferences.getString("AuthToken", ""));
                connection.setUseCaches(false);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

                json.put("success", false);
                json.put("info", "Something went wrong. Retry!");
                // add the user email and password to
                // the params
                holder.put("appointment", taskObj);
                String time = beginningHour + ":" + beginningMinute;
                taskObj.put("beginning", date + " " + time);
                time = endHour + ":" + endMinute;
                taskObj.put("end", date + " " + time);

                wr.writeBytes(holder.toString());

                wr.flush();
                wr.close();

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
            /*
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
                    String time = hours.get(0).toString() + ":" + minutes.get(0).toString();
                    taskObj.put("beginning", date + " " + time);
                    time = hours.get(1).toString() + ":" + minutes.get(1).toString();
                    taskObj.put("end", date + " " + time);
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
            */
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getBoolean("success")) {
                    //JSONObject appointment = json.getJSONObject("data").getJSONObject("client").getJSONObject("appointment");
                    //appointmentID = appointment.getInt("id");
                    Intent intent = new Intent(NewAppointmentActivity.this, ShowClientActivity.class);
                    //intent.putExtra("AppointmentID", appointmentID);
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
}
