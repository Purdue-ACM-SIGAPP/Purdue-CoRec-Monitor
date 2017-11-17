package club.sigapp.purduecorecmonitor.Adapters;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;


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

    public CoRecAdapter(Context context, String[] favorites, List<LocationsModel> data){
        this.locations = data;
        this.favorites = favorites;
        this.context = context;
    }

    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_main_layout, parent, false);

        return new AreaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AreaViewHolder holder, int position) {
        holder.cardTitle.setText(locations.get(position).LocationName);
        String headString = "Headcount: " + locations.get(position).Headcount;
        holder.headCount.setText(headString);
        boolean favorited = false;

        for (int i = 0; i < favorites.length; i++){
            if (locations.get(position).LocationId.equals(favorites[i])){
                holder.favButton.setImageResource(R.drawable.ic_favorited_star);
                favorited = true;
            }
        }

        if (!favorited){
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
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.fav_button)
        public void onClickFav() {
            Set<String> favorites = Favorites.getFavorites(context);
            if(favorites.contains(cardTitle.getText())){
                //Item was already favorited, so change it to the unfavorited star and remove from favorites
                favButton.setImageResource(R.drawable.ic_unfavorited_star);
                Favorites.removeFavorite(context, (String)cardTitle.getText());
            }

            Toast.makeText(context, cardTitle.getText() + " added to favorites.", Toast.LENGTH_SHORT).show();
        }

    }
}
