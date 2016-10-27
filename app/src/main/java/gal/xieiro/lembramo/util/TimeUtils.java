package gal.xieiro.lembramo.util;

import android.text.format.Time;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.threeten.bp.LocalDate;


public class TimeUtils {
    private static final String TAG = "TimeUtils";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String HOUR_FORMAT = "HH:mm";
    public static final String DATE_HOUR_FORMAT = "dd/MM/yyyy HH:mm";


    public TimeUtils() {
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date());
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(HOUR_FORMAT);
        return sdf.format(new Date());
    }

    public static String getStringDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_HOUR_FORMAT);
        return sdf.format(new Date(millis));
    }

    public static int getHour(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[1]));
    }

    public static Calendar parseTime(String time) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(HOUR_FORMAT);
        try {
            c.setTime(sdf.parse(time));
            return c;
        } catch (ParseException e) {
            Log.e(TAG, "Time parsing error: " + e);
        }
        return null;
    }

    public static Calendar getCalendarTimeFromString(String time) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, getHour(time));
        c.set(Calendar.MINUTE, getMinute(time));
        return c;
    }

    public static Calendar getCalendarDateFromString(String date) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            c.setTime(sdf.parse(date));
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            return c;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Calendar getCalendarDateFromMillis(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c;
    }

    public static Time getTimeDateFromString(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Time t = new Time();
            t.set(sdf.parse(date).getTime());
            return t;
        } catch(ParseException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Time getTimeDateFromMillis(long millis) {
        Time t = new Time();
        t.set(millis);
        return t;
    }
}
