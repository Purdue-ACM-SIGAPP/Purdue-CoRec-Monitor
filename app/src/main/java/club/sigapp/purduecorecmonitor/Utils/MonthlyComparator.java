package club.sigapp.purduecorecmonitor.Utils;

import java.util.Comparator;

import club.sigapp.purduecorecmonitor.Models.MonthlyTrendsModel;

public class MonthlyComparator implements Comparator<MonthlyTrendsModel>{
    @Override
    public int compare(MonthlyTrendsModel t1, MonthlyTrendsModel t2) {
        return new Integer(t1.EntryMonth).compareTo(new Integer(t2.EntryMonth));
    }
}
