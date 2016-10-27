package gal.xieiro.lembramo.ui.preference;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import gal.xieiro.lembramo.util.TimeUtils;

public class TimePreference extends Preference {

    private Calendar mTime;
    private SimpleDateFormat mSdf;

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        mTime = Calendar.getInstance();
        mSdf = new SimpleDateFormat(TimeUtils.HOUR_FORMAT);
    }

    public String getTime() {
        return getPersistedString(mSdf.format(mTime.getTime()));
    }

    @Override
    protected void onClick() {
        TimePickerDialog tpd = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mTime.set(Calendar.MINUTE, minute);
                        String text = mSdf.format(mTime.getTime());

                        if (callChangeListener(text)) {
                            persistString(text);
                        }
                    }
                },
                mTime.get(Calendar.HOUR_OF_DAY),
                mTime.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getContext())
        );
        tpd.show();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        mTime.set(Calendar.HOUR_OF_DAY, TimeUtils.getHour(time));
        mTime.set(Calendar.MINUTE, TimeUtils.getMinute(time));
    }
}
