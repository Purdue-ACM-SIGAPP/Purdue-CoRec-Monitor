package club.sigapp.purduecorecmonitor.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import club.sigapp.purduecorecmonitor.Fragments.MonthlyFragment;
import club.sigapp.purduecorecmonitor.Fragments.WeeklyFragment;


public class StatisticPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public StatisticPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        addFragment(fragment, title, true);
    }

    public void addFragment(Fragment fragment, String title, boolean hasOptionsMenu) {
        fragment.setHasOptionsMenu(hasOptionsMenu);
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int i) {
        return mFragmentTitles.get(i);
    }
}
