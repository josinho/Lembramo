package gal.xieiro.lembramo.alarm;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.TimeFormatException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.LembramoContentProvider;
import gal.xieiro.lembramo.model.CalendarRange;
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

    private Calendar startSchedule;
    private EventRecurrence recurrence;
    private List<MedicineIntake> dailyIntakes;
    private List<MedicineIntake> schedule;


    public ScheduleHelper(String startDate, String recurrenceRule, String intakesRule) {
        schedule = new ArrayList<>();

        startSchedule = TimeUtils.getCalendarDateFromString(startDate);

        recurrence = new EventRecurrence();
        recurrence.setStartDate(TimeUtils.getTimeDateFromString(startDate));
        if (recurrenceRule != null) {
            recurrence.parse(recurrenceRule);
        }

        dailyIntakes = IntakeUtils.parseDailyIntakes(intakesRule);
    }

    public ScheduleHelper(Medicine medicine) {

    }

    public static void getLastIntake(Medicine medicine) {
        String recurrenceRule = medicine.getRecurrenceRule();
        String intakesRule = medicine.getSchedule();

        if (!TextUtils.isEmpty(recurrenceRule) && !TextUtils.isEmpty(intakesRule)) {
            Time dtStart = TimeUtils.getTimeDateFromMillis(medicine.getStartDate());
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

    private void hazElTrabajoDuro(Context context) {
        String[] projection = {
                DBContract.Medicines._ID,
                DBContract.Medicines.COLUMN_NAME_ALARM,
                DBContract.Medicines.COLUMN_NAME_STARTDATE,
                DBContract.Medicines.COLUMN_NAME_RECURRENCE,
                DBContract.Medicines.COLUMN_NAME_SCHEDULE
        };

        Cursor cursor = context.getContentResolver().query(
                LembramoContentProvider.CONTENT_URI_MEDICINES,
                projection,
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                //medicina en vigor?
                String startDate = cursor.getString(cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_STARTDATE));
                Time dtStart = TimeUtils.getTimeDateFromString(startDate);
                String recurrenceRule = cursor.getString(cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_RECURRENCE));

                RecurrenceSet recurrenceSet = new RecurrenceSet(recurrenceRule, null, null, null);
                RecurrenceProcessor rp = new RecurrenceProcessor();
                long lastOcurrence;
                try {
                    lastOcurrence = rp.getLastOccurence(dtStart, recurrenceSet);
                } catch (DateException de) {
                    lastOcurrence = 0;
                    Log.i(TAG, de.getMessage());
                }

                if (lastOcurrence == 0) {
                    //no hay una fecha
                } else if (lastOcurrence == -1) {
                    //tratamiento es par siempre
                } else {
                    //fecha concreta

                }
            }
        }
    }

    public int getDailyIntakesCount() {
        return dailyIntakes.size();
    }


    //TODO renombrar
    private void calcula_con_intervalo(long idMedicine, CalendarRange scheduleWindow) {

        //caso de uso: freq: DAILY | with interval | duration forever

        int interval = recurrence.interval;
        Calendar lastScheduledIntake = null; //getLastScheduledIntake(idMedicine);


        if (lastScheduledIntake == null) {
            //nunca hubo una planificación --> tomamos como primera fecha la del inicio del tratamiento
            lastScheduledIntake = startSchedule;
        }

        if (!scheduleWindow.isAfterRange(lastScheduledIntake)) {
            //la última fecha de planificación no es posterior a la ventana de planificación
            if (scheduleWindow.isInRange(lastScheduledIntake)) {
                do {
                    //planificar el día
                    scheduleDay(lastScheduledIntake);
                    //sumar el intervalo
                    lastScheduledIntake.add(Calendar.DAY_OF_MONTH, interval);
                } while (scheduleWindow.isInRange(lastScheduledIntake));

            } else {
                //la última fecha de planificación es anterior a la ventana de planificación
            }
        }
        //else: la fecha de inicio del tratamiento es posterior a la ventana -> aún no toca planificar
    }

    private void scheduleDay(Calendar day) {
        for (MedicineIntake intake : dailyIntakes) {
            //por cada toma diaria creamos un objeto copia
            MedicineIntake copy = new MedicineIntake(intake);
            //al que le fijamos el día de la toma
            copy.setDate(
                    day.get(Calendar.YEAR),
                    day.get(Calendar.MONTH),
                    day.get(Calendar.DAY_OF_MONTH)
            );
            //y lo añadimos a la planificación
            schedule.add(copy);
        }
    }


    private void startSchedule(Context context, long idMedicine) {
        boolean shouldSchedule = true;

        switch (getDurationType(null)) {
            case BY_DATE:
                //si hoy es una fecha posterior al final del tratamiento no planificamos más
                if (isExpired())
                    shouldSchedule = false;
                break;
            case BY_INTAKES:
                //comprobar si ya se han planificado todas las tomas
                if (isFullyPlanned(context, idMedicine, recurrence.count))
                    shouldSchedule = false;
                break;
            case FOREVER:
                //ninguna comprobación
                break;
        }

        if (shouldSchedule) {
            //schedule
        }
    }

    private boolean isExpired() {
        try {
            Time t = new Time();
            t.parse(recurrence.until);
            Calendar until = Calendar.getInstance();
            until.setTimeInMillis(t.toMillis(false));

            Calendar today = Calendar.getInstance();
            if (today.after(until)) return true;
        } catch (TimeFormatException e) {
        }
        return false;
    }

    private boolean isFullyPlanned(Context context, long idMedicine, int max) {
        String[] projection = {"count(*)"};
        String selection = DBContract.Intakes.COLUMN_NAME_ID_MEDICINE + " = " + idMedicine;

        Cursor cursor = context.getContentResolver().query(
                LembramoContentProvider.CONTENT_URI_INTAKES,
                projection,
                selection,
                null, //selectionArgs
                null  //sortOrder
        );

        if (cursor != null) {
            cursor.moveToFirst();
            int result = cursor.getInt(0);
            cursor.close();
            if (result < max) {
                return false;
            }
        }
        return true;
    }
}
