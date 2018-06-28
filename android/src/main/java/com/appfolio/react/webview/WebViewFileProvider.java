package com.appfolio.react.webview;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;

public class WebViewFileProvider extends FileProvider {
    private static ProviderInfo providerInfo;

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        providerInfo = info;
    }

    private static String getAuthority() {
        return providerInfo.authority;
    }

    public static Uri getUriForFile(@NonNull Context context, @NonNull File file) {
        return getUriForFile(context, getAuthority(), file);
    }
}
