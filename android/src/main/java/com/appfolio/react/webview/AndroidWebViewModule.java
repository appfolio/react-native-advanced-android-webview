package com.appfolio.react.webview;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class AndroidWebViewModule extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "AEAdvancedAndroidWebViewModule";

    public AndroidWebViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public ReactContext getReactContext() {
        return getReactApplicationContext();
    }
}
