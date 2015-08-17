package gal.xieiro.lembramo.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.db.DBAdapter;
import gal.xieiro.lembramo.db.DBContract;


public class ListMedicinesActivity extends BaseActivity {

    private static final long NO_ID = -1;

    private Context mContext;
    private ListView mListaMedicamentos;
    private DBAdapter mDBAdapter;
    private ListAdapter mAdapter;
    private ActionMode mActionMode = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mListaMedicamentos = (ListView) findViewById(R.id.listaMedicamentos);
        mListaMedicamentos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListaMedicamentos.setMultiChoiceModeListener(new ActionModeCallback());


        mListaMedicamentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //cargar la activity para editar un medicamento
                startDetailActivity(id);
            }
        });

        /*
        mListaMedicamentos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null)
                    return false;

                mActionMode = startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });
*/
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cargar la activity para crear un medicamento
                startDetailActivity(NO_ID);
            }
        });

        mAdapter = new ListAdapter(this, null);
        mListaMedicamentos.setAdapter(mAdapter);

        //obtener el cursor con los medicamentos en segundo plano
        mDBAdapter = new DBAdapter(this);
        new AsyncDBTask().execute(mDBAdapter);

        if (savedInstanceState != null) {
            //venimos de un cambio de orientación

            final int[] checked = savedInstanceState.getIntArray("selected");
            if (checked != null) {
                //retrasar la restauración un poco
                //al girar pantalla con un Adaptador basado en CursorAdapter
                //deselecciona los elementos uno a uno
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //hacer la restauración 200ms después para que Android haga sus
                        //"de-selecciones" en una lista que no existe
                        //RAREZAS
                        for (int aChecked : checked) {
                            mListaMedicamentos.setItemChecked(aChecked, true);
                        }
                    }
                }, 200);
            }
        }
    }

    private void startDetailActivity(long id) {
        Intent intent = new Intent(ListMedicinesActivity.this, DetailMedicineActivity.class);
        if (id != NO_ID) {
            intent.putExtra("id", id);
        }
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDBAdapter.close();
        if (mActionMode != null) mActionMode.finish();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActionMode != null) {
            outState.putIntArray("selected", getSelectedPositions());
        }
    }

    private int[] getSelectedPositions() {
        //SparseBooleanArray no es Parcelable -> conversión a int[]
        SparseBooleanArray checked = mAdapter.getSelectedPositions();
        int[] copy = new int[checked.size()];

        for (int i = 0; i < checked.size(); i++) {
            copy[i] = checked.keyAt(i);
        }
        return copy;
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
        }
    }

    private class ListAdapter extends ResourceCursorAdapter {
        private SparseBooleanArray mSelectedItems;

        public ListAdapter(Context context, Cursor cursor) {
            super(context, R.layout.medicine_item, cursor, false);
            mSelectedItems = new SparseBooleanArray();
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String path;
            int columnIndex;
            ImageLoader imageLoader;

            if (cursor != null) {
                //long id = cursor.getLong(cursor.getColumnIndex("_id"));
                imageLoader = ImageLoader.getInstance();

                TextView ruta = (TextView) view.findViewById(R.id.ruta);
                columnIndex = cursor.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO);
                ruta.setText(cursor.getString(columnIndex));

                // tratar el nombre del medicamento
                TextView nombre = (TextView) view.findViewById(R.id.nombre);
                columnIndex = cursor.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_NAME);
                nombre.setText(cursor.getString(columnIndex));

                // tratar la imagen de la caja
                SquareImageView caja = (SquareImageView) view.findViewById(R.id.imagenCaja);
                columnIndex = cursor.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO);
                path = cursor.getString(columnIndex);
                if (path == null)
                    caja.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.caja));
                else
                    imageLoader.displayImage(path, caja);

                //tratar la imagen de la pastilla
                SquareImageView pastilla = (SquareImageView) view.findViewById(R.id.imagenPastilla);
                columnIndex = cursor.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO);
                path = cursor.getString(columnIndex);
                if (path == null)
                    pastilla.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pastilla));
                else
                    imageLoader.displayImage(path, pastilla);
            }
        }

        public void selectView(int position, boolean value) {
            if (value)
                mSelectedItems.put(position, true);
            else
                mSelectedItems.delete(position);
            notifyDataSetChanged();
        }

        public void toggleSelection(int position) {
            selectView(position, !mSelectedItems.get(position));
        }

        public int getSelectedCount() {
            return mSelectedItems.size();
        }

        public SparseBooleanArray getSelectedPositions() {
            return mSelectedItems;
        }

        public void removeSelection() {
            mSelectedItems = new SparseBooleanArray();
            notifyDataSetChanged();
        }
    }


    private class ActionModeCallback implements AbsListView.MultiChoiceModeListener {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            mActionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.removeSelection();
            mActionMode = null;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            mAdapter.toggleSelection(position);
            int num = mAdapter.getSelectedCount();
            mode.setTitle(getResources().getQuantityString(R.plurals.selected, num, num));
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    //hacer borrado de verdad
                    deleteMed();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        private void deleteMed() {
            SparseBooleanArray checked = mAdapter.getSelectedPositions();
            for (int i = (checked.size() - 1); i >= 0; i--) {
                if (checked.valueAt(i)) {
                    long id = mAdapter.getItemId(checked.keyAt(i));
                    Toast.makeText(mContext, "Delete Id: " + id, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
