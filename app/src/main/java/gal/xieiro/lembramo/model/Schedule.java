package gal.xieiro.lembramo.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gal.xieiro.lembramo.ui.recurrencepicker.EventRecurrence;
import gal.xieiro.lembramo.util.Utils;

public class Schedule {

    private Calendar startSchedule;
    private EventRecurrence recurrence;
    private ArrayList<MedicineIntake> dailyIntakes;
    private ArrayList<MedicineIntake> schedule;


    public Schedule(String startDate, String recurrenceRule, String intakesRule) {
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
            if(scheduleWindow.isInRange(lastScheduledIntake)) {
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
        for(MedicineIntake intake : dailyIntakes) {
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
}
