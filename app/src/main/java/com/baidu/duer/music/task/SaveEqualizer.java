package com.baidu.duer.music.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by niufei01 on 17/2/3.
 */

public class SaveEqualizer extends AsyncTask<Void, Void, Void> {
    Activity activity;
    HomeConfigInfo homeConfigInfo;
    public SaveEqualizer(Activity activity,HomeConfigInfo homeConfigInfo) {

        this.activity =activity;
        this.homeConfigInfo =homeConfigInfo;
    }
    @Override
    protected Void doInBackground(Void... params) {
        if (!homeConfigInfo.isSaveEqualizerRunning()) {
            homeConfigInfo.setSaveEqualizerRunning(true);
            try {
                String json2 = new Gson().toJson(homeConfigInfo.getEqualizerModel());
                SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("equalizer", json2);
                prefsEditor.commit();
            } catch (Exception e) {

            }
            homeConfigInfo.setSaveEqualizerRunning(false);
        }
        return null;
    }
}