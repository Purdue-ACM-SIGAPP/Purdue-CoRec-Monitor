package club.sigapp.purduecorecmonitor.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.R;
import club.sigapp.purduecorecmonitor.Utils.*;

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
        ButterKnife.bind(view);

        List<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            barEntries.add(new BarEntry(i,i));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "Label");
        return view;
    }

}
