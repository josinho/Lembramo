package gal.xieiro.lembramo.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.util.Utils;


public class SchedulerDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "SchedulerDialog";
    private static final int INTERVAL_MAX = 12;
    private static final int HOUR_FREQ_DEFAULT = 8;
    private static final int TIMES_FREQ_DEFAULT = 3;

    private View mView, mInitialHourGroup, mMealGroup;
    private EditText mHourFreq, mTimesFreq;
    private RadioButton mHourFreqButton, mTimesFreqButton, mMealFreqButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mView = inflater.inflate(R.layout.scheduler, container, true);

        //no funciona el radiogroup porque tenemos otros layouts en medio de los radiobuttons

        mHourFreqButton = (RadioButton) mView.findViewById(R.id.hourFreq);
        mHourFreqButton.setOnClickListener(this);
        mTimesFreqButton = (RadioButton) mView.findViewById(R.id.timesFreq);
        mTimesFreqButton.setOnClickListener(this);
        mMealFreqButton = (RadioButton) mView.findViewById(R.id.mealFreq);
        mMealFreqButton.setOnClickListener(this);

        mInitialHourGroup = mView.findViewById(R.id.initialHourGroup);
        mMealGroup = mView.findViewById(R.id.mealGroup);


        mHourFreq = (EditText) mView.findViewById(R.id.interval1);
        mHourFreq.addTextChangedListener(
                new MinMaxTextWatcher(1, HOUR_FREQ_DEFAULT, INTERVAL_MAX) {
                    @Override
                    void onChange(int v) {

                    }
                });

        mTimesFreq = (EditText) mView.findViewById(R.id.interval2);
        mTimesFreq.addTextChangedListener(
                new MinMaxTextWatcher(1, TIMES_FREQ_DEFAULT, INTERVAL_MAX) {
                    @Override
                    void onChange(int v) {

                    }
                });

        final TextView horaInicio = (TextView) mView.findViewById(R.id.horaInicio);
        horaInicio.setText(Utils.getCurrentTime());
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
                                        horaInicio.setText(sdf.format(c.getTime()));
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

        //marcar el primero TODO excepto que vengamos de savedInstanceState
        //valores iniciales
        mHourFreqButton.setChecked(true);
        mTimesFreq.setEnabled(false);
        mTimesFreq.setText("" + TIMES_FREQ_DEFAULT);
        mHourFreq.setText("" + HOUR_FREQ_DEFAULT);

        mView.findViewById(R.id.set_button).setOnClickListener(this);
        mView.findViewById(R.id.cancel_button).setOnClickListener(this);

        return mView;
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
                Toast.makeText(getActivity(), "Aceptar", Toast.LENGTH_LONG).show();
                break;
            case R.id.cancel_button:
                Toast.makeText(getActivity(), "Cancelar", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
