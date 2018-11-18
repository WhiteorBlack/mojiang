package cn.idcby.jiajubang.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.Admire;
import cn.idcby.jiajubang.Bean.Collect;
import cn.idcby.jiajubang.Bean.NewsCommentList;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.CommentAdapter;
import cn.idcby.jiajubang.interf.AddCommentCallBack;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.AddCommentPopup;
import cn.idcby.jiajubang.view.TopBarRightPopup;

public class NewsDetailActivity extends BaseMoreStatusActivity implements AddCommentCallBack{
    private Activity mActivity ;
    private String articleID;

    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;
    private List<NewsCommentList> mCommentList = new ArrayList<>() ;

    private ListView mListView;
    private WebView mWebView;
    private TextView mTvAddComment;
    private TextView mApplyTv;
    private TextView mTvCommentNumber;
    private TextView mTvAdmireNumber;
    private TextView mTvTopAdmireNumber;
    private LinearLayout mLlAdmire;
    private LinearLayout mLlCollect;

    private TextView mCollectionTv ;
    private TextView mSupportTv ;
    private ImageView mImgAdmire;
    private ImageView mImgCollect;

    private CommentAdapter mCommentAdapter;
    private NewsDetail newsDetail;

    private LoadingDialog loadingDialog;

    //评论相关
    private int mCurPosition ;
    private AddCommentPopup mCommentPopup ;

    private TextView mFooterTv ;
    private View mToTopIv ;

    private boolean mIsWvFinish = false ;

    private TopBarRightPopup mRightPopup ;
    private View mRightView ;

    @Override
    public void requestData() {
        requestNewsDetail();
        getNewsCommentList() ;
    }


    @Override
    public int getSuccessViewId() {
        return R.layout.activity_news_detail;
    }

