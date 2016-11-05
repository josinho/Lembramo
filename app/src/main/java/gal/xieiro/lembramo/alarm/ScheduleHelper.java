package gal.xieiro.lembramo.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import org.threeten.bp.Instant;
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

            long[] dates;
            int length;
            switch (getDurationType(dtStart, recurrenceRule)) {
                case BY_DATE:
                    dates = expand(dtStart, recurrenceRule, dtStart.toMillis(false), -1);
                    length = dates.length;
                    if (length > 0) {
                        medicine.setEndDate(dates[length - 1]);
                    } else {
                        medicine.setEndDate(0);
                    }
                    break;

                case BY_INTAKES:
                    dates = expand(dtStart, recurrenceRule, dtStart.toMillis(false), -1);
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


    private static long[] expand(Time dtStart, String recurrenceRule, long rangeStartMillis, long rangeEndMillis) {
        RecurrenceSet recurrenceSet = new RecurrenceSet(recurrenceRule, null, null, null);
        RecurrenceProcessor rp = new RecurrenceProcessor();
        try {
            return rp.expand(dtStart, recurrenceSet, rangeStartMillis, rangeEndMillis);

        } catch (DateException de) {
            Log.e(TAG, de.getMessage());
        }
        return new long[0];
    }

    private static int getDurationType(Time dtStart, String recurrenceRule) {
        EventRecurrence recurrence = new EventRecurrence();
        recurrence.setStartDate(dtStart);
        recurrence.parse(recurrenceRule);

        //devolver el tipo de duración del tratamiento: fecha tope, nº de tomas, para siempre
        if (recurrence.count > 0) return BY_INTAKES;
        if (recurrence.until != null) return BY_DATE;
        return FOREVER;
    }

    public static void scheduleAll(Context context) {
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

        boolean active, started;
        long idMedicine, endMillis, rangeStartMillis, rangeEndMillis;
        LocalDate startDate, endDate;
        LocalDate now = LocalDate.now();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                idMedicine = cursor.getLong(cursor.getColumnIndex(DBContract.Medicines._ID));
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
                            setInactive(context, idMedicine);
                        } else {
                            active = true;
                        }
                    }

                    // hemos comenzado el tratamiento?
                    started = true;
                    if (now.isBefore(startDate)) {
                        //aun no se ha llegado a la fecha de inicio del tratamiento
                        // si queda más de un día no planificamos todavía
                        if (Period.between(now, startDate).getDays() > 1) started = false;
                    }

                    if (active && started) {
                        String recurrenceRule = cursor.getString(
                                cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_RECURRENCE));
                        String intakeRule = cursor.getString(
                                cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_SCHEDULE));
                        Time dtStart = TimeUtils.getTimeDateFromString(
                                cursor.getString(cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_STARTDATE)));

                        //buscar en tabla intakes última planificación
                        Instant last = getLastPlannedIntakeDate(context, idMedicine);
                        boolean plan = true;
                        if (last.equals(Instant.EPOCH)) {
                            //no hay ninguna planificacion previa, partimos de cero
                            rangeStartMillis = TimeUtils.getMillis(startDate);
                            rangeEndMillis = TimeUtils.getMillis(startDate.plusDays(1));

                        } else {
                            //hubo planificaciones anteriores
                            LocalDate d = TimeUtils.getDateFromMillis(last.toEpochMilli()).plusDays(1);
                            if (Period.between(now, d).getDays() > 1) {
                                plan = false;
                                rangeStartMillis = rangeEndMillis = 0;
                            } else {
                                rangeStartMillis = TimeUtils.getMillis(d);
                                rangeEndMillis = TimeUtils.getMillis(d.plusDays(1));
                            }
                        }

                        if (plan) {
                            long[] days = expand(dtStart, recurrenceRule, rangeStartMillis, rangeEndMillis);
                            if (days.length > 0) {
                                List<MedicineIntake> dailyIntakes = IntakeUtils.parseDailyIntakes(intakeRule);

                                for (long day : days) {
                                    LocalDate date = TimeUtils.getDateFromMillis(day);
                                    //añadir las tomas de ese día
                                    for (MedicineIntake intake : dailyIntakes) {
                                        long intakeInstant = TimeUtils.getMillis(date, intake.getTime());
                                        saveIntake(context, intakeInstant, intake.getDose(), idMedicine);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            cursor.close();
        }
    }

    private static void saveIntake(Context context, long intakeInstant, double dose, long idMedicine) {
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Intakes.COLUMN_NAME_DATE, intakeInstant);
        cv.put(DBContract.Intakes.COLUMN_NAME_DOSE, dose);
        cv.put(DBContract.Intakes.COLUMN_NAME_ID_MEDICINE, idMedicine);

        context.getContentResolver().insert(LembramoContentProvider.CONTENT_URI_INTAKES, cv);
    }

    private static void setInactive(Context context, long id) {
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Medicines.COLUMN_NAME_ENDDATE, 0);

        String uri = LembramoContentProvider.CONTENT_URI_MEDICINES.toString() + "/" + id;
        context.getContentResolver().update(Uri.parse(uri), cv, null, null);
    }

    private static Instant getLastPlannedIntakeDate(Context context, long idMedicine) {
        String[] projection = {"MAX(" + DBContract.Intakes.COLUMN_NAME_DATE + ")"};
        String selection = DBContract.Intakes.COLUMN_NAME_ID_MEDICINE + "= ?";
        String[] selectionArgs = {new Long(idMedicine).toString()};

        Cursor cursor = context.getContentResolver().query(
                LembramoContentProvider.CONTENT_URI_INTAKES,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            long millis = cursor.getLong(cursor.getColumnIndex(DBContract.Intakes.COLUMN_NAME_DATE));
            return Instant.ofEpochMilli(millis);
        }
        return Instant.EPOCH;
    }
}

