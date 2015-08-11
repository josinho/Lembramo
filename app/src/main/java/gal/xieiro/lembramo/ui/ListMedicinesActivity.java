package gal.xieiro.lembramo.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.db.DBAdapter;
import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.util.ImageUtils;


public class ListMedicinesActivity extends BaseActivity {

    private static final long NO_ID = -1;

    private ListView mListaMedicamentos;
    private DBAdapter mDBAdapter;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //relate the listView from java to the one created in xml
        mListaMedicamentos = (ListView) findViewById(R.id.listaMedicamentos);
        mListaMedicamentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //cargar la activity para editar un medicamento
                startDetailActivity(id);
            }
        });

        mListaMedicamentos.setLongClickable(true);
        mListaMedicamentos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListMedicinesActivity.this,
                        "LONG CLICK | Row " + position, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cargar la activity para crear un medicamento
                startDetailActivity(NO_ID);
            }
        });
    }

    private void startDetailActivity(long id) {
        Intent intent = new Intent(ListMedicinesActivity.this, DetailMedicineActivity.class);
        if(id != NO_ID) {
            intent.putExtra("id", id);
        }
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        //obtener el cursor con los medicamentos en segundo plano
        mDBAdapter = new DBAdapter(this);
        new AsyncDBTask().execute(mDBAdapter);

        mAdapter = new ListAdapter(this, null);
        mListaMedicamentos.setAdapter(mAdapter);
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

    private class ListAdapter extends ResourceCursorAdapter {

        public ListAdapter(Context context, Cursor cursor) {
            super(context, R.layout.medicine_item, cursor, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String path;
            int targetWidth, targetHeight, columnIndex;


            if (cursor != null) {
                //long id = cursor.getLong(cursor.getColumnIndex("_id"));

                TextView ruta = (TextView) view.findViewById(R.id.ruta);
                columnIndex = cursor.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO);
                ruta.setText(cursor.getString(columnIndex));

                // tratar el nombre del medicamento
                TextView nombre = (TextView) view.findViewById(R.id.nombre);
                columnIndex = cursor.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_NAME);
                nombre.setText(cursor.getString(columnIndex));

                // tratar la imagen de la caja
                SquareImageView caja = (SquareImageView) view.findViewById(R.id.imagenCaja);
                targetWidth = caja.getWidth();
                targetHeight = caja.getHeight();
                columnIndex = cursor.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO);
                path = cursor.getString(columnIndex);
                if (path == null)
                    caja.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.caja));
                else
                    //caja.setImageBitmap(ImageUtils.scaleImage(path, targetWidth, targetHeight));
                    caja.setImageBitmap(ImageUtils.getSquareBitmap(ImageUtils.scaleImage(path, 100, 100)));

                //tratar la imagen de la pastilla
                SquareImageView pastilla = (SquareImageView) view.findViewById(R.id.imagenPastilla);
                targetWidth = pastilla.getWidth();
                targetHeight = pastilla.getHeight();
                columnIndex = cursor.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO);
                path = cursor.getString(columnIndex);
                if (path == null)
                    pastilla.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pastilla));
                else
                    //pastilla.setImageBitmap(ImageUtils.scaleImage(path, targetWidth, targetHeight));
                    pastilla.setImageBitmap(ImageUtils.getSquareBitmap(ImageUtils.scaleImage(path, 100, 100)));
            }
        }
    }
}
