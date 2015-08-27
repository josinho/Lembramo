package gal.xieiro.lembramo.ui;

import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.ui.recurrencepicker.EventRecurrence;
import gal.xieiro.lembramo.ui.recurrencepicker.EventRecurrenceFormatter;
import gal.xieiro.lembramo.ui.recurrencepicker.RecurrencePickerDialog;
import gal.xieiro.lembramo.util.Utils;


public class Fragment1 extends Fragment
        implements RecurrencePickerDialog.OnRecurrenceSetListener {

    private NumberPicker np;
    private TextView mTextView;
    private Button mButton;
    private String mRrule;
    private EventRecurrence mEventRecurrence = new EventRecurrence();

    public static Fragment1 newInstance() {
        return new Fragment1();
    }

    public Fragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment1, container, false);

        final TextView tv = (TextView) v.findViewById(R.id.thetime);
        tv.setText(Utils.getCurrentTime());
        final Calendar c = Calendar.getInstance();
        tv.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog tpd = new TimePickerDialog(
                                getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        tv.setText(hourOfDay + ":" + minute);
                                    }
                                },
                                c.get(Calendar.HOUR_OF_DAY),
                                c.get(Calendar.MINUTE),
                                DateFormat.is24HourFormat(getActivity())
                        );
                        tpd.show();
                    }
                });

        np = (NumberPicker) v.findViewById(R.id.numberPicker2);
        np.setMinValue(0);
        np.setMaxValue(9);
        final String[] valores = {"Cero", "Uno", "Dos", "Tres", "Cuatro", "Cinco",
                "Seis", "Siete", "Ocho", "Nueve"};
        np.setDisplayedValues(valores);
        np.setWrapSelectorWheel(true);

        if (savedInstanceState != null)
            np.setValue(savedInstanceState.getInt("valor"));


        mTextView = (TextView) v.findViewById(R.id.textView);
        mButton = (Button) v.findViewById(R.id.button);
        mTextView.setText("Aquí se mostrará el resultado");

        mButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        RecurrencePickerDialog rpd =
                                (RecurrencePickerDialog) fm.findFragmentByTag("RecurrentPickerTAG");
                        if (rpd != null) {
                            rpd.dismiss();
                        }
                        rpd = new RecurrencePickerDialog();
                        rpd.setOnRecurrenceSetListener(Fragment1.this);
                        rpd.show(fm, "RecurrentPickerTAG");
                    }
                }
        );


        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("valor", np.getValue());
    }

    @Override
    public void onRecurrenceSet(String rrule) {
        mRrule = rrule;
        if (mRrule != null) {
            mEventRecurrence.parse(mRrule);
        }
        populateRepeats();
    }

    private void populateRepeats() {
        Resources r = getResources();
        String repeatString = "";
        if (!TextUtils.isEmpty(mRrule)) {
            repeatString = EventRecurrenceFormatter.getRepeatString(
                    getActivity(), r, mEventRecurrence, true);
        }

        mTextView.setText(mRrule + "\n" + repeatString);
    }
}
