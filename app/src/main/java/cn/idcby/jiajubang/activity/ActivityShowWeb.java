package cn.idcby.jiajubang.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.view.TopBarRightPopup;

/**
 * D:显示网页内容
 */
public class ActivityShowWeb extends BaseActivity{
	private ProgressBar mProgressBar ;
	private WebView mWebView;
	private TextView mTitleTv ;
	
	private String mTitle ;
	private String mHref ;
	private String mThumbUrl ;

    private TopBarRightPopup mRightPopup ;
    private View mRightView ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_show_web_js;
    }

    @Override
    public void initView() {
        initBaseView() ;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        if(view.getId() == R.id.acti_show_web_right_tv){
//            ShareUtils.shareWeb(mActivity ,StringUtils.convertNull(mTitle),mHref ,StringUtils.convertNull(mThumbUrl) ,"");
            showRightPopup() ;
        }
    }


    /**
     * 显示右边popup
     */
    private void showRightPopup(){
        if(null == mRightPopup){
            mRightPopup = new TopBarRightPopup(mContext, mRightView, new TopBarRightPopup.TopRightPopupCallBack() {
                @Override
                public void onItemClick(int position) {
                    if(TopBarRightPopup.POPUP_ITEM_SHARE == position){//分享
                        shareToOtherPlant();
                    }else if(TopBarRightPopup.POPUP_ITEM_REQUEST == position){//投诉
                        SkipUtils.toRequestActivity(mContext,null) ;
                    }
                }
            }) ;
        }

        mRightPopup.displayDialog() ;
    }


    private void shareToOtherPlant(){
        ShareUtils.shareWeb(mActivity ,StringUtils.convertNull(mTitle),mHref ,StringUtils.convertNull(mThumbUrl) ,"");
    }

    /**
     * 初始化view
     */
    private void initBaseView(){
    	mProgressBar = findViewById(R.id.show_web_progress) ;
    	mTitleTv = findViewById(R.id.acti_show_web_title_tv) ;
        mRightView = findViewById(R.id.acti_show_web_right_tv) ;

    	boolean showShare = getIntent().getBooleanExtra(SkipUtils.INTENT_WEB_SHARE,false) ;

    	mHref = getIntent().getStringExtra(SkipUtils.INTENT_WEB_HREF) ;
    	mTitle = getIntent().getStringExtra(SkipUtils.INTENT_WEB_TITLE) ;

        mTitle = StringUtils.convertNull(mTitle) ;

        if(!"".equals(mTitle)){
            mTitleTv.setText(mTitle) ;
        }

        if(showShare){
            mThumbUrl = getIntent().getStringExtra(SkipUtils.INTENT_WEB_SHARE_IMAGE) ;
            mRightView.setVisibility(View.VISIBLE) ;
            mRightView.setOnClickListener(this);
        }

        initWebInfo();
    }

    /**
     * 初始化web信息
     */
	@SuppressLint("SetJavaScriptEnabled")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initWebInfo() {
    	
        mWebView = findViewById(R.id.acti_show_web_web);

        WebSettings setting = mWebView.getSettings() ;
        setting.setJavaScriptEnabled(true);// 设置使用够执行JS脚本
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setBuiltInZoomControls(false);
        setting.setSupportZoom(false);
        setting.setLoadWithOverviewMode(true);
        setting.setUseWideViewPort(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN){
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_") ;
            mWebView.removeJavascriptInterface("accessibility") ;
            mWebView.removeJavascriptInterface("accessibilityTraversal") ;
        }

        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setDownloadListener(new MyDownLoadListener());

        loadWebUrl() ;
    }

    /**
     * 加载
     */
    private void loadWebUrl(){
        mWebView.loadUrl(StringUtils.convertHttpUrl(mHref));
    }

    /**
     * 下载监听
     */
    private class MyDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private class MyWebViewClient extends WebViewClient {
    	
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * 处理弹出对话框出现 来自xxxx的消息  重写  WebChromeClient
     * @author sate
     *
     */
    private final class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog,
                                      boolean userGesture, Message resultMsg) {
            return super.onCreateWindow(view, dialog, userGesture, resultMsg);
        }

        /**
         * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
         */
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(message)
                    .setPositiveButton("确定", null);
            // 不需要绑定按键事件
            // 屏蔽keycode等于84之类的按键
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
                    return true;
                }
            });
            // 禁止响应按back键的事件
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
            return true;
        }

        public boolean onJsBeforeUnload(WebView view, String url,
                                        String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        /**
         * 覆盖默认的window.confirm展示界面，避免title里显示为“：来自file:////”
         */
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(message)
                    .setPositiveButton("确定",new  DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            result.confirm();
                        }
                    })
                    .setNeutralButton("取消", new  DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    result.cancel();
                }
            });

            // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
                    return true;
                }
            });
            // 禁止响应按back键的事件
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {

                if (View.GONE == mProgressBar.getVisibility()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                mProgressBar.setProgress(newProgress);
            }
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message
                , String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            if("".equals(mTitle)){
                mTitleTv.setText(StringUtils.convertNull(title)) ;
            }
        }

        @Override
        public void onRequestFocus(WebView view) {
            super.onRequestFocus(view);
        }
    }

}
