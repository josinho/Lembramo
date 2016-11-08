package gal.xieiro.lembramo.alarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public abstract class WakefulIntentService extends IntentService {

    private static final String LOCK_NAME_STATIC = "gal.xieiro.lembramo.alarm.Static";
    private static final String LOCK_NAME_LOCAL = "gal.xieiro.lembramo.alarm.Local";

    private static PowerManager.WakeLock lockStatic = null;
    private PowerManager.WakeLock lockLocal = null;

    public WakefulIntentService(String name) {
        super(name);
    }

    /**
     * Acquire a partial static WakeLock, you need too call this within the class
     * that calls startService()
     *
     * @param context
     */
    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return (lockStatic);
    }

    /**
     * Has to be overrated in the class that will inherit from this one and here is where
     * the user of this class takes action.
     *
     * @param intent Intent.
     */
    public abstract void doReminderWork(Intent intent);

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        lockLocal = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_LOCAL);
        lockLocal.setReferenceCounted(true);
    }

    @Override
    public void onStart(Intent intent, final int startId) {
        lockLocal.acquire();
        super.onStart(intent, startId);
        getLock(this).release();
    }

    @Override
    final protected void onHandleIntent(Intent intent) {
        try {
            doReminderWork(intent);
        } finally {
            lockLocal.release();
        }

    }
}
