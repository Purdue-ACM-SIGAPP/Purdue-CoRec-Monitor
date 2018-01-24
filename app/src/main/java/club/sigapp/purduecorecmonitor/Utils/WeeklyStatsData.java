package club.sigapp.purduecorecmonitor.Utils;

import java.util.List;

import club.sigapp.purduecorecmonitor.Models.WeeklyTrendsModel;

/**
 * Created by Slang on 1/23/2018.
 */
public class WeeklyStatsData {

    private static WeeklyStatsData weeklyStatsData = null;
    private List<WeeklyTrendsModel> data;

    public void setData(List<WeeklyTrendsModel> data){
        this.data = data;
    }

    public List<WeeklyTrendsModel> getData(){
        return data;
    }

    public static void setInstance(WeeklyStatsData weeklyStats){
        weeklyStatsData = weeklyStats;
    }

    public static WeeklyStatsData getInstance() {
        return weeklyStatsData;
    }
}