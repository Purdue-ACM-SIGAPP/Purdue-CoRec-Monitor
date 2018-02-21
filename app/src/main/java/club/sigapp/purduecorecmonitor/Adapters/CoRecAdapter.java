package club.sigapp.purduecorecmonitor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import club.sigapp.purduecorecmonitor.Activities.StatisticsActivity;
import club.sigapp.purduecorecmonitor.Models.Location;
import club.sigapp.purduecorecmonitor.Analytics.AnalyticsHelper;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.R;
import club.sigapp.purduecorecmonitor.Utils.Favorites;

public class CoRecAdapter extends RecyclerView.Adapter<CoRecAdapter.AreaViewHolder> {
    private List<LocationsModel> locations;
    private List<LocationsModel> filteredLocations;
    private String[] favorites;
    private Context context;
    private String searchText = "";

    public CoRecAdapter(Context context, List<LocationsModel> data) {
        this.locations = data;
        this.filteredLocations = this.locations;
        if (Favorites.getFavorites(context) != null) {
            this.favorites = Favorites.getFavorites(context).toArray(new String[0]);
        }
        this.context = context;

        reorderList();
    }

    public void reorderList() {

        if (searchText.length() != 0) {
            filteredLocations = new ArrayList<>();
            for (LocationsModel location : locations) {
                if (location.LocationName.toLowerCase().contains(searchText.toLowerCase())) {
                    filteredLocations.add(location);
                }
            }
        }
        else {
            filteredLocations = locations;
        }

        if (Favorites.getFavorites(context) != null) {
            this.favorites = Favorites.getFavorites(context).toArray(new String[0]);
        }
        Collections.sort(filteredLocations);

        int count = 0;
        //iterate through all locations that have been favorited
        if (favorites != null) {
            for (String s : favorites) {
                //iterate through all locations to search for favorited location
                for (int i = count; i < filteredLocations.size(); i++) {
                    if (s.equals(filteredLocations.get(i).LocationId)) {
                    /*move this location to index count and shift all others between count index
                    and i down one to make room at top
                     */
                        LocationsModel temp = filteredLocations.get(i);
                        for (int index = i; index > count; index--) {
                            //shift location to the right
                            filteredLocations.set(index, filteredLocations.get(index - 1));
                        }
                        filteredLocations.set(count++, temp);
                    }
                }
            }
        }

        notifyDataSetChanged();
    }


    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_main_layout, parent, false);

        return new AreaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AreaViewHolder holder, int position) {
        holder.cardTitle.setText(filteredLocations.get(position).LocationName);
        String headString = "Headcount: " + filteredLocations.get(position).Count + " / Max: " + filteredLocations.get(position).Capacity;
        holder.headCount.setText(headString);

        switch(filteredLocations.get(position).Location.Zone.ZoneName){
            case "CoRec Basement":
                Picasso.with(context).load(R.drawable.ic_floor_basement).fit().into(holder.icon);
                break;
            case "CoRec Level 1":
                Picasso.with(context).load(R.drawable.ic_floor_one).fit().into(holder.icon);
                break;
            case "CoRec Level 2":
                Picasso.with(context).load(R.drawable.ic_floor_two).fit().into(holder.icon);
                break;
            case "CoRec Level 3":
                Picasso.with(context).load(R.drawable.ic_floor_three).fit().into(holder.icon);
                break;
            case "CoRec Level 4":
                Picasso.with(context).load(R.drawable.ic_floor_four).fit().into(holder.icon);
                break;
            case "TREC":
                Picasso.with(context).load(R.drawable.ic_floor_trec).fit().into(holder.icon);
                break;
            case "Comp Pool":
                Picasso.with(context).load(R.drawable.ic_floor_pool).fit().into(holder.icon);
                break;
            case "Dive Pool":
                Picasso.with(context).load(R.drawable.ic_floor_pool).fit().into(holder.icon);
                break;
            case "Rec Pool":
                Picasso.with(context).load(R.drawable.ic_floor_pool).fit().into(holder.icon);
                break;
            default:
                Log.e("MainActivity", "Unknown zone name.");
        }

        boolean favorited = false;

        if (favorites != null) {
            for (String favorite : favorites) {
                if (filteredLocations.get(position).LocationId.equals(favorite)) {
                    holder.favButton.setImageResource(R.drawable.ic_favorited_star);
                    favorited = true;
                }
            }
        }

        if (!favorited) {
            holder.favButton.setImageResource(R.drawable.ic_unfavorited_star);
        }
    }

    @Override
    public int getItemCount() {
        return filteredLocations.size();
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }


    public class AreaViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_main)
        CardView cardMain;

        @BindView(R.id.fav_button)
        ImageButton favButton;

        @BindView(R.id.headcount)
        TextView headCount;

        @BindView(R.id.card_main_title)
        TextView cardTitle;

        @BindView (R.id.icons)
        ImageView icon;

        public AreaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.card_main)
        public void onClickCard() {
            String locationId = filteredLocations.get(this.getLayoutPosition()).Location.LocationId;
            AnalyticsHelper.sendEventHit("Location Clicked", AnalyticsHelper.CLICK, locations.get(this.getLayoutPosition()).LocationName);
            Intent intent = new Intent(context, StatisticsActivity.class);
            intent.putExtra("LocationId", locationId);
            intent.putExtra("CorecRoom", filteredLocations.get(this.getLayoutPosition()).LocationName);
            context.startActivity(intent);
        }

        @OnClick(R.id.fav_button)
        public void onClickFav() {
            Set<String> favorites = Favorites.getFavorites(context);

            String locationId = filteredLocations.get(this.getLayoutPosition()).LocationId;

            if (favorites != null && favorites.contains(locationId)) {
                //Item was already favorited, so change it to the unfavorited star and remove from favorites
                favButton.setImageResource(R.drawable.ic_unfavorited_star);
                Favorites.removeFavorite(context, locationId);
            } else {
                //Not in favorites yet - change to favorited star and add to favorites
                favButton.setImageResource(R.drawable.ic_favorited_star);
                Favorites.addFavorite(context, locationId);
            }


            reorderList();
            notifyDataSetChanged();

        }

    }
}
