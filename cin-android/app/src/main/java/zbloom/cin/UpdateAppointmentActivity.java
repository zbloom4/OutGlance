package zbloom.cin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import zbloom.cin.adapters.NavigationAdapter;
import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.API;
import zbloom.cin.models.Appointment;
import zbloom.cin.models.Navigation;


public class UpdateAppointmentActivity extends ActionBarActivity {

    private SharedPreferences mPreferences;
    private Editable mNote;
    private File file;
    private String path;
    int clientID = 0;
    int appointmentID = 0;
    private API api = new API();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private NavigationAdapter mNavigationAdapter;
    private String[] goals;
    private String tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_appointment);

        final Bundle extras = getIntent().getExtras();
        clientID = extras.getInt("ClientID");
        appointmentID = extras.getInt("AppointmentID");
        path = Environment.getExternalStorageDirectory() + "/signature.png";
        file = new File(path);

        final EditText noteField = (EditText) findViewById(R.id.appointmentNote);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        goals = getResources().getStringArray(R.array.goals_array);

        setUp();
    }

    public void updateAppointment(View button) {
        new AlertDialog.Builder(this)
                .setTitle("Really check out?")
                .setMessage("Are you sure you want to check out?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        EditText noteField = (EditText) findViewById(R.id.appointmentNote);
                        mNote =  noteField.getText();

                        Log.d("note", mNote.toString());

                        if (mNote.length() == 0) {
                            // input fields are empty
                            Toast.makeText(UpdateAppointmentActivity.this,
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
                }).create().show();
        /*
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
        */
    }

    private class UpdateAppointment extends UrlJsonAsyncTask {
        public UpdateAppointment(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject holder = new JSONObject();
            JSONObject taskObj = new JSONObject();
            JSONObject data = new JSONObject();
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
                taskObj.put("note", mNote.toString());
                data.put("data", Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT));
                data.put("filename", path);
                data.put("content_type", "image/png");
                taskObj.put("image", data);
                holder.put("appointment", taskObj);

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
            JSONObject data = new JSONObject();
            String response = null;
            JSONObject json = new JSONObject();

            try {
                try {
                    json.put("success", false);
                    json.put("info", "Something went wrong. Retry!");
                    taskObj.put("note", mNote.toString());
                    data.put("data", Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT));
                    data.put("filename", path);
                    data.put("content_type", "image/png");
                    taskObj.put("image", data);
                    holder.put("appointment", taskObj);
                    StringEntity se = new StringEntity(holder.toString());
                    post.setEntity(se);
                    /*
                    //MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                    //multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    //FileBody fb = new FileBody(file, "image/png");
                    //multipartEntityBuilder.addPart("appointment[image]", fb);
                    //multipartEntityBuilder.addTextBody("note", mNote.toString());
                    //final HttpEntity myEntity = multipartEntityBuilder.build();
                    //post.setEntity(myEntity);
                    JSONObject pictureData = new JSONObject();
                    pictureData.put("content_type", "jpg");
                    String[] fileName = path.split("/");
                    String encodedImage = Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT);
                    pictureData.put("original_filename", "base64:"+fileName[fileName.length-1]);
                    pictureData.put("filename", fileName[fileName.length-1]);
                    pictureData.put("picture_path", encodedImage);
                    */
                    /*
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("Data-type", "json");
                    post.setHeader("X-User-Email", mPreferences.getString("UserEmail", ""));
                    post.setHeader("X-User-Token", mPreferences.getString("AuthToken", ""));
                    //post.setEntity(new StringEntity(pictureData.toString()));
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

    public static List getData(String[] goals){
        List<Navigation> navigation = new ArrayList<>();
        for (int i = 0; i < goals.length; i++){
            Navigation current = new Navigation();
            current.setIconID(R.drawable.ic_add_black_24dp);
            current.setTitle(goals[i]);
            navigation.add(current);
        }
        return navigation;
    }

    public void setUp(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Daily Note");
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.goalList);


        mNavigationAdapter = new NavigationAdapter(UpdateAppointmentActivity.this, getData(goals));
        recyclerView.setAdapter(mNavigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(UpdateAppointmentActivity.this));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(UpdateAppointmentActivity.this, recyclerView, new UpdateAppointmentActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final EditText noteField = (EditText) findViewById(R.id.appointmentNote);
                if (noteField.length() == 0) {
                    tasks = noteField.getText() + goals[position] + ": ";
                }
                else{
                    tasks = noteField.getText() + "\n" + goals[position] + ": ";
                }
                noteField.setText(tasks);
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
