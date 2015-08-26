package gal.xieiro.lembramo.ui;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.util.Utils;


public class Fragment2 extends Fragment {

    public static Fragment2 newInstance() {
        return new Fragment2();
    }

    public Fragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);

        final TextView fechaInicio = (TextView) view.findViewById(R.id.fechainicio);
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


        RadioButton rb1 = (RadioButton) view.findViewById(R.id.radioButton1);
        rb1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        NumberPickerDialogFragment dialog =
                                NumberPickerDialogFragment.newInstance(1, 24, 3, "Veces al día");
                        dialog.setOnNumberPickerDialogListener(
                                new NumberPickerDialogFragment.NumberPickerDialogListener() {
                                    @Override
                                    public void onSet(int value) {
                                        Toast.makeText(Fragment2.this.getActivity(),
                                                "Valor: " + value, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancel() {
                                        Toast.makeText(Fragment2.this.getActivity(),
                                                "Canceló", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                        dialog.show(getActivity().getSupportFragmentManager(), "numberPicker");
                    }
                }
        );

        RadioButton rb2 = (RadioButton) view.findViewById(R.id.radioButton2);
        rb2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        NumberPickerDialogFragment dialog =
                                NumberPickerDialogFragment.newInstance(1, 24, 8, "Cada x horas");
                        dialog.setOnNumberPickerDialogListener(
                                new NumberPickerDialogFragment.NumberPickerDialogListener() {
                                    @Override
                                    public void onSet(int value) {
                                        Toast.makeText(Fragment2.this.getActivity(),
                                                "Valor: " + value, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancel() {
                                        Toast.makeText(Fragment2.this.getActivity(),
                                                "Canceló", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                        dialog.show(getActivity().getSupportFragmentManager(), "numberPicker");
                    }
                }
        );

        return view;
    }


}