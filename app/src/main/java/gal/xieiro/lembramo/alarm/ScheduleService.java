package gal.xieiro.lembramo.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.threeten.bp.Instant;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.ui.MainActivity;


public class ScheduleService extends WakefulIntentService {
    public static final String TAG = "ScheduleService";

    public ScheduleService() {
        super(TAG);
    }

    @Override
    public void doReminderWork(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Service working");
            createNotification();
            //ScheduleHelper.scheduleAll(this);
        }
    }


    private void createNotification() {
        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.pastilla)
                .setContentTitle("Lémbramo")
                .setContentText("¡Tómate la pastilla!")
                .setVibrate(new long[] { 1000, 200, 500, 200, 100, 200, 1000 })
                .setSound(soundUri);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // Thisensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(Instant.now().hashCode(), mBuilder.build());
    }
}

