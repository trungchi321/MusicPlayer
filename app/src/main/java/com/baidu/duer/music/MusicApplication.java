package com.baidu.duer.music;

import android.app.Application;
import android.content.Context;

import com.baidu.duer.music.imageLoader.ImageLoader;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Harjot on 28-Aug-16.
 */
public class MusicApplication extends Application {

    private RefWatcher refWatcher;
    private static ImageLoader imageLoader;
    public static RefWatcher getRefWatcher(Context context) {
        MusicApplication application = (MusicApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    public static ImageLoader getImageLoader(){
        return imageLoader;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        refWatcher = LeakCanary.install(this);
        imageLoader =new ImageLoader(this);

    }

}
