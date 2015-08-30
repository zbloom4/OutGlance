package zbloom.cin;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by bloom on 6/1/15.
 */

public class JobSchedulerService extends JobService {

    private Handler mJobHandler = new Handler( new Handler.Callback() {
        @Override
        public boolean handleMessage( Message msg ) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_action_event)
                            .setContentTitle("My notification")
                            .setContentText("Hello World!");
            Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            //Toast.makeText(getApplicationContext(), "JobService task running", Toast.LENGTH_SHORT).show();
            mBuilder.setContentIntent(resultPendingIntent);
            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
            jobFinished( (JobParameters) msg.obj, false );
            return true;
        }
    } );

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mJobHandler.sendMessage( Message.obtain( mJobHandler, 1, jobParameters ) );
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mJobHandler.removeMessages( 1 );
        return false;
    }
}
