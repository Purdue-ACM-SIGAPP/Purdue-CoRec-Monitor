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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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

public class MainActivity extends ScreenTrackedActivity implements MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener {
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.loadingBar)
    public ProgressBar loadingBar;

    @BindView(R.id.status)
    public TextView status;

    @BindView(R.id.recycler_view_search)
    public RecyclerView recyclerViewSearch;

    private MenuItem searchButton;
    private MenuItem fitButton;
    private SearchView searchView;

    public static FloorTabAdapter floorTabAdapter;
    public static CoRecAdapter coRecAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingBar.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);

        floorTabAdapter = new FloorTabAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(floorTabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        AnalyticsHelper.initDefaultTracker(this.getApplication());
        setScreenName("Main List");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        searchButton = menu.findItem(R.id.action_search);
        searchButton.setOnActionExpandListener(this);
        searchView = (SearchView) searchButton.getActionView();
        searchView.setOnQueryTextListener(this);

        fitButton = menu.findItem(R.id.action_fit);
        fitButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onClickFit();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        fitButton.setVisible(false);
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        recyclerViewSearch.setVisibility(View.VISIBLE);

        if (coRecAdapter == null) {
            coRecAdapter = new CoRecAdapter(this, floorTabAdapter.locationsModels, null);
        } else {
            coRecAdapter.setLocations(floorTabAdapter.locationsModels);
        }
        coRecAdapter.reorderList();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewSearch.setLayoutManager(linearLayoutManager);
        recyclerViewSearch.setAdapter(coRecAdapter);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        fitButton.setVisible(true);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        recyclerViewSearch.setVisibility(View.GONE);
        floorTabAdapter.getFragments().get(viewPager.getCurrentItem()).updateNeighbors();
        //force favorites to update itself with new favorites list
        floorTabAdapter.getFragments().get(0).favoritesUpdate();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        coRecAdapter.searchLocations(query);
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
        coRecAdapter.searchLocations(s);
        return true;
    }

    public void onClickFit() {
        PackageManager manager = getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage("com.google.android.apps.fitness");
            if (i == null) {
                throw new ActivityNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Google Fit error or not installed.", Toast.LENGTH_LONG).show();
        }
    }

    public void callRetrofit(final SwipeRefreshLayout swipeRefreshLayout) {
        CoRecApiHelper.getInstance().getAllLocations().enqueue(new Callback<List<LocationsModel>>() {
            @Override
            public void onResponse(Call<List<LocationsModel>> call, Response<List<LocationsModel>> response) {
                loadingBar.setVisibility(View.GONE);
                status.setVisibility(View.GONE);

                if (response.body() == null || response.code() != 200) {
                    Toast.makeText(MainActivity.this, R.string.main_loading_fail, Toast.LENGTH_LONG).show();
                    return;
                }
                boolean hasNonZero = false;
                for (LocationsModel location : response.body()) {
                    if (location.Count != 0) {
                        hasNonZero = true;
                        break;
                    }
                }
                if (!hasNonZero) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.corec_website_failure_title)
                            .setMessage(R.string.corec_website_failure)
                            .setCancelable(false)
                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    callRetrofit(swipeRefreshLayout);
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
                floorTabAdapter.parseData(response.body());
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<LocationsModel>> call, Throwable t) {
                loadingBar.setVisibility(View.GONE);
                status.setVisibility(View.GONE);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.internet_error_title)
                        .setMessage(R.string.internet_error_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callRetrofit(swipeRefreshLayout);
                            }
                        });
                AlertDialog failure = alertDialogBuilder.create();
                failure.show();
            }
        });
    }
}
