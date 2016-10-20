package gal.xieiro.lembramo.alarm;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ScheduleService extends WakefulIntentService {
    public static final String ACTION_SCHEDULE = "gal.xieiro.lembramo.alarm.action.SCHEDULE";

    public ScheduleService() {
        super("ScheduleService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (ACTION_SCHEDULE.equals(intent.getAction())) {
                //handleActionSchedule(param1, param2);
            }
        }
        super.onHandleIntent(intent);
    }
}
