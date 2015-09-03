package gal.xieiro.lembramo.ui;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.ui.recurrencepicker.EventRecurrence;
import gal.xieiro.lembramo.ui.recurrencepicker.EventRecurrenceFormatter;
import gal.xieiro.lembramo.ui.recurrencepicker.RecurrencePickerDialog;
import gal.xieiro.lembramo.util.Utils;


public class FrecuencyFragment extends Fragment implements
        RecurrencePickerDialog.OnRecurrenceSetListener{

    private String mRule;
    private EventRecurrence mEventRecurrence;
    private TextView mRRule;

    public static FrecuencyFragment newInstance() {
        return new FrecuencyFragment();
    }

    public FrecuencyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventRecurrence = new EventRecurrence();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frecuency, container, false);

        final TextView fechaInicio = (TextView) view.findViewById(R.id.fechaInicio);
        fechaInicio.setText(Utils.getCurrentDate());

        fechaInicio.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar newCalendar = Calendar.getInstance();
                        DatePickerDialog dpd = new DatePickerDialog(
                                getActivity(),
                                new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        Calendar newDate = Calendar.getInstance();
                                        newDate.set(year, monthOfYear, dayOfMonth);
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        fechaInicio.setText(sdf.format(newDate.getTime()));
                                    }
                                },
                                newCalendar.get(Calendar.YEAR),
                                newCalendar.get(Calendar.MONTH),
                                newCalendar.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.show();
                    }
                }
        );

        final TextView horaInicio = (TextView) view.findViewById(R.id.horaInicio);
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


        mRRule = (TextView) view.findViewById(R.id.rrule);
        mRRule.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Time t = new Time();
                        t.setToNow();
                        mEventRecurrence.setStartDate(t);
                        Bundle b = new Bundle();
                        b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS,
                                t.toMillis(false));
                        b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE,
                                t.timezone);
                        b.putString(RecurrencePickerDialog.BUNDLE_RRULE, mRule);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        RecurrencePickerDialog rpd =
                                (RecurrencePickerDialog) fm.findFragmentByTag("RecurrentPickerTAG");
                        if (rpd != null) {
                            rpd.dismiss();
                        }
                        rpd = new RecurrencePickerDialog();
                        rpd.setArguments(b);
                        rpd.setOnRecurrenceSetListener(FrecuencyFragment.this);
                        rpd.show(fm, "RecurrentPickerTAG");
                    }
                }
        );

        return view;
    }

    @Override
    public void onRecurrenceSet(String rrule) {
        mRule = rrule;
        if (mRule != null) {
            mEventRecurrence.parse(mRule);
        }
        populateRepeats();
    }

    private void populateRepeats() {
        Resources r = getResources();
        String repeatString = "";
        if (!TextUtils.isEmpty(mRule)) {
            repeatString = EventRecurrenceFormatter.getRepeatString(
                    getActivity(), r, mEventRecurrence, true);
        }

        mRRule.setText(mRule + "\n" + repeatString);
    }
}