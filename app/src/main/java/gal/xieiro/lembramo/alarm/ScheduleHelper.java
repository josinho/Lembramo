package gal.xieiro.lembramo.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
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

@SuppressWarnings("deprecation")
public class ScheduleHelper {
    private static final String TAG = "ScheduleHelper";

    private static final int END_BY_DATE = 1;
    private static final int END_BY_COUNT = 2;
    private static final int END_NEVER = 3;


    public static void getLastIntake(Medicine medicine) {
        String recurrenceRule = medicine.getRecurrenceRule();
        String intakesRule = medicine.getSchedule();

        if (!TextUtils.isEmpty(recurrenceRule) && !TextUtils.isEmpty(intakesRule)) {
            Time dtStart = TimeUtils.getTimeDateFromString(medicine.getStartDate());
            List<MedicineIntake> intakes = IntakeUtils.parseDailyIntakes(intakesRule);

            long[] dates;
            int length;
            LocalDate date;
            LocalTime time;

            int durationType = getDurationType(dtStart, recurrenceRule);

            if (durationType == END_NEVER) {
                medicine.setEndDate(-1);
            } else {
                dates = expand(dtStart, recurrenceRule, dtStart.toMillis(false), -1);
                length = dates.length;
                if (length > 0) {
                    if (durationType == END_BY_DATE) {
                        date = TimeUtils.getDateFromMillis(dates[length - 1]);
                        time = intakes.get(intakes.size() - 1).getTime();
                    } else {
                        // durationType == END_BY_COUNT
                        int dailyIntakes = intakes.size();
                        if (length % dailyIntakes == 0) {
                            date = TimeUtils.getDateFromMillis(dates[(length / dailyIntakes) - 1]);
                            time = intakes.get(intakes.size() - 1).getTime();
                        } else {
                            date = TimeUtils.getDateFromMillis(dates[length / dailyIntakes]);
                            time = intakes.get((length % dailyIntakes) - 1).getTime();
                        }
                    }
                    medicine.setEndDate(TimeUtils.getMillis(date, time));
                } else {
                    medicine.setEndDate(0);
                }
            }
        }
    }


    public static long[] expand(Time dtStart, String recurrenceRule, long rangeStartMillis, long rangeEndMillis) {
        RecurrenceSet recurrenceSet = new RecurrenceSet(recurrenceRule, null, null, null);
        RecurrenceProcessor rp = new RecurrenceProcessor();
        try {
            return rp.expand(dtStart, recurrenceSet, rangeStartMillis, rangeEndMillis);
        } catch (DateException de) {
            Log.e(TAG, de.getMessage());
        }
        return new long[0];
    }

    public static int getDurationType(Time dtStart, String recurrenceRule) {
        EventRecurrence recurrence = new EventRecurrence();
        recurrence.setStartDate(dtStart);
        recurrence.parse(recurrenceRule);

        //devolver el tipo de duración del tratamiento: fecha tope, nº de tomas, para siempre
        if (recurrence.count > 0) return END_BY_COUNT;
        if (recurrence.until != null) return END_BY_DATE;
        return END_NEVER;
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

        String selection = DBContract.Medicines.COLUMN_NAME_ENDDATE + " != ?";
        String[] selectionArgs = {"0"};

        //obtener medicamentos en vigor: fecha fin distinta de cero
        Cursor cursor = context.getContentResolver().query(
                LembramoContentProvider.CONTENT_URI_MEDICINES,
                projection,
                selection,
                selectionArgs,
                null
        );

        boolean active, started;
        long idMedicine, endMillis;
        long rangeStartMillis = 0, rangeEndMillis = 0;
        LocalDate startDate, endDate;
        LocalDate now = LocalDate.now();


        while (cursor.moveToNext()) {
            idMedicine = cursor.getLong(cursor.getColumnIndex(DBContract.Medicines._ID));
            startDate = TimeUtils.parseDate(cursor.getString(
                    cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_STARTDATE)));
            endMillis = cursor.getLong(
                    cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_ENDDATE));
            endDate = TimeUtils.getDateFromMillis(endMillis);

