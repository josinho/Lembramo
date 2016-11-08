package gal.xieiro.lembramo.alarm;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import gal.xieiro.lembramo.LembramoApp;


public class ScheduleService extends WakefulIntentService {
    public ScheduleService() {
        super("ScheduleService");
    }

    @Override
    public void doReminderWork(Intent intent) {
        if (intent != null) {
            if (LembramoApp.ACTION_SCHEDULE.equals(intent.getAction())) {
                //handleActionSchedule(param1, param2);
            }
        }
    }
}

