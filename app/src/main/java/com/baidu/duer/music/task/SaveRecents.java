package com.baidu.duer.music.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.baidu.duer.music.HomeActivity;
import com.google.gson.Gson;

/**
 * Created by niufei01 on 17/2/3.
 */

public class SaveRecents extends AsyncTask<Void, Void, Void> {

    Activity activity;
    HomeConfigInfo homeConfigInfo;
    public SaveRecents(Activity activity, HomeConfigInfo homeConfigInfo) {

        this.activity =activity;
        this.homeConfigInfo =homeConfigInfo;
    }

    public SaveRecents(HomeActivity activity) {
        this.activity =activity;
        this.homeConfigInfo =activity.getHomeConfigInfo();
    }


    @Override
    protected Void doInBackground(Void... params) {

        if (!homeConfigInfo.isSaveRecentsRunning()) {
            homeConfigInfo.setSaveRecentsRunning(true);
            try {
                String json4 = new Gson().toJson(homeConfigInfo.getRecentlyPlayed());
                SharedPreferences mPrefs = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("recentlyPlayed", json4);
                prefsEditor.commit();
            } catch (Exception e) {

            }
            homeConfigInfo.setSaveQueueRunning(false);
        }
        return null;
    }
}