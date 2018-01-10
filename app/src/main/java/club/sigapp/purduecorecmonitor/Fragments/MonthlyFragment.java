package club.sigapp.purduecorecmonitor.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Activities.StatisticsActivity;
import club.sigapp.purduecorecmonitor.Models.MonthlyTrendsModel;
import club.sigapp.purduecorecmonitor.Models.WeeklyTrendsModel;
import club.sigapp.purduecorecmonitor.Networking.CoRecApi;
import club.sigapp.purduecorecmonitor.Networking.CoRecApiHelper;
import club.sigapp.purduecorecmonitor.R;
import club.sigapp.purduecorecmonitor.Utils.MonthlyComparator;
import club.sigapp.purduecorecmonitor.Utils.Properties;
import club.sigapp.purduecorecmonitor.Utils.StackedLineGraphXAxisFormatter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonthlyFragment extends Fragment {

    @BindView(R.id.stacked_Line_Chart)
    LineChart stackedLineChart;

    String locationId;

    public MonthlyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monthly, container, false);
        ButterKnife.bind(this, view);

        locationId = ((StatisticsActivity) getActivity()).getLocationId();

        initializeStackedLineChart();
        return view;
    }

    private void initializeStackedLineChart() {
        final List<Entry> currentOccupancy = new ArrayList<>();
        final List<Entry> maxOccupancy = new ArrayList<>();
        final List<ILineDataSet> chartLines = new ArrayList<>();

        CoRecApi api = CoRecApiHelper.getInstance();

        api.getLocationMonthlyTrend().enqueue(new Callback<List<MonthlyTrendsModel>>() {
            @Override
            public void onResponse(Call<List<MonthlyTrendsModel>> call, Response<List<MonthlyTrendsModel>> response) {
                if (response.code() == 200) {
                    List<MonthlyTrendsModel> monthlyTrendsModel = response.body();

                    for (Iterator<MonthlyTrendsModel> iterator = monthlyTrendsModel.iterator(); iterator.hasNext(); ) {
                        if (!iterator.next().LocationId.equals(locationId))
                            iterator.remove();
                    }

                    Collections.sort(monthlyTrendsModel, new MonthlyComparator());
                    for (MonthlyTrendsModel data : monthlyTrendsModel) {
                        currentOccupancy.add(new Entry(data.EntryMonth, data.Count));
                        maxOccupancy.add(new Entry(data.EntryMonth, data.Capacity));
                    }

                    LineDataSet maxCapacity = new LineDataSet(maxOccupancy, "Max Capacity");
                    maxCapacity.setValueFormatter(new IValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                            return new DecimalFormat("###,###,##0").format(value);
                        }
                    });
                    maxCapacity.setDrawFilled(true);
                    maxCapacity.setFillColor(Color.BLACK);
                    maxCapacity.setFillAlpha(155);
                    LineDataSet currentCapacity = new LineDataSet(currentOccupancy, "Average Capacity");
                    currentCapacity.setValueFormatter(new IValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                            return new DecimalFormat("###,###,##0").format(value);
                        }
                    });
                    currentCapacity.enableDashedLine(10, 10, 0);
                    currentCapacity.setDrawFilled(true);
                    currentCapacity.setFillColor(Color.YELLOW);
                    chartLines.add(maxCapacity);
                    chartLines.add(currentCapacity);

                    LineData lineData = new LineData(chartLines);
                    stackedLineChart.setData(lineData);
                    stackedLineChart.invalidate();
                    stackedLineChart.animateX(1000);

                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<List<MonthlyTrendsModel>> call, Throwable t) {

            }
        });
        XAxis xAxis = stackedLineChart.getXAxis();
        xAxis.setLabelCount(13, true);
        xAxis.setTextSize(11f);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new StackedLineGraphXAxisFormatter(Properties.getMonthsOfYear()));

        stackedLineChart.getDescription().setEnabled(false);

        stackedLineChart.setDragEnabled(false);
        stackedLineChart.setScaleEnabled(false);

        Legend legend = stackedLineChart.getLegend();
        legend.setEnabled(false);

        YAxis right = stackedLineChart.getAxisRight();
        right.setEnabled(false);

        YAxis left = stackedLineChart.getAxisLeft();
        left.setLabelCount(10, false);
        left.setAxisMinimum(0.0f);
    }

}
