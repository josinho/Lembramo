package gal.xieiro.lembramo.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import gal.xieiro.lembramo.LembramoApp;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    private static final long INTERVAL_MINUTE = 60000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            initScheduleAlarm(context);
        }
    }

    public static void initScheduleAlarm(Context context) {
        Log.d(TAG, "initScheduleAlarm");
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(LembramoApp.ACTION_SCHEDULE);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                AlarmReceiver.REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                INTERVAL_MINUTE,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }
}