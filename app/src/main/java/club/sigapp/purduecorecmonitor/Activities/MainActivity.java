package club.sigapp.purduecorecmonitor.Activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Adapters.CoRecAdapter;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.Networking.CoRecApiHelper;
import club.sigapp.purduecorecmonitor.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainRecyclerView)
    RecyclerView mainRecyclerView;
    private CoRecAdapter coRecAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String[] favorites = new String[1];
        CoRecApiHelper.getInstance().getAllLocations().enqueue(new Callback<List<LocationsModel>>() {
            @Override
            public void onResponse(Call<List<LocationsModel>> call, Response<List<LocationsModel>> response) {
                coRecAdapter = new CoRecAdapter(favorites, response.body());
                mainRecyclerView.setAdapter(coRecAdapter);
            }

            @Override
            public void onFailure(Call<List<LocationsModel>> call, Throwable t) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Retry")
                        .setMessage("Unable to connect to the Internet");
            }
        });


    }
}
