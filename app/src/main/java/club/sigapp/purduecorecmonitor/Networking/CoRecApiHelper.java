package club.sigapp.purduecorecmonitor.Networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoRecApiHelper {
    private static CoRecApi coRecApi;

    public static CoRecApi getInstance() {
        if (coRecApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.purdue.edu/drsfacilityusage/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            coRecApi = retrofit.create(CoRecApi.class);
        }

        return coRecApi;
    }
}
