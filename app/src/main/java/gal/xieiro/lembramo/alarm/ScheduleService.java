package gal.xieiro.lembramo.alarm;

import android.content.Intent;

import android.util.Log;


public class ScheduleService extends WakefulIntentService {
    public static final String TAG = "ScheduleService";

    public ScheduleService() {
        super(TAG);
    }

    @Override
    public void doReminderWork(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Service working");
            ScheduleHelper.scheduleAll(this);
            AlarmHelper.createAlarms(this);
        }
    }
}
