package club.sigapp.purduecorecmonitor.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Activities.StatisticsActivity;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.Models.WeeklyTrendsModel;
import club.sigapp.purduecorecmonitor.Networking.CoRecApi;
import club.sigapp.purduecorecmonitor.Networking.CoRecApiHelper;
import club.sigapp.purduecorecmonitor.R;
import club.sigapp.purduecorecmonitor.Utils.BarGraphXAxisFormatter;
import club.sigapp.purduecorecmonitor.Utils.LineGraphXAxisFormatter;
import club.sigapp.purduecorecmonitor.Utils.Properties;
import club.sigapp.purduecorecmonitor.Utils.SharedPrefsHelper;
import club.sigapp.purduecorecmonitor.Utils.WeeklyStatsData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklyFragment extends Fragment {

    private static final String SHOW_ONBOARDING_COUNTDOWN = "show_onboarding_countdown";

    /**
     * The number of times to show the snackbar explaining how to start a timer
     * unless the user manually dismisses it.
     */
    private static final int ONBOARDING_COUNTDOWN = 5;

    Snackbar mOnboardingSnackbar;

    @BindView(R.id.bar_chart)
    BarChart barChart;

    String locationId;

    @BindView(R.id.line_chart)
    LineChart lineChart;

    @BindView(R.id.statProgressBar)
    ProgressBar statProgressBar;

    @BindView(R.id.statStatus)
    TextView statStatus;

    @BindView(R.id.weeklyStatsLayout)
    LinearLayout weeklyStatsLayout;

    @BindView(R.id.hourlyText)
    TextView hourlyText;

    List<WeeklyTrendsModel> weeklyTrendsModels;

    private int capacity = 0;

    /* This is never used */
    public WeeklyFragment() {
        // Required empty public constructor
    }


    /* This is the 'constructor' */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        ButterKnife.bind(this, view);

        locationId = ((StatisticsActivity) getActivity()).getLocationId();

        showOnboardingIfNecessary();

        statProgressBar.setVisibility(View.VISIBLE);
        statStatus.setVisibility(View.VISIBLE);
        weeklyStatsLayout.setVisibility(View.GONE);

        if (WeeklyStatsData.getInstance() != null) {
            initializeWeeklyFragment(WeeklyStatsData.getInstance().getData());
        } else {
            CoRecApiHelper.getInstance().getLocationWeeklyTrend().enqueue(new Callback<List<WeeklyTrendsModel>>() {
                @Override
                public void onResponse(Call<List<WeeklyTrendsModel>> call, Response<List<WeeklyTrendsModel>> response) {
                    if (response.code() == 200) {
                        weeklyTrendsModels = response.body();
                        WeeklyStatsData weeklyStatsData = new WeeklyStatsData();
                        weeklyStatsData.setData(response.body());
                        WeeklyStatsData.setInstance(weeklyStatsData);
                        initializeWeeklyFragment(WeeklyStatsData.getInstance().getData());
                    } else {
                        Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<WeeklyTrendsModel>> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.another_internet_error_message, Toast.LENGTH_LONG).show();
                    statProgressBar.setVisibility(View.GONE);
                    statStatus.setVisibility(View.GONE);
                }
            });
        }
        return view;
    }

    private void initializeWeeklyFragment(List<WeeklyTrendsModel> data){
        weeklyTrendsModels = new ArrayList<>();

        statProgressBar.setVisibility(View.GONE);
        statStatus.setVisibility(View.GONE);
        weeklyStatsLayout.setVisibility(View.VISIBLE);

        for (Iterator<WeeklyTrendsModel> iterator = data.iterator(); iterator.hasNext(); ) {
            WeeklyTrendsModel weekData = iterator.next();
            if (weekData.LocationId.equals(locationId))
                weeklyTrendsModels.add(weekData);
        }

        if (weeklyTrendsModels != null && weeklyTrendsModels.size() > 0)
            capacity = weeklyTrendsModels.get(0).Capacity;

        initializeBarChart();
        lineChart.setVisibility(View.INVISIBLE);
        hourlyText.setVisibility(View.INVISIBLE);
        initializeLineChart();
    }

    private void initializeBarChart() {

        List<BarEntry> barEntries = new ArrayList<>();

        List<WeeklyTrendsModel> dailyTrendModel;

        for (int i = 0; i < 7; i++) {
            dailyTrendModel = convertWeekToDay(i, weeklyTrendsModels);
            barEntries.add(new BarEntry(i, averageUsersForTheDay(dailyTrendModel)));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "Weekly Data");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(16f);
        barDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return new DecimalFormat("###,###,##0").format(value);
            }
        });
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();
        barChart.animateY(1000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new BarGraphXAxisFormatter(Properties.getDaysOfWeek()));

        barChart.getDescription().setEnabled(false);

        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        YAxis right = barChart.getAxisRight();
        right.setEnabled(false);

        YAxis left = barChart.getAxisLeft();
        left.setAxisMinimum(0.0f);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d("Weekly", Properties.getDaysOfWeek()[(int) e.getX()]);
                lineChart.setVisibility(View.VISIBLE);
                hourlyText.setVisibility(View.VISIBLE);
                updateLineChart((int) e.getX());
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void initializeLineChart() {
        final ArrayList<Entry> entries = new ArrayList<Entry>();

        List<WeeklyTrendsModel> dailyTrendModel;
        for (int i = 0; i < 7; i++) {
            dailyTrendModel = convertWeekToDay(i, weeklyTrendsModels);
            entries.add(new Entry(i, averageUsersForTheDay(dailyTrendModel)));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Weekly Data");
        dataSet.setValueTextSize(16f);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return new DecimalFormat("###,###,##0").format(value);
            }
        });
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.enableDashedLine(14f, 6f, 1f);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
        lineChart.animateY(1000);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new BarGraphXAxisFormatter(Properties.getDaysOfWeek()));

        lineChart.getDescription().setEnabled(false);

        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        YAxis right = lineChart.getAxisRight();
        right.setEnabled(false);

        YAxis left = lineChart.getAxisLeft();
        left.setAxisMinimum(0.0f);
        lineChart.invalidate();
    }

    private void updateLineChart(int day) {
        List<WeeklyTrendsModel> dayOfWeek = convertWeekToDay(day, weeklyTrendsModels);
        final ArrayList<Entry> entries = new ArrayList<>();
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new LineGraphXAxisFormatter(Properties.getHoursOfDay()));

        int max = 0;

        for (int i = 0; i < dayOfWeek.size(); i++) {
            WeeklyTrendsModel data = dayOfWeek.get(i);
            entries.add(new Entry(data.EntryHour, data.Count));
            //find max count for hours
            if(data.Count > max){
                max = data.Count;
            }
        }
        Collections.reverse(entries);
        Collections.sort(entries, new EntryXComparator());
        LineDataSet dataSet = new LineDataSet(entries, "Daily Data");
        dataSet.setValueTextSize(16f);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return new DecimalFormat("###,###,##0").format(value);
            }
        });
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.enableDashedLine(14f, 6f, 1f);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        switch (day) {
            case 0:
                xAxis.setAxisMinimum(10.0f);
                xAxis.setAxisMaximum(23.0f);
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                xAxis.setAxisMinimum(5.0f);
                xAxis.setAxisMaximum(23.0f);
                break;
            case 6:
                xAxis.setAxisMinimum(8.0f);
                xAxis.setAxisMaximum(23.0f);
        }

        YAxis left = lineChart.getAxisLeft();
        left.setAxisMinimum(0.0f);
        //decision tree for finding max of hours line graph
        if (capacity != 0 && max < capacity) {
            //sets max of chart to size where you can notice counts for high capacity locations
            if(capacity > 5 * max){
                left.setAxisMaximum(capacity / 5);
            }else {
                left.setAxisMaximum(capacity);
            }
        }else {
            //if capacity is less than max(Count) then base max of graph on max(Count) instead of capacity
            left.setAxisMaximum(max + 5);
        }
        lineChart.animateXY(1000, 1000);
        lineChart.invalidate();
    }

    private List<WeeklyTrendsModel> convertWeekToDay(int dayOfWeek, List<WeeklyTrendsModel> weeklyTrendsModels) {
        List<WeeklyTrendsModel> dayDataSet = new ArrayList<>();
        for (WeeklyTrendsModel data : weeklyTrendsModels) {
            if (data.EntryDayOfWeek == dayOfWeek) {
                dayDataSet.add(data);
            }
        }
        return dayDataSet;
    }

    private float averageUsersForTheDay(List<WeeklyTrendsModel> weeklyTrendsModels) {
        float average = 0;
        for (WeeklyTrendsModel data : weeklyTrendsModels) {
            average += data.Count;
        }
        return average;
    }

    /**
     * When the user first accesses the Machines Activity, we should show
     * a snackbar telling them how to create a timer. We hope this will increase
     * the use of timers.
     */
    private void showOnboardingIfNecessary() {
        int numberOfTimesToShowOnboarding =
                SharedPrefsHelper.getSharedPrefs(getContext()).getInt(SHOW_ONBOARDING_COUNTDOWN, ONBOARDING_COUNTDOWN);

        if ((mOnboardingSnackbar != null && !mOnboardingSnackbar.isShown()))
            return;

        //add BuildConfig.DEBUG to this statement to make it display always for testing.
        if (numberOfTimesToShowOnboarding > 0) {
            //show onboarding snackbar.
            mOnboardingSnackbar = Snackbar
                    .make(lineChart,
                            R.string.tutorial_text,
                            Snackbar.LENGTH_INDEFINITE)
                    .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        /**
                         * If the user dismisses the snackbar, we should respect their
                         * desire to not show the tutorial again.
                         * We do this by setting the countdown to zero.
                         */
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (event == DISMISS_EVENT_SWIPE) {
                                SharedPrefsHelper
                                        .getSharedPrefs(getContext())
                                        .edit()
                                        .putInt(SHOW_ONBOARDING_COUNTDOWN,
                                                0)
                                        .apply();
                            }
                        }
                    })
                    .setAction(R.string.dismiss, new View.OnClickListener() {

                        /**
                         * If the user dismisses the snackbar, we should respect their
                         * desire to not show the tutorial again.
                         * We do this by setting the countdown to zero.
                         */
                        @Override
                        public void onClick(View v) {
                            SharedPrefsHelper
                                    .getSharedPrefs(getContext())
                                    .edit()
                                    .putInt(SHOW_ONBOARDING_COUNTDOWN,
                                            0)
                                    .apply();
                        }
                    }).setActionTextColor(Color.WHITE);

            mOnboardingSnackbar.show();

            SharedPrefsHelper
                    .getSharedPrefs(getContext())
                    .edit()
                    .putInt(SHOW_ONBOARDING_COUNTDOWN,
                            --numberOfTimesToShowOnboarding)
                    .apply();
        }
    }

}
