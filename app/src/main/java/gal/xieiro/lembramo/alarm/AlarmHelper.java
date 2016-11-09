package gal.xieiro.lembramo.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import org.threeten.bp.Instant;

import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.LembramoContentProvider;

public class AlarmHelper {

    private AlarmHelper() {
    }

    public static void createAlarms(Context context) {
        long now = Instant.now().toEpochMilli();

        String selection = DBContract.Intakes.COLUMN_NAME_DATE + " >= ?";
        String[] selectionArgs = {Long.valueOf(now).toString()};

        Cursor cursor = context.getContentResolver().query(
                LembramoContentProvider.CONTENT_URI_INTAKES,
                null,
                selection,
                selectionArgs,
                null
        );

        while (cursor.moveToNext()) {
            //TODO idMedicine? params?
            long instant = cursor.getLong(cursor.getColumnIndex(DBContract.Intakes.COLUMN_NAME_DATE));
            setAlarm(context, instant);

        }
        cursor.close();
    }


    private static void setAlarm(Context context, long instant) {
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                AlarmReceiver.REQUEST_CODE,
                new Intent(context, AlarmReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, instant, pendingIntent);
        }
    }
}
