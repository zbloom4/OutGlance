package zbloom.cin;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.API;


public class UpdateAppointmentActivity extends Activity {

    private SharedPreferences mPreferences;
    private Editable mNote;
    int clientID = 0;
    int appointmentID = 0;
    private API api = new API();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_appointment);

        final Bundle extras = getIntent().getExtras();
        clientID = extras.getInt("ClientID");
        appointmentID = extras.getInt("AppointmentID");

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }

    public void updateAppointment(View button) {
        EditText noteField = (EditText) findViewById(R.id.appointmentNote);
        mNote =  noteField.getText();

        Log.d("note", mNote.toString());

        if (mNote.length() == 0) {
            // input fields are empty
            Toast.makeText(this,
                    "Please fill out daily note for today's appointment",
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            // everything is ok!
            UpdateAppointment updateAppointment = new UpdateAppointment(UpdateAppointmentActivity.this);
            updateAppointment.setMessageLoading("Updating appointment...");
            api.setClient_id(clientID);
            api.setAppointment_id(appointmentID);
            api.setUPDATE_APPOINTMENT_URL();
            updateAppointment.execute(api.getUPDATE_APPOINTMENT_URL());
        }
    }

    private class UpdateAppointment extends UrlJsonAsyncTask {
        public UpdateAppointment(Context context) {
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
                    taskObj.put("note", mNote.toString());
                    holder.put("appointment", taskObj);
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
                    Intent intent = new Intent(getApplicationContext(),
                            HomeActivity.class);
                    startActivity(intent);
                    finish();
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
}
