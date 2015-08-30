package zbloom.cin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import zbloom.cin.adapters.NavigationAdapter;
import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.API;
import zbloom.cin.models.Appointment;
import zbloom.cin.models.Client;
import zbloom.cin.models.Navigation;
import zbloom.cin.models.User;

public class ShowClientProfileActivity extends ActionBarActivity {

    Integer clientID = 0;
    private static Client client;
    Integer appointmentID = null;
    private SharedPreferences mPreferences;
    private API api = new API();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private NavigationAdapter mNavigationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_client_profile);

        final Bundle extras = getIntent().getExtras();
        clientID = extras.getInt("ClientID");
        api.setClient_id(clientID);
        Log.d("client_id", api.getClient_id().toString());
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Client Profile");
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
            api.setSHOW_CLIENT_PROFILE_URL();
            loadClientProfile(api.getSHOW_CLIENT_PROFILE_URL());
        } else {
            Intent intent = new Intent(ShowClientProfileActivity.this, LoginActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    private void loadClientProfile(String url) {
        GetClientProfile getClientProfile = new GetClientProfile(ShowClientProfileActivity.this);
        getClientProfile.setMessageLoading("Loading client...");
        getClientProfile.setAuthToken(mPreferences.getString("AuthToken", ""));
        getClientProfile.execute(url);
    }


    private class GetClientProfile extends UrlJsonAsyncTask {
        public GetClientProfile(Context context) {
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
            String first = "";
            String last = "";
            String address = "";
            Integer id = 0;
            String image = "";
            try {
                JSONObject jsonClient = json.getJSONObject("data").getJSONObject("client");
                id = jsonClient.getInt("id");
                first = jsonClient.getString("first");
                last = jsonClient.getString("last");
                address = jsonClient.getString("address");
                image = jsonClient.getString("data");
                client = new Client(first, last, id, address, image);
                setUp();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(json);
        }
    }

    private void logoutFromAPI(String url) {
        Logout logout = new Logout(ShowClientProfileActivity.this);
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

                    Intent intent = new Intent(ShowClientProfileActivity.this,
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


    public void onClickBack(View view)
    {

        Intent intent = new Intent(ShowClientProfileActivity.this, ShowClientActivity.class);
        intent.putExtra("ClientID", clientID);
        startActivityForResult(intent, 0);
    }

    public void onClickEdit(View view)
    {

        Intent intent = new Intent(ShowClientProfileActivity.this, NewClientActivity.class);
        intent.putExtra("ClientID", clientID);
        intent.putExtra("isUpdate", true);
        intent.putExtra("mclientFirst", client.getFirst());
        intent.putExtra("mclientLast", client.getLast());
        intent.putExtra("mclientAddress", client.getAddress());
        startActivityForResult(intent, 0);
    }

    public static List getData(){
        int[] icons = {R.drawable.ic_action_person, R.drawable.ic_home_black_24dp};
        String[] titles = {client.getName(), client.getAddress()};
        List<Navigation> navigation = new ArrayList<>();
        for (int i = 0; i < 2; i++){
            Navigation current = new Navigation();
            current.setIconID(icons[i]);
            current.setTitle(titles[i]);
            navigation.add(current);
        }
        return navigation;
    }

    public void setUp(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Client Profile");
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.clientList);

        ImageView iv = (ImageView) findViewById(R.id.ReturnedImageView);

        byte[] decodedImage = Base64.decode(client.getImage(), Base64.DEFAULT);

        Bitmap bmp = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);

        iv.setImageBitmap(bmp);

        mNavigationAdapter = new NavigationAdapter(ShowClientProfileActivity.this, getData());
        recyclerView.setAdapter(mNavigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowClientProfileActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_client_profile, menu);
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
}
