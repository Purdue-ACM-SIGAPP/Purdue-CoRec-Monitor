package club.sigapp.purduecorecmonitor.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import club.sigapp.purduecorecmonitor.R;

public class MonthlyFragment extends Fragment {


    public MonthlyFragment() {
        // Required empty public constructor
    }

    private LineChart mChart;
    private TextView tvX, tvY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monthly, container, false);
    }

}
