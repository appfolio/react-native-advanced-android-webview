package com.appfolio.react.webview;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.webview.ReactWebViewManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

public class AndroidWebViewManager extends ReactWebViewManager {
    protected static final String REACT_CLASS = "AEAdvancedAndroidWebView";

    private static final int PICK_IMAGE = 1;

    private static Uri getTemporaryPhotoFile(Context context) {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
        String timeStamp = dateFormat.format(today);

        File photoFile = new File(context.getExternalCacheDir(), "IMG_" + timeStamp + ".jpg");
        photoFile.getParentFile().mkdirs();

        Uri photoOutputUri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            photoOutputUri = Uri.fromFile(photoFile);
        } else {
            photoOutputUri = WebViewFileProvider.getUriForFile(context, photoFile);
        }

        return photoOutputUri;
    }

    private static Intent createChooserIntent(Context context, boolean allowMultiple) {
        // HACK: hardcoded to images
        List<Intent> intentList = new ArrayList<>();

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTemporaryPhotoFile(context));
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);

        if (allowMultiple) {
            pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        pickIntent.setType("image/*");

        intentList.add(pickIntent);

        Intent chooserIntent = null;
        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), null);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }

        return list;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected WebView createViewInstance(final ThemedReactContext reactContext) {
        WebView view = super.createViewInstance(reactContext);

        // HACK: there seems to be a bug with reloading react-native that causes a view manager to not get
        // constructed, which means the ReactApplicationContext goes stale
        // modules, however, do get constructed again with a fresh ReactApplicationContext
        // also, ThemedReactContext does not receive activity events
        final ReactContext moduleReactContext = reactContext.getNativeModule(AndroidWebViewModule.class).getReactContext();

        view.setWebChromeClient(new WebChromeClient() {
            public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> fileUriCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                Log.d("AndroidWebView", "onShowFileChooser: Web page requested file chooser");

                final BaseActivityEventListener listener = new BaseActivityEventListener() {
                    @Override
                    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                        moduleReactContext.removeActivityEventListener(this);

                        if (resultCode != Activity.RESULT_OK) {
                            fileUriCallback.onReceiveValue(null);
                            return;
                        }

                        switch (requestCode) {
                            case PICK_IMAGE:
                                Uri[] results;
                                if (data == null || data.getDataString() == null && data.getClipData() == null) {
                                    // image from camera
                                    results = new Uri[]{ getTemporaryPhotoFile(activity) };
                                } else {
                                    String dataString = data.getDataString();
                                    ClipData clipData = data.getClipData();

                                    // image from gallery
                                    if (clipData != null) {
                                        results = new Uri[clipData.getItemCount()];

                                        for (int i = 0; i < clipData.getItemCount(); i++) {
                                            ClipData.Item item = clipData.getItemAt(i);
                                            results[i] = item.getUri();
                                        }
                                    } else {
                                        results = new Uri[]{ Uri.parse(dataString) };
                                    }
                                }

                                fileUriCallback.onReceiveValue(results);
                                break;

                            default:
                                Log.w("AndroidWebView", String.format("onActivityResult: unhandled request code %d", requestCode));
                        }
                    }
                };

                moduleReactContext.addActivityEventListener(listener);

                try {
                    final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;

                    Context context = reactContext.getCurrentActivity().getApplicationContext();
                    Intent chooserIntent = createChooserIntent(context, allowMultiple);
                    reactContext.getCurrentActivity().startActivityForResult(chooserIntent, PICK_IMAGE);
                } catch (Exception e) {
                    Log.e("AndroidWebView", e.toString());
                    moduleReactContext.removeActivityEventListener(listener);
                    fileUriCallback.onReceiveValue(null);
                }

                return true;
            }
        });

        return view;
    }
}
