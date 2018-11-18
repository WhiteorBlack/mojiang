package cn.idcby.commonlibrary.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.idcby.commonlibrary.R;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;


/**
 * Created by mrrlb on 2016/11/24.
 * Web界面
 */
public class WebActivity extends BaseActivity {

    private String title;
    private String url;

    private TextView mTvTitle;
    private android.webkit.WebView mWebView;
    private ProgressBar mProgressBar;
    private View mRlError;
    private TextView mTvAgain;


    @Override
    public int getLayoutID() {
        return R.layout.activity_web;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mWebView = (android.webkit.WebView) findViewById(R.id.webview);
        android.webkit.WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);//支持javaScript
        settings.setDefaultTextEncodingName("utf-8");//设置网页默认编码
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(true);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mRlError = (RelativeLayout) findViewById(R.id.rl_error);
        mTvAgain = (TextView) findViewById(R.id.tv_again);
        mWebView.addJavascriptInterface(new WebContact(), "finish");
    }

    @Override
    public void initData() {
        getIntentData();
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        url = bundle.getString("url");
        initTitle();
        setWebViewUrl();
    }

    private void setWebViewUrl() {

        loadWebViewUrl();

    }

    private void loadWebViewUrl() {
        if (MyUtils.isNetworkConnected(mContext)) {

            mWebView.loadUrl(url);
            mProgressBar.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.VISIBLE);
            mRlError.setVisibility(View.GONE);

        } else {
            mProgressBar.setVisibility(View.GONE);
            mWebView.setVisibility(View.GONE);
            mRlError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initTitle() {
        mTvTitle.setText(title);
    }

    @Override
    public void initListener() {
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

        mTvAgain.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {

        int id = view.getId();
        if (id == R.id.tv_again) {
            loadWebViewUrl();

        }

    }


    private class MyWebChromeClient extends WebChromeClient {


        @Override
        public void onProgressChanged(android.webkit.WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            if (newProgress == 100)
                MyUtils.setViewDisappearWithAnima(mProgressBar);

            super.onProgressChanged(view, newProgress);
            super.onProgressChanged(view, newProgress);

        }

        @Override
        public boolean onJsAlert(android.webkit.WebView view, String url, String message, JsResult result) {

            return true;
        }



    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(android.webkit.WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            LogUtils.showLog("加载网页失败");
            ToastUtils.showNetErrorToast(mContext);
            mWebView.setVisibility(View.GONE);
            mRlError.setVisibility(View.VISIBLE);
        }

    }


    private class WebContact {
        public void finish() {
            WebActivity.this.finish();
        }
    }


}
