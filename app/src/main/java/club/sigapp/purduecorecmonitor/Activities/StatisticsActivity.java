package club.sigapp.purduecorecmonitor.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Adapters.StatisticPagerAdapter;
import club.sigapp.purduecorecmonitor.Fragments.MonthlyFragment;
import club.sigapp.purduecorecmonitor.Fragments.WeeklyFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import club.sigapp.purduecorecmonitor.Models.WeeklyTrendsModel;
import club.sigapp.purduecorecmonitor.Networking.CoRecApi;
import club.sigapp.purduecorecmonitor.Networking.CoRecApiHelper;

import club.sigapp.purduecorecmonitor.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.chart)
    LineData data;

    private StatisticPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

        initToolbar();
        setupTabLayout();
        initializeLineChart();
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
    //i have no god damn clue if any of this works
    //but it's cool. no worries.
    private void initializeLineChart() {
        double[] dataObjects = new double[10];
        final ArrayList<Entry> entries = new ArrayList<Entry>();

        for(int i = 0; i < dataObjects.length; i++) {
            entries.add(new Entry(i, 2 * i));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Line Data");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        data = new LineData(dataSets);
        LineChart chart = new LineChart(this);

        chart.setData(data);
        chart.invalidate();
        /*CoRecApi api = CoRecApiHelper.getInstance();
        api.getLocationWeeklyTrend("7071edb7-856e-4d05-8957-4001484f9aec").enqueue(new Callback<List<WeeklyTrendsModel>>() { //the running one
            @Override
            public void onResponse(Call<List<WeeklyTrendsModel>> call, Response<List<WeeklyTrendsModel>> response) {
                if (response.code() != 200) {
                    List<WeeklyTrendsModel> weeklyTrendsModels = response.body();
                    for(WeeklyTrendsModel week: weeklyTrendsModels) {
                        if (week == null) {
                            continue;
                        } else {

                        }
                    }
                } else {
                    Toast.makeText(StatisticsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<WeeklyTrendsModel>> call, Throwable t) {

            }
        });
        */

    }

}
