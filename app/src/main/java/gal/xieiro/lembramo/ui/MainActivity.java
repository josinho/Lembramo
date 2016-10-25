package gal.xieiro.lembramo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.MedicineContentProvider;
import gal.xieiro.lembramo.model.Medicine;
import gal.xieiro.lembramo.recurrence.DateException;
import gal.xieiro.lembramo.recurrence.RecurrenceProcessor;
import gal.xieiro.lembramo.recurrence.RecurrenceSet;
import gal.xieiro.lembramo.util.TimeUtils;
import gal.xieiro.lembramo.util.Utils;


public class MainActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private Medicine mMedicine;
    private TextView mResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationIcon(R.mipmap.ic_launcher);
        mResultados = (TextView) findViewById(R.id.resultados);
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

    public void testExpansion(View view) {
        mMedicine = new Medicine(Utils.NO_ID);
        long id = 1;
        Bundle bundle = new Bundle();
        bundle.putLong(DBContract.Medicines._ID, id);
        getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long key = args.getLong(DBContract.Medicines._ID);
        String uri = MedicineContentProvider.CONTENT_URI.toString() + "/" + key;
        String[] projection = {
                DBContract.Medicines._ID,
                DBContract.Medicines.COLUMN_NAME_NAME,
                DBContract.Medicines.COLUMN_NAME_STARTDATE,
                DBContract.Medicines.COLUMN_NAME_ENDDATE,
                DBContract.Medicines.COLUMN_NAME_RECURRENCE,
                DBContract.Medicines.COLUMN_NAME_SCHEDULE
        };
        return new CursorLoader(this, Uri.parse(uri), projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c != null) {
            c.moveToFirst();
            mMedicine.setId(c.getLong(c.getColumnIndex(DBContract.Medicines._ID)));
            mMedicine.setName(c.getString(c.getColumnIndex(DBContract.Medicines.COLUMN_NAME_NAME)));
            mMedicine.setStartDate(c.getLong(c.getColumnIndex(DBContract.Medicines.COLUMN_NAME_STARTDATE)));
            mMedicine.setEndDate(c.getLong(c.getColumnIndex(DBContract.Medicines.COLUMN_NAME_ENDDATE)));
            mMedicine.setRecurrenceRule(c.getString(c.getColumnIndex(DBContract.Medicines.COLUMN_NAME_RECURRENCE)));
            mMedicine.setSchedule(c.getString(c.getColumnIndex(DBContract.Medicines.COLUMN_NAME_SCHEDULE)));
        }

        String resultado = "ID = " + mMedicine.getId() + "\n" +
                "Name = " + mMedicine.getName() + "\n" +
                "StartDate = " + TimeUtils.getStringDate(mMedicine.getStartDate()) + "\n" +
                "EndDate = " + TimeUtils.getStringDate(mMedicine.getEndDate()) + "\n" +
                "Recurrence = " + mMedicine.getRecurrenceRule() + "\n" +
                "Schedule = " + mMedicine.getSchedule() + "\n\n";

        Time dtStart = TimeUtils.getTimeDateFromMillis(mMedicine.getStartDate());
        RecurrenceSet recurrenceSet = new RecurrenceSet(mMedicine.getRecurrenceRule(), null, null, null);
        long rangeStartMillis = dtStart.toMillis(false); //Utils.getTimeDateFromString("01/12/2016").toMillis(false);
        long rangeEndMilllis = -1; //Utils.getTimeDateFromString("01/03/2017").toMillis(false);

        try {
            RecurrenceProcessor rp = new RecurrenceProcessor();
            long[] dates = rp.expand(dtStart, recurrenceSet, rangeStartMillis, rangeEndMilllis);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (long l : dates) {
                resultado = resultado + sdf.format(l) + "\n";
            }

            resultado = resultado + "Last ocurrence: " + sdf.format(rp.getLastOccurence(dtStart,recurrenceSet));
        } catch (DateException de) {
            Log.i("MainActivity", "DateException al hacer expand");
        }
        mResultados.setText(resultado);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
