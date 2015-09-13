package zbloom.cin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;


public class NewSignatureActivity extends ActionBarActivity {

    GestureOverlayView gestureView;
    String path;
    File file;
    Bitmap bitmap;
    public boolean gestureTouch = false;
    Integer appointmentID = 0;
    Integer clientID = 0;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_signature);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        final Bundle extras = getIntent().getExtras();
        clientID = extras.getInt("ClientID");
        appointmentID = extras.getInt("AppointmentID");

        ButtonRectangle doneButton = (ButtonRectangle) findViewById(R.id.done_button);
        doneButton.setText("Done");
        ButtonRectangle clearButton = (ButtonRectangle) findViewById(R.id.clear_button);
        clearButton.setText("Clear");

        path = Environment.getExternalStorageDirectory() + "/signature.png";
        file = new File(path);
        file.delete();
        gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
        gestureView.setDrawingCacheEnabled(true);

        gestureView.setAlwaysDrawnWithCacheEnabled(true);
        gestureView.setHapticFeedbackEnabled(false);
        gestureView.cancelLongPress();
        gestureView.cancelClearAnimation();
        gestureView.addOnGestureListener(new GestureOverlayView.OnGestureListener() {

            @Override
            public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureCancelled(GestureOverlayView arg0,
                                           MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureStarted(GestureOverlayView arg0,
                                         MotionEvent arg1) {
                // TODO Auto-generated method stub
                if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    gestureTouch = false;
                } else {
                    gestureTouch = true;
                }
            }
        });
    }
    public void OnClickDoneButton(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try
                        {
                            bitmap = Bitmap.createBitmap(gestureView.getDrawingCache());
                            file.createNewFile();
                            FileOutputStream fos = new FileOutputStream(file);
                            fos = new FileOutputStream(file);
                            // compress to specified format (PNG), quality - which is
                            // ignored for PNG, and out stream
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        if (gestureTouch == false)

                        {
                            //setResult(0);
                            //finish();
                            Intent intent = new Intent(NewSignatureActivity.this, UpdateAppointmentActivity.class);
                            intent.putExtra("ClientID", clientID);
                            intent.putExtra("AppointmentID", appointmentID);
                            intent.putExtra("noInternet", false);
                            startActivity(intent);
                        } else

                        {
                            //setResult(1);
                            //finish();
                            Intent intent = new Intent(NewSignatureActivity.this, UpdateAppointmentActivity.class);
                            intent.putExtra("ClientID", clientID);
                            intent.putExtra("AppointmentID", appointmentID);
                            intent.putExtra("noInternet", false);
                            startActivity(intent);
                        }
                    }
                }).create().show();
    }
    public void OnClickClearButton(View view) {
        gestureView.invalidate();
        gestureView.clear(true);
        gestureView.clearAnimation();
        gestureView.cancelClearAnimation();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        OnClickDoneButton(findViewById(R.id.done_button));
                    }
                }).create().show();
    }

}