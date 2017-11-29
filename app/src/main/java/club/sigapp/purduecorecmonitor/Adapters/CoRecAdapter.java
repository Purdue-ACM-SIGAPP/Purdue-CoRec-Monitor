package club.sigapp.purduecorecmonitor.Adapters;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;

import club.sigapp.purduecorecmonitor.R;
import club.sigapp.purduecorecmonitor.Utils.Favorites;

public class CoRecAdapter extends RecyclerView.Adapter<CoRecAdapter.AreaViewHolder> {

    private List<LocationsModel> locations;
    private String[] favorites;
    private Context context;

    public CoRecAdapter(Context context, List<LocationsModel> data) {
        this.locations = data;
        if (Favorites.getFavorites(context) != null) {
            this.favorites = Favorites.getFavorites(context).toArray(new String[0]);
        }
        this.context = context;

        reorderList();
    }

    private void reorderList() {
        if (Favorites.getFavorites(context) != null) {
            this.favorites = Favorites.getFavorites(context).toArray(new String[0]);
        }
        Collections.sort(locations);

        int count = 0;
        //iterate through all locations that have been favorited
        if (favorites != null) {
            for (String s : favorites) {
                //iterate through all locations to search for favorited location
                for (int i = count; i < locations.size(); i++) {
                    if (s.equals(locations.get(i).LocationId)) {
                    /*move this location to index count and shift all others between count index
                    and i down one to make room at top
                     */
                        LocationsModel temp = locations.get(i);
                        for (int index = i; index > count; index--) {
                            //shift location to the right
                            locations.set(index, locations.get(index - 1));
                        }
                        locations.set(count++, temp);
                    }
                }
            }
        }
    }

    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_main_layout, parent, false);

        return new AreaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AreaViewHolder holder, int position) {
        holder.cardTitle.setText(locations.get(position).LocationName);
        String headString = "Headcount: " + locations.get(position).Headcount + " / Max: " + locations.get(position).Capacity;
        holder.headCount.setText(headString);
        boolean favorited = false;

        if (favorites != null) {
            for (String favorite : favorites) {
                if (locations.get(position).LocationId.equals(favorite)) {
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
        return locations.size();
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

        public AreaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.fav_button)
        public void onClickFav() {
            Set<String> favorites = Favorites.getFavorites(context);

            String locationId = locations.get(this.getLayoutPosition()).LocationId;

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
