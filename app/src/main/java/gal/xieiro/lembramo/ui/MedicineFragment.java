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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.model.Medicine;

public class MedicineFragment extends Fragment {

    private static final String TAG = "MedicineFragment";
    private static final String MEDICINE_PARAM = "Medicine";
    private Medicine mMedicine;
    private EditText mName;
    private ImageSelectorFragment mBoxFragment, mPillFragment;

    public static MedicineFragment newInstance(Medicine medicine) {
        MedicineFragment fragment = new MedicineFragment();
        Bundle args = new Bundle();
        args.putParcelable(MEDICINE_PARAM, medicine);
        fragment.setArguments(args);
        return fragment;
    }

    public MedicineFragment() {
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
        View view = inflater.inflate(R.layout.fragment_medicine, container, false);

        mName = (EditText) view.findViewById(R.id.medicineName);
        mName.setText(mMedicine.getName());
        mName.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mMedicine.setName(s.toString());
                    }
                }
        );

        //colocar los fragments con las imágenes por defecto
        mBoxFragment = ImageSelectorFragment.newInstance(R.drawable.caja, mMedicine.getPillboxImage());
        mPillFragment = ImageSelectorFragment.newInstance(R.drawable.pastilla, mMedicine.getPillImage());
        getFragmentManager()
                .beginTransaction()
                .add(R.id.boxContainer, mBoxFragment)
                .add(R.id.pillContainer, mPillFragment)
                .commit();


        // a ViewPagerActivity no le da tiempo de consultar la base de datos a tiempo
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        mMedicine = intent.getParcelableExtra("medicine");
                        mName.setText(mMedicine.getName());
                        mBoxFragment.setImage(mMedicine.getPillboxImage());
                        mPillFragment.setImage(mMedicine.getPillImage());
                    }
                },
                new IntentFilter("MedicineLoaded")
        );

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MEDICINE_PARAM, mMedicine);
    }

    public boolean validate() {
        if (TextUtils.isEmpty(mMedicine.getName())) {
            mName.setError(getResources().getString(R.string.required));
            return false;
        }
        return true;
    }

    public Medicine getMedicine() {
        return mMedicine;
    }
}