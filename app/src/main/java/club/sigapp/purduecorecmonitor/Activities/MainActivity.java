package club.sigapp.purduecorecmonitor.Activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import club.sigapp.purduecorecmonitor.Adapters.FloorTabAdapter;
import club.sigapp.purduecorecmonitor.Analytics.AnalyticsHelper;
import club.sigapp.purduecorecmonitor.Analytics.ScreenTrackedActivity;
import club.sigapp.purduecorecmonitor.R;

public class MainActivity extends ScreenTrackedActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.loadingBar)
    ProgressBar loadingBar;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout swipeRefreshLayout;

    final private Context context = this;
    private FloorTabAdapter floorTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        floorTabAdapter = new FloorTabAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(floorTabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        AnalyticsHelper.initDefaultTracker(this.getApplication());
        setScreenName("Main List");
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        floorTabAdapter.callRetrofit();
    }
}
