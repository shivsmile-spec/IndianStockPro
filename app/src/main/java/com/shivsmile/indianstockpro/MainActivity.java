package com.shivsmile.indianstockpro;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView web = findViewById(R.id.webView);
        WebSettings s = web.getSettings();

        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                try {
                    InputStream input = getAssets().open("stocks.json");
                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    byte[] buffer = new byte[8192];
                    int length;

                    while ((length = input.read(buffer)) != -1) {
                        output.write(buffer, 0, length);
                    }

                    input.close();

                    String json = output.toString(StandardCharsets.UTF_8.name());

                    String encoded = Base64.encodeToString(
                            json.getBytes(StandardCharsets.UTF_8),
                            Base64.NO_WRAP
                    );

                    String js =
                            "window.setStockData(JSON.parse(" +
                            "decodeURIComponent(escape(atob('" +
                            encoded +
                            "')))" +
                            "));";

                    view.evaluateJavascript(js, null);

                } catch (Exception e) {
                    view.evaluateJavascript(
                            "document.getElementById('status').textContent=" +
                            "'Could not load bundled stock data.';",
                            null
                    );
                }
            }
        });

        web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        WebView web = findViewById(R.id.webView);

        if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
