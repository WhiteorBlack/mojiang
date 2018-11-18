package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.youku.cloud.module.PlayerErrorInfo;
import com.youku.cloud.player.PlayerListener;
import com.youku.cloud.player.VideoDefinition;
import com.youku.cloud.player.YoukuPlayerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.Admire;
import cn.idcby.jiajubang.Bean.Collect;
import cn.idcby.jiajubang.Bean.NewsCommentList;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.CommentAdapter;
import cn.idcby.jiajubang.interf.AddCommentCallBack;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
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

/**
 * 视频资讯详情
 */
public class NewsDetailVideoActivity extends BaseActivity implements AddCommentCallBack{
    private String articleID;

    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;
    private List<NewsCommentList> mCommentList = new ArrayList<>() ;

    //视频播放view
    private YoukuPlayerView youkuPlayerView;

    private ListView mListView;
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

    @Override
    public int getLayoutID() {
        return R.layout.activity_news_video_details;
    }

    public void initView() {
        articleID = getIntent().getStringExtra("articleID");
        String videoVid = getIntent().getStringExtra("videoPath");

//        videoVid = "XMTM2MDQ5MzUxMg==" ;
//        videoVid = "XMTgzOTgzODYwMA==" ;
//        videoVid = "XMzg1NDgwNDY1Ng==" ;

        youkuPlayerView = findViewById(R.id.acti_news_video_play_pv);
        youkuPlayerView.getLayoutParams().height = (int) (ResourceUtils.getScreenWidth(mContext)
                / ImageWidthUtils.getIndexHotNewsBigImageRote());

        // 初始化播放器
        youkuPlayerView.attachActivity(this);
        youkuPlayerView.setPreferVideoDefinition(VideoDefinition.VIDEO_HD);
        youkuPlayerView.setShowBackBtn(true);
        youkuPlayerView.setPlayerListener(new MyPlayerListener());
        youkuPlayerView.setShowFullBtn(true);
//        youkuPlayerView.setShowVideoQualityBtn(true);
        //vId
        youkuPlayerView.playYoukuVideo(videoVid) ;

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
    }

    @Override
    public void initListener() {
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
            }
        });
    }

    @Override
    public void initData() {
        super.initData();

        requestNewsDetail();
        getNewsCommentList() ;
    }

    private void addHeadView() {
        View mHeadView = View.inflate(mContext, R.layout.view_head_for_news_detail, null);
        mApplyTv = mHeadView.findViewById(R.id.header_news_dt_apply_tv);
        mTvTopAdmireNumber = mHeadView.findViewById(R.id.tv_top_admire_number);
        View qqLay = mHeadView.findViewById(R.id.header_news_dt_share_qq_lay);
        View qqZeroLay = mHeadView.findViewById(R.id.header_news_dt_share_qq_zero_lay);
        View wxLay = mHeadView.findViewById(R.id.header_news_dt_share_wx_lay);
        View wxCircleLay = mHeadView.findViewById(R.id.header_news_dt_share_wx_circle_lay);
        mListView.addHeaderView(mHeadView);

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

    // 添加播放器的监听器
    private class MyPlayerListener extends PlayerListener {
        @Override
        public void onComplete() {
            // TODO Auto-generated method stub
            super.onComplete();
        }

        @Override
        public void onError(int code, PlayerErrorInfo info) {
            // TODO Auto-generated method stub

        }

        @Override
        public void OnCurrentPositionChanged(int msec) {
            // TODO Auto-generated method stub
            super.OnCurrentPositionChanged(msec);
        }

        @Override
        public void onVideoNeedPassword(int code) {
            // TODO Auto-generated method stub
            super.onVideoNeedPassword(code);
        }

        @Override
        public void onVideoSizeChanged(int width, int height) {
            // TODO Auto-generated method stub
            super.onVideoSizeChanged(width, height);
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
        }
    }

    private void addCommentToList(int commentLevel ,String parentId){
        if(null == mCommentPopup){
            mCommentPopup = new AddCommentPopup(mActivity, AddCommentPopup.COMMENT_TYPE_NEWS
                    , youkuPlayerView, new AddCommentCallBack() {
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

                    }

                    @Override
                    public void onFail(Exception e) {

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
    protected void onPause() {
        youkuPlayerView.onPause();
        super.onPause();
        // 必须重写的onPause()

    }

    @Override
    protected void onResume() {
        youkuPlayerView.onResume();
        super.onResume();
        // 必须重写的onResume()

    }


    @Override
    protected void onDestroy() {
        youkuPlayerView.onDestroy();

        super.onDestroy();

        NetUtils.cancelTag("getNewsDetails") ;
        NetUtils.cancelTag("supportNews") ;
        NetUtils.cancelTag("collectionNews") ;
        NetUtils.cancelTag("getCommentList") ;
    }

    @Override
    public void commentCallBack(String commentNum) {

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //后退键的处理，当全屏时，后退为变为小屏
        if (youkuPlayerView.isFullScreen()) {
            youkuPlayerView.goSmallScreen();
        } else {
            super.onBackPressed();
        }
    }
}
