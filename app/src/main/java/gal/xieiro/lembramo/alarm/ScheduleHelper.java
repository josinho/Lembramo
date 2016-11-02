package gal.xieiro.lembramo.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;

import java.util.List;

import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.LembramoContentProvider;
import gal.xieiro.lembramo.model.Medicine;
import gal.xieiro.lembramo.model.MedicineIntake;
import gal.xieiro.lembramo.recurrence.DateException;
import gal.xieiro.lembramo.recurrence.EventRecurrence;
import gal.xieiro.lembramo.recurrence.RecurrenceProcessor;
import gal.xieiro.lembramo.recurrence.RecurrenceSet;
import gal.xieiro.lembramo.util.IntakeUtils;
import gal.xieiro.lembramo.util.TimeUtils;

public class ScheduleHelper {
    private static final String TAG = "ScheduleHelper";

    private static final int BY_DATE = 1;
    private static final int BY_INTAKES = 2;
    private static final int FOREVER = 3;


    public static void getLastIntake(Medicine medicine) {
        String recurrenceRule = medicine.getRecurrenceRule();
        String intakesRule = medicine.getSchedule();

        if (!TextUtils.isEmpty(recurrenceRule) && !TextUtils.isEmpty(intakesRule)) {
            Time dtStart = TimeUtils.getTimeDateFromString(medicine.getStartDate());
            EventRecurrence recurrence = new EventRecurrence();
            recurrence.setStartDate(dtStart);
            recurrence.parse(recurrenceRule);

            long[] dates;
            int length;
            switch (getDurationType(recurrence)) {
                case BY_DATE:
                    dates = expand(dtStart, recurrenceRule);
                    length = dates.length;
                    if (length > 0) {
                        medicine.setEndDate(dates[length - 1]);
                    } else {
                        medicine.setEndDate(0);
                    }
                    break;

                case BY_INTAKES:
                    dates = expand(dtStart, recurrenceRule);
                    length = dates.length;
                    if (length > 0) {
                        List<MedicineIntake> intakes = IntakeUtils.parseDailyIntakes(intakesRule);
                        int dailyIntakes = intakes.size();
                        if (length % dailyIntakes == 0) {
                            medicine.setEndDate(dates[(length / dailyIntakes) - 1]);
                        } else {
                            medicine.setEndDate(dates[length / dailyIntakes]);
                        }
                    } else {
                        medicine.setEndDate(0);
                    }
                    break;

                default: //forever
                    medicine.setEndDate(-1);
                    break;
            }
        }
    }


    private static long[] expand(Time dtStart, String recurrenceRule) {
        RecurrenceSet recurrenceSet = new RecurrenceSet(recurrenceRule, null, null, null);
        RecurrenceProcessor rp = new RecurrenceProcessor();
        try {
            return rp.expand(dtStart, recurrenceSet, dtStart.toMillis(false), -1);

        } catch (DateException de) {
            Log.e(TAG, de.getMessage());
        }
        return new long[0];
    }

    private static int getDurationType(EventRecurrence recurrence) {
        //devolver el tipo de duración del tratamiento: fecha tope, nº de tomas, para siempre
        if (recurrence.count > 0) return BY_INTAKES;
        if (recurrence.until != null) return BY_DATE;
        return FOREVER;
    }

    private void scheduleAll(Context context) {
        String[] projection = {
                DBContract.Medicines._ID,
                DBContract.Medicines.COLUMN_NAME_ALARM,
                DBContract.Medicines.COLUMN_NAME_STARTDATE,
                DBContract.Medicines.COLUMN_NAME_ENDDATE,
                DBContract.Medicines.COLUMN_NAME_RECURRENCE,
                DBContract.Medicines.COLUMN_NAME_SCHEDULE
        };

        //quizas haya que poner selection: endDate != 0

        Cursor cursor = context.getContentResolver().query(
                LembramoContentProvider.CONTENT_URI_MEDICINES,
                projection,
                null, null, null
        );

        boolean active, plan;
        long id, endMillis;
        LocalDate startDate, endDate;
        LocalDate now = LocalDate.now();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                id = cursor.getLong(cursor.getColumnIndex(DBContract.Medicines._ID));
                startDate = TimeUtils.parseDate(cursor.getString(
                        cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_STARTDATE)));
                endMillis = cursor.getLong(
                        cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_ENDDATE));


                //medicina en vigor?
                if (endMillis == 0) {
                    // tratamiento finalizado
                } else {
                    // tratamiento para siempre o con fecha de finalización
                    if (endMillis == -1) {
                        // tratamiento para siempre
                        active = true;
                    } else {
                        // fecha concreta de finalización
                        endDate = TimeUtils.getDateFromMillis(endMillis);

                        if (now.isAfter(endDate)) {
                            active = false;
                            setInactive(context, id);
                        } else {
                            active = true;
                        }
                    }

                    // hemos comenzado el tratamiento?
                    plan = true;
                    if (now.isBefore(startDate)) {
                        //aun no se ha llegado a la fecha de inicio del tratamiento
                        // si queda más de un día no planificamos todavía
                        if (Period.between(now, startDate).getDays() > 1) plan = false;
                    }

                    if (active && plan) {
                        Log.i(TAG, "A Planificaaaaaar");
                    }
                }
            }
            cursor.close();
        }
    }

    private void setInactive(Context context, long id) {
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Medicines.COLUMN_NAME_ENDDATE, 0);

        String uri = LembramoContentProvider.CONTENT_URI_MEDICINES.toString() + "/" + id;
        context.getContentResolver().update(Uri.parse(uri), cv, null, null);
    }
}

