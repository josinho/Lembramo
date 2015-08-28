package gal.xieiro.lembramo.ui;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.ui.recurrencepicker.RecurrencePickerDialog;


public class MainActivity extends BaseActivity
        implements RecurrencePickerDialog.OnRecurrenceSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationIcon(R.mipmap.ic_launcher);
        pruebaRecurrencePicker(savedInstanceState);
        RecurrencePickerDialog rpd =
                (RecurrencePickerDialog) getSupportFragmentManager()
                        .findFragmentByTag("RecurrentPickerTAG");
        if (rpd != null) {
            rpd.setOnRecurrenceSetListener(this);
        }

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    public void editMedicamentos(View view) {
        //cargar la pantalla para editar medicamentos
        Intent intent = new Intent(this, ListMedicinesActivity.class);
        startActivity(intent);
    }

    public void startViewPager(View view) {
        Intent intent = new Intent(this, ViewPagerActivity.class);
        startActivity(intent);
    }

    private void pruebaRecurrencePicker(Bundle bundle) {
        Button mButton = (Button) findViewById(R.id.button2);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getSupportFragmentManager();
                        RecurrencePickerDialog rpd =
                                (RecurrencePickerDialog) fm.findFragmentByTag("RecurrentPickerTAG");
                        if (rpd != null) {
                            rpd.dismiss();
                        }
                        rpd = new RecurrencePickerDialog();
                        Log.v(TAG, "MainActivity: Creating RecurrencePickerDialog");
                        rpd.setOnRecurrenceSetListener(MainActivity.this);
                        rpd.show(fm, "RecurrentPickerTAG");
                    }
                }
        );
    }

    @Override
    public void onRecurrenceSet(String rrule) {

    }
}
