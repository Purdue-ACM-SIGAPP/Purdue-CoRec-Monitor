package club.sigapp.purduecorecmonitor.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Favorites {
    public static void addFavorite(Context c, String newFavorite) {
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
    }

    public static void removeFavorite(Context c, String toRemoveFavorite) {
        SharedPreferences shared = SharedPrefsHelper.getSharedPrefs(c);
        if (shared.contains("favorites")) {
            Set<String> favorites = shared.getStringSet("favorites", null);
            if (favorites.contains(toRemoveFavorite)) {
                favorites.remove(toRemoveFavorite);
                shared.edit().remove("favorites").apply();
                shared.edit().putStringSet("favorites", favorites).apply();
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
}
