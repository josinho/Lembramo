package gal.xieiro.lembramo.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gal.xieiro.lembramo.ui.recurrencepicker.EventRecurrence;
import gal.xieiro.lembramo.util.Utils;

public class Schedule {

    private Calendar startDate;
    private EventRecurrence recurrence;
    private ArrayList<MedicineIntake> dailyIntakes;


    public Schedule(String startDate, String recurrence, String dailyIntakes) {
        this.startDate = Utils.getCalendarDateFromString(startDate);

        this.recurrence = new EventRecurrence();
        this.recurrence.setStartDate(Utils.getTimeDateFromString(startDate));
        if(recurrence != null) {
            this.recurrence.parse(recurrence);
        }

        this.dailyIntakes = new ArrayList<>();
        parseDailyIntakes(dailyIntakes);
    }

    private void parseDailyIntakes(String dailyIntakes) {
        if (!TextUtils.isEmpty(dailyIntakes)) {
            String[] intakeStrings = dailyIntakes.split(";");
            for (String intakeString : intakeStrings) {
                String[] intake = intakeString.split(",");
                Calendar hour = Utils.parseTime(intake[0]);
                MedicineIntake medicineIntake = new MedicineIntake(hour);
                medicineIntake.setDose(Double.valueOf(intake[1]));
                this.dailyIntakes.add(medicineIntake);
            }
        }
    }

    public int getDailyIntakesCount() {
        return dailyIntakes.size();
    }

    public List<Calendar> getScheduledDates(CalendarRange range) {
        return null;
    }
}
