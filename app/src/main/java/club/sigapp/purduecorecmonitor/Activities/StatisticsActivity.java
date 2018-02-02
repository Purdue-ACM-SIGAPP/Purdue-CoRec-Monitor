package club.sigapp.purduecorecmonitor.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

    protected String locationId;
    protected String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

        locationId = getIntent().getStringExtra("LocationId");
        roomName = getIntent().getStringExtra("CorecRoom");
        initToolbar();
        setupTabLayout();

    }

    private void initToolbar() {
        toolbar.setTitle(roomName);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupTabLayout() {
        pagerAdapter = new StatisticPagerAdapter
                (getSupportFragmentManager());
        pagerAdapter.addFragment(new WeeklyFragment(), getResources().getText(R.string.weekly_title).toString(), false);
        pagerAdapter.addFragment(new MonthlyFragment(), getResources().getText(R.string.monthly_title).toString(), false);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public String getLocationId() {
        return locationId;
    }
}
