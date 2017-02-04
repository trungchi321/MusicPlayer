package com.baidu.duer.music.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.baidu.duer.music.utils.AndroidUtils;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by niufei01 on 17/2/3.
 */

public class SaveVersionCode extends AsyncTask<Void, Void, Void> {
    Activity activity;
    public SaveVersionCode(Activity activity) {

        this.activity =activity;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            String json7 = new Gson().toJson(AndroidUtils.getVersionCode(activity));
            SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putString("versionCode", json7);
            prefsEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}