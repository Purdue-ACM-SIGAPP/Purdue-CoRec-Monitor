package club.sigapp.purduecorecmonitor.Activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import club.sigapp.purduecorecmonitor.Adapters.CoRecAdapter;
import club.sigapp.purduecorecmonitor.Adapters.FloorTabAdapter;
import club.sigapp.purduecorecmonitor.Analytics.AnalyticsHelper;
import club.sigapp.purduecorecmonitor.Analytics.ScreenTrackedActivity;
import club.sigapp.purduecorecmonitor.Fragments.FloorFragment;
import club.sigapp.purduecorecmonitor.R;

public class MainActivity extends ScreenTrackedActivity {
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

    final private Context context = this;
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

        final MenuItem searchButton = menu.findItem(R.id.action_search);
        final MenuItem fitButton = menu.findItem(R.id.action_fit);

        final SearchView searchView = (SearchView) searchButton.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });

        searchButton.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                fitButton.setVisible(false);
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                recyclerViewSearch.setVisibility(View.VISIBLE);

                if(coRecAdapter == null) {
                    coRecAdapter = new CoRecAdapter(context, floorTabAdapter.locationsModels, null);
                }else{
                    coRecAdapter.setLocations(floorTabAdapter.locationsModels);
                }
                coRecAdapter.reorderList();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
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
                floorTabAdapter.getFragments().get(0).favoritesUpdate();
                return true;
            }
        });

        fitButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onClickFit();
                return true;
            }
        });
        return true;
    }

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
