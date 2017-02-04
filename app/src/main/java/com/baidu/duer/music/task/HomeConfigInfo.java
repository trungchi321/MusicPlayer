package com.baidu.duer.music.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.duer.music.model.AllMusicFolders;
import com.baidu.duer.music.model.AllPlaylists;
import com.baidu.duer.music.model.AllSavedDNA;
import com.baidu.duer.music.model.EqualizerModel;
import com.baidu.duer.music.model.Favourite;
import com.baidu.duer.music.model.Queue;
import com.baidu.duer.music.model.RecentlyPlayed;
import com.baidu.duer.music.model.SavedDNA;
import com.baidu.duer.music.model.Settings;
import com.baidu.duer.music.utils.AndroidUtils;
import com.google.gson.Gson;

import java.io.Serializable;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by niufei01 on 17/1/26.
 */

public class HomeConfigInfo implements Serializable{

    Settings settings;

    AllPlaylists allPlaylists;
    AllMusicFolders allMusicFolders;
    RecentlyPlayed recentlyPlayed;
    Favourite favouriteTracks;
    AllSavedDNA savedDNAs;
    SavedDNA tempSavedDNA;
    Queue queue;
    Queue originalQueue;
    int queueCurrentIndex = 0;
    EqualizerModel equalizerModel;
    int prevVersionCode = -1;


    boolean isSaveQueueRunning = false;
    boolean isSaveRecentsRunning = false;
    boolean isSaveFavouritesRunning = false;
    boolean isSaveSettingsRunning = false;
    boolean isSaveDNAsRunning = false;
    boolean isSavePLaylistsRunning = false;
    boolean isSaveEqualizerRunning = false;


