package com.whitelightning.jetskiandmore;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        // Enable JavaScript in the WebView (optional)
        webView.getSettings().setJavaScriptEnabled(true);

        // Set a WebViewClient to handle page navigation and avoid opening in external browsers
        webView.setWebViewClient(new WebViewClient());

        // Load a URL in the WebView
        webView.loadUrl("https://www.jetskiandmore.com");
    }

    // Handle back button navigation in the WebView
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
