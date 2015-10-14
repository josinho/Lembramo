package gal.xieiro.lembramo.model;


import java.util.Calendar;

public class CalendarRange {
    private Calendar start;
    private Calendar end;

    public CalendarRange(Calendar start, Calendar end) {
        this.start = start;
        this.end = end;
    }

    public boolean isInRange(Calendar c) {
        return c.after(start) && c.before(end);
    }

    public boolean isAfterRange(Calendar c) {
        return c.after(end);
    }

    public boolean isBeforeRange(Calendar c) {
        return c.before(start);
    }
}

