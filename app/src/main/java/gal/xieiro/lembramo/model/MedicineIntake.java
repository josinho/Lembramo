package gal.xieiro.lembramo.model;

import java.util.Calendar;


public class MedicineIntake implements Comparable<MedicineIntake> {
    private Calendar date;
    private double dose;
    private boolean checked;

    public MedicineIntake(Calendar date) {
        this.date = date;
        this.dose = 1;
    }

    public MedicineIntake(MedicineIntake m) {
        date = m.getDate();
        dose = m.getDose();
        checked = m.isChecked();
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setDate(int year, int month, int day) {
        //ojo que los meses van de 0 a 11
        date.set(year, month, day);
    }

    public double getDose() {
        return dose;
    }

    public void setDose(double dose) {
        this.dose = dose;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int compareTo(MedicineIntake obj) {
        //comparar dos intakes primero por horas y llegado el caso por minutos
        //para ordenar los posibles intakes de un día
        int hourOfDay = date.get(Calendar.HOUR_OF_DAY);
        int hourComp = hourOfDay - obj.getDate().get(Calendar.HOUR_OF_DAY);
        if (hourComp < 0)
            return -1;
        else if (hourComp > 0)
            return 1;
        else {
            int minute = date.get(Calendar.MINUTE);
            int minuteComp = minute - obj.getDate().get(Calendar.MINUTE);
            if (minuteComp < 0)
                return -1;
            else if (minuteComp > 0)
                return 1;
            else
                return 0;
        }
    }
}
