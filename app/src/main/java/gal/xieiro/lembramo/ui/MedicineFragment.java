package gal.xieiro.lembramo.ui;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.MedicamentContentProvider;
import gal.xieiro.lembramo.util.Utils;

public class MedicineFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private static final String ID_PARAM = "ID";
    private long mId;

    private View mView;
    private ImageSelectorFragment mBoxFragment, mPillFragment;
    private String mCajaUri, mPastillaUri;

    public static MedicineFragment newInstance(long id) {
        MedicineFragment fragment = new MedicineFragment();
        Bundle args = new Bundle();
        args.putLong(ID_PARAM, id);
        fragment.setArguments(args);
        return fragment;
    }

    public MedicineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mId = getArguments().getLong(ID_PARAM);
        }

        if (savedInstanceState != null) {
            //venimos de una restauración
            mCajaUri = savedInstanceState.getString("BoxImagePath");
            mPastillaUri = savedInstanceState.getString("PillImagePath");
        } else {
            mCajaUri = mPastillaUri = null;
            if (mId != Utils.NO_ID) {
                //modo editar y no venimos de una restauración
                Bundle bundle = new Bundle();
                bundle.putLong(DBContract.Medicamentos._ID, mId);
                getLoaderManager().initLoader(LOADER_ID, bundle, this);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_medicine, container, false);

        //colocar los fragments con las imágenes por defecto
        mBoxFragment = ImageSelectorFragment.newInstance(R.drawable.caja, mCajaUri);
        mPillFragment = ImageSelectorFragment.newInstance(R.drawable.pastilla, mPastillaUri);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.boxContainer, mBoxFragment)
                .add(R.id.pillContainer, mPillFragment)
                .commit();

        return mView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("BoxImagePath", mBoxFragment.getImagePath());
        outState.putString("PillImagePath", mPillFragment.getImagePath());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long key = args.getLong(DBContract.Medicamentos._ID);
        String uri = MedicamentContentProvider.CONTENT_URI.toString() + "/" + key;
        return new CursorLoader(getActivity(), Uri.parse(uri), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c != null) {
            c.moveToFirst();
            ((EditText) mView.findViewById(R.id.medicineName)).setText(
                    c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_NAME)));
            mBoxFragment.setImage(
                    c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO)));
            mPillFragment.setImage(
                    c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}