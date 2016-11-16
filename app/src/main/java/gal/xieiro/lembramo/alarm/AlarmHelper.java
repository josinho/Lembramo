package gal.xieiro.lembramo.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import org.threeten.bp.Instant;

import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.LembramoContentProvider;
import gal.xieiro.lembramo.model.MedicineIntake;
import gal.xieiro.lembramo.util.TimeUtils;

public class AlarmHelper {
    public static final String EXTRA_PARAMS = "intake_info";


    private AlarmHelper() {
    }

    public static void createAlarms(Context context) {
        long now = Instant.now().toEpochMilli();

        String selection = DBContract.Intakes.COLUMN_NAME_DATE + " >= ? AND " +
                DBContract.Intakes.COLUMN_NAME_INTAKE_DATE + " IS NULL";
        String[] selectionArgs = {Long.valueOf(now).toString()};

        Cursor cursor = context.getContentResolver().query(
                LembramoContentProvider.CONTENT_URI_INTAKES,
                null,
                selection,
                selectionArgs,
                null
        );

        while (cursor.moveToNext()) {
            MedicineIntake intake = new MedicineIntake();
            intake.setId(
                    cursor.getLong(cursor.getColumnIndex(DBContract.Intakes._ID))
            );
            intake.setMedicineId(
                    cursor.getLong(cursor.getColumnIndex(DBContract.Intakes.COLUMN_NAME_ID_MEDICINE))
            );
            intake.setIntakeInstant(TimeUtils.getDateTimeFromMillis(
                    cursor.getLong(cursor.getColumnIndex(DBContract.Intakes.COLUMN_NAME_DATE))
            ));
            intake.setDose(
                    cursor.getDouble(cursor.getColumnIndex(DBContract.Intakes.COLUMN_NAME_DOSE))
            );

            setAlarm(context, intake);

        }
        cursor.close();
    }


    private static void setAlarm(Context context, MedicineIntake intake) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_PARAMS, intake);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                intake.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    TimeUtils.getMillis(intake.getIntakeInstant()),
                    pendingIntent
            );
        }
    }

    private static void cancelAlarm(Context context, MedicineIntake intake) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_PARAMS, intake);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                intake.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