            // hemos comenzado el tratamiento?
            started = true;
            if (now.isBefore(startDate)) {
                // aun no se ha llegado a la fecha de inicio del tratamiento
                // si queda más de un día no planificamos todavía
                if (Period.between(now, startDate).getDays() > 1) started = false;
            }

            // endMillis != 0 porque lo hemos pedido así en la consulta
            // si endMillis == -1 es tratamiento para siempre
            active = true;
            if (endMillis != -1) {
                // fecha concreta de finalización
                if (now.isAfter(endDate)) {
                    active = false;
                    setInactive(context, idMedicine);
                }
            }

            if (started && active) {
                String recurrenceRule = cursor.getString(
                        cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_RECURRENCE));
                String intakeRule = cursor.getString(
                        cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_SCHEDULE));
                Time dtStart = TimeUtils.getTimeDateFromString(
                        cursor.getString(cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_STARTDATE)));

                //buscar en tabla intakes última planificación
                long last = getLastPlannedIntakeDate(context, idMedicine);
                boolean plan = true;
                if (last == 0) {
                    //no hay ninguna planificacion previa, partimos de cero
                    rangeStartMillis = TimeUtils.getMillis(startDate);

                    if (now.isAfter(startDate))
                        // el móvil estuvo apagado, estamos en medio de la planificación
                        // pero aún no hubo ninguna. se perdieron intakes
                        rangeEndMillis = TimeUtils.getMillis(now.plusDays(1));
                    else
                        rangeEndMillis = TimeUtils.getMillis(startDate.plusDays(1));


                } else {
                    //hubo planificaciones anteriores
                    LocalDate d = TimeUtils.getDateFromMillis(last).plusDays(1);
                    if (Period.between(now, d).getDays() > 1) {
                        plan = false;
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
                            int max;
                            // controlar las que son COUNT
                            if ((getDurationType(dtStart, recurrenceRule) == END_BY_COUNT) &&
                                    (date.isEqual(endDate)))
                                max = getNumberOfLastIntakesByCount(dtStart, recurrenceRule, dailyIntakes.size());
                            else
                                max = dailyIntakes.size();

                            MedicineIntake intake;
                            long intakeInstant;
                            int i = 0;
                            while (i < max) {
                                //añadir las tomas de ese día
                                intake = dailyIntakes.get(i);
                                intakeInstant = TimeUtils.getMillis(date, intake.getTime());
                                saveIntake(context, intakeInstant, intake.getDose(), idMedicine);
                                i++;
                            }
                        }
                    }
                }
            }
        }
        cursor.close();
    }

    private static int getNumberOfLastIntakesByCount(Time dtStart, String recurrenceRule, int dailyIntakes) {
        long[] dates = expand(dtStart, recurrenceRule, dtStart.toMillis(false), -1);
        int length = dates.length;
        if (length > 0) {
            int result = length % dailyIntakes;
            return (result == 0) ? dailyIntakes : result;
        }
        return 0;
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

    private static long getLastPlannedIntakeDate(Context context, long idMedicine) {
        String[] projection = {"MAX(" + DBContract.Intakes.COLUMN_NAME_DATE + ") AS " +
                DBContract.Intakes.COLUMN_NAME_DATE};
        String selection = DBContract.Intakes.COLUMN_NAME_ID_MEDICINE + " = ?";
        String[] selectionArgs = {Long.valueOf(idMedicine).toString()};
        long last;

        Cursor cursor = context.getContentResolver().query(
                LembramoContentProvider.CONTENT_URI_INTAKES,
                projection,
                selection,
                selectionArgs,
                null
        );

        cursor.moveToFirst();
        if (cursor.moveToFirst())
            last = cursor.getLong(cursor.getColumnIndex(DBContract.Intakes.COLUMN_NAME_DATE));
        else
            last = 0;
        cursor.close();
        return last;
    }
}

