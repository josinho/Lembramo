package gal.xieiro.lembramo.alarm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.threeten.bp.Instant;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.ui.AlarmActivity;
import gal.xieiro.lembramo.ui.MainActivity;

public class NotificationHelper {

    public static void createNotification(Context context, Intent alarmIntent) {
        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.pastilla)
                .setContentTitle("Lémbramo")
                .setContentText("¡Tómate la pastilla!")
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 200, 500, 200, 100, 200, 1000 })
                .setSound(soundUri);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, AlarmActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // Thisensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

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
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(Instant.now().hashCode(), mBuilder.build());
    }
}
