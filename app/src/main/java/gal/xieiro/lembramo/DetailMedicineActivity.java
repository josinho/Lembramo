package gal.xieiro.lembramo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
