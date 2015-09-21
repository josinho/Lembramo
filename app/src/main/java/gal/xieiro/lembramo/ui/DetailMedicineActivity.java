package gal.xieiro.lembramo.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.MedicamentContentProvider;


public class DetailMedicineActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final long NO_ID = -1;
    private static final int LOADER_ID = 1;

    private long id = NO_ID;
    private ImageSelectorFragment mCaja, mPastilla;
    private String mCajaUri, mPastillaUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // usar un aspa como forma de retroceder a la anterior activity simulando un cancelar
        setNavigationIcon(R.drawable.ic_clear_white_24dp);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", NO_ID);
        if (id != NO_ID) {
            //modo editar
            setToolbarTitle(R.string.title_edit_medicine);
        }

        if (savedInstanceState != null) {
            //venimos de una restauración
            mCajaUri = savedInstanceState.getString("imagenCaja");
            mPastillaUri = savedInstanceState.getString("imagenPastilla");
        } else {
            mCajaUri = mPastillaUri = null;
            if (id != NO_ID) {
                //modo editar y no venimos de una restauración
                Bundle bundle = new Bundle();
                bundle.putLong(DBContract.Medicamentos._ID, id);
                getLoaderManager().initLoader(LOADER_ID, bundle, this);
            }
        }

        //colocar los fragments con las imágenes por defecto
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mCaja = ImageSelectorFragment.newInstance(R.drawable.caja, mCajaUri);
        mPastilla = ImageSelectorFragment.newInstance(R.drawable.pastilla, mPastillaUri);

        ft.add(R.id.imagenCaja_container, mCaja);
        ft.add(R.id.imagenPastilla_container, mPastilla);
        ft.commit();
    }


    @Override
    protected int getLayoutResource() {
        // indicar el layout de esta activity, necesario para BaseActivity
        return R.layout.fragment_detail_medicine;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medicine, menu);
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
                setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void saveMedicineBD() {
        FragmentManager fm = getSupportFragmentManager();
        //TODO: validar datos

        ContentValues cv = new ContentValues();
        cv.put(DBContract.Medicamentos.COLUMN_NAME_NAME,
                ((EditText) findViewById(R.id.txtNombre)).getText().toString());
        cv.put(DBContract.Medicamentos.COLUMN_NAME_COMMENT,
                ((EditText) findViewById(R.id.txtComentario)).getText().toString());
        cv.put(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO,
                ((ImageSelectorFragment) fm.findFragmentById(R.id.imagenCaja_container)).getImagePath());
        cv.put(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO,
                ((ImageSelectorFragment) fm.findFragmentById(R.id.imagenPastilla_container)).getImagePath());

        if (id == NO_ID) {
            //create
            getContentResolver().insert(MedicamentContentProvider.CONTENT_URI, cv);
        } else {
            //update
            String uri = MedicamentContentProvider.CONTENT_URI.toString() + "/" + id;
            getContentResolver().update(Uri.parse(uri), cv, null, null);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long key = args.getLong(DBContract.Medicamentos._ID);
        String uri = MedicamentContentProvider.CONTENT_URI.toString() + "/" + key;
        return new CursorLoader(this, Uri.parse(uri), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c != null) {
            c.moveToFirst();
            ((EditText) findViewById(R.id.txtNombre)).setText(
                    c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_NAME)));
            ((EditText) findViewById(R.id.txtComentario)).setText(
                    c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_COMMENT)));
            mCaja.setImage(
                    c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO)));
            mPastilla.setImage(
                    c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imagenCaja", mCaja.getImagePath());
        outState.putString("imagenPastilla", mPastilla.getImagePath());
    }
}
