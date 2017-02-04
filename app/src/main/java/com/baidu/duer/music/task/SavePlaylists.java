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

public class SavePlaylists extends AsyncTask<Void, Void, Void> {
    Activity activity;
    HomeConfigInfo homeConfigInfo ;
    public SavePlaylists(Activity activity,HomeConfigInfo homeConfigInfo) {

        this.activity =activity;
        this.homeConfigInfo = homeConfigInfo;
    }

    public SavePlaylists(HomeActivity activity) {
        this.activity =activity;
        this.homeConfigInfo = activity.getHomeConfigInfo();

    }

    @Override
    protected Void doInBackground(Void... params) {
        if (!homeConfigInfo.isSavePLaylistsRunning()) {
            homeConfigInfo.setSavePLaylistsRunning(true);
            try {
                String json2 = new Gson().toJson(homeConfigInfo.getAllPlaylists());
                SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("allPlaylists", json2);
                prefsEditor.commit();
            } catch (Exception e) {

            }
            homeConfigInfo.setSavePLaylistsRunning(false);
        }
        return null;
    }
}