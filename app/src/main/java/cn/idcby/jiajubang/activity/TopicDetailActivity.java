package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.jiajubang.Bean.Collect;
import cn.idcby.jiajubang.Bean.CommentCircleList;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.Bean.TopicDetail;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterCircleComment;
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

/**
 * 话题详情
 * 2018-09-14 15:48:15
 * 圈子详情改为原生的了，所以把话题详情剥离出来，之前是两者公用一个界面
 */
public class TopicDetailActivity extends BaseMoreStatusActivity {
    private Activity mActivity ;
    private String mTopicId;

    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;
    private List<CommentCircleList> mCommentList = new ArrayList<>() ;

    private ListView mListView;
    private WebView mWebView;
    private TextView mApplyTv ;
    private TextView mHeadCommentCountTv ;
    private TextView mTvAddComment;
    private TextView mTvCommentNumber;
    private TextView mTvSupportNumber;
    private View mSupportLay;
    private ImageView mImgSupportIv;
    private View mImgCollectLay;
    private ImageView mImgCollectIv;

    private AdapterCircleComment mCommentAdapter;
    private TopicDetail mTopicDetail;

    private LoadingDialog loadingDialog;

    //评论相关
    private int mCurPosition ;
    private AddCommentPopup mCommentPopup ;

    private TextView mFooterTv ;

    private TopBarRightPopup mRightPopup ;
    private View mRightView ;

    private boolean mIsWvFinish = false ;

    private static final int REQUEST_CODE_CIRCLE_COMMENT = 1001 ;
    private static final int REQUEST_CODE_CIRCLE_SUPPORT = 1002 ;
    private static final int REQUEST_CODE_CIRCLE_COLLECTION = 1003 ;
    private static final int REQUEST_CODE_CIRCLE_COMMENT_REPLY = 1004 ;


    @Override
    public void requestData() {
        mCurPage = 1 ;
        mIsMore = true ;

        getTopicDetails();
        getCircleCommentList() ;
    }


    @Override
    public int getSuccessViewId() {
        return R.layout.activity_topic_detail;
    }

