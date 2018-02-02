package club.sigapp.purduecorecmonitor.Adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import club.sigapp.purduecorecmonitor.Fragments.FloorFragment;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;

public class FloorTabAdapter extends FragmentPagerAdapter {
	private ArrayList<String> locations;
	private ArrayList<Fragment> fragments;

	public FloorTabAdapter(FragmentManager fm, List<LocationsModel> data) {
		super(fm);

		fragments = new ArrayList<>();
		HashMap<String, List<LocationsModel>> partitionedData = new HashMap<>();
		for (LocationsModel model : data) {
			String floor = model.Location.Zone.ZoneName.replace("CoRec ", "");
			if (!partitionedData.containsKey(floor)) {
				partitionedData.put(floor, new ArrayList<LocationsModel>());
			}

			partitionedData.get(floor).add(model);
		}

		locations = new ArrayList<>(partitionedData.keySet());
		for (String s : locations) {
			FloorFragment fragment = new FloorFragment();
			fragment.setModels(partitionedData.get(s));
			fragments.add(fragment);
		}
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return locations.get(position);
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@NonNull
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}
}
