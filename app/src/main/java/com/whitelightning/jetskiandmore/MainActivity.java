package com.whitelightning.jetskiandmore;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends Activity {
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);


        webView = findViewById(R.id.webView);

        // Enable JavaScript in the WebView (optional)
        webView.getSettings().setJavaScriptEnabled(true);

        // Set a WebViewClient to handle page navigation and avoid opening in external browsers
        webView.setWebViewClient(new WebViewClient());

        // Load a URL in the WebView
        webView.loadUrl("https://www.jetskiandmore.com");

        // Handle back button navigation in the WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Inject custom CSS to remove padding
                String customCss = "body { margin: 0; padding: 0; }";
                view.loadUrl("javascript:(function() {" +
                        "var style = document.createElement('style');" +
                        "style.innerHTML = '" + customCss + "';" +
                        "document.head.appendChild(style);" +
                        "})()");
            }
        });



        // Handle pull-down to refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload(); // Refresh the WebView when pulled down
            }
        });

        // Handle the end of the refresh operation
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false); // Hide the refresh indicator
            }
        });
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
