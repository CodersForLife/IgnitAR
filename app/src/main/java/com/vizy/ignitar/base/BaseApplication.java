package com.vizy.ignitar.base;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;

public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        AccountKit.initialize(getApplicationContext());
        Firebase.setAndroidContext(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
