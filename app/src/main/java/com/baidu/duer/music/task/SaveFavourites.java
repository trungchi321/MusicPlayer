package com.baidu.duer.music.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.baidu.duer.music.HomeActivity;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by niufei01 on 17/2/3.
 */

public class SaveFavourites extends AsyncTask<Void, Void, Void> {
    Activity activity;
    HomeConfigInfo homeConfigInfo;

    public SaveFavourites(HomeActivity activity) {

        this.activity =activity;
        this.homeConfigInfo = activity.getHomeConfigInfo();

    }
    public SaveFavourites(Activity activity,HomeConfigInfo homeConfigInfo) {

        this.activity =activity;
        this.homeConfigInfo = homeConfigInfo;

    }
    @Override
    protected Void doInBackground(Void... params) {
        if (!homeConfigInfo.isSaveFavouritesRunning()) {
            homeConfigInfo.setSaveFavouritesRunning(true);
            try {
                String json5 = new Gson().toJson(homeConfigInfo.getFavouriteTracks());
                SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("favouriteTracks", json5);
                prefsEditor.commit();
            } catch (Exception e) {

            }
            homeConfigInfo.setSaveFavouritesRunning(false);
        }
        return null;
    }
}