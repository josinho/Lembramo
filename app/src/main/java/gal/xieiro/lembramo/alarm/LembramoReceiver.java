package gal.xieiro.lembramo.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import gal.xieiro.lembramo.LembramoApp;

public class LembramoReceiver extends BroadcastReceiver {
    private static final String TAG = "LembramoReceiver";

    public static final int REQUEST_CODE = 123;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            /**
             * This BroadcastReceiver automatically (re)starts the alarm when the device is
             * rebooted. This receiver is set to be disabled (android:enabled="false") in the
             * application's manifest file. When the user sets the alarm, the receiver is enabled.
             * When the user cancels the alarm, the receiver is disabled, so that rebooting the
             * device will not trigger this receiver.
             */
            case "android.intent.action.BOOT_COMPLETED":
                ScheduleHelper.initScheduleAlarm(context);
                break;
            case LembramoApp.ACTION_SCHEDULE:
                Log.d(TAG, "intent ACTION SCHEDULE");
                ScheduleService.acquireStaticLock(context);
                context.startService(new Intent(context, ScheduleService.class));
                break;
        }
    }
}
