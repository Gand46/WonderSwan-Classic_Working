package com.gand46.wonderswanmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;

public class MainActivity extends Activity {
    private static final int REQUEST_FILE_CHOOSER = 1001;
    private static final int REQUEST_SAVE_FILE = 1002;

    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private byte[] pendingSaveData;
    private String pendingSaveMime = "application/octet-stream";
    private String pendingSaveName = "WonderSwan.bin";

    @Override
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(6, 11, 17));
        getWindow().setNavigationBarColor(Color.rgb(6, 11, 17));
        enableImmersive();

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(6, 11, 17));
        webView.setKeepScreenOn(false);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null || url.startsWith("file:///android_asset/")) {
                    return false;
                }
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception ignored) {
                    }
                    return true;
                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView webView,
                    ValueCallback<Uri[]> filePathCallbackNew,
                    FileChooserParams fileChooserParams) {
                if (filePathCallback != null) {
                    filePathCallback.onReceiveValue(null);
                }
                filePathCallback = filePathCallbackNew;

                Intent intent;
                try {
                    intent = fileChooserParams.createIntent();
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, REQUEST_FILE_CHOOSER);
                    return true;
                } catch (ActivityNotFoundException e) {
                    filePathCallback = null;
                    Toast.makeText(MainActivity.this, "No hay selector de archivos disponible", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });

        setContentView(webView);
        if (savedInstanceState == null) {
            webView.loadUrl("file:///android_asset/index.html");
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) {
            webView.saveState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
        enableImmersive();
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (filePathCallback != null) {
            filePathCallback.onReceiveValue(null);
            filePathCallback = null;
        }
        if (webView != null) {
            webView.removeJavascriptInterface("AndroidBridge");
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersive();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILE_CHOOSER) {
            if (filePathCallback != null) {
                Uri[] results = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                filePathCallback.onReceiveValue(results);
                filePathCallback = null;
            }
            return;
        }

        if (requestCode == REQUEST_SAVE_FILE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null && pendingSaveData != null) {
                Uri uri = data.getData();
                try (OutputStream out = getContentResolver().openOutputStream(uri, "w")) {
                    if (out == null) {
                        throw new IllegalStateException("No se pudo abrir el destino");
                    }
                    out.write(pendingSaveData);
                    out.flush();
                    Toast.makeText(this, "Archivo guardado", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            pendingSaveData = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void enableImmersive() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private String cleanFileName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "WonderSwan.bin";
        }
        return value.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    public final class AndroidBridge {
        @JavascriptInterface
        public String getVersion() {
            return BuildConfig.VERSION_NAME;
        }

        @JavascriptInterface
        public void setKeepScreenOn(final boolean enabled) {
            runOnUiThread(() -> {
                if (webView != null) {
                    webView.setKeepScreenOn(enabled);
                }
                if (enabled) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });
        }

        @JavascriptInterface
        public void setOrientation(final String mode) {
            runOnUiThread(() -> {
                if ("portrait".equalsIgnoreCase(mode)) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                } else if ("landscape".equalsIgnoreCase(mode)) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            });
        }

        @JavascriptInterface
        public void toggleImmersive() {
            runOnUiThread(MainActivity.this::enableImmersive);
        }

        @JavascriptInterface
        public void saveBase64(final String fileName, final String mimeType, final String base64Data) {
            if (base64Data == null) {
                return;
            }
            final byte[] decoded;
            try {
                decoded = Base64.decode(base64Data, Base64.DEFAULT);
            } catch (IllegalArgumentException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Datos de archivo inválidos", Toast.LENGTH_SHORT).show());
                return;
            }

            runOnUiThread(() -> {
                pendingSaveData = decoded;
                pendingSaveName = cleanFileName(fileName);
                pendingSaveMime = (mimeType == null || mimeType.isEmpty()) ? "application/octet-stream" : mimeType;

                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(pendingSaveMime);
                intent.putExtra(Intent.EXTRA_TITLE, pendingSaveName);
                try {
                    startActivityForResult(intent, REQUEST_SAVE_FILE);
                } catch (ActivityNotFoundException e) {
                    pendingSaveData = null;
                    Toast.makeText(MainActivity.this, "No hay gestor de archivos disponible", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
