package com.baidu.duer.music.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by niufei01 on 17/2/3.
 */

public class SaveSettings extends AsyncTask<Void, Void, Void> {
    Activity activity;
    HomeConfigInfo homeConfigInfo;
    public SaveSettings(Activity activity,HomeConfigInfo homeConfigInfo) {

        this.activity =activity;
        this.homeConfigInfo =homeConfigInfo;
    }
    @Override
    protected Void doInBackground(Void... params) {
        if (!homeConfigInfo.isSaveSettingsRunning()) {
            homeConfigInfo.setSaveSettingsRunning(true);
            try {
                String json8 = new Gson().toJson(homeConfigInfo.getSettings());
                SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("settings", json8);
                prefsEditor.commit();
            } catch (Exception e) {

            }
            homeConfigInfo.setSaveSettingsRunning(false);
        }
        return null;
    }
}