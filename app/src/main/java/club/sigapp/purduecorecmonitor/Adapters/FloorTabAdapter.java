package club.sigapp.purduecorecmonitor.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import club.sigapp.purduecorecmonitor.Fragments.FloorFragment;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;

public class FloorTabAdapter extends FragmentPagerAdapter {
	private ArrayList<String> locations;
	private ArrayList<FloorFragment> fragments;

	public FloorTabAdapter(FragmentManager fm, List<LocationsModel> data) {
		super(fm);

		if (data == null) {
			locations = new ArrayList<>();
			fragments = new ArrayList<>();
			return;
		}

		HashMap<String, List<LocationsModel>> partitionedData = new HashMap<>();

		fragments = new ArrayList<>();
		for (LocationsModel model : data) {
			String floor = model.Location.Zone.ZoneName.replace("CoRec ", "");
			if (!partitionedData.containsKey(floor)) {
				partitionedData.put(floor, new ArrayList<LocationsModel>());
			}

			partitionedData.get(floor).add(model);
		}

		locations = new ArrayList<>(partitionedData.keySet());
		Collections.sort(locations, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return getLevelRank(s1) - getLevelRank(s2);
			}
		});
		for (String s : locations) {
			FloorFragment fragment = new FloorFragment();
			fragment.setModels(partitionedData.get(s));
			fragments.add(fragment);
		}
	}

	public void searchLocations(String s) {
		for (FloorFragment fragment : fragments) {
			fragment.searchLocations(s);
		}
	}

	private int getLevelRank(String l) {
		switch (l) {
			case "Basement": return 0;
			case "Level 1": return 1;
			case "Level 2": return 2;
			case "Level 3": return 3;
			case "Level 4": return 4;
			case "TREC": return 5;
			case "Comp Pool": return 6;
			case "Dive Pool": return 7;
			case "Rec Pool": return 8;
			default: return 9;
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
}
