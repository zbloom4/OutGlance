package zbloom.cin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zbloom.cin.adapters.NavigationAdapter;
import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.API;

import zbloom.cin.models.Navigation;
import zbloom.cin.models.User;


public class CIN extends ActionBarActivity {

    private static User user;
    private SharedPreferences mPreferences;
    private API api = new API();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private NavigationAdapter mNavigationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_appbar);
        setTitle("CIN");

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

        if (mPreferences.contains("AuthToken"))
        {
            loadUserFromAPI(api.getSHOW_USER_URL());
        }
        else {
            Intent intent = new Intent(CIN.this, WelcomeActivity.class);
            startActivityForResult(intent, 0);
        }
    }


    private void loadUserFromAPI(String url) {
        ShowUser showUser = new ShowUser(CIN.this);
        showUser.setMessageLoading("Loading user...");
        showUser.setAuthToken(mPreferences.getString("AuthToken", ""));
        showUser.execute(url);
    }
    private class ShowUser extends UrlJsonAsyncTask {
        public ShowUser(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient user = new DefaultHttpClient();
            HttpGet get = new HttpGet(urls[0]);
            String response = null;
            JSONObject check = new JSONObject();
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
                    response = user.execute(get, responseHandler);
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
            String first = "";
            String last = "";
            Double hours = 0.0;
            Integer id = 0;
            String email = "";
            JSONObject check = new JSONObject();
            try {
                check = json.getJSONObject("data");
                JSONObject jsonUser = json.getJSONObject("data").getJSONObject("user");
                id = jsonUser.getInt("id");
                first = jsonUser.getString("first");
                last = jsonUser.getString("last");
                hours = jsonUser.getDouble("hours");
                email = jsonUser.getString("email");
                user = new User(first, last, email, id, hours);
            }
            catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (check.length() == 0){
                Intent intent = new Intent(CIN.this, WelcomeActivity.class);
                startActivityForResult(intent, 0);
            }
            else{
                setUp();
            }

            super.onPostExecute(json);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
        Logout logout = new Logout(CIN.this);
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

                    Intent intent = new Intent(CIN.this,
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

    public void onClickForward(View view)
    {
        Intent intent = new Intent(CIN.this, HomeActivity.class);
        startActivityForResult(intent, 0);
    }

    public static List getData(){
        int[] icons = {R.drawable.ic_action_about, R.drawable.ic_action_person, R.drawable.ic_action_email, R.drawable.ic_action_time};
        String[] titles = {user.getID()+"", user.getName(), user.getEmail(), String.format("%.3f", user.getHours()) + " hours"};
        List<Navigation> navigation = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            Navigation current = new Navigation();
            current.setIconID(icons[i]);
            current.setTitle(titles[i]);
            navigation.add(current);
        }
        return navigation;
    }

    public void setUp(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("My Profile");
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.userList);


        mNavigationAdapter = new NavigationAdapter(CIN.this, getData());
        recyclerView.setAdapter(mNavigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CIN.this));
    }
}

