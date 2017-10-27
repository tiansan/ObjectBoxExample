package cn.amtxts.objectboxexample;

import android.app.Application;

import io.objectbox.BoxStore;

/**
 * Created by geust on 2017/10/27.
 */

public class App extends Application {

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        boxStore = MyObjectBox.builder().androidContext(App.this).build();
//        if (BuildConfig.DEBUG) {
//            new AndroidObjectBrowser(boxStore).start(this);
//        }
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

}
