package club.sigapp.purduecorecmonitor.Activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import club.sigapp.purduecorecmonitor.Adapters.CoRecAdapter;
import club.sigapp.purduecorecmonitor.Adapters.FloorTabAdapter;
import club.sigapp.purduecorecmonitor.Analytics.AnalyticsHelper;
import club.sigapp.purduecorecmonitor.Analytics.ScreenTrackedActivity;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.Networking.CoRecApiHelper;
import club.sigapp.purduecorecmonitor.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ScreenTrackedActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.loadingBar)
    ProgressBar loadingBar;

    @BindView(R.id.status)
    TextView status;

    final private Context context = this;
    private FloorTabAdapter floorTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        callRetrofit();

        AnalyticsHelper.initDefaultTracker(this.getApplication());
        setScreenName("Main List");
    }


    private void callRetrofit() {
        status.setVisibility(View.VISIBLE);
        status.setText(R.string.loading);
        loadingBar.setVisibility(View.VISIBLE);
        CoRecApiHelper.getInstance().getAllLocations().enqueue(new Callback<List<LocationsModel>>() {
            @Override
            public void onResponse(Call<List<LocationsModel>> call, Response<List<LocationsModel>> response) {
                if (response.body() == null || response.code() != 200) {
                    Toast.makeText(getApplicationContext(), R.string.main_loading_fail, Toast.LENGTH_LONG).show();
                    return;
                }
                status.setVisibility(View.GONE);
                loadingBar.setVisibility(View.GONE);
                boolean hasNonZero = false;
                for (LocationsModel location : response.body()) {
                    if (location.Count != 0) {
                        hasNonZero = true;
                        break;
                    }
                }
                if (!hasNonZero) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                            .setTitle(R.string.corec_website_failure_title)
                            .setMessage(R.string.corec_website_failure)
                            .setCancelable(false)
                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    callRetrofit();
                                }
                            }).setNegativeButton(R.string.okay, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    AlertDialog failure = alertDialogBuilder.create();
                    failure.show();
                }
                startAdapter(response.body());
            }

            @Override
            public void onFailure(Call<List<LocationsModel>> call, Throwable t) {
                status.setVisibility(View.GONE);
                loadingBar.setVisibility(View.GONE);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                        .setTitle(R.string.internet_error_title)
                        .setMessage(R.string.internet_error_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
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

    private void startAdapter(List<LocationsModel> data) {
        floorTabAdapter = new FloorTabAdapter(getSupportFragmentManager(), data);
        viewPager.setAdapter(floorTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onRefresh() {
        callRetrofit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                floorTabAdapter.searchLocations(query);
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                floorTabAdapter.searchLocations(s);
                return true;
            }
        });
        return true;
    }

    @OnClick(R.id.fit_button)
    public void onClickFit() {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage("com.google.android.apps.fitness");
            if (i == null) {
                throw new ActivityNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Google Fit error or not installed.", Toast.LENGTH_LONG).show();
        }
    }
}
