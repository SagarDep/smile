package me.zsj.smile.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;

import butterknife.Bind;
import me.zsj.smile.R;

/**
 * Created by zsj on 2015/10/4 0004.
 */
public class VideoActivity extends ToolbarActivity {

    @Bind(R.id.progressbar) NumberProgressBar mProgressBar;
    @Bind(R.id.vedio_web) WebView mWebView;

    public static final String VIDEO_URL = "VIDEO_URL";
    public static final String VIDEO_DESC = "VIDEO_DESC";
    private String mVedioUrl;
    private String mVedioTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gank_video;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        parserIntent();
        mToolbar.setTitle(mVedioTitle);
        setNavigationListener();
        setupWebView();

    }

    private void setupWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setSupportZoom(true);
        mWebView.setWebChromeClient(new VedioClient());
        mWebView.setWebViewClient(new VedioViewClient());
        mWebView.loadUrl(mVedioUrl);
    }

    private void setNavigationListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoActivity.this.onBackPressed();
            }
        });
    }

    private void parserIntent() {
        mVedioUrl = getIntent().getExtras().getString(VIDEO_URL);
        mVedioTitle = getIntent().getExtras().getString(VIDEO_DESC);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    }else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private class VedioClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            try {
                mProgressBar.setProgress(newProgress);
            }catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            }else {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    private class VedioViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null) {
                view.loadUrl(url);
            }
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vedio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshVedio:
                if (mWebView != null) mWebView.reload();
                break;
            case R.id.openWithBrowse:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(mVedioUrl);
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "打开浏览器失败!", Toast.LENGTH_LONG).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null)
            mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWebView != null)
            mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null)
            mWebView.destroy();
    }
}
