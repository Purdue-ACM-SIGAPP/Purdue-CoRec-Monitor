package club.sigapp.purduecorecmonitor.Adapters;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import club.sigapp.purduecorecmonitor.Activities.MainActivity;
import club.sigapp.purduecorecmonitor.Fragments.FloorFragment;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.Networking.CoRecApiHelper;
import club.sigapp.purduecorecmonitor.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FloorTabAdapter extends FragmentPagerAdapter {
	private ArrayList<String> locations;
	private ArrayList<FloorFragment> fragments;
	private MainActivity context;

	public FloorTabAdapter(FragmentManager fm, MainActivity context) {
		super(fm);
		this.context = context;
		this.locations = new ArrayList<>();
		this.fragments = new ArrayList<>();
		callRetrofit(null);
	}

	private void parseData(List<LocationsModel> data) {
		fragments.clear();
		locations.clear();
		notifyDataSetChanged();

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
			fragment.setModels(partitionedData.get(s), context);
			fragments.add(fragment);
		}
		notifyDataSetChanged();
		//context.swipeRefreshLayout.setRefreshing(false);
	}

	public void callRetrofit(final SwipeRefreshLayout swipeRefreshLayout) {
		CoRecApiHelper.getInstance().getAllLocations().enqueue(new Callback<List<LocationsModel>>() {
			@Override
			public void onResponse(Call<List<LocationsModel>> call, Response<List<LocationsModel>> response) {
				if (response.body() == null || response.code() != 200) {
					Toast.makeText(context, R.string.main_loading_fail, Toast.LENGTH_LONG).show();
					return;
				}
				boolean hasNonZero = false;
				for (LocationsModel location : response.body()) {
					if (location.Count != 0) {
						hasNonZero = true;
						break;
					}
				}
				if (!hasNonZero) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
							.setTitle(R.string.corec_website_failure_title)
							.setMessage(R.string.corec_website_failure)
							.setCancelable(false)
							.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									callRetrofit(swipeRefreshLayout);
								}
							}).setNegativeButton(R.string.okay, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									dialogInterface.dismiss();
								}
							});
					AlertDialog failure = alertDialogBuilder.create();
					failure.show();
				}
				parseData(response.body());
				if(swipeRefreshLayout != null) {
					swipeRefreshLayout.setRefreshing(false);
				}
			}

			@Override
			public void onFailure(Call<List<LocationsModel>> call, Throwable t) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
						.setTitle(R.string.internet_error_title)
						.setMessage(R.string.internet_error_message)
						.setCancelable(false)
						.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								callRetrofit(swipeRefreshLayout);
							}
						});
				AlertDialog failure = alertDialogBuilder.create();
				failure.show();
			}
		});
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
