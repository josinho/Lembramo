package gal.xieiro.lembramo.ui;


import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.model.Medicine;
import gal.xieiro.lembramo.model.MedicineIntake;
import gal.xieiro.lembramo.recurrence.EventRecurrence;
import gal.xieiro.lembramo.ui.recurrencepicker.EventRecurrenceFormatter;
import gal.xieiro.lembramo.ui.recurrencepicker.RecurrencePickerDialog;
import gal.xieiro.lembramo.util.Utils;


public class FrequencyFragment extends Fragment implements
        RecurrencePickerDialog.OnRecurrenceSetListener,
        SchedulerDialog.OnPlanningSetListener {


    private final static String TAG = "FrequencyFragment";
    private static final String MEDICINE_PARAM = "Medicine";
    private Resources mResources;
    private Medicine mMedicine;
    private EventRecurrence mEventRecurrence;
    private TextView mRRule;
    private IntakeFragment mIntakeFragment;

    public static FrequencyFragment newInstance(Medicine medicine) {
        FrequencyFragment fragment = new FrequencyFragment();
        Bundle args = new Bundle();
        args.putParcelable(MEDICINE_PARAM, medicine);
        fragment.setArguments(args);
        return fragment;
    }

    public FrequencyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventRecurrence = new EventRecurrence();

        mResources = getResources();
        if (savedInstanceState != null) {
            //venimos de una restauración
            mMedicine = savedInstanceState.getParcelable(MEDICINE_PARAM);
        } else {
            if (getArguments() != null) {
                // venimos de newInstance()
                mMedicine = getArguments().getParcelable(MEDICINE_PARAM);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frecuency, container, false);

        final TextView fechaInicio = (TextView) view.findViewById(R.id.fechaInicio);
        mMedicine.setStartDate(Utils.getCurrentDate());
        fechaInicio.setText(mMedicine.getStartDate());
        mEventRecurrence.setStartDate(Utils.getTimeDateFromString(mMedicine.getStartDate()));

        fechaInicio.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar now = Calendar.getInstance();
                        final Calendar startDate = Utils.getCalendarDateFromString(mMedicine.getStartDate());
                        final Calendar minDate = now.before(startDate) ? now : startDate;
                        DatePickerDialog dpd = new DatePickerDialog(
                                getActivity(),
                                new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        Calendar newDate = Calendar.getInstance();
                                        newDate.set(year, monthOfYear, dayOfMonth);
                                        if(newDate.before(minDate)) newDate = minDate;

                                        SimpleDateFormat sdf = new SimpleDateFormat(Utils.DATE_FORMAT);
                                        mMedicine.setStartDate(sdf.format(newDate.getTime()));
                                        fechaInicio.setText(mMedicine.getStartDate());
                                        mEventRecurrence.setStartDate(Utils.getTimeDateFromString(mMedicine.getStartDate()));
                                    }
                                },
                                startDate.get(Calendar.YEAR),
                                startDate.get(Calendar.MONTH),
                                startDate.get(Calendar.DAY_OF_MONTH)
                        );
                        //no permitir fechas anteriores a la ya fijada salvo si es posterior a hoy
                        dpd.getDatePicker().setMinDate(minDate.getTime().getTime());
                        dpd.show();
                    }
                }
        );

        mRRule = (TextView) view.findViewById(R.id.rrule);
        mRRule.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Time t = mEventRecurrence.startDate;
                        Bundle b = new Bundle();
                        b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS,
                                t.toMillis(false));
                        b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE,
                                t.timezone);
                        b.putString(RecurrencePickerDialog.BUNDLE_RRULE, mMedicine.getRecurrenceRule());
                        FragmentManager fm = getFragmentManager();
                        RecurrencePickerDialog rpd =
                                (RecurrencePickerDialog) fm.findFragmentByTag("RecurrentPickerTAG");
                        if (rpd != null) {
                            rpd.dismiss();
                        }
                        rpd = new RecurrencePickerDialog();
                        rpd.setArguments(b);
                        rpd.setOnRecurrenceSetListener(FrequencyFragment.this);
                        rpd.show(fm, "RecurrentPickerTAG");
                    }
                }
        );


        ImageView scheduleWizard = (ImageView) view.findViewById(R.id.schedulerWizard);


        Drawable normalDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_alarm_black_24dp);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.bpRed));
        scheduleWizard.setImageDrawable(wrapDrawable);


        scheduleWizard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getFragmentManager();

                        SchedulerDialog sd = (SchedulerDialog) fm.findFragmentByTag("SchedulerTAG");
                        if (sd != null) {
                            sd.dismiss();
                        }
                        sd = new SchedulerDialog();
                        sd.setOnPlanningSetListener(FrequencyFragment.this);
                        sd.show(fm, "SchedulerTAG");
                    }
                }
        );

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            mIntakeFragment = IntakeFragment.newInstance(mMedicine.getSchedule());
            fragmentTransaction.add(R.id.intake_container, mIntakeFragment);
            fragmentTransaction.commit();
        }

        // a ViewPagerActivity no le da tiempo de consultar la base de datos a tiempo
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        mMedicine = intent.getParcelableExtra("medicine");
                        fechaInicio.setText(mMedicine.getStartDate());
                        populateRepeats(mMedicine.getRecurrenceRule());
                        mIntakeFragment.addIntakes(mMedicine.getSchedule());
                    }
                },
                new IntentFilter("MedicineLoaded")
        );

        return view;
    }

    @Override
    public void onRecurrenceSet(String recurrenceRule) {
        mMedicine.setRecurrenceRule(recurrenceRule);
        populateRepeats(recurrenceRule);
    }

    private void populateRepeats(String recurrenceRule) {
        mEventRecurrence.setStartDate(Utils.getTimeDateFromString(mMedicine.getStartDate()));
        if (!TextUtils.isEmpty(recurrenceRule)) {
            mEventRecurrence.parse(recurrenceRule);
            mRRule.setText(EventRecurrenceFormatter.getRepeatString(
                    getActivity(),
                    mResources,
                    mEventRecurrence,
                    true
            ));
        } else {
            mRRule.setText(R.string.does_not_repeat);
        }
    }

    @Override
    public void onPlanningSet(List<MedicineIntake> intakes) {
        mIntakeFragment.addIntakes(intakes);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MEDICINE_PARAM, mMedicine);
    }

    public boolean validate() {
        String schedule = mIntakeFragment.getIntakes();
        if (TextUtils.isEmpty(schedule)) {
            Toast.makeText(
                    getActivity(),
                    getResources().getString(R.string.schedule_required),
                    Toast.LENGTH_LONG
            ).show();
            return false;
        }
        return true;
    }

    public Medicine getMedicine() {
        mMedicine.setSchedule(mIntakeFragment.getIntakes());
        return mMedicine;
    }
}