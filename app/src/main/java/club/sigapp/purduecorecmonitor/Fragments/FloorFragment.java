package club.sigapp.purduecorecmonitor.Fragments;

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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import club.sigapp.purduecorecmonitor.Adapters.CoRecAdapter;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.R;

public class FloorFragment extends Fragment {
	private List<LocationsModel> models;

	@BindView(R.id.recycler_view)
	RecyclerView recyclerView;
	private CoRecAdapter coRecAdapter;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_floor, container, false);
		ButterKnife.bind(this, v);

		coRecAdapter = new CoRecAdapter(getContext(), models);
		coRecAdapter.notifyDataSetChanged();
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setAdapter(coRecAdapter);

		return v;
	}

	public void searchLocations(String s) {
		if (coRecAdapter == null) return;
		coRecAdapter.searchLocations(s);
	}

	public void setModels(List<LocationsModel> models) {
		this.models = models;
	}
}
