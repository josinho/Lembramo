package gal.xieiro.lembramo.ui;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.MedicamentContentProvider;
import gal.xieiro.lembramo.model.Medicine;
import gal.xieiro.lembramo.util.Utils;

public class ViewPagerActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MedicineFragment.OnMedicineFragmentListener,
        CommentFragment.OnCommentFragmentListener,
        FrequencyFragment.OnFrequencyFragmentListener,
        IntakeFragment.OnFragmentInteractionListener {

    private static final int LOADER_ID = 1;
    private Medicine mMedicine;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_viewpager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMedicine = new Medicine(Utils.NO_ID);

        Intent intent = getIntent();
        long id = intent.getLongExtra("id", Utils.NO_ID);
        if (id != Utils.NO_ID) {
            //modo editar
            setToolbarTitle(R.string.title_edit_medicine);
            Bundle bundle = new Bundle();
            bundle.putLong(DBContract.Medicamentos._ID, id);
            getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MedicineFragmentPagerAdapter(
                getSupportFragmentManager(),
                getResources().getStringArray(R.array.viewpager_tabs)
        ));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
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
        cv.put(DBContract.Medicamentos.COLUMN_NAME_NAME, mMedicine.getName());
        cv.put(DBContract.Medicamentos.COLUMN_NAME_COMMENT, mMedicine.getComment());
        cv.put(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO, mMedicine.getPillboxImage());
        cv.put(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO, mMedicine.getPillImage());

        if (mMedicine.getId() == Utils.NO_ID) {
            //create
            getContentResolver().insert(MedicamentContentProvider.CONTENT_URI, cv);
        } else {
            //update
            String uri = MedicamentContentProvider.CONTENT_URI.toString() + "/" + mMedicine.getId();
            getContentResolver().update(Uri.parse(uri), cv, null, null);
        }
    }


    private class MedicineFragmentPagerAdapter extends FragmentPagerAdapter {
        private String mTabTitles[];

        public MedicineFragmentPagerAdapter(FragmentManager fm, String[] tabTitles) {
            super(fm);
            mTabTitles = tabTitles;
        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f;
            switch (position) {
                case 0:
                    f = MedicineFragment.newInstance(mMedicine);
                    break;
                case 1:
                    f = FrequencyFragment.newInstance(mMedicine);
                    break;
                case 2:
                    f = CommentFragment.newInstance(mMedicine);
                    break;
                default:
                    f = Fragment1.newInstance();
            }
            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return mTabTitles[position];
        }
    }

    @Override
    public void onFragmentInteraction(long id) {
//TODO borrar
    }

    @Override
    public void onMedicineChange(Medicine medicine) {
        mMedicine.setName(medicine.getName());
        mMedicine.setPillboxImage(medicine.getPillboxImage());
        mMedicine.setPillImage(medicine.getPillImage());
    }

    @Override
    public void onCommentChange(Medicine medicine) {
        mMedicine.setComment(medicine.getComment());
    }

    @Override
    public void onFrequencyChange(Medicine medicine) {
        mMedicine.setStartDate(medicine.getStartDate());
        mMedicine.setRecurrenceRule(medicine.getRecurrenceRule());
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
            mMedicine.setId(c.getLong(c.getColumnIndex(DBContract.Medicamentos._ID)));
            mMedicine.setName(c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_NAME)));
            mMedicine.setComment(c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_COMMENT)));
            mMedicine.setPillboxImage(c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO)));
            mMedicine.setPillImage(c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO)));
            // TODO pendiente crear los campos en la BD
            //mMedicine.setRecurrenceModel(c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_RECURRENCE)));
            //mMedicine.setSchedule(c.getString(c.getColumnIndex(DBContract.Medicamentos.COLUMN_NAME_SCHEDULE)));

            Intent intent = new Intent("MedicineLoaded");
            intent.putExtra("medicine", mMedicine);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