    @Override
    public String setTitle() {
        return "资讯详情";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
        mRightView = imgRight ;

        imgRight.setVisibility(View.VISIBLE);
        imgRight.setImageResource(R.mipmap.ic_top_more_grey);
        setMessageCountShow(false) ;
        imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRightPopup();
            }
        });
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

    /**
     * 分享
     */
    private void shareToOtherPlant() {
        String strTitle = newsDetail.getTitle();
        String strUrl = newsDetail.getH5Url();
        String strImgurl = newsDetail.getImgUrl();
        String strSubscriotion = "";
        ShareUtils.shareWeb(mActivity,strTitle,strUrl,strImgurl,strSubscriotion);
    }

    @Override
    public void init() {
        mActivity = this ;

        initView();
        initListenner();
    }

    private void initView() {
        articleID = getIntent().getStringExtra("articleID");

        mListView = findViewById(R.id.list_view);
        mTvAddComment = findViewById(R.id.tv_add_comment);
        addHeadView();
        mTvCommentNumber = findViewById(R.id.tv_comment_number);
        mTvAdmireNumber = findViewById(R.id.tv_admire_number);
        mLlAdmire = findViewById(R.id.ll_admire);
        mLlCollect = findViewById(R.id.ll_collect);
        mImgAdmire = findViewById(R.id.img_admire);
        mImgCollect = findViewById(R.id.img_collect);
        mCollectionTv = findViewById(R.id.tv_collection_tv) ;
        mSupportTv = findViewById(R.id.tv_admire_tv) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);
    }

    private void addHeadView() {
        View mHeadView = View.inflate(mContext, R.layout.view_head_for_news_detail, null);
        mWebView = mHeadView.findViewById(R.id.webview);
        mApplyTv = mHeadView.findViewById(R.id.header_news_dt_apply_tv);
        mTvTopAdmireNumber = mHeadView.findViewById(R.id.tv_top_admire_number);
        View qqLay = mHeadView.findViewById(R.id.header_news_dt_share_qq_lay);
        View qqZeroLay = mHeadView.findViewById(R.id.header_news_dt_share_qq_zero_lay);
        View wxLay = mHeadView.findViewById(R.id.header_news_dt_share_wx_lay);
        View wxCircleLay = mHeadView.findViewById(R.id.header_news_dt_share_wx_circle_lay);
        mListView.addHeaderView(mHeadView);
        mWebView.setVisibility(View.VISIBLE) ;

        initWebInfo() ;

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mListView.addFooterView(mFooterTv) ;

        qqLay.setOnClickListener(this);
        qqZeroLay.setOnClickListener(this);
        wxLay.setOnClickListener(this);
        wxCircleLay.setOnClickListener(this);

        mCommentAdapter = new CommentAdapter(mCommentList, mActivity, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    mCurPosition = position ;
                    if(LoginHelper.isNotLogin(mContext)){
                        SkipUtils.toLoginActivityForResult(mActivity ,1004);
                    }else{
                        NewsCommentList commentList = mCommentList.get(position) ;
                        if(commentList != null){
                            addCommentToList(2 ,commentList.getArticleCommentID()) ;
                        }
                    }
                }
            }
        });
        mListView.setAdapter(mCommentAdapter);
    }

    /**
     * 初始化web信息
     */
    @SuppressLint("SetJavaScriptEnabled")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initWebInfo() {
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
    }

    private void initListenner() {
        mTvAddComment.setOnClickListener(this);
        mLlAdmire.setOnClickListener(this);
        mLlCollect.setOnClickListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i > 5 && i1 + i2 >= i){
                    getNewsCommentList() ;
                }

                ViewUtil.setViewVisible(mToTopIv ,mIsWvFinish && i > 0);
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            mIsWvFinish = true ;
            showSuccessPage();
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
        }

        @Override
        public void onRequestFocus(WebView view) {
            super.onRequestFocus(view);
        }
    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_add_comment) {
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, 1001);
                return;
            }

            addCommentToList(1 ,"") ;

        } else if (i == R.id.ll_admire) {
            admire();

        } else if (i == R.id.ll_collect) {
            collect();

        }else if (i == R.id.header_news_dt_share_qq_lay) {
            ToastUtils.showToast(mContext ,"当前版本只支持微信分享") ;
//            shareByType(SHARE_MEDIA.QQ) ;
        }else if (i == R.id.header_news_dt_share_qq_zero_lay) {
            ToastUtils.showToast(mContext ,"当前版本只支持微信分享") ;
//            shareByType(SHARE_MEDIA.QZONE) ;
        }else if (i == R.id.header_news_dt_share_wx_lay) {
            shareByType(SHARE_MEDIA.WEIXIN) ;
        }else if (i == R.id.header_news_dt_share_wx_circle_lay) {
            shareByType(SHARE_MEDIA.WEIXIN_CIRCLE) ;
        }else if (R.id.acti_lv_to_top_iv == i) {
            mListView.setSelection(0) ;
            mToTopIv.setVisibility(View.GONE);
        }
    }

    private void addCommentToList(int commentLevel ,String parentId){
        if(null == mCommentPopup){
            mCommentPopup = new AddCommentPopup(mActivity, AddCommentPopup.COMMENT_TYPE_NEWS
                    , mFlContainer, new AddCommentCallBack() {
                @Override
                public void commentCallBack(String commentNum) {
                    mCurPage = 1 ;
                    getNewsCommentList();

                    if(!"".equals(StringUtils.convertNull(commentNum))){
                        mTvCommentNumber.setText(commentNum) ;
                    }
                }
            }) ;
        }

        mCommentPopup.displayDialog(newsDetail.ArticleID, commentLevel, parentId);
    }

    private void shareByType(SHARE_MEDIA platform){
        String strTitle = newsDetail.getTitle();
        String strUrl = newsDetail.getH5Url();
        String strImgurl = newsDetail.getImgUrl();
        String strSubscriotion = "";
        ShareUtils.shareWeb(mActivity ,strUrl ,strTitle  ,strSubscriotion ,strImgurl,platform) ;
    }


    private void collect() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mActivity , 1003);
            return;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("Code", articleID);
        NetUtils.getDataFromServerByPost(mContext, Urls.ARTICLE_COLLECT, false, para,
                new RequestObjectCallBack<Collect>("collectionNews", mContext, Collect.class) {
                    @Override
                    public void onSuccessResult(Collect bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (bean.AddOrDelete == 1) {
                            mImgCollect.setImageResource(R.mipmap.ic_collection_checked);
                        } else {
                            mImgCollect.setImageResource(R.mipmap.ic_collection_nomal);
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 点赞
     */
    private void admire() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mActivity , 1002);
            return;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("Code", articleID);
        NetUtils.getDataFromServerByPost(mContext, Urls.ARTICLE_ADMIRE, false, para,
                new RequestObjectCallBack<Admire>("supportNews", mContext, Admire.class) {
                    @Override
                    public void onSuccessResult(Admire bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (bean.AddOrDelete == 1) {
                            mImgAdmire.setImageResource(R.mipmap.ic_support_checked);

                        } else {
                            mImgAdmire.setImageResource(R.mipmap.ic_support_nomal);
                        }
                        MyUtils.setBageShow(mTvAdmireNumber, bean.LikeNumber);
                        mTvTopAdmireNumber.setText(String.valueOf(bean.LikeNumber));
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }
                });
    }

    private void requestNewsDetail() {
        Map<String, String> para = ParaUtils.getParaNece(mContext);
        para.put("Code", articleID);

        NetUtils.getDataFromServerByPost(mContext, Urls.ARTICLE_DETAIL, false, para,
                new RequestObjectCallBack<NewsDetail>("getNewsDetails", mContext, NewsDetail.class) {
                    @Override
                    public void onSuccessResult(NewsDetail bean) {
                        newsDetail = bean;

                        updateUI();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showErrorPage();
                    }

                    @Override
                    public void onFail(Exception e) {
                        showNetErrorPage();
                    }
                });
    }

    /**
     * 获取评论列表
     */
    private void getNewsCommentList(){
        mIsLoading = true ;
        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String, String> para = ParaUtils.getParaNece(mContext);
        para.put("Keyword", articleID);
        para.put("Page", "" + mCurPage) ;
        para.put("PageSize", "10");

        NetUtils.getDataFromServerByPost(mContext, Urls.ARTICLE_COMMENT_LIST, false, para,
                new RequestListCallBack<NewsCommentList>("getCommentList", mContext, NewsCommentList.class) {
                    @Override
                    public void onSuccessResult(List<NewsCommentList> beans) {
                        if(1 == mCurPage){
                            mCommentList.clear();
                        }
                        mCommentList.addAll(beans) ;
                        mCommentAdapter.notifyDataSetChanged() ;

                        if(beans.size() < 10){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        finishRequest() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishRequest() ;
                    }

                    @Override
                    public void onFail(Exception e) {
                        finishRequest() ;
                    }
                });
    }

    private void finishRequest(){
        mIsLoading = false ;
        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mCommentList.size() == 0){
            mFooterTv.setText("暂无评论") ;
        }
    }

    private void updateUI() {
        if (!MyUtils.isEmpty(newsDetail.H5Url))
            mWebView.loadUrl(newsDetail.H5Url);

        if(!"".equals(newsDetail.getApplyText())){
            mApplyTv.setText(newsDetail.getApplyText());
        }
        mTvTopAdmireNumber.setText(String.valueOf(newsDetail.LikeNumber));
        MyUtils.setBageShow(mTvCommentNumber, newsDetail.CommentNumber);
        MyUtils.setBageShow(mTvAdmireNumber, newsDetail.LikeNumber);

        if (newsDetail.IsLike == 1) {
            mImgAdmire.setImageResource(R.mipmap.ic_support_checked);
        } else {
            mImgAdmire.setImageResource(R.mipmap.ic_support_nomal);
        }

        if (newsDetail.IsCollection == 1) {
            mImgCollect.setImageResource(R.mipmap.ic_collection_checked);
        } else {
            mImgCollect.setImageResource(R.mipmap.ic_collection_nomal);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1001:
                if(RESULT_OK == resultCode){
                    addCommentToList(1 ,"") ;
                }
                break;
            case 1002:
                if(RESULT_OK == resultCode){
                    admire() ;
                }
                break;
            case 1003:
                if(RESULT_OK == resultCode){
                    collect();
                }
                break;
            case 1004:
                if(RESULT_OK == resultCode){
                    NewsCommentList commentList = mCommentList.get(mCurPosition) ;
                    if(commentList != null){
                        addCommentToList(2 ,commentList.getArticleCommentID()) ;
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getNewsDetails") ;
        NetUtils.cancelTag("supportNews") ;
        NetUtils.cancelTag("collectionNews") ;
        NetUtils.cancelTag("getCommentList") ;
    }

    @Override
    public void commentCallBack(String commentNum) {



    }
}
