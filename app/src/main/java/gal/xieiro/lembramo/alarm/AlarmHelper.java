package gal.xieiro.lembramo.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import org.threeten.bp.Instant;

import java.util.ArrayList;

import gal.xieiro.lembramo.LembramoApp;
import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.LembramoContentProvider;
import gal.xieiro.lembramo.model.MedicineIntake;
import gal.xieiro.lembramo.util.TimeUtils;

public class AlarmHelper {
    public static final String EXTRA_PARAMS = "intake_info";


    private AlarmHelper() {
    }

    public static void createAlarms(Context context) {
        Cursor cursor = getCursor(context, LembramoContentProvider.NO_ID);

        while (cursor.moveToNext()) {
            setAlarm(context, getMedicineIntake(cursor));
        }
        cursor.close();
    }

    public static void cancelAlarms(Context context, long medicineID) {
        Cursor cursor = getCursor(context, medicineID);

        while (cursor.moveToNext()) {
            cancelAlarm(context, getMedicineIntake(cursor));
        }
        cursor.close();
    }

    private static void setAlarm(Context context, MedicineIntake intake) {
        final PendingIntent pendingIntent = getPendingIntent(context, intake);
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
        final PendingIntent pendingIntent = getPendingIntent(context, intake);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    private static PendingIntent getPendingIntent(Context context, MedicineIntake intake) {
        Intent intent = new Intent(context, LembramoReceiver.class);
        intent.setAction(LembramoApp.ACTION_ALARM);
        intent.putExtra(EXTRA_PARAMS, intake);

        return PendingIntent.getBroadcast(
                context,
                intake.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private static Cursor getCursor(Context context, long medicineID) {
        String selection = "";
        ArrayList<String> selectionList = new ArrayList<>();

        if (medicineID != LembramoContentProvider.NO_ID) {
            selection = DBContract.Intakes.COLUMN_NAME_ID_MEDICINE + " = ? AND ";
            selectionList.add(Long.valueOf(medicineID).toString());
        }


        selection += DBContract.Intakes.COLUMN_NAME_DATE + " >= ? AND " +
                DBContract.Intakes.COLUMN_NAME_INTAKE_DATE + " IS NULL";
        selectionList.add(TimeUtils.getCurrentStringMillis());

        String[] selectionArgs = new String[selectionList.size()];
        selectionArgs = selectionList.toArray(selectionArgs);

        Cursor cursor = context.getContentResolver().query(
                LembramoContentProvider.CONTENT_URI_INTAKES,
                null,
                selection,
                selectionArgs,
                null
        );

        return cursor;
    }

    private static MedicineIntake getMedicineIntake(Cursor cursor) {
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

        return intake;
    }
}