    @Override
    public String setTitle() {
        return "详情";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
        mRightView = imgRight ;
        imgRight.setVisibility(View.VISIBLE);
        imgRight.setImageResource(R.mipmap.ic_top_more_grey);
        imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRightPopup() ;
            }
        });
        setMessageCountShow(false) ;
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
        String strTitle = mTopicDetail.getTitle();
        String strUrl = mTopicDetail.getH5Url();
        String strImgurl = mTopicDetail.getImgUrl() ;
        String strSubscriotion = "";
        ShareUtils.shareWeb(mActivity, strTitle, strUrl, strImgurl, strSubscriotion);
    }

    @Override
    public void init() {
        mActivity = this ;
        mTopicId = getIntent().getStringExtra(SkipUtils.INTENT_ARTICLE_ID);

        initView();
        initListenner();
    }

    private void initView() {
        mListView = findViewById(R.id.acti_circle_details_lv);
        mTvAddComment = findViewById(R.id.acti_circle_details_add_comment_tv);
        addHeadView();

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mListView.addFooterView(mFooterTv) ;

        mTvCommentNumber = findViewById(R.id.acti_circle_details_comment_count_tv);
        mTvSupportNumber = findViewById(R.id.acti_circle_details_support_count_tv);
        mSupportLay = findViewById(R.id.acti_circle_details_support_lay);
        mImgCollectLay = findViewById(R.id.acti_circle_details_collect_lay);
        mImgSupportIv = findViewById(R.id.acti_circle_details_support_iv);
        mImgCollectIv = findViewById(R.id.acti_circle_details_collect_iv);

        mCommentAdapter = new AdapterCircleComment(mCommentList, mActivity, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    mCurPosition = position ;

                    if(LoginHelper.isNotLogin(mContext)){
                        SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_CIRCLE_COMMENT_REPLY);
                    }else{
                        CommentCircleList commentList = mCommentList.get(position) ;
                        if(commentList != null){
                            addCommentToList(2 ,commentList.getID()) ;
                        }
                    }
                }
            }
        });
        mListView.setAdapter(mCommentAdapter);
    }

    private void addHeadView() {
        View headView = View.inflate(mContext, R.layout.view_head_for_topic_detail, null);
        mWebView = headView.findViewById(R.id.head_view_circle_details_webview);
        mApplyTv = headView.findViewById(R.id.header_circle_details_apply_tv);
        mHeadCommentCountTv = headView.findViewById(R.id.header_circle_details_comment_count_tv) ;

        mListView.addHeaderView(headView);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                mIsWvFinish = true ;
                showSuccessPage();
            }
        });
    }

    private void initListenner() {
        mTvAddComment.setOnClickListener(this);
        mSupportLay.setOnClickListener(this);
        mImgCollectLay.setOnClickListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && mIsWvFinish && i > 5 && i1 + i2 >= i){
                    getCircleCommentList() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();

        if (i == R.id.acti_circle_details_add_comment_tv) {
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, REQUEST_CODE_CIRCLE_COMMENT);
                return;
            }

            addCommentToList(1, "");
        } else if (i == R.id.acti_circle_details_support_lay) {
            supportCircle();
        } else if (i == R.id.acti_circle_details_collect_lay) {
            collectCircle();
        }
    }

    /**
     * 评论
     * @param commentLevel level
     * @param parentId parentId
     */
    private void addCommentToList(int commentLevel ,String parentId){
        if(null == mCommentPopup){
            mCommentPopup = new AddCommentPopup(mActivity, AddCommentPopup.COMMENT_TYPE_CIRCLE
                    , mFlContainer, new AddCommentCallBack() {
                @Override
                public void commentCallBack(String commentNum) {
                    mCurPage = 1 ;
                    getCircleCommentList();

                    if(!"".equals(StringUtils.convertNull(commentNum))){
                        mTvCommentNumber.setVisibility(View.VISIBLE);
                        mTvCommentNumber.setText(commentNum) ;
                        mHeadCommentCountTv.setText("留言（" + commentNum + "）");
                    }
                }
            }) ;
        }

        mCommentPopup.displayDialog(mTopicId, commentLevel, parentId);
    }

    private void collectCircle() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mActivity , REQUEST_CODE_CIRCLE_COLLECTION);
            return;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("Code", mTopicId);
        NetUtils.getDataFromServerByPost(mContext, Urls.CIRCLE_COLLECT, false, para,
                new RequestObjectCallBack<Collect>("collectionCircle", mContext, Collect.class) {
                    @Override
                    public void onSuccessResult(Collect bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (bean.AddOrDelete == 1) {
                            mImgCollectIv.setImageResource(R.mipmap.ic_collection_checked);
                        } else {
                            mImgCollectIv.setImageResource(R.mipmap.ic_collection_nomal);
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
    private void supportCircle() {
        if (LoginHelper.isNotLogin(mContext)) {
            SkipUtils.toLoginActivityForResult(mActivity , REQUEST_CODE_CIRCLE_SUPPORT);
            return;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getParaWithToken(mContext);
        para.put("Code", mTopicId);
        NetUtils.getDataFromServerByPost(mContext, Urls.CIRCLE_SUPPORT, false, para,
                new RequestObjectCallBack<SupportResult>("supportCircle", mContext, SupportResult.class) {
                    @Override
                    public void onSuccessResult(SupportResult bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (bean.AddOrDelete == 1) {
                            mImgSupportIv.setImageResource(R.mipmap.ic_support_checked);

                        } else {
                            mImgSupportIv.setImageResource(R.mipmap.ic_support_nomal);
                        }
                        MyUtils.setBageShow(mTvSupportNumber, bean.LikeNumber);
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
     * 获取话题详情
     */
    private void getTopicDetails() {
        Map<String,String> para = ParaUtils.getParaNece(mContext);
        para.put("Code", mTopicId);

        NetUtils.getDataFromServerByPost(mContext,Urls.CIRCLE_TOPIC_DETAILS, para,
                new RequestObjectCallBack<TopicDetail>("getTopicDetails", mContext, TopicDetail.class) {
                    @Override
                    public void onSuccessResult(TopicDetail bean) {
                        if(null == bean){
                            showErrorPage() ;
                        }else{
                            mTopicDetail = bean;
                            updateUI();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showErrorPage();
                    }

                    @Override
                    public void onFail(Exception e) {
                        showErrorPage();
                    }
                });
    }

    /**
     * 获取评论列表
     */
    private void getCircleCommentList(){
        mIsLoading = true ;

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String, String> para = ParaUtils.getParaNece(mContext);
        para.put("Keyword", mTopicId);
        para.put("Page", "" + mCurPage) ;
        para.put("PageSize", "10");

        NetUtils.getDataFromServerByPost(mContext, Urls.CIRCLE_COMMENT_LIST, false, para,
                new RequestListCallBack<CommentCircleList>("getCommentList", mContext, CommentCircleList.class) {
                    @Override
                    public void onSuccessResult(List<CommentCircleList> beans) {
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

                        finishReqeust();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishReqeust();
                    }

                    @Override
                    public void onFail(Exception e) {
                        finishReqeust();
                    }
                });
    }

    private void finishReqeust(){
        mIsLoading = false ;

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mCommentList.size() == 0){
            mFooterTv.setText("暂无评论");
        }
    }

    private void updateUI() {
        if (!MyUtils.isEmpty(mTopicDetail.H5Url))
            mWebView.loadUrl(mTopicDetail.H5Url);

        if(!"".equals(mTopicDetail.getApplyText())){
            mApplyTv.setText(mTopicDetail.getApplyText());
        }

        String comCount = "留言（" + StringUtils.convertString2Count(mTopicDetail.getCommentNumber()+"") + "）" ;

        mHeadCommentCountTv.setText(comCount);
        MyUtils.setBageShow(mTvCommentNumber, mTopicDetail.CommentNumber);
        MyUtils.setBageShow(mTvSupportNumber, mTopicDetail.LikeNumber);
        if (mTopicDetail.IsLike == 1) {
            mImgSupportIv.setImageResource(R.mipmap.ic_support_checked);
        } else {
            mImgSupportIv.setImageResource(R.mipmap.ic_support_nomal);
        }

        if (mTopicDetail.IsCollection == 1) {
            mImgCollectIv.setImageResource(R.mipmap.ic_collection_checked);
        } else {
            mImgCollectIv.setImageResource(R.mipmap.ic_collection_nomal);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_CIRCLE_COMMENT:
                if(RESULT_OK == resultCode){
                    addCommentToList(1,"");
                }
                break;
            case REQUEST_CODE_CIRCLE_SUPPORT:
                if(RESULT_OK == resultCode){
                    supportCircle() ;
                }
                break;
            case REQUEST_CODE_CIRCLE_COLLECTION:
                if(RESULT_OK == resultCode){
                    collectCircle();
                }
                break;
            case REQUEST_CODE_CIRCLE_COMMENT_REPLY:
                if(RESULT_OK == resultCode){
                    CommentCircleList commentList = mCommentList.get(mCurPosition) ;
                    if(commentList != null){
                        addCommentToList(2 ,commentList.getID()) ;
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

        NetUtils.cancelTag("getTopicDetails") ;
        NetUtils.cancelTag("supportCircle") ;
        NetUtils.cancelTag("collectionCircle") ;
        NetUtils.cancelTag("getCommentList") ;
    }
}
