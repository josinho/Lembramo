package gal.xieiro.lembramo.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import gal.xieiro.lembramo.LembramoApp;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    public static final int REQUEST_CODE = 123;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case LembramoApp.ACTION_SCHEDULE:
                Log.d(TAG, "intent ACTION SCHEDULE");
                ScheduleService.acquireStaticLock(context);
                context.startService(new Intent(context, ScheduleService.class));
                break;
        }
    }
}
