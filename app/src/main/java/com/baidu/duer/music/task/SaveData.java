package com.baidu.duer.music.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by niufei01 on 17/2/3.
 */

public class SaveData extends AsyncTask<Void, Void, Void> {
    Activity activity;
    HomeConfigInfo homeConfigInfo;
    public SaveData(Activity activity,HomeConfigInfo homeConfigInfo) {

        this.activity =activity;
        this.homeConfigInfo =homeConfigInfo;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            String json6 = new Gson().toJson(homeConfigInfo.getQueueCurrentIndex());
            SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putString("queueCurrentIndex", json6);
            prefsEditor.commit();
        } catch (Exception e) {

        }
        return null;
    }
}