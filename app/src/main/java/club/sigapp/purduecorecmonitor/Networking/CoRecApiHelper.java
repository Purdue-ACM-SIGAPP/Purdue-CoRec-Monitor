package club.sigapp.purduecorecmonitor.Networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoRecApiHelper {
    private static CoRecApi coRecApi;

    public static CoRecApi getInstance() {
        if (coRecApi == null) {
            Gson gson = new GsonBuilder().setLenient().create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.purdue.edu/drsfacilityusage/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            coRecApi = retrofit.create(CoRecApi.class);
        }

        return coRecApi;
    }
}
