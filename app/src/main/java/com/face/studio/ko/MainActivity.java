package com.face.studio.ko;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final int PICK_IMAGE = 4201;
    private WebView webView;
    private ValueCallback<Uri[]> pickerCallback;
    private File exportFile;
    private String exportName;
    private boolean exportShare;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(21, 19, 24));
        getWindow().setNavigationBarColor(Color.rgb(21, 19, 24));
        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(21, 19, 24));
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        webView.addJavascriptInterface(new NativeBridge(), "FaceStudioNative");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (pickerCallback != null) pickerCallback.onReceiveValue(null);
                pickerCallback = callback;
                try {
                    startActivityForResult(params.createIntent(), PICK_IMAGE);
                    return true;
                } catch (Exception e) {
                    pickerCallback = null;
                    callback.onReceiveValue(null);
                    Toast.makeText(MainActivity.this, "사진 선택기를 열지 못했어요.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String path = uri.getPath() == null ? "" : uri.getPath();
                return !"file".equals(uri.getScheme()) || !path.startsWith("/android_asset/face-studio/");
            }
        });
        if (Build.VERSION.SDK_INT >= 35) {
            webView.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }
        setContentView(webView);
        webView.loadUrl("file:///android_asset/face-studio/index.html");
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && pickerCallback != null) {
            Uri[] result = resultCode == RESULT_OK ? WebChromeClient.FileChooserParams.parseResult(resultCode, data) : null;
            pickerCallback.onReceiveValue(result);
            pickerCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (pickerCallback != null) pickerCallback.onReceiveValue(null);
        if (exportFile != null) exportFile.delete();
        if (webView != null) {
            webView.removeJavascriptInterface("FaceStudioNative");
            webView.destroy();
        }
        super.onDestroy();
    }

    private final class NativeBridge {
        @JavascriptInterface public synchronized boolean beginPngExport(String name, boolean share) {
            try {
                if (exportFile != null) exportFile.delete();
                exportName = name == null ? "face-studio.png" : name.replaceAll("[^A-Za-z0-9._-]", "_");
                if (!exportName.toLowerCase().endsWith(".png")) exportName += ".png";
                exportShare = share;
                exportFile = new File(getCacheDir(), "export-" + UUID.randomUUID() + ".png");
                return exportFile.createNewFile();
            } catch (Exception e) { exportFile = null; return false; }
        }

        @JavascriptInterface public synchronized boolean appendPngExport(String chunk) {
            try {
                if (exportFile == null || chunk == null) return false;
                byte[] bytes = android.util.Base64.decode(chunk, android.util.Base64.DEFAULT);
                try (FileOutputStream out = new FileOutputStream(exportFile, true)) { out.write(bytes); }
                return true;
            } catch (Exception e) { return false; }
        }

        @JavascriptInterface public synchronized boolean finishPngExport() {
            File source = exportFile;
            Uri imageUri = null;
            boolean share = exportShare;
            exportFile = null;
            if (source == null) return false;
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, exportName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Face Studio");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
                Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                imageUri = getContentResolver().insert(collection, values);
                if (imageUri == null) return false;
                try (InputStream in = new FileInputStream(source);
                     OutputStream out = getContentResolver().openOutputStream(imageUri, "w")) {
                    if (out == null) throw new IllegalStateException("No output stream");
                    byte[] buffer = new byte[65536];
                    int n;
                    while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
                    out.flush();
                }
                ContentValues ready = new ContentValues();
                ready.put(MediaStore.Images.Media.IS_PENDING, 0);
                getContentResolver().update(imageUri, ready, null, null);
                source.delete();
                if (share) {
                    Uri uri = imageUri;
                    runOnUiThread(() -> {
                        Intent send = new Intent(Intent.ACTION_SEND);
                        send.setType("image/png");
                        send.putExtra(Intent.EXTRA_STREAM, uri);
                        send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(send, "합성 이미지 공유"));
                    });
                }
                return true;
            } catch (Exception e) {
                if (imageUri != null) getContentResolver().delete(imageUri, null, null);
                source.delete();
                return false;
            }
        }

        @JavascriptInterface public synchronized void cancelPngExport() {
            if (exportFile != null) exportFile.delete();
            exportFile = null;
            exportName = null;
        }
    }
}
