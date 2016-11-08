package gal.xieiro.lembramo.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import gal.xieiro.lembramo.LembramoApp;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 123;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case LembramoApp.ACTION_SCHEDULE:
                ScheduleService.acquireStaticLock(context);
                //TODO añadir action ¿o no?

                context.startService(new Intent(context, ScheduleService.class));
                break;
        }
    }
}
