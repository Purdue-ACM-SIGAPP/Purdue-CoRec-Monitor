package club.sigapp.purduecorecmonitor.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import club.sigapp.purduecorecmonitor.Models.LocationsModel;

public class Favorites {

    static ArrayList<String> runtimeFavorites;
    static List<LocationsModel> favoriteModels;

    public static void initalizeFavoriteFragment(List<LocationsModel> favs, Context c){
        favoriteModels = favs;
        SharedPreferences shared = SharedPrefsHelper.getSharedPrefs(c);
        if (shared.contains("favorites")) {
            Set<String> favorites = shared.getStringSet("favor" +
                    "ites", null);
            runtimeFavorites = new ArrayList<String>(Arrays.asList((favorites.toArray(new String[favorites.size()]))));
        } else {
            runtimeFavorites = new ArrayList<>();
        }
    }

    public static void addFavorite(Context c, String newFavorite, LocationsModel favoriteModel) {
        SharedPreferences shared = SharedPrefsHelper.getSharedPrefs(c);
        if (shared.contains("favorites")) {
            Set<String> favorites = shared.getStringSet("favor" +
                    "ites", null);
            if (!favorites.contains(newFavorite)) {
                favorites.add(newFavorite);
                shared.edit().remove("favorites").apply();
                shared.edit().putStringSet("favorites", favorites).apply();
            }
        } else {
            Set<String> newSet = new HashSet<>();
            newSet.add(newFavorite);
            shared.edit().putStringSet("favorites", newSet).apply();
        }

        LocationsModel model = new LocationsModel();
        model.LocationId = favoriteModel.LocationId;
        model.Location = favoriteModel.Location;
        model.ZoneId = favoriteModel.ZoneId;
        model.Capacity = favoriteModel.Capacity;
        model.Count = favoriteModel.Count;
        model.EntryDate = favoriteModel.EntryDate;
        model.DisplayName = favoriteModel.DisplayName;
        model.LocationName = favoriteModel.LocationName;
        favoriteModels.add(model);
        runtimeFavorites.add(newFavorite);

    }

    public static void removeFavorite(Context c, String toRemoveFavorite) {
        SharedPreferences shared = SharedPrefsHelper.getSharedPrefs(c);
        if (shared.contains("favorites")) {
            Set<String> favorites = shared.getStringSet("favorites", null);
            if (favorites.contains(toRemoveFavorite)) {
                favorites.remove(toRemoveFavorite);
                shared.edit().remove("favorites").apply();
                shared.edit().putStringSet("favorites", favorites).apply();
                runtimeFavorites.remove(toRemoveFavorite);
                for (int i = 0; i < favoriteModels.size(); i++){
                    if (favoriteModels.get(i).LocationId.equals(toRemoveFavorite)){
                        favoriteModels.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public static Set<String> getFavorites(Context c) {
        SharedPreferences shared = SharedPrefsHelper.getSharedPrefs(c);
        if (shared.contains("favorites")) {
            return shared.getStringSet("favorites", null);
        } else {
            return null;
        }
    }

    public static String[] getRuntimeFavorites() {
        return runtimeFavorites.toArray(new String[runtimeFavorites.size()]);
    }

    public static List<LocationsModel> getFavoriteModels() {
        return favoriteModels;
    }
}
