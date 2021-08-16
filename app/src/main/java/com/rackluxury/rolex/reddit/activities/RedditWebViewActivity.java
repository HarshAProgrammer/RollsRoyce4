package com.rackluxury.rolex.reddit.activities;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.r0adkll.slidr.Slidr;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rolex.reddit.Infinity;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rolex.reddit.customviews.LollipopBugFixedWebView;
import com.rackluxury.rolex.reddit.utils.SharedPreferencesUtils;
import com.rackluxury.rolex.reddit.utils.Utils;

public class RedditWebViewActivity extends BaseActivity {

    @BindView(R.id.coordinator_layout_web_view_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_web_view_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_web_view_activity)
    Toolbar toolbar;
    @BindView(R.id.web_view_web_view_activity)
    LollipopBugFixedWebView webView;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        setImmersiveModeNotApplicable();

        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_web_view);
        } catch (InflateException ie) {
            Log.e("LoginActivity", "Failed to inflate LoginActivity: " + ie.getMessage());
            Toast.makeText(RedditWebViewActivity.this, R.string.no_system_webview_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ButterKnife.bind(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            Slidr.attach(this);
        }

        setSupportActionBar(toolbar);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        url = getIntent().getDataString();
        toolbar.setTitle(url);
        webView.loadUrl(url);

        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                RedditWebViewActivity.this.url = url;
                toolbar.setTitle(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                toolbar.setTitle(view.getTitle());
            }
        };
        webView.setWebViewClient(client);
    }

    @Override
    protected SharedPreferences getDefaultSharedPreferences() {
        return mSharedPreferences;
    }

    @Override
    protected CustomThemeWrapper getCustomThemeWrapper() {
        return mCustomThemeWrapper;
    }

    @Override
    protected void applyCustomTheme() {
        coordinatorLayout.setBackgroundColor(mCustomThemeWrapper.getBackgroundColor());
        applyAppBarLayoutAndToolbarTheme(appBarLayout, toolbar);
        Drawable closeIcon = Utils.getTintedDrawable(this, R.drawable.ic_close_black_24dp, mCustomThemeWrapper.getToolbarPrimaryTextAndIconColor());
        toolbar.setNavigationIcon(closeIcon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_view_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_share_link_web_view_activity) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.no_activity_found_for_share, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.action_copy_link_web_view_activity) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("simple text", url);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, R.string.copy_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.copy_link_failed, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}