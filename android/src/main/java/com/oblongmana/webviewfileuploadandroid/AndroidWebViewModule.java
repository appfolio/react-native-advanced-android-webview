package com.oblongmana.webviewfileuploadandroid;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ActivityEventListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.common.annotations.VisibleForTesting;


public class AndroidWebViewModule extends ReactContextBaseJavaModule {
    private ReactApplicationContext reactAppContext;
    private ActivityEventListener activityEventListener;
    private AndroidWebViewPackage aPackage;

    @VisibleForTesting
    public static final String REACT_CLASS = "AndroidWebViewModule";

    public AndroidWebViewModule(ReactApplicationContext context) {
        super(context);
        reactAppContext = context;
    }

    public AndroidWebViewPackage getPackage() {
        return this.aPackage;
    }

    public void setPackage(AndroidWebViewPackage aPackage) {
        this.aPackage = aPackage;
    }

    @Override
    public String getName(){
        return REACT_CLASS;
    }

    @SuppressWarnings("unused")
    Activity getActivity() {
        return getCurrentActivity();
    }

    void setActivityEventListener(ActivityEventListener activityEventListener) {
        if (reactAppContext == null) {
            Log.wtf("AndroidWebView", "React application context was deallocated");
            return;
        }

        if (this.activityEventListener != null) {
            reactAppContext.removeActivityEventListener(this.activityEventListener);
        }

        if (activityEventListener != null) {
            reactAppContext.addActivityEventListener(activityEventListener);
            this.activityEventListener = activityEventListener;
        }
    }
}
