package club.sigapp.purduecorecmonitor.Utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class BarGraphXAxisFormatter implements IAxisValueFormatter {
    String[] values;

    public BarGraphXAxisFormatter(String[] values) {
        this.values = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return values[(int) value];
    }
}
