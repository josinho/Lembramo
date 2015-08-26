package gal.xieiro.lembramo.ui;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import gal.xieiro.lembramo.R;

public class ViewPagerActivity extends BaseActivity {

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
                        getTabTitles()
                )
        );

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    private String[] getTabTitles() {
        return new String[] {
                getResources().getString(R.string.tab1),
                getResources().getString(R.string.tab2),
                getResources().getString(R.string.tab3),
                getResources().getString(R.string.tab4),
        };
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
                case 2:
                case 4:
                    f = Fragment1.newInstance();
                    break;
                case 1:
                case 3:
                case 5:
                    f = Fragment2.newInstance();
                    break;
            }

            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return mTabTitles[position];
        }
    }
}
