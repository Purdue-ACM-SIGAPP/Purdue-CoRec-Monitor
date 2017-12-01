package club.sigapp.purduecorecmonitor.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Adapters.CoRecAdapter;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.Networking.CoRecApiHelper;
import club.sigapp.purduecorecmonitor.R;
import club.sigapp.purduecorecmonitor.Utils.Favorites;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainRecyclerView)
    RecyclerView mainRecyclerView;

    @BindView(R.id.loadingBar)
    ProgressBar loadingBar;

    @BindView(R.id.status)
    TextView status;

    private CoRecAdapter coRecAdapter;
    final private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        callRetrofit();
    }

    private void callRetrofit() {
        status.setVisibility(View.VISIBLE);
        status.setText("Loading...");
        loadingBar.setVisibility(View.VISIBLE);
        CoRecApiHelper.getInstance().getAllLocations().enqueue(new Callback<List<LocationsModel>>() {
            @Override
            public void onResponse(Call<List<LocationsModel>> call, Response<List<LocationsModel>> response) {
                status.setVisibility(View.GONE);
                loadingBar.setVisibility(View.GONE);
                boolean hasNonZero = false;
                for (LocationsModel location : response.body()) {
                    if (location.Headcount != 0) {
                        hasNonZero = true;
                        break;
                    }
                }
                if (!hasNonZero) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                            .setTitle("CoRec Website Error")
                            .setMessage("It appears that the CoRec website returned all locations as" +
                                    " having no people. This probably means that the website is down.")
                            .setCancelable(false)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    callRetrofit();
                                }
                            }).setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    AlertDialog failure = alertDialogBuilder.create();
                    failure.show();
                }
                startAdaptor(response.body());

            }

            @Override
            public void onFailure(Call<List<LocationsModel>> call, Throwable t) {
                status.setVisibility(View.GONE);
                loadingBar.setVisibility(View.GONE);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                        .setTitle("Data retrieval failed")
                        .setMessage("Unable to connect to the Internet")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callRetrofit();
                            }
                        });
                AlertDialog failure = alertDialogBuilder.create();
                failure.show();
            }
        });
    }

    private void startAdaptor(List<LocationsModel> data) {
        coRecAdapter = new CoRecAdapter(this, data);
        coRecAdapter.notifyDataSetChanged();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mainRecyclerView.setLayoutManager(linearLayoutManager);

        mainRecyclerView.setAdapter(coRecAdapter);

    }


}
