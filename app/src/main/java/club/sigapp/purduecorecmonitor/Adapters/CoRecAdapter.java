package club.sigapp.purduecorecmonitor.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class CoRecAdapter extends RecyclerView.Adapter<CoRecAdapter.AreaViewHolder> {
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

        public AreaViewHolder(View itemView) {
            super(itemView);
        }
    }
}
