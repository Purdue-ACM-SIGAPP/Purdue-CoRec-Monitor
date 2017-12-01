package club.sigapp.purduecorecmonitor.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Models.WeeklyTrendsModel;
import club.sigapp.purduecorecmonitor.Networking.CoRecApi;
import club.sigapp.purduecorecmonitor.Networking.CoRecApiHelper;
import club.sigapp.purduecorecmonitor.R;
import club.sigapp.purduecorecmonitor.Utils.BarGraphXAxisFormatter;
import club.sigapp.purduecorecmonitor.Utils.Properties;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklyFragment extends Fragment {

    @BindView(R.id.bar_chart)
    BarChart barChart;

    /* This is never used */
    public WeeklyFragment() {
        // Required empty public constructor
    }


    /* This is the 'constructor' */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_weekly, container, false);
        ButterKnife.bind(this, view);

        initializeBarChart();
        return view;
    }

    private void initializeBarChart() {

        final List<BarEntry> barEntries = new ArrayList<>();

        CoRecApi api = CoRecApiHelper.getInstance();
        api.getLocationWeeklyTrend("7071edb7-856e-4d05-8957-4001484f9aec").enqueue(new Callback<List<WeeklyTrendsModel>>() { //the running one
            @Override
            public void onResponse(Call<List<WeeklyTrendsModel>> call, Response<List<WeeklyTrendsModel>> response) {
                if (response.code() == 200) {
                    List<WeeklyTrendsModel> weeklyTrendsModels = response.body();
                    List<WeeklyTrendsModel> dailyTrendModel;
                    for (int i = 0; i < 7; i++) {
                       dailyTrendModel = convertWeekToDay(i, weeklyTrendsModels);
                        barEntries.add(new BarEntry(i,averageUsersForTheDay(dailyTrendModel)));
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
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<WeeklyTrendsModel>> call, Throwable t) {

            }
        });

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
                Log.d("Weekly", Properties.getDaysOfWeek()[(int)e.getX()]);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private List<WeeklyTrendsModel> convertWeekToDay(int dayOfWeek, List<WeeklyTrendsModel> weeklyTrendsModels) {
        List<WeeklyTrendsModel> dayDataSet = new ArrayList<>();
        for (WeeklyTrendsModel data : weeklyTrendsModels) {
            if (data.DayOfWeek == dayOfWeek) {
                dayDataSet.add(data);
            }
        }
        return dayDataSet;
    }

    private float averageUsersForTheDay(List<WeeklyTrendsModel> weeklyTrendsModels) {
        float average = 0;
        for (WeeklyTrendsModel data : weeklyTrendsModels) {
            average += data.Headcount;
        }
        return average;
    }

}
