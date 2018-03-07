package club.sigapp.purduecorecmonitor.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Adapters.CoRecAdapter;
import club.sigapp.purduecorecmonitor.Adapters.FloorTabAdapter;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.R;
import club.sigapp.purduecorecmonitor.Utils.Favorites;

import static club.sigapp.purduecorecmonitor.Activities.MainActivity.floorTabAdapter;

public class FloorFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.no_favorites_text)
    TextView noFavsText;

    private CoRecAdapter coRecAdapter;
    boolean isFavFragment = false;
    int myFragmentIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_floor, container, false);
        ButterKnife.bind(this, v);
        noFavsText.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(coRecAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        return v;
    }

    public void updateNeighbors(){
        if ((myFragmentIndex == 0) && (FloorTabAdapter.getFragments().size() > 1)){
            FloorTabAdapter.getFragments().get(1).favoritesUpdate();
        } else if (myFragmentIndex == 1){
            FloorTabAdapter.getFragments().get(0).favoritesUpdate();
        }
    }

    public void favoritesUpdate(){
        getAdaptor().setFavorites(Favorites.getRuntimeFavorites());
        if (isFavFragment){
            getAdaptor().setLocations(Favorites.getFavoriteModels());
            getAdaptor().notifyDataSetChanged();
            checkDisplayNoFavorites();
        } else if (myFragmentIndex == 1){
            getAdaptor().notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        favoritesUpdate();
    }

    public void searchLocations(String s) {
        coRecAdapter.setSearchText(s);
        coRecAdapter.reorderList();
    }

    public void setModels(List<LocationsModel> models, Context c) {
        coRecAdapter = new CoRecAdapter(c, models, this);
        coRecAdapter.notifyDataSetChanged();
    }

    public int getMyFragmentIndex() {
        return myFragmentIndex;
    }

    public void setMyFragmentIndex(int myFragmentIndex) {
        this.myFragmentIndex = myFragmentIndex;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        floorTabAdapter.callRetrofit(swipeRefreshLayout);
    }

    private CoRecAdapter getAdaptor(){ //don't call from other things
        return (CoRecAdapter) recyclerView.getAdapter();
    }

    public void checkDisplayNoFavorites(){
        if (getAdaptor().getItemCount() == 0){
            noFavsText.setVisibility(View.VISIBLE);
        } else {
            noFavsText.setVisibility(View.GONE);
        }
    }

    public void setFavFragment(boolean favFragment) {
        isFavFragment = favFragment;
    }
}
