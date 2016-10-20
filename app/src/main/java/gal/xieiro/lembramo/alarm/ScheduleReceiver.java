package gal.xieiro.lembramo.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduleReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 123;

    public ScheduleReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduleService.acquireStaticLock(context);
        context.startService(new Intent(context, ScheduleService.class));
    }
}
