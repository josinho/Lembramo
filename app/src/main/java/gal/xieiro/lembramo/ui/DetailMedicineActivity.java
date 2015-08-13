package gal.xieiro.lembramo.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.db.DBAdapter;
import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.model.Medicament;


public class DetailMedicineActivity extends BaseActivity {
    private static final long NO_ID = -1;

    private long id = NO_ID;
    private ImageSelectorFragment mCaja, mPastilla;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // usar un aspa como forma de retroceder a la anterior activity
        // simulando un cancelar
        setNavigationIcon(R.drawable.ic_clear_white_24dp);


        // evitar overlapping de fragments si venimos de una restauración
        if (savedInstanceState != null) {
            return;
        }

        //colocar los fragments con las imágenes por defecto
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        mCaja = ImageSelectorFragment.newInstance(R.drawable.caja);
        mPastilla = ImageSelectorFragment.newInstance(R.drawable.pastilla);

        ft.add(R.id.imagenCaja_container, mCaja);
        ft.add(R.id.imagenPastilla_container, mPastilla);
        ft.commit();


        Intent intent = getIntent();
        id = intent.getLongExtra("id", NO_ID);
        if (id != NO_ID) {
            //modo editar
            setToolbarTitle(R.string.title_activity_edit_medicamento);
            new DBGetAsyncTask().execute(this);
        }
    }

    @Override
    protected int getLayoutResource() {
        // indicar el layout de esta activity, necesario para BaseActivity
        return R.layout.activity_detail_medicine;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_medicamento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_save:
                saveMedicineBD();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void saveMedicineBD() {
        boolean result;
        Medicament med = new Medicament();
        FragmentManager fm = getFragmentManager();

        // obtener los datos a guardar de la UI
        med.name = ((EditText) findViewById(R.id.txtNombre)).getText().toString();
        med.comment = ((EditText) findViewById(R.id.txtComentario)).getText().toString();
        med.pillboxImage = ((ImageSelectorFragment) fm.findFragmentById(R.id.imagenCaja_container)).getImagePath();
        med.pillImage = ((ImageSelectorFragment) fm.findFragmentById(R.id.imagenPastilla_container)).getImagePath();

        //TODO: validar datos

        // guardar en segundo plano en otro hilo
        new DBSaveAsyncTask().execute(this, med, (id == NO_ID));
    }

    protected class DBSaveAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private boolean result = false;
        private Context context;

        @Override
        protected Boolean doInBackground(Object... params) {
            context = (Context) params[0];
            Medicament med = (Medicament) params[1];
            boolean nuevoMed = (Boolean) params[2];

            DBAdapter dbAdapter = new DBAdapter(context);
            try {
                dbAdapter.open();

                if (nuevoMed)
                    dbAdapter.insertMedicamento(med.name, med.comment, med.pillboxImage, med.pillImage);
                else
                    dbAdapter.updateMedicamento(id, med.name, med.comment, med.pillboxImage, med.pillImage);

                dbAdapter.close();
                return true;
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                Toast.makeText(context, R.string.ok_db_toast, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, R.string.error_db_toast, Toast.LENGTH_LONG).show();
        }
    }

    protected class DBGetAsyncTask extends AsyncTask<Object, Void, Medicament> {
        private Context context;
        Medicament med = new Medicament();

        @Override
        protected Medicament doInBackground(Object... params) {
            context = (Context) params[0];

            DBAdapter dbAdapter = new DBAdapter(context);
            try {
                dbAdapter.open();
                Cursor c = dbAdapter.getMedicamento(id);
                if (c != null) {
                    med.id = c.getLong(c.getColumnIndex(DBContract.Medicamentos._ID));
                    med.name = c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_NAME));
                    med.comment = c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_COMMENT));
                    med.pillboxImage = c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO));
                    med.pillImage = c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO));
                    c.close();
                }
                dbAdapter.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            return med;
        }

        @Override
        protected void onPostExecute(Medicament result) {
            if (med.id == id) {
                ((EditText) findViewById(R.id.txtNombre)).setText(med.name);
                ((EditText) findViewById(R.id.txtComentario)).setText(med.comment);
                mCaja.setImage(med.pillboxImage);
                mPastilla.setImage(med.pillImage);
            }
        }
    }
}
