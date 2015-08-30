package zbloom.cin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import zbloom.cin.libs.UrlJsonAsyncTask;
import zbloom.cin.models.API;

public class NewClientActivity extends Activity {

    private SharedPreferences mPreferences;
    private String mclientFirst;
    private String mclientLast;
    private String mclientAddress;
    private Integer clientID = 0;
    private Boolean isUpdate = false;
    private API api = new API();
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    private File clientPhoto;
    String path;
    File file;
    Bitmap bitmap;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        final Bundle extras = getIntent().getExtras();
        clientID = extras.getInt("ClientID");
        isUpdate = extras.getBoolean("isUpdate");

        EditText clientFirstField = (EditText) findViewById(R.id.clientFirst);
        EditText clientLastField = (EditText) findViewById(R.id.clientLast);
        EditText clientAddressField = (EditText) findViewById(R.id.clientAddress);

        if (isUpdate){
            mclientFirst = extras.getString("mclientFirst");
            mclientLast = extras.getString("mclientLast");
            mclientAddress = extras.getString("mclientAddress");
            clientFirstField.setText(mclientFirst, TextView.BufferType.EDITABLE);
            clientLastField.setText(mclientLast, TextView.BufferType.EDITABLE);
            clientAddressField.setText(mclientAddress, TextView.BufferType.EDITABLE);
        }

        mclientFirst =  clientFirstField.getText().toString();
        mclientLast =  clientLastField.getText().toString();
        mclientAddress =  clientAddressField.getText().toString();
    }

    public void onTakePicture(View view){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                ImageView iv = (ImageView) findViewById(R.id.ReturnedImageView);

                // Decode it for real 
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = false;

                //imageFilePath image path which you pass with intent 
                Bitmap bmp = BitmapFactory.decodeFile(fileUri.getPath(), bmpFactoryOptions);
                path = Environment.getExternalStorageDirectory() + "/client.png";
                file = new File(path);
                file.delete();

                try {
                    ExifInterface exif = new ExifInterface(fileUri.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    }
                    else if (orientation == 3) {
                        matrix.postRotate(180);
                    }
                    else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true); // rotating bitmap
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos = new FileOutputStream(file);
                    // compress to specified format (PNG), quality - which is
                    // ignored for PNG, and out stream
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                }
                catch (Exception e) {

                }
                // Display it
                iv.setImageBitmap(bmp);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    public void onSaveClient(View button) {

        EditText clientFirstField = (EditText) findViewById(R.id.clientFirst);
        EditText clientLastField = (EditText) findViewById(R.id.clientLast);
        EditText clientAddressField = (EditText) findViewById(R.id.clientAddress);

        mclientFirst =  clientFirstField.getText().toString();
        mclientLast =  clientLastField.getText().toString();
        mclientAddress =  clientAddressField.getText().toString();

        if (mclientFirst.length() == 0 || mclientLast.length() == 0 || mclientAddress.length() == 0) {
            // input fields are empty
            Toast.makeText(this,
                    "Please fill out first and last name of new client",
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            // everything is ok!
            CreateClient createClient = new CreateClient(NewClientActivity.this);
            createClient.setMessageLoading("Creating new client...");
            if (isUpdate == true) {
                api.setClient_id(clientID);
                api.setUPDATE_CLIENT_URL();
                createClient.execute(api.getUPDATE_CLIENT_URL());
            } else {
                createClient.execute(api.getCREATE_CLIENT_ENDPOINT_URL());
            }
        }
    }

    private class CreateClient extends UrlJsonAsyncTask {
        public CreateClient(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            //DefaultHttpClient client = new DefaultHttpClient();
            JSONObject holder = new JSONObject();
            JSONObject taskObj = new JSONObject();
            JSONObject data = new JSONObject();
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
                taskObj.put("first", mclientFirst);
                taskObj.put("last", mclientLast);
                taskObj.put("address", mclientAddress);
                //clientPhoto = new File(fileUri.getPath());
                if (file != null) {
                    data.put("data", Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT));
                    data.put("filename", fileUri.getPath());
                    data.put("content_type", "image/png");
                    taskObj.put("image", data);
                }
                //data.put("filename", fileUri.getPath());
                //data.put("content_type", "image/png");
                //taskObj.put("image", data);
                holder.put("client", taskObj);

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
                    taskObj.put("first", mclientFirst);
                    taskObj.put("last", mclientLast);
                    taskObj.put("address", mclientAddress);
                    //clientPhoto = new File(fileUri.getPath());
                    data.put("data", Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT));
                    data.put("filename", fileUri.getPath());
                    data.put("content_type", "image/png");
                    taskObj.put("image", data);
                    holder.put("client", taskObj);
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
        getMenuInflater().inflate(R.menu.menu_new_client, menu);
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
