package gal.xieiro.lembramo.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.util.Utils;

public class ViewPagerActivity extends BaseActivity implements
        IntakeFragment.OnFragmentInteractionListener {

    private long mId = Utils.NO_ID;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_viewpager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mId = intent.getLongExtra("id", Utils.NO_ID);
        if (mId != Utils.NO_ID) {
            //modo editar
            setToolbarTitle(R.string.title_edit_medicine);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(
                new MedicineFragmentPagerAdapter(
                        getSupportFragmentManager(),
                        getResources().getStringArray(R.array.viewpager_tabs)
                )
        );

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
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

            Fragment f = null;
            switch (position) {
                case 0:
                    f = MedicineFragment.newInstance(mId);
                    break;
                case 1:
                    f = FrequencyFragment.newInstance();
                    break;
                case 2:
                    f = CommentFragment.newInstance();
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

    public void onFragmentInteraction(long id) {

    }
}
