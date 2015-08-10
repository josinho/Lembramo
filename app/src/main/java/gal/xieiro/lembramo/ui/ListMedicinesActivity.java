package gal.xieiro.lembramo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.db.DBAdapter;
import gal.xieiro.lembramo.db.DBContract;


public class ListMedicinesActivity extends BaseActivity {

    private ListView mListaMedicamentos;
    private DBAdapter mDBAdapter;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //relate the listView from java to the one created in xml
        mListaMedicamentos = (ListView) findViewById(R.id.listaMedicamentos);
        mListaMedicamentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListMedicinesActivity.this,
                        "Row " + position + " clicked", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cargar la activity para crear un medicamento
                Intent intent = new Intent(ListMedicinesActivity.this, DetailMedicineActivity.class);
                startActivity(intent);
            }
        });


        //Preparar el origen de los datos de la lista
        String[] from = {
                DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO,
                DBContract.Medicamentos.COLUMN_NAME_NAME,
                DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO,
                DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO
        };
        int[] to = {R.id.ruta, R.id.nombre, R.id.imagenCaja, R.id.imagenPastilla};

        mAdapter = new SimpleCursorAdapter(
                this, //contexto actual
                R.layout.medicine_item, //el layout de un medicamento
                null, //todavía no hay un cursor
                from, //las columnas del cursor a usar
                to, //los campos del layout correpondientes
                0 //no flags
        );
        mListaMedicamentos.setAdapter(mAdapter);

        //obtener el cursor con los medicamentos en segundo plano
        mDBAdapter = new DBAdapter(this);
        new AsyncDBTask().execute(mDBAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDBAdapter.close();
    }

    @Override
    protected int getLayoutResource() {
        // indicar el layout de esta activity, necesario para BaseActivity
        return R.layout.activity_list_medicines;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medicamentos, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class AsyncDBTask extends AsyncTask<DBAdapter, Void, Cursor> {

        @Override
        protected Cursor doInBackground(DBAdapter... param) {
            DBAdapter dbAdapter = param[0];
            try {
                dbAdapter.open();
                return dbAdapter.getAllMedicamentos();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor c) {
            mAdapter.changeCursor(c);
            //  adapter.notifyDataSetChanged();
        }
    }
}
