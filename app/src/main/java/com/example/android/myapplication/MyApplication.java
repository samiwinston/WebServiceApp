package com.example.android.myapplication;

import android.app.Application;
import android.content.res.Configuration;

import org.acra.*;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by abedch on 9/17/2015.
 */



public class MyApplication extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }
}
