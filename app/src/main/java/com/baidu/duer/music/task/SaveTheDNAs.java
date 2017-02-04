package com.baidu.duer.music.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by niufei01 on 17/2/3.
 */

public class SaveTheDNAs extends AsyncTask<Void, Void, Void> {
    Activity activity;
    HomeConfigInfo homeConfigInfo;
    public SaveTheDNAs(Activity activity,HomeConfigInfo homeConfigInfo) {

        this.activity =activity;
        this.homeConfigInfo =homeConfigInfo;
    }
    @Override
    protected Void doInBackground(Void... params) {
        if (!homeConfigInfo.isSaveDNAsRunning()) {
            homeConfigInfo.setSaveDNAsRunning(true);
            try {
                String json = new Gson().toJson(homeConfigInfo.getSavedDNAs());
                SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("savedDNAs", json);
                prefsEditor.commit();
            } catch (Exception e) {

            }
            homeConfigInfo.setSaveDNAsRunning(false);
        }
        return null;
    }
}