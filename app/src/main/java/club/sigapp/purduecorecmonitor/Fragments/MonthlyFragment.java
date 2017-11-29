package club.sigapp.purduecorecmonitor.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.R;

public class MonthlyFragment extends Fragment {

    @BindView(R.id.chart1)
    LineChart mChart;

    public MonthlyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_monthly, container, false);
        ButterKnife.bind(this, view);

        List<Entry> LineEntries = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            LineEntries.add(new Entry(i,10));
        }


        List<Entry> LineEntries2 = new ArrayList<>();

        for(int i = 0; i < 7; i++) {
            LineEntries2.add(new Entry(i,i));
        }

        List<ILineDataSet> IlineData = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(LineEntries, "label");
        LineDataSet lineDataSet2 = new LineDataSet(LineEntries2, "label");

        IlineData.add(lineDataSet1);
        IlineData.add(lineDataSet2);

        LineData lineData = new LineData(IlineData);
        mChart.setData(lineData);
        mChart.invalidate();
        return view;
    }

}
