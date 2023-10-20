package com.whitelightning.jetskiandmore;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.whitelightning.jetskiandmore.R;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private FloatingActionButton fabEmail;
    private FloatingActionButton fabCall;
    private boolean areButtonsVisible = false;
    private UserDataManager userDataManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupWebView();
        setButtonListeners();
    }

    private void initializeViews() {
        webView = findViewById(R.id.webView);
        userDataManager = new UserDataManager(this);
        fabEmail = findViewById(R.id.fabEmail);
        fabCall = findViewById(R.id.fabCall);
        fabEmail.setVisibility(View.GONE);
        fabCall.setVisibility(View.GONE);
    }

    private void setupWebView() {
        configureWebViewSettings();
        setWebViewClient();
        loadWebViewPage();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebViewSettings() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);

        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
    }

    private void setWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Page finished loading
            }
        });
    }

    private void loadWebViewPage() {
        if (isNetworkAvailable()) {
            webView.loadUrl("https://jetskiandmore.com");
        } else {
            displayNetworkError();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    private void displayNetworkError() {
        Toast.makeText(this, "Network not available. Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonListeners() {
        FloatingActionButton fabMain = findViewById(R.id.fab);
        ConstraintLayout rootLayout = findViewById(R.id.rootLayout);

        rootLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isTouchInsideView(event, fabEmail) && isTouchInsideView(event, fabCall)) {
                    // Hide the FABs
                    hideFloatingButtons();
                } else {
                    // Handle the click event for the rootLayout
                    rootLayout.performClick();
                }
            }
            return true;
        });

        fabMain.setOnClickListener(v -> toggleFloatingButtons());
        fabEmail.setOnClickListener(v -> sendEmail());
        fabCall.setOnClickListener(v -> makePhoneCall());
    }

    private boolean isTouchInsideView(MotionEvent event, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        float x = event.getRawX();
        float y = event.getRawY();

        return (x >= viewX && x <= (viewX + view.getWidth()) && y >= viewY && y <= (viewY + view.getHeight()));
    }

    private void hideFloatingButtons() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator emailFabAnimator = ObjectAnimator.ofFloat(fabEmail, "alpha", 1f, 0f);
        emailFabAnimator.setDuration(300);
        ObjectAnimator callFabAnimator = ObjectAnimator.ofFloat(fabCall, "alpha", 1f, 0f);
        callFabAnimator.setDuration(300);

        animatorSet.playTogether(emailFabAnimator, callFabAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabEmail.setVisibility(View.GONE);
                fabCall.setVisibility(View.GONE);
            }
        });

        animatorSet.start();
        areButtonsVisible = false;
    }

    private void toggleFloatingButtons() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator emailFabAnimator;
        if (!areButtonsVisible) {
            emailFabAnimator = ObjectAnimator.ofFloat(fabEmail, "alpha", 0f, 1f);
            emailFabAnimator.setDuration(300);
            ObjectAnimator callFabAnimator = ObjectAnimator.ofFloat(fabCall, "alpha", 0f, 1f);
            callFabAnimator.setDuration(300);

            animatorSet.playSequentially(emailFabAnimator, callFabAnimator);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    fabEmail.setVisibility(View.VISIBLE);
                    fabCall.setVisibility(View.VISIBLE);
                }
            });

        } else {
            emailFabAnimator = ObjectAnimator.ofFloat(fabEmail, "alpha", 1f, 0f);
            emailFabAnimator.setDuration(200);
            ObjectAnimator callFabAnimator = ObjectAnimator.ofFloat(fabCall, "alpha", 1f, 0f);
            callFabAnimator.setDuration(300);

            animatorSet.playSequentially(emailFabAnimator, callFabAnimator);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fabEmail.setVisibility(View.GONE);
                    fabCall.setVisibility(View.GONE);
                }
            });
        }
        animatorSet.start();
        areButtonsVisible = !areButtonsVisible;
    }

    public class JavaScriptInterface {
        private final Context context;

        public JavaScriptInterface(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void sendEmail() {
        String[] emailRecipients = {"jetskiadventures1@gmail.com"};
        String emailSubject = "Subject: Jet Ski Enquiry";
        String emailBody = "Hello,\n\nThis is " + userDataManager.getUserName() + ". I would like to inquire about a jet ski booking.";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, emailRecipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);

        Intent chooser = Intent.createChooser(intent, "Send Email");

        if (chooser.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            showToast("No email app found to handle the request.");
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void makePhoneCall() {
        String phoneNumber = "tel:0795558249";

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(phoneNumber));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            showToast("No app available to make the call.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