    public void getSavedData(Activity activity) {
        if(activity==null){return;}
        try {

            SharedPreferences mPrefs = activity.getPreferences(MODE_PRIVATE);
            Gson gson = new Gson();
            Log.d("TIME", "start");
            String json2 = mPrefs.getString("allPlaylists", "");
            allPlaylists = gson.fromJson(json2, AllPlaylists.class);
            Log.d("TIME", "allPlaylists");
            String json3 = mPrefs.getString("queue", "");
            queue = gson.fromJson(json3, Queue.class);
            Log.d("TIME", "queue");
            String json4 = mPrefs.getString("recentlyPlayed", "");
            recentlyPlayed = gson.fromJson(json4, RecentlyPlayed.class);
            Log.d("TIME", "recents");
            String json5 = mPrefs.getString("favouriteTracks", "");
            favouriteTracks = gson.fromJson(json5, Favourite.class);
            Log.d("TIME", "fav");
            String json6 = mPrefs.getString("queueCurrentIndex", "");
            queueCurrentIndex = gson.fromJson(json6, Integer.class);
            Log.d("TIME", "queueCurrentindex");
            String json8 = mPrefs.getString("settings", "");
            settings = gson.fromJson(json8, Settings.class);
            Log.d("TIME", "settings");
            String json9 = mPrefs.getString("equalizer", "");
            equalizerModel = gson.fromJson(json9, EqualizerModel.class);
            Log.d("TIME", "equalizer");
            String json = mPrefs.getString("savedDNAs", "");
            savedDNAs = gson.fromJson(json, AllSavedDNA.class);
            Log.d("TIME", "savedDNAs");
            String json7 = mPrefs.getString("versionCode", "");
            prevVersionCode = new Gson().fromJson(json7, Integer.class);
            Log.d("TIME", "VersionCode : " + prevVersionCode + " : " + AndroidUtils.getVersionCode(activity));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public Settings getSettings() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public AllPlaylists getAllPlaylists() {
        if (allPlaylists == null) {
            allPlaylists = new AllPlaylists();
        }
        return allPlaylists;
    }

    public void setAllPlaylists(AllPlaylists allPlaylists) {
        this.allPlaylists = allPlaylists;
    }

    public AllMusicFolders getAllMusicFolders() {
        if (allMusicFolders == null) {
            allMusicFolders = new AllMusicFolders();
        }
        return allMusicFolders;
    }

    public void setAllMusicFolders(AllMusicFolders allMusicFolders) {
        this.allMusicFolders = allMusicFolders;
    }

    public RecentlyPlayed getRecentlyPlayed() {
        if (recentlyPlayed == null) {
            recentlyPlayed = new RecentlyPlayed();
        }
        return recentlyPlayed;
    }

    public void setRecentlyPlayed(RecentlyPlayed recentlyPlayed) {
        this.recentlyPlayed = recentlyPlayed;
    }

    public Favourite getFavouriteTracks() {
        if (favouriteTracks == null) {
            favouriteTracks = new Favourite();
        }
        return favouriteTracks;
    }

    public void setFavouriteTracks(Favourite favouriteTracks) {
        this.favouriteTracks = favouriteTracks;
    }

    public AllSavedDNA getSavedDNAs() {
        if (savedDNAs == null) {
            savedDNAs = new AllSavedDNA();
        }
        return savedDNAs;
    }

    public void setSavedDNAs(AllSavedDNA savedDNAs) {
        this.savedDNAs = savedDNAs;
    }

    public SavedDNA getTempSavedDNA() {
        return tempSavedDNA;
    }

    public void setTempSavedDNA(SavedDNA tempSavedDNA) {
        this.tempSavedDNA = tempSavedDNA;
    }

    public Queue getQueue() {
        if (queue == null) {
            queue = new Queue();
        }
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Queue getOriginalQueue() {
        return originalQueue;
    }

    public void setOriginalQueue(Queue originalQueue) {
        this.originalQueue = originalQueue;
    }

    public int getQueueCurrentIndex() {
        if(queueCurrentIndex<0){
            queueCurrentIndex=0;
        }
        return queueCurrentIndex;
    }

    public void setQueueCurrentIndex(int queueCurrentIndex) {
        this.queueCurrentIndex = queueCurrentIndex;
    }

    public EqualizerModel getEqualizerModel() {
        return equalizerModel;
    }

    public void setEqualizerModel(EqualizerModel equalizerModel) {
        this.equalizerModel = equalizerModel;
    }

    public int getPrevVersionCode() {
        return prevVersionCode;
    }

    public void setPrevVersionCode(int prevVersionCode) {
        this.prevVersionCode = prevVersionCode;
    }

    public boolean isSaveQueueRunning() {
        return isSaveQueueRunning;
    }

    public void setSaveQueueRunning(boolean saveQueueRunning) {
        isSaveQueueRunning = saveQueueRunning;
    }

    public boolean isSaveRecentsRunning() {
        return isSaveRecentsRunning;
    }

    public void setSaveRecentsRunning(boolean saveRecentsRunning) {
        isSaveRecentsRunning = saveRecentsRunning;
    }

    public boolean isSaveFavouritesRunning() {
        return isSaveFavouritesRunning;
    }

    public void setSaveFavouritesRunning(boolean saveFavouritesRunning) {
        isSaveFavouritesRunning = saveFavouritesRunning;
    }

    public boolean isSaveSettingsRunning() {
        return isSaveSettingsRunning;
    }

    public void setSaveSettingsRunning(boolean saveSettingsRunning) {
        isSaveSettingsRunning = saveSettingsRunning;
    }

    public boolean isSaveDNAsRunning() {
        return isSaveDNAsRunning;
    }

    public void setSaveDNAsRunning(boolean saveDNAsRunning) {
        isSaveDNAsRunning = saveDNAsRunning;
    }

    public boolean isSavePLaylistsRunning() {
        return isSavePLaylistsRunning;
    }

    public void setSavePLaylistsRunning(boolean savePLaylistsRunning) {
        isSavePLaylistsRunning = savePLaylistsRunning;
    }

    public boolean isSaveEqualizerRunning() {
        return isSaveEqualizerRunning;
    }

    public void setSaveEqualizerRunning(boolean saveEqualizerRunning) {
        isSaveEqualizerRunning = saveEqualizerRunning;
    }
}
