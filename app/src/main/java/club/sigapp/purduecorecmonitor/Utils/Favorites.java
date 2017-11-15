package club.sigapp.purduecorecmonitor.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Favorites {
    public static void addFavorite(Context c, String newFavorite) {
        SharedPreferences shared = SharedPrefsHelper.getSharedPrefs(c);
        if (shared.contains("favorite")) {
            Set<String> favorites = shared.getStringSet("favorite", null);
            if (!favorites.contains(newFavorite)) {
                favorites.add(newFavorite);
                shared.edit().remove("favorite").apply();
                shared.edit().putStringSet("favorite", favorites).apply();
            }
        } else {
            Set<String> newSet = new HashSet<>();
            newSet.add(newFavorite);
            shared.edit().putStringSet("favorite", newSet).apply();
        }

    }

    public static void removeFavorite(Context c, String newFavorite) {


    }

}
