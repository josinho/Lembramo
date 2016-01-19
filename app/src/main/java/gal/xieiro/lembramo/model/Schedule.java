package gal.xieiro.lembramo.model;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.TimeFormatException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.IntakeContentProvider;
import gal.xieiro.lembramo.db.MedicineContentProvider;
import gal.xieiro.lembramo.ui.recurrencepicker.EventRecurrence;
import gal.xieiro.lembramo.util.Utils;

public class Schedule {
    private final int BY_DATE = 1;
    private final int BY_INTAKES = 2;
    private final int FOREVER = 3;

    private Context context;
    private Calendar startSchedule;
    private EventRecurrence recurrence;
    private ArrayList<MedicineIntake> dailyIntakes;
    private ArrayList<MedicineIntake> schedule;


    public Schedule(Context context, String startDate, String recurrenceRule, String intakesRule) {
        this.context = context;
        schedule = new ArrayList<>();

        startSchedule = Utils.getCalendarDateFromString(startDate);

        recurrence = new EventRecurrence();
        recurrence.setStartDate(Utils.getTimeDateFromString(startDate));
        if (recurrenceRule != null) {
            recurrence.parse(recurrenceRule);
        }

        dailyIntakes = new ArrayList<>();
        parseDailyIntakes(intakesRule);
    }

    private void parseDailyIntakes(String intakesRule) {
        if (!TextUtils.isEmpty(intakesRule)) {
            String[] intakeStrings = intakesRule.split(";");
            for (String intakeString : intakeStrings) {
                String[] intake = intakeString.split(",");
                Calendar hour = Utils.parseTime(intake[0]);
                MedicineIntake medicineIntake = new MedicineIntake(hour);
                medicineIntake.setDose(Double.valueOf(intake[1]));
                dailyIntakes.add(medicineIntake);
            }
        }
    }

    public int getDailyIntakesCount() {
        return dailyIntakes.size();
    }

    //TODO este sería el método principal
    public List<MedicineIntake> getScheduledDates(CalendarRange range) {
        return null;
    }

    //TODO este método deberá estar en otra clase y acceder a la BD
    public Calendar getLastScheduledIntake(long idMedicine) {
        return null;
    }

    //TODO renombrar
    private void calcula_con_intervalo(long idMedicine, CalendarRange scheduleWindow) {

        //caso de uso: freq: DAILY | with interval | duration forever

        int interval = recurrence.interval;
        Calendar lastScheduledIntake = getLastScheduledIntake(idMedicine);


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

    private int getDurationType() {
        //devolver el tipo de duración del tratamiento: fecha tope, nº de tomas, para siempre
        if (recurrence.count > 0) return BY_INTAKES;
        if (recurrence.until != null) return BY_DATE;
        return FOREVER;
    }

    private void startSchedule(long idMedicine) {
        boolean shouldSchedule = true;

        switch (getDurationType()) {
            case BY_DATE:
                //si hoy es una fecha posterior al final del tratamiento no planificamos más
                if (isExpired())
                    shouldSchedule = false;
                break;
            case BY_INTAKES:
                //comprobar si ya se han planificado todas las tomas
                if (isFullyPlanned(idMedicine, recurrence.count))
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

    private boolean isFullyPlanned(long idMedicine, int max) {
        String[] projection = {"count(*)"};
        String selection = DBContract.Intakes.COLUMN_NAME_ID_MEDICINE + " = " + idMedicine;

        Cursor cursor = context.getContentResolver().query(
                IntakeContentProvider.CONTENT_URI,
                projection,
                selection,
                null, //selectionArgs
                null  //sortOrder
        );

        if(cursor != null) {
            cursor.moveToFirst();
            int result =  cursor.getInt(0);
            cursor.close();
            if(result < max) {
                return false;
            }
        }
        return true;
    }
}
