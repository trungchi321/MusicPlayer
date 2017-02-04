package com.baidu.duer.music.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by niufei01 on 17/2/3.
 */

public class SaveQueue extends AsyncTask<Void, Void, Void> {
    Activity activity;
    HomeConfigInfo homeConfigInfo;
    public SaveQueue(Activity activity,HomeConfigInfo homeConfigInfo) {

        this.activity =activity;
        this.homeConfigInfo = homeConfigInfo;
    }
    @Override
    protected Void doInBackground(Void... params) {
        if (!homeConfigInfo.isSaveQueueRunning()) {
            homeConfigInfo.setSaveQueueRunning(true);
            try {
                SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                String json3 = new Gson().toJson(homeConfigInfo.getQueue());
                prefsEditor.putString("queue", json3);
                String json6 = new Gson().toJson(homeConfigInfo.getQueueCurrentIndex());
                prefsEditor.putString("queueCurrentIndex", json6);
                prefsEditor.commit();
            } catch (Exception e) {

            }
            homeConfigInfo.setSaveQueueRunning(false);
        }
        return null;
    }
}