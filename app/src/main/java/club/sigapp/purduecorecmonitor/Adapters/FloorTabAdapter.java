package club.sigapp.purduecorecmonitor.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import club.sigapp.purduecorecmonitor.Activities.MainActivity;
import club.sigapp.purduecorecmonitor.Fragments.FloorFragment;
import club.sigapp.purduecorecmonitor.Models.LocationsModel;
import club.sigapp.purduecorecmonitor.Utils.Favorites;

public class FloorTabAdapter extends FragmentPagerAdapter {
    private ArrayList<String> locations;
    private static ArrayList<FloorFragment> fragments;
    private MainActivity context;
    private static final String FAVORITEKEY = "favorites";
    public static List<LocationsModel> locationsModels;

    public FloorTabAdapter(FragmentManager fm, MainActivity context) {
        super(fm);
        this.context = context;
        this.locations = new ArrayList<>();
        fragments = new ArrayList<>();
        context.callRetrofit(null);
    }

    public void parseData(List<LocationsModel> data) {
        locations.clear();
        locationsModels = data;
        HashMap<String, List<LocationsModel>> partitionedData = new HashMap<>();
        String[] favorites;
        if (Favorites.getFavorites(context) != null) {
            favorites = Favorites.getFavorites(context).toArray(new String[0]);
        } else {
            favorites = new String[]{};
        }
        //must instantiate list if first time through
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        for (LocationsModel model : data) {
            String floor = model.Location.Zone.ZoneName.replace("CoRec ", "");
            if (!partitionedData.containsKey(floor)) {
                partitionedData.put(floor, new ArrayList<LocationsModel>());
            }
            partitionedData.get(floor).add(model);
            for (String s : favorites) {
                if (s.equals(model.LocationId)) {
                    if (!partitionedData.containsKey(FAVORITEKEY)) {
                        partitionedData.put(FAVORITEKEY, new ArrayList<LocationsModel>());
                    }
                    LocationsModel m = new LocationsModel();
                    m.Count = model.Count;
                    m.Location = model.Location;
                    m.LocationName = model.LocationName;
                    m.Capacity = model.Capacity;
                    m.LocationId = model.LocationId;
                    m.DisplayName = model.DisplayName;
                    m.EntryDate = model.EntryDate;
                    m.ZoneId = model.ZoneId;
                    partitionedData.get(FAVORITEKEY).add(m);
                }
            }
        }

        locations = new ArrayList<>(partitionedData.keySet());
        boolean displayEmptyFavorites = false;
        if (!locations.contains(FAVORITEKEY)) {
            locations.add(FAVORITEKEY);
            displayEmptyFavorites = true;
        }
        Collections.sort(locations, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return getLevelRank(s1) - getLevelRank(s2);
            }
        });
        if (fragments.size() == 0) {
            //first time creating fragments
            for (String s : locations) {
                FloorFragment fragment = new FloorFragment();
                if (s.equals(FAVORITEKEY)) {
                    List<LocationsModel> favModels;
                    if (displayEmptyFavorites) {
                        favModels = new ArrayList<>();
                    } else {
                        favModels = partitionedData.get(FAVORITEKEY);
                    }
                    Favorites.initalizeFavoriteFragment(favModels, context);
                    fragment.setFavFragment(true);
                    fragment.setModels(favModels, context);
                } else {
                    fragment.setModels(partitionedData.get(s), context);
                }
                fragment.setMyFragmentIndex(fragments.size());
                fragment.setLocationString(s);
                fragments.add(fragment);
            }
        } else {
            //updating fragments with the new info
            for (FloorFragment fragment : fragments) {
                if (fragment.getLocationString().equals(FAVORITEKEY)) {
                    List<LocationsModel> favModels;
                    if (displayEmptyFavorites) {
                        favModels = new ArrayList<>();
                    } else {
                        favModels = partitionedData.get(FAVORITEKEY);
                    }
                    Favorites.initalizeFavoriteFragment(favModels, context);
                    fragment.setFavFragment(true);
                    fragment.setModels(favModels, context);
                } else {
                    List<LocationsModel> model = partitionedData.get(fragment.getLocationString());
                    if (model != null) {
                        fragment.setModels(model, context);
                    } else {
                        fragment.setModels(new ArrayList<LocationsModel>(), context);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private int getLevelRank(String l) {
        switch (l) {
            case FAVORITEKEY:
                return -1;
            case "Basement":
                return 0;
            case "Level 1":
                return 1;
            case "Level 2":
                return 2;
            case "Level 3":
                return 3;
            case "Level 4":
                return 4;
            case "TREC":
                return 5;
            case "Comp Pool":
                return 6;
            case "Dive Pool":
                return 7;
            case "Rec Pool":
                return 8;
            default:
                return 9;
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

    public static ArrayList<FloorFragment> getFragments() {
        return fragments;
    }
}
