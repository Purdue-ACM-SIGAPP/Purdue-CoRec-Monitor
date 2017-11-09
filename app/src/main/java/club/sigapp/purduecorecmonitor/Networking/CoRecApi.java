package club.sigapp.purduecorecmonitor.Networking;

import java.util.List;

import club.sigapp.purduecorecmonitor.Models.LastUpdatedModel;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.Models.MonthlyTrendsModel;
import club.sigapp.purduecorecmonitor.Models.WeeklyTrendsModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CoRecApi {
    @GET("currentactivity")
    Call<List<LocationsModel>> getAllLocations();

    @GET("locations/{location}")
    Call<LocationsModel> getLocationDetails(@Path("location") String location);

    @GET("monthlytrends/{location}")
    Call<List<MonthlyTrendsModel>> getLocationMonthlyTrend(@Path("location") String location);

    @GET("weeklytrends/{location}")
    Call<List<WeeklyTrendsModel>> getLocationWeeklyTrend(@Path("location") String location);

    @GET("lastupdatedtime/{location}")
    Call<List<LastUpdatedModel>> getLocationLastUpdatedTime(@Path("location") String location);

    @GET("lastupdatedtime")
    Call<List<LastUpdatedModel>> getLastUpdatedTime();
}
