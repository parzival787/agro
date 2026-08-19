package com.example.agro; // Declares the package this class belongs to.

import android.graphics.Bitmap; // Imports the Bitmap class to work with images.
import android.os.Bundle; // Imports the Bundle class to pass data between activities.
import android.view.View; // Imports the View class, the basic building block for user interface components.
import android.webkit.WebChromeClient; // Imports WebChromeClient to handle browser UI events.
import android.webkit.WebResourceError; // Imports WebResourceError to represent an error that occurred while loading a web page.
import android.webkit.WebResourceRequest; // Imports WebResourceRequest to represent a request for a web resource.
import android.webkit.WebSettings; // Imports WebSettings to configure a WebView.
import android.webkit.WebView; // Imports the WebView class to display web pages.
import android.webkit.WebViewClient; // Imports WebViewClient to handle events related to a WebView.
import android.widget.ProgressBar; // Imports the ProgressBar class to show progress of an operation.
import android.widget.Toast; // Imports the Toast class to show short, transient messages to the user.

import androidx.appcompat.app.AppCompatActivity; // Imports AppCompatActivity for a modern Android activity with an app bar.

public class WebViewActivity extends AppCompatActivity { // Declares the WebViewActivity class.

    private WebView webView; // Declares a WebView to display web content.
    private ProgressBar progressBar; // Declares a ProgressBar to indicate loading progress.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Overrides the onCreate method, which is called when the activity is first created.
        super.onCreate(savedInstanceState); // Calls the superclass's onCreate method.
        setContentView(R.layout.activity_web_view); // Sets the user interface layout for this activity.

        webView = findViewById(R.id.webView); // Initializes the WebView.
        progressBar = findViewById(R.id.progressBar); // Initializes the ProgressBar.

        // 1. Get the URL passed from the previous screen
        String url = getIntent().getStringExtra("url"); // Gets the URL passed from the previous activity.

        // 2. Configure WebView
        WebSettings webSettings = webView.getSettings(); // Gets the WebSettings for the WebView.
        webSettings.setJavaScriptEnabled(true); // Enables JavaScript.
        webSettings.setDomStorageEnabled(true); // Enables DOM storage.
        webSettings.setLoadWithOverviewMode(true); // Loads the WebView with an overview mode.
        webSettings.setUseWideViewPort(true); // Enables the use of a wide viewport.
        webSettings.setBuiltInZoomControls(true); // Enables built-in zoom controls.
        webSettings.setDisplayZoomControls(false); // Hides the on-screen zoom controls.

        // 3. Force links to open INSIDE this WebView, not Chrome
        webView.setWebViewClient(new WebViewClient() { // Sets a new WebViewClient to handle page loading events.
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) { // Called when a page starts loading.
                super.onPageStarted(view, url, favicon); // Calls the superclass's method.
                progressBar.setVisibility(View.VISIBLE); // Shows the progress bar.
            }

            @Override
            public void onPageFinished(WebView view, String url) { // Called when a page finishes loading.
                super.onPageFinished(view, url); // Calls the superclass's method.
                progressBar.setVisibility(View.GONE); // Hides the progress bar.
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) { // Called when an error occurs while loading a page.
                super.onReceivedError(view, request, error); // Calls the superclass's method.
                Toast.makeText(WebViewActivity.this, "Failed to load page. Please check your internet connection.", Toast.LENGTH_LONG).show(); // Shows an error message.
            }
        });

        webView.setWebChromeClient(new WebChromeClient() { // Sets a new WebChromeClient to handle UI-related events.
            @Override
            public void onProgressChanged(WebView view, int newProgress) { // Called when the loading progress changes.
                super.onProgressChanged(view, newProgress); // Calls the superclass's method.
                progressBar.setProgress(newProgress); // Sets the progress of the ProgressBar.
                if (newProgress == 100) { // Checks if the page has finished loading.
                    progressBar.setVisibility(View.GONE); // Hides the progress bar.
                }
            }
        });

        // 4. Load the URL
        if (url != null) { // Checks if a URL was provided.
            webView.loadUrl(url); // Loads the URL in the WebView.
        } else { // If no URL was provided.
            Toast.makeText(this, "URL not provided.", Toast.LENGTH_SHORT).show(); // Shows a message indicating that no URL was provided.
        }
    }

    // 5. Handle the Android "Back" button
    @Override
    public void onBackPressed() { // Overrides the method to handle back button presses.
        if (webView.canGoBack()) { // Checks if the WebView can go back in its history.
            webView.goBack(); // Goes back to the previous page.
        } else { // If the WebView cannot go back.
            super.onBackPressed(); // Closes the activity.
        }
    }
}
