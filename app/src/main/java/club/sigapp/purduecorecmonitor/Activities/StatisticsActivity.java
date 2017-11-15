package club.sigapp.purduecorecmonitor.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

import club.sigapp.purduecorecmonitor.R;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        LineChart chart = (LineChart) findViewById(R.id.chart);

        double[] dataObjects = new double[1];
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for(double data: dataObjects) {
            //entries.add(new Entry(data.getValueX(), data.getValueY()));
        }
    }
}
