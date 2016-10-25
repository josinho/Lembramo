package gal.xieiro.lembramo.ui;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.model.MedicineIntake;
import gal.xieiro.lembramo.ui.component.MinMaxTextWatcher;
import gal.xieiro.lembramo.util.TimeUtils;
import gal.xieiro.lembramo.util.Utils;


public class SchedulerDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "SchedulerDialog";
    private static final int INTERVAL_MAX = 12;
    private static final int HOUR_FREQ_DEFAULT = 8;
    private static final int TIMES_FREQ_DEFAULT = 3;

    private View mInitialHourGroup, mMealGroup;
    private RadioButton mHourFreqButton, mTimesFreqButton, mMealFreqButton;
    private EditText mHourFreq, mTimesFreq;
    private String mHoraInicio;
    private OnPlanningSetListener mPlanningSetListener;
    private CheckBox mBreakfast, mLunch, mDinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.scheduler, container, true);

        //no funciona el radiogroup porque tenemos otros layouts en medio de los radiobuttons
        //hay que hacer el control de selección de los RadioButton a mano

        mHourFreqButton = (RadioButton) view.findViewById(R.id.hourFreq);
        mHourFreqButton.setOnClickListener(this);
        mTimesFreqButton = (RadioButton) view.findViewById(R.id.timesFreq);
        mTimesFreqButton.setOnClickListener(this);
        mMealFreqButton = (RadioButton) view.findViewById(R.id.mealFreq);
        mMealFreqButton.setOnClickListener(this);

        mInitialHourGroup = view.findViewById(R.id.initialHourGroup);
        mMealGroup = view.findViewById(R.id.mealGroup);

        mBreakfast = (CheckBox) view.findViewById(R.id.breakfast);
        mBreakfast.setText(mBreakfast.getText() + " " +
                getSetting("breakfast_time", R.string.default_breakfast_time));
        mLunch = (CheckBox) view.findViewById(R.id.lunch);
        mLunch.setText(mLunch.getText() + " " +
                getSetting("lunch_time", R.string.default_lunch_time));
        mDinner = (CheckBox) view.findViewById(R.id.dinner);
        mDinner.setText(mDinner.getText() + " " +
                getSetting("dinner_time", R.string.default_dinner_time));

        mHourFreq = (EditText) view.findViewById(R.id.interval1);
        mHourFreq.addTextChangedListener(
                new MinMaxTextWatcher(1, HOUR_FREQ_DEFAULT, INTERVAL_MAX) {
                    @Override
                    protected void onChange(int v) {

                    }
                });

        mTimesFreq = (EditText) view.findViewById(R.id.interval2);
        mTimesFreq.addTextChangedListener(
                new MinMaxTextWatcher(1, TIMES_FREQ_DEFAULT, INTERVAL_MAX) {
                    @Override
                    protected void onChange(int v) {

                    }
                });

        mHoraInicio = TimeUtils.getCurrentTime();
        final TextView horaInicio = (TextView) view.findViewById(R.id.horaInicio);
        horaInicio.setText(mHoraInicio);
        final Calendar c = Calendar.getInstance();
        horaInicio.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog tpd = new TimePickerDialog(
                                getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        mHoraInicio = sdf.format(c.getTime());
                                        horaInicio.setText(mHoraInicio);
                                    }
                                },
                                c.get(Calendar.HOUR_OF_DAY),
                                c.get(Calendar.MINUTE),
                                DateFormat.is24HourFormat(getActivity())
                        );
                        tpd.show();
                    }
                }
        );

        TextView labelHours = (TextView) view.findViewById(R.id.lblHours);
        labelHours.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHourFreqButton.performClick();
                    }
                }
        );

        TextView labelTimes = (TextView) view.findViewById(R.id.lblTimes);
        labelTimes.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTimesFreqButton.performClick();
                    }
                }
        );

        view.findViewById(R.id.set_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_button).setOnClickListener(this);

        //marcar el primero TODO excepto que vengamos de savedInstanceState
        //valores iniciales
        mHourFreqButton.setChecked(true);
        mTimesFreq.setEnabled(false);
        mTimesFreq.setText("" + TIMES_FREQ_DEFAULT);
        mHourFreq.setText("" + HOUR_FREQ_DEFAULT);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hourFreq:
                mTimesFreqButton.setChecked(false);
                mMealFreqButton.setChecked(false);
                mHourFreqButton.setChecked(true);
                mInitialHourGroup.setVisibility(View.VISIBLE);
                mMealGroup.setVisibility(View.GONE);
                mHourFreq.setEnabled(true);
                mTimesFreq.setEnabled(false);
                break;
            case R.id.timesFreq:
                mHourFreqButton.setChecked(false);
                mMealFreqButton.setChecked(false);
                mTimesFreqButton.setChecked(true);
                mInitialHourGroup.setVisibility(View.VISIBLE);
                mMealGroup.setVisibility(View.GONE);
                mHourFreq.setEnabled(false);
                mTimesFreq.setEnabled(true);
                break;
            case R.id.mealFreq:
                mHourFreqButton.setChecked(false);
                mTimesFreqButton.setChecked(false);
                mMealFreqButton.setChecked(true);
                mInitialHourGroup.setVisibility(View.GONE);
                mMealGroup.setVisibility(View.VISIBLE);
                mHourFreq.setEnabled(false);
                mTimesFreq.setEnabled(false);
                break;
            case R.id.set_button:
                if (mPlanningSetListener != null) {
                    mPlanningSetListener.onPlanningSet(createPlanning());
                }
                dismiss();
                break;
            case R.id.cancel_button:
                if (mPlanningSetListener != null) {
                    mPlanningSetListener.onPlanningSet(null);
                }
                dismiss();
                break;
        }
    }

    private List<MedicineIntake> createPlanning() {
        List<MedicineIntake> plan = new ArrayList<>();
        MedicineIntake intake;
        Calendar time;
        int hour, minute;


        if (mHourFreqButton.isChecked()) {
            // hora incicial y sumar x horas
            time = TimeUtils.getCalendarTimeFromString(mHoraInicio);
            intake = new MedicineIntake(time);
            intake.setChecked(true);
            plan.add(intake);

            hour = time.get(Calendar.HOUR_OF_DAY);
            minute = time.get(Calendar.MINUTE);
            int hours = Integer.parseInt(mHourFreq.getText().toString());

            hour += hours;
            while (hour < 24) {
                time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, hour);
                time.set(Calendar.MINUTE, minute);
                intake = new MedicineIntake(time);
                intake.setChecked(true);
                plan.add(intake);
                hour += hours;
            }
        } else {
            if (mTimesFreqButton.isChecked()) {
                // hora inicial y número de veces
                time = TimeUtils.getCalendarTimeFromString(mHoraInicio);
                intake = new MedicineIntake(time);
                intake.setChecked(true);
                plan.add(intake);

                hour = time.get(Calendar.HOUR_OF_DAY);
                minute = time.get(Calendar.MINUTE);

                int freq = Integer.parseInt(mTimesFreq.getText().toString());
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                try {
                    Date initial = sdf.parse(hour + ":" + minute);
                    long interval = (sdf.parse("23:59").getTime() - initial.getTime()) / freq;
                    Date other = new Date(initial.getTime());

                    for (int i = 1; i < freq; i++) {
                        other = new Date(other.getTime() + interval);
                        time = Calendar.getInstance();
                        time.setTime(other);
                        intake = new MedicineIntake(time);
                        intake.setChecked(true);
                        plan.add(intake);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing difference" + e);
                }
            } else {
                //mMealFreqButton.isChecked()
                String setting;

                //desayuno
                if (mBreakfast.isChecked()) {
                    setting = getSetting("breakfast_time", R.string.default_breakfast_time);
                    intake = new MedicineIntake(TimeUtils.getCalendarTimeFromString(setting));
                    intake.setChecked(true);
                    plan.add(intake);
                }

                //comida
                if (mLunch.isChecked()) {
                    setting = getSetting("lunch_time", R.string.default_lunch_time);
                    intake = new MedicineIntake(TimeUtils.getCalendarTimeFromString(setting));
                    intake.setChecked(true);
                    plan.add(intake);
                }

                //cena
                if (mDinner.isChecked()) {
                    setting = getSetting("dinner_time", R.string.default_dinner_time);
                    intake = new MedicineIntake(TimeUtils.getCalendarTimeFromString(setting));
                    intake.setChecked(true);
                    plan.add(intake);
                }
            }
        }
        return plan;
    }

    private String getSetting(String key, int defaultId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return settings.getString(key, getString(defaultId));
    }

    public interface OnPlanningSetListener {
        void onPlanningSet(List<MedicineIntake> mIntakes);
    }

    public void setOnPlanningSetListener(OnPlanningSetListener l) {
        mPlanningSetListener = l;
    }
}
