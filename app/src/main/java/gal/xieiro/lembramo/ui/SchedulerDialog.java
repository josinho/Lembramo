package gal.xieiro.lembramo.ui;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.model.IntakeHelper;
import gal.xieiro.lembramo.ui.component.MinMaxTextWatcher;
import gal.xieiro.lembramo.util.TimeUtils;


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
        final LocalTime now = LocalTime.now();
        horaInicio.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog tpd = new TimePickerDialog(
                                getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        mHoraInicio = DateTimeFormatter
                                                .ofPattern(TimeUtils.HOUR_FORMAT)
                                                .format(LocalTime.of(hourOfDay, minute));
                                        horaInicio.setText(mHoraInicio);
                                    }
                                },
                                now.getHour(),
                                now.getMinute(),
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

    private List<IntakeHelper> createPlanning() {
        List<IntakeHelper> plan = new ArrayList<>();
        IntakeHelper intake;
        LocalTime time;

        if (mHourFreqButton.isChecked()) {
            // hora incicial y sumar x horas
            time = TimeUtils.parseTime(mHoraInicio);
            int hours = Integer.parseInt(mHourFreq.getText().toString());
            int hour = time.getHour();

            while (hour < 24) {
                intake = new IntakeHelper(time);
                intake.setChecked(true);
                plan.add(intake);
                time = time.plusHours(hour);
                hour += hours;
            }
        } else {
            if (mTimesFreqButton.isChecked()) {
                // hora inicial y número de veces
                time = TimeUtils.parseTime(mHoraInicio);
                int freq = Integer.parseInt(mTimesFreq.getText().toString());
                long interval = time.until(LocalTime.of(23, 59), ChronoUnit.MINUTES) / freq;
                for (int i = 1; i <= freq; i++) {
                    intake = new IntakeHelper(time);
                    intake.setChecked(true);
                    plan.add(intake);
                    time = time.plusMinutes(interval);
                }
            } else {
                //mMealFreqButton.isChecked()
                String setting;

                //desayuno
                if (mBreakfast.isChecked()) {
                    setting = getSetting("breakfast_time", R.string.default_breakfast_time);
                    intake = new IntakeHelper(TimeUtils.parseTime(setting));
                    intake.setChecked(true);
                    plan.add(intake);
                }

                //comida
                if (mLunch.isChecked()) {
                    setting = getSetting("lunch_time", R.string.default_lunch_time);
                    intake = new IntakeHelper(TimeUtils.parseTime(setting));
                    intake.setChecked(true);
                    plan.add(intake);
                }

                //cena
                if (mDinner.isChecked()) {
                    setting = getSetting("dinner_time", R.string.default_dinner_time);
                    intake = new IntakeHelper(TimeUtils.parseTime(setting));
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
        void onPlanningSet(List<IntakeHelper> mIntakes);
    }

    public void setOnPlanningSetListener(OnPlanningSetListener l) {
        mPlanningSetListener = l;
    }
}
