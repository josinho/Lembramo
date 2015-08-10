package gal.xieiro.lembramo.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.db.DBAdapter;
import gal.xieiro.lembramo.model.Medicament;
import gal.xieiro.lembramo.ui.BaseActivity;
import gal.xieiro.lembramo.ui.ImageSelectorFragment;


public class DetailMedicineActivity extends BaseActivity {

    private final static String TAG = "DetailMedicineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // usar un aspa como forma de retroceder a la anterior activity
        // simulando un cancelar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);

        // evitar overlapping de fragments si venimos de una restauración
        if (savedInstanceState != null) {
            return;
        }

        //colocar los fragments con las imágenes por defecto
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.imagenCaja_container, ImageSelectorFragment.newInstance(R.drawable.caja));
        ft.add(R.id.imagenPastilla_container, ImageSelectorFragment.newInstance(R.drawable.pastilla));
        ft.commit();
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
        new AsyncDBTask().execute(this, med);
    }

    protected class AsyncDBTask extends AsyncTask<Object, Void, Boolean> {
        private boolean result = false;
        private Context context;

        @Override
        protected Boolean doInBackground(Object... params) {
            context = (Context) params[0];
            Medicament med = (Medicament) params[1];

            DBAdapter dbAdapter = new DBAdapter(context);
            try {
                dbAdapter.open();
                dbAdapter.insertMedicamento(med.name, med.comment, med.pillboxImage, med.pillImage);
                dbAdapter.close();
                return true;
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
                Toast.makeText(context, R.string.ok_db_toast, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, R.string.error_db_toast, Toast.LENGTH_LONG).show();
        }
    }
}
