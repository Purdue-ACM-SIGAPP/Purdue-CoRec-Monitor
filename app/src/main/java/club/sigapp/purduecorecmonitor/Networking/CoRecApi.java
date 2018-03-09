package club.sigapp.purduecorecmonitor.Networking;

import java.util.List;

import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.Models.MonthlyTrendsModel;
import club.sigapp.purduecorecmonitor.Models.WeeklyTrendsModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CoRecApi {
    @GET("CurrentActivity")
    Call<List<LocationsModel>> getAllLocations();

    @GET("MonthlyTrends")
    Call<List<MonthlyTrendsModel>> getLocationMonthlyTrend();

    @GET("WeeklyTrends")
    Call<List<WeeklyTrendsModel>> getLocationWeeklyTrend();
}
