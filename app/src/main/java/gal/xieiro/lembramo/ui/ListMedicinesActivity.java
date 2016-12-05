package gal.xieiro.lembramo.ui;

import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.alarm.AlarmHelper;
import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.LembramoContentProvider;
import gal.xieiro.lembramo.ui.component.SquareImageView;


public class ListMedicinesActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //TODO comprobar por qué ImageLoader carga imágenes que no corresponden con la medicina

    private static final int LOADER_ID = 1;
    private static final int INTAKE_TOKEN = 2;
    private static final int MEDICINE_TOKEN = 3;

    private Context mContext;
    private ListView mListaMedicamentos;
    private ListAdapter mAdapter;
    private ActionMode mActionMode = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationIcon(R.mipmap.ic_launcher);
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

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cargar la activity para crear un medicamento
                startDetailActivity(LembramoContentProvider.NO_ID);
            }
        });

        mAdapter = new ListAdapter(this, null);
        mListaMedicamentos.setAdapter(mAdapter);
        mListaMedicamentos.setEmptyView(findViewById(R.id.empty));

        getLoaderManager().initLoader(LOADER_ID, null, this);

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
        Intent intent = new Intent(ListMedicinesActivity.this, ViewPagerActivity.class);
        if (id != LembramoContentProvider.NO_ID) {
            intent.putExtra("id", id);
        }
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mAdapter.notifyDataSetChanged();
                restartLoader();
            }
            /*
            if (resultCode == RESULT_CANCELED) {

            }
            */
        }
    }


    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActionMode != null) mActionMode.finish();
    }

    @Override
    protected int getLayoutResource() {
        // indicar el layout de esta activity, necesario para BaseActivity
        return R.layout.activity_list_medicines;
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


    private final class ListAdapter extends ResourceCursorAdapter {
        private SparseBooleanArray mSelectedItems;

        public ListAdapter(Context context, Cursor cursor) {
            super(context, R.layout.medicine_item, cursor, false);
            mSelectedItems = new SparseBooleanArray();
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            String path;
            int columnIndex;
            ImageLoader imageLoader;

            if (cursor != null) {
                columnIndex = cursor.getColumnIndex(DBContract.Medicines._ID);
                final long id = cursor.getLong(columnIndex);

                final ImageView alarm = (ImageView) view.findViewById(R.id.alarm);
                columnIndex = cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_ALARM);
                final int alarmStatus = cursor.getInt(columnIndex);
                if (alarmStatus == 0) {
                    alarm.setImageResource(R.drawable.ic_alarm_off_black_24dp);
                } else {
                    alarm.setImageResource(R.drawable.ic_alarm_on_black_24dp);
                }

                alarm.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (alarmStatus == 0) {
                                    updateAlarmStatus(id, 1);
                                    alarm.setImageResource(R.drawable.ic_alarm_on_black_24dp);
                                } else {
                                    updateAlarmStatus(id, 0);
                                    alarm.setImageResource(R.drawable.ic_alarm_off_black_24dp);
                                }
                            }
                        }
                );

                // tratar el nombre del medicamento
                TextView nombre = (TextView) view.findViewById(R.id.nombre);
                columnIndex = cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_NAME);
                nombre.setText(cursor.getString(columnIndex));

                imageLoader = ImageLoader.getInstance();

                // tratar la imagen de la caja
                SquareImageView caja = (SquareImageView) view.findViewById(R.id.imagenCaja);
                columnIndex = cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_BOXPHOTO);
                path = cursor.getString(columnIndex);
                if (path == null)
                    caja.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.caja));
                else
                    imageLoader.displayImage(path, caja);

                //tratar la imagen de la pastilla
                SquareImageView pastilla = (SquareImageView) view.findViewById(R.id.imagenPastilla);
                columnIndex = cursor.getColumnIndex(DBContract.Medicines.COLUMN_NAME_MEDPHOTO);
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


    private final class ActionModeCallback implements AbsListView.MultiChoiceModeListener {
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
    }

    private static final class DeleteHandler extends AsyncQueryHandler {
        private Context mContext;
        private final WeakReference<ListMedicinesActivity> mActivity;

        private DeleteHandler(Context context, ListMedicinesActivity activity) {
            super(context.getContentResolver());
            mContext = context;
            mActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            if (token == MEDICINE_TOKEN) {
                Toast.makeText(mContext,
                        mContext.getResources().getQuantityString(R.plurals.deleted, result, result),
                        Toast.LENGTH_LONG).show();
                ListMedicinesActivity activity = mActivity.get();
                if (activity != null) activity.restartLoader();
            }
        }
    }

    private void deleteMed() {
        String where = " IN(";
        SparseBooleanArray checked = mAdapter.getSelectedPositions();
        for (int i = (checked.size() - 1); i >= 0; i--) {
            if (checked.valueAt(i)) {
                where += mAdapter.getItemId(checked.keyAt(i)) + ",";

                //borrar alarmas
                // TODO asyncTask
                AlarmHelper.cancelAlarms(this, mAdapter.getItemId(checked.keyAt(i)) );
            }
        }
        where = where.substring(0, where.length() - 1) + ")";

        DeleteHandler deleteHandler = new DeleteHandler(mContext, this);

        // borrar intakes
        deleteHandler.startDelete(
                INTAKE_TOKEN,
                null,
                LembramoContentProvider.CONTENT_URI_INTAKES,
                DBContract.Intakes.COLUMN_NAME_ID_MEDICINE + where,
                null
        );

        // borrar medicinas
        deleteHandler.startDelete(
                MEDICINE_TOKEN,
                null,
                LembramoContentProvider.CONTENT_URI_MEDICINES,
                DBContract.Medicines._ID + where,
                null
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                DBContract.Medicines._ID,
                DBContract.Medicines.COLUMN_NAME_ALARM,
                DBContract.Medicines.COLUMN_NAME_NAME,
                DBContract.Medicines.COLUMN_NAME_BOXPHOTO,
                DBContract.Medicines.COLUMN_NAME_MEDPHOTO
        };
        return new CursorLoader(this, LembramoContentProvider.CONTENT_URI_MEDICINES, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ID:
                mAdapter.changeCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }


    private void updateAlarmStatus(long id, int alarmStatus) {
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Medicines.COLUMN_NAME_ALARM, alarmStatus);

        String uri = LembramoContentProvider.CONTENT_URI_MEDICINES.toString() + "/" + id;
        getContentResolver().update(Uri.parse(uri), cv, null, null);
        restartLoader();
    }
}
