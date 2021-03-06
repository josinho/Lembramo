package gal.xieiro.lembramo.util;

import android.text.format.Time;
import android.util.Log;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@SuppressWarnings("deprecation")
public class TimeUtils {
    private static final String TAG = "TimeUtils";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String HOUR_FORMAT = "HH:mm";
    public static final String DATE_HOUR_FORMAT = "dd/MM/yyyy HH:mm";

    private TimeUtils() {
    }

    public static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern(HOUR_FORMAT));
    }

    public static String getCurrentStringMillis() {
        return Long.valueOf(Instant.now().toEpochMilli()).toString();
    }

    public static String getStringDate(long millis) {
        return DateTimeFormatter.ofPattern(DATE_HOUR_FORMAT)
                .format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()));
    }

    public static String getStringDate(LocalDate date) {
        return DateTimeFormatter.ofPattern(DATE_FORMAT).format(date);
    }

    public static String getStringTime(LocalTime time) {
        return DateTimeFormatter.ofPattern(HOUR_FORMAT).format(time);
    }

    public static String getStringDateTime(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern(DATE_HOUR_FORMAT).format(dateTime);
    }

    public static long getMillis(LocalDate date) {
        return getMillis(date, LocalTime.MIDNIGHT);
    }

    public static long getMillis(LocalDate date, LocalTime time) {
        return date.atTime(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getMillis(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static int getHour(String time) {
        String[] pieces = time.split(":");
        return (Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");
        return (Integer.parseInt(pieces[1]));
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(DATE_HOUR_FORMAT));
    }

    public static LocalDate getDateFromMillis(long millis) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime getDateTimeFromMillis(long millis) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time, DateTimeFormatter.ofPattern(HOUR_FORMAT));
        } catch (DateTimeParseException e) {
            Log.e(TAG, "Time parsing error: " + e);
        }
        return null;
    }

    public static Time getTimeDateFromString(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Time t = new Time();
            t.set(sdf.parse(date).getTime());
            return t;
        } catch (ParseException e) {
            Log.e(TAG, "Time parsing error: " + e);
        }
        return null;
    }
}
