package gal.xieiro.lembramo.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gal.xieiro.lembramo.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class HourFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private HourAdapter mHourAdapter;

    // TODO: Rename and change types of parameters
    public static HourFragment newInstance(String param1, String param2) {
        HourFragment fragment = new HourFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HourFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hour_list, container, false);

        RecyclerView hourList = (RecyclerView) view.findViewById(R.id.hourList);
        hourList.setHasFixedSize(true);
        hourList.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST)
        );

        ArrayList<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(i + ":00");
            hours.add(i + ":30");
        }
        mHourAdapter = new HourAdapter(hours);
        hourList.setAdapter(mHourAdapter);
        hourList.setLayoutManager(new LinearLayoutManager(getActivity()));


        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                TimePickerDialog tpd = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mHourAdapter.add(hourOfDay + ":" + minute);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(mHourAdapter.getItemId(position));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(long id);
    }


    private class HourAdapter extends RecyclerView.Adapter<HourAdapter.ViewHolder> {

        // View lookup cache
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView hour;

            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                hour = (TextView) itemView.findViewById(R.id.hour);
            }
        }

        private List<String> mHours;

        public HourAdapter(List<String> hours) {
            mHours = hours;
        }

        @Override
        public HourAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View itemView = inflater.inflate(R.layout.hour_item, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(HourAdapter.ViewHolder viewHolder, int position) {
            // Get the data model based on position
            String hour = mHours.get(position);

            // Set item views based on the data model
            TextView textView = viewHolder.hour;
            textView.setText(hour);
        }

        // Return the total count of items
        @Override
        public int getItemCount() {
            return mHours.size();
        }

        public void add(String hour) {
            mHours.set(0, hour);
            // Notify the adapter
            this.notifyItemInserted(0);
        }

    }
}
