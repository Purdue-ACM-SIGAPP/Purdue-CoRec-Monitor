package club.sigapp.purduecorecmonitor.Utils;

import java.util.Comparator;

import club.sigapp.purduecorecmonitor.Models.MonthlyTrendsModel;

/**
 * Created by vikastatineni on 11/30/17.
 */

public class MonthlyComparator implements Comparator<MonthlyTrendsModel>{
    @Override
    public int compare(MonthlyTrendsModel t1, MonthlyTrendsModel t2) {
        return new Integer(t1.Month).compareTo(new Integer(t2.Month));
    }
}
