package gal.xieiro.lembramo.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import gal.xieiro.lembramo.R;


public class NumberPickerDialogFragment extends DialogFragment {
    private static final String ARG_MIN_VALUE = "minValue";
    private static final String ARG_MAX_VALUE = "maxValue";
    private static final String ARG_INIT_VALUE = "initValue";
    private static final String ARG_TITLE = "title";

    NumberPickerDialogListener mListener;
    private int mMin, mMax, mInitValue;
    private String mTitle;
    private NumberPicker mNumberPicker;


    public NumberPickerDialogFragment() {
        // Required empty public constructor
    }

    public static NumberPickerDialogFragment newInstance(int min, int max, int value,
                                                         String title) {
        NumberPickerDialogFragment np = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        if (min <= max) {
            args.putInt(ARG_MIN_VALUE, min);
            args.putInt(ARG_MAX_VALUE, max);
        } else {
            args.putInt(ARG_MIN_VALUE, max);
            args.putInt(ARG_MAX_VALUE, min);
        }
        if (value < min || value > max) {
            args.putInt(ARG_INIT_VALUE, min);
        } else {
            args.putInt(ARG_INIT_VALUE, value);
        }
        args.putString(ARG_TITLE, title);
        np.setArguments(args);
        return np;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.number_picker, null);
        mNumberPicker = (NumberPicker) v.findViewById(R.id.numberPicker);

        if (getArguments() != null) {
            mMin = getArguments().getInt(ARG_MIN_VALUE);
            mMax = getArguments().getInt(ARG_MAX_VALUE);
            mInitValue = getArguments().getInt(ARG_INIT_VALUE);
            mTitle = getArguments().getString(ARG_TITLE);
        }

        if (savedInstanceState != null) {
            mInitValue = savedInstanceState.getInt("currentValue");
        }

        mNumberPicker.setMinValue(mMin);
        mNumberPicker.setMaxValue(mMax);
        mNumberPicker.setValue(mInitValue);
        mNumberPicker.setWrapSelectorWheel(true);

        return new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(v)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null)
                            mListener.onSet(mNumberPicker.getValue());
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null)
                            mListener.onCancel();
                    }
                })
                .create();
    }


    public void setOnNumberPickerDialogListener(NumberPickerDialogListener listener) {
        mListener = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentValue", mNumberPicker.getValue());
    }

    /*
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            try {
                mListener = (NumberPickerDialogListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() +
                        " must implement NumberPickerDialogListener");
            }

            Log.v("NumberPicker", activity.toString());
        }


            @Override
            public void onDetach() {
                super.onDetach();
                mListener = null;
            }
           */
    public interface NumberPickerDialogListener {
        void onSet(int value);

        void onCancel();
    }
}
