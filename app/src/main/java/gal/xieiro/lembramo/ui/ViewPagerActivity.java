package gal.xieiro.lembramo.ui;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import gal.xieiro.lembramo.R;

public class ViewPagerActivity extends BaseActivity implements
    IntakeFragment.OnFragmentInteractionListener{

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_viewpager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    f = Fragment1.newInstance();
                    break;
                case 1:
                    f = FrequencyFragment.newInstance();
                    break;
                default:
                    f = IntakeFragment.newInstance("param1", "param2");
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
