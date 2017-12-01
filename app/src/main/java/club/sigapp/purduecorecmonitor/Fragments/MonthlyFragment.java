package club.sigapp.purduecorecmonitor.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Models.MonthlyTrendsModel;
import club.sigapp.purduecorecmonitor.R;
import club.sigapp.purduecorecmonitor.Networking.CoRecApi;
import club.sigapp.purduecorecmonitor.Networking.CoRecApiHelper;
import club.sigapp.purduecorecmonitor.Utils.MonthlyComparator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonthlyFragment extends Fragment {

    @BindView(R.id.stacked_Line_Chart)
    LineChart stackedLineChart;

    public MonthlyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_monthly, container, false);
        ButterKnife.bind(this, view);

        initializeStackedLineChart();
        return view;
    }

    private void initializeStackedLineChart(){
        final List<Entry> currentOccupancy = new ArrayList<>();
        final List<Entry> maxOccupancy = new ArrayList<>();
        final List<ILineDataSet> chartLines = new ArrayList<>();


        CoRecApi api = CoRecApiHelper.getInstance();

        api.getLocationMonthlyTrend("7071edb7-856e-4d05-8957-4001484f9aec").enqueue(new Callback<List<MonthlyTrendsModel>>() {
            @Override
            public void onResponse(Call<List<MonthlyTrendsModel>> call, Response<List<MonthlyTrendsModel>> response) {
                if(response.code() == 200){
                    List<MonthlyTrendsModel> monthlyTrendsModel = response.body();
                    Collections.sort(monthlyTrendsModel, new MonthlyComparator());
                    for(MonthlyTrendsModel data : monthlyTrendsModel){
                        currentOccupancy.add(new Entry(data.Month,data.Headcount));
                        maxOccupancy.add(new Entry(data.Month,data.Capacity));
                    }

                    LineDataSet maxCapacity = new LineDataSet(maxOccupancy, "Max Capacity");
                    LineDataSet currentCapacity = new LineDataSet(currentOccupancy, "Average Capacity");
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

    }

}
