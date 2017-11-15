package club.sigapp.purduecorecmonitor.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.OnClick;


import java.util.List;

import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;

import club.sigapp.purduecorecmonitor.R;

public class CoRecAdapter extends RecyclerView.Adapter<CoRecAdapter.AreaViewHolder> {

    public CoRecAdapter(String[] favorites, List<LocationsModel> data){

    }

    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AreaViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }



    public class AreaViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fav_button)
        CardView areaCardView;

        public AreaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.fav_button)
        public void onClickFav() {
            
        }
    }
}
