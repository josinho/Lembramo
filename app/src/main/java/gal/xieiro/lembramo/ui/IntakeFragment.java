package gal.xieiro.lembramo.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.model.MedicineIntake;
import gal.xieiro.lembramo.ui.component.DividerItemDecoration;
import gal.xieiro.lembramo.ui.component.DosePicker;
import gal.xieiro.lembramo.util.TimeUtils;


public class IntakeFragment extends Fragment {

    private static final String SCHEDULE = "schedule";

    private String mSchedule;
    private RecyclerView mIntakeList;
    private IntakeAdapter mIntakeAdapter;

    public static IntakeFragment newInstance(String schedule) {
        IntakeFragment fragment = new IntakeFragment();
        Bundle args = new Bundle();
        args.putString(SCHEDULE, schedule);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IntakeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSchedule = getArguments().getString(SCHEDULE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intake_list, container, false);

        mIntakeList = (RecyclerView) view.findViewById(R.id.hourList);
        mIntakeList.setHasFixedSize(true);
        mIntakeList.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST)
        );

        mIntakeAdapter = new IntakeAdapter(setInitialIntakes());
        mIntakeList.setAdapter(mIntakeAdapter);
        mIntakeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        addIntakes(mSchedule);


        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), mIntakeAdapter.getIntakes(), Toast.LENGTH_LONG).show();

                final Calendar c = Calendar.getInstance();
                TimePickerDialog tpd = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                MedicineIntake med = new MedicineIntake(c);
                                med.setChecked(true);
                                int position = mIntakeAdapter.add(med);
                                mIntakeList.scrollToPosition(position);
                            }
                        },
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(getActivity())
                );
                tpd.show();
            }
        });

        return view;
    }

    private ArrayList<MedicineIntake> setInitialIntakes() {
        ArrayList<MedicineIntake> intakes = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            Calendar hour = Calendar.getInstance();
            hour.set(Calendar.HOUR_OF_DAY, i);
            hour.set(Calendar.MINUTE, 0);
            Calendar halfHour = Calendar.getInstance();
            halfHour.set(Calendar.HOUR_OF_DAY, i);
            halfHour.set(Calendar.MINUTE, 30);

            intakes.add(new MedicineIntake(hour));
            intakes.add(new MedicineIntake(halfHour));
        }
        return intakes;
    }

    public void addIntakes(String schedule) {
        if (!TextUtils.isEmpty(schedule)) {
            String[] intakeStrings = schedule.split(";");
            for (String intakeString : intakeStrings) {
                String[] intake = intakeString.split(",");
                Calendar hour = TimeUtils.parseTime(intake[0]);
                MedicineIntake medicineIntake = new MedicineIntake(hour);
                medicineIntake.setDose(Double.valueOf(intake[1]));
                medicineIntake.setChecked(true);
                mIntakeAdapter.add(medicineIntake);
            }
        }
    }

    public void addIntakes(List<MedicineIntake> intakes) {
        int pos = 0;
        if (intakes != null) {
            for (int i = intakes.size() - 1; i >= 0; i--) {
                MedicineIntake intake = intakes.get(i);
                pos = mIntakeAdapter.add(intake);
            }
            mIntakeList.scrollToPosition(pos);
        }
    }

    public String getIntakes() {
        List<MedicineIntake> intakes = mIntakeAdapter.getIntakes();
        StringBuilder s = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.HOUR_FORMAT);

        for (int i = 0; i < intakes.size(); i++) {
            MedicineIntake intake = intakes.get(i);
            if (intake.isChecked()) {
                s.append(sdf.format(intake.getDate().getTime())).
                        append(",").append(intake.getDose()).append(";");
            }
        }
        return s.toString().replaceAll(";$", "");
    }

    private class IntakeAdapter extends RecyclerView.Adapter<IntakeAdapter.ViewHolder> {

        // View lookup cache
        public class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox hour;
            DosePicker dose;

            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);
                hour = (CheckBox) itemView.findViewById(R.id.hour);
                dose = (DosePicker) itemView.findViewById(R.id.dosePicker);
            }
        }

        private List<MedicineIntake> mIntakes;

        public IntakeAdapter(List<MedicineIntake> intakes) {
            mIntakes = intakes;
        }

        @Override
        public IntakeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View itemView = inflater.inflate(R.layout.intake_item, parent, false);

            // Return a new holder instance
            return new ViewHolder(itemView);
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(final IntakeAdapter.ViewHolder viewHolder, int position) {
            // Get the data model based on position
            final MedicineIntake intake = mIntakes.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.HOUR_FORMAT);
            viewHolder.hour.setText(sdf.format(intake.getDate().getTime()));

            if (intake.isChecked()) {
                viewHolder.hour.setChecked(true);
                viewHolder.dose.setValue(intake.getDose());
                viewHolder.dose.setVisibility(View.VISIBLE);
            } else {
                viewHolder.hour.setChecked(false);
                viewHolder.dose.setVisibility(View.INVISIBLE);
            }

            viewHolder.hour.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (((CheckBox) v).isChecked()) {
                                intake.setChecked(true);
                                viewHolder.dose.setValue(intake.getDose());
                                viewHolder.dose.setVisibility(View.VISIBLE);
                            } else {
                                intake.setChecked(false);
                                viewHolder.dose.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
            );


            viewHolder.dose.setOnDoseChangeListener(new DosePicker.OnDoseChangeListener() {
                @Override
                public void onDoseChange(double dose) {
                    intake.setDose(dose);
                }
            });
        }

        // Returns the total count of items
        @Override
        public int getItemCount() {
            return mIntakes.size();
        }

        // Returns the insertion position
        public int add(MedicineIntake intake) {
            int i, comparison;

            //insertar en orden
            for (i = 0; i < mIntakes.size(); i++) {
                comparison = mIntakes.get(i).compareTo(intake);
                if (comparison < 0) continue;
                if (comparison == 0) {
                    mIntakes.set(i, intake);
                    this.notifyItemChanged(i);
                    return i;
                }
                break;
            }
            mIntakes.add(i, intake);
            this.notifyItemInserted(i);
            return i;
        }

        public List<MedicineIntake> getIntakes() {
            return mIntakes;
        }
    }
}
