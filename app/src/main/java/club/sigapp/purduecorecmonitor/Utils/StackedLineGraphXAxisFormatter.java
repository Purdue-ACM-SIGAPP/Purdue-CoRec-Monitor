package club.sigapp.purduecorecmonitor.Utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class StackedLineGraphXAxisFormatter implements IAxisValueFormatter {
    String[] values;

    public StackedLineGraphXAxisFormatter(String[] values) {
        this.values = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return values[(int) value];
    }
}