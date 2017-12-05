package club.sigapp.purduecorecmonitor.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Adapters.StatisticPagerAdapter;
import club.sigapp.purduecorecmonitor.Fragments.MonthlyFragment;
import club.sigapp.purduecorecmonitor.Fragments.WeeklyFragment;
import club.sigapp.purduecorecmonitor.R;

public class StatisticsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    private StatisticPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

        initToolbar();
        setupTabLayout();
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }


    private void setupTabLayout() {
        pagerAdapter = new StatisticPagerAdapter
                (getSupportFragmentManager());
        pagerAdapter.addFragment(new WeeklyFragment(), "Weekly", false);
        pagerAdapter.addFragment(new MonthlyFragment(), "Monthly", false);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
