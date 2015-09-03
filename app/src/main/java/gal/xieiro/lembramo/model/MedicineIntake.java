package gal.xieiro.lembramo.model;

import java.util.Calendar;


public class MedicineIntake implements Comparable<MedicineIntake> {
    private Calendar hour;
    private double dose;

    public MedicineIntake(Calendar hour) {
        this.hour = hour;
        this.dose = 1;
    }

    public Calendar getHour() {
        return hour;
    }

    public void setHour(Calendar hour) {
        this.hour = hour;
    }

    public double getDose() {
        return dose;
    }

    public void setDose(double dose) {
        this.dose = dose;
    }

    @Override
    public int compareTo(MedicineIntake obj) {
        int hourOfDay = hour.get(Calendar.HOUR_OF_DAY);
        int hourComp = hourOfDay - obj.getHour().get(Calendar.HOUR_OF_DAY);
        if (hourComp < 0)
            return -1;
        else if (hourComp > 0)
            return 1;
        else {
            int minute = hour.get(Calendar.MINUTE);
            int minuteComp = minute - obj.getHour().get(Calendar.MINUTE);
            if (minuteComp < 0)
                return -1;
            else if (minuteComp > 0)
                return 1;
            else
                return 0;
        }
    }
}
