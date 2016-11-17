package gal.xieiro.lembramo.model;


import org.threeten.bp.LocalTime;

public class IntakeHelper implements Comparable<IntakeHelper> {

    private LocalTime time;
    private double dose;
    private boolean checked;

    public IntakeHelper(LocalTime time) {
        this.time = time;
        this.dose = 1;
    }

    public IntakeHelper(IntakeHelper m) {
        time = m.getTime();
        dose = m.getDose();
        checked = m.isChecked();
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
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
    public int compareTo(IntakeHelper other) {
        return time.compareTo(other.getTime());
    }
}
