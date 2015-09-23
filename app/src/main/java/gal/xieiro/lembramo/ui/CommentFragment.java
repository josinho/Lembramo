package gal.xieiro.lembramo.ui;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.model.Medicine;

public class CommentFragment extends Fragment {

    private final static String TAG = "CommentFragment";
    private static final String MEDICINE_PARAM = "Medicine";

    private Medicine mMedicine;
    private OnCommentFragmentListener mListener;


    public static CommentFragment newInstance(Medicine medicine) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(MEDICINE_PARAM, medicine);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View v = inflater.inflate(R.layout.fragment_comments, container, false);
        final EditText comment = (EditText) v.findViewById(R.id.comment);
        comment.setText(mMedicine.getComment());
        comment.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mMedicine.setComment(s.toString());
                    }
                }
        );

        // a ViewPagerActivity no le da tiempo de consultar la base de datos a tiempo
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        mMedicine = intent.getParcelableExtra("medicine");
                        comment.setText(mMedicine.getComment());
                    }
                },
                new IntentFilter("MedicineLoaded")
        );
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MEDICINE_PARAM, mMedicine);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCommentFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCommentFragmentListener");
        }
        Log.v(TAG, "onAttach()");
    }

    @Override
    public void onDetach() {
        Log.v(TAG, "onDetach()");
        super.onDetach();
        mListener.onCommentChange(mMedicine);
        mListener = null;
    }

    public interface OnCommentFragmentListener {
        void onCommentChange(Medicine medicine);
    }
}