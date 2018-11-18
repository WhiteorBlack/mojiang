package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.CircleDetail;
import cn.idcby.jiajubang.Bean.CircleTransInfo;
import cn.idcby.jiajubang.Bean.Collect;
import cn.idcby.jiajubang.Bean.CommentCircleList;
import cn.idcby.jiajubang.Bean.FocusResult;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterCircleComment;
import cn.idcby.jiajubang.adapter.AdapterCircleThreeImage;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.AddCommentCallBack;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
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
import cn.idcby.jiajubang.view.TopBarRightPopup;

/**
 * 圈子详情
 * 2018-09-14 15:48:15
 *
 * 圈子详情添加转发功能，话题没有
 * 转载的圈子详情跟列表类似，是展示转载的内容的，如果是原始贴，才展示具体内容
 * 所以当前界面，调整为 如果是原始帖 ，显示webView ；如果是转载贴，展示转载样式。
 *
 * 注意：当前界面为 圈子详情 和 话题详情界面
 *
 * 圈子详情改为原生界面，所以要把话题详情剥离出去了
 * {@link TopicDetailActivity}
 */
public class CircleDetailActivity extends BaseMoreStatusActivity {
    private Activity mActivity ;
    private String articleID;

    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;
    private List<CommentCircleList> mCommentList = new ArrayList<>() ;

    private ListView mListView;

    private TextView mApplyTv ;
    private TextView mHeadCommentCountTv ;
    private TextView mTvAddComment;
    private TextView mTvCommentNumber;
    private TextView mTvSupportNumber;
    private TextView mTvTransNumber;//转发数量
    private View mSupportLay;
    private ImageView mImgSupportIv;
    private View mImgCollectLay;
    private ImageView mImgCollectIv;

    //基本信息
    private ImageView mAutherIv ;
    private TextView mAutherNameTv ;
    private View mAutherVIv ;
    private TextView mTimeTv ;
    private TextView mCompanyNameTv ;
    private TextView mPostNameTv ;
    private TextView mAttentionTv ;
    private TextView mSeeCountTv ;
    private TextView mContentTv ;

    //单图
    private ImageView mDtSingleIv ;

    //4图
    private View mDtFourLay ;
    private ImageView mImageOneIv ;
    private ImageView mImageTwoIv ;
    private ImageView mImageThreeIv ;
    private ImageView mImageFourIv ;

    //多图
    private RecyclerView mImageRv ;

    //转载的圈子
    private View mTransDtLay ;
    private ImageView mTransportIv ;
    private TextView mTransportTitleTv ;
    private TextView mTransportTypeTv ;
    private View mTransportDeletedLay ;

    private AdapterCircleComment mCommentAdapter;
    private CircleDetail mCircleDetail;

    private LoadingDialog loadingDialog;

    //评论相关
    private int mCurPosition ;
    private AddCommentPopup mCommentPopup ;

    private TextView mFooterTv ;

    private TopBarRightPopup mRightPopup ;
    private View mRightView ;

    private boolean mIsWvFinish = false ;

    private static final int REQUEST_CODE_CIRCLE_FROM_TRANSPORT = 1000 ;
    private static final int REQUEST_CODE_CIRCLE_COMMENT = 1001 ;
    private static final int REQUEST_CODE_CIRCLE_SUPPORT = 1002 ;
    private static final int REQUEST_CODE_CIRCLE_COLLECTION = 1003 ;
    private static final int REQUEST_CODE_CIRCLE_COMMENT_REPLY = 1004 ;
    private static final int REQUEST_CODE_CIRCLE_TO_TRANSPORT = 1005 ;
    private static final int REQUEST_CODE_FOCUS = 1006 ;


    @Override
    public void requestData() {
        mCurPage = 1 ;
        mIsMore = true ;

        requestCircleDetail();
        getCircleCommentList() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_circle_detail;
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
        String strTitle = mCircleDetail.getBodyContent();
        String strUrl = mCircleDetail.getH5Url();
        String strImgurl = mCircleDetail.getImgUrl() ;
        String strSubscriotion = "";
        ShareUtils.shareWeb(mActivity, strTitle, strUrl, strImgurl, strSubscriotion);
    }

    @Override
    public void init() {
        mActivity = this ;
        articleID = getIntent().getStringExtra(SkipUtils.INTENT_ARTICLE_ID);

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
        View mTransLay = findViewById(R.id.acti_circle_details_trans_lay) ;
        mTvTransNumber = findViewById(R.id.acti_circle_details_trans_count_tv) ;
        mTransLay.setOnClickListener(this);

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
        View headView = View.inflate(mContext, R.layout.view_head_for_circle_detail, null);
        mApplyTv = headView.findViewById(R.id.header_circle_details_apply_tv);
        mHeadCommentCountTv = headView.findViewById(R.id.header_circle_details_comment_count_tv) ;

        mAutherIv = headView.findViewById(R.id.head_circle_dt_trans_user_iv);
        mAutherVIv = headView.findViewById(R.id.head_circle_dt_trans_user_v_iv);
        mAutherNameTv = headView.findViewById(R.id.head_circle_dt_trans_user_name_tv);
        mTimeTv = headView.findViewById(R.id.head_circle_dt_trans_send_time_tv);
        mCompanyNameTv = headView.findViewById(R.id.head_circle_dt_trans_send_company_tv);
        mPostNameTv = headView.findViewById(R.id.head_circle_dt_trans_send_post_tv);
        mSeeCountTv = headView.findViewById(R.id.head_circle_dt_trans_see_count_tv);
        mAttentionTv = headView.findViewById(R.id.head_circle_dt_trans_focus_tv);
        mContentTv = headView.findViewById(R.id.head_circle_dt_trans_content_tv);
        mAutherIv.setOnClickListener(this);
        mAutherNameTv.setOnClickListener(this);
        mAttentionTv.setOnClickListener(this);

        mImageRv = headView.findViewById(R.id.head_circle_dt_img_rv) ;

        mDtFourLay = headView.findViewById(R.id.head_circle_dt_four_lay) ;
        mImageOneIv = headView.findViewById(R.id.head_circle_dt_item_one_iv) ;
        mImageTwoIv = headView.findViewById(R.id.head_circle_dt_item_two_iv) ;
        mImageThreeIv = headView.findViewById(R.id.head_circle_dt_item_three_iv) ;
        mImageFourIv = headView.findViewById(R.id.head_circle_dt_item_four_iv) ;

        mDtSingleIv =  headView.findViewById(R.id.head_circle_dt_single_iv);

        mTransDtLay = headView.findViewById(R.id.head_circle_dt_transport_dt_lay) ;
        mTransportIv = headView.findViewById(R.id.head_circle_dt_transport_dt_iv) ;
        mTransportTitleTv = headView.findViewById(R.id.head_circle_dt_transport_dt_title_tv) ;
        mTransportTypeTv = headView.findViewById(R.id.head_circle_dt_transport_dt_type_tv) ;
        mTransportDeletedLay = headView.findViewById(R.id.head_circle_dt_transport_dt_deleted_lay) ;

        mListView.addHeaderView(headView);
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
        } else if (i == R.id.acti_circle_details_trans_lay) {//转发
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(mActivity, REQUEST_CODE_CIRCLE_TO_TRANSPORT);
                return;
            }

            toTransportActivity();
        }else if(i == R.id.head_circle_dt_trans_user_iv
                || i == R.id.head_circle_dt_trans_user_name_tv){
            SkipUtils.toOtherUserInfoActivity(mContext ,mCircleDetail.getCreateUserId()) ;
        }else if(i == R.id.head_circle_dt_trans_focus_tv){
            changeFocusState() ;
        }
    }

    /**
     * 跳转到转载
     */
    private void toTransportActivity(){
        CircleTransportActivity.launch(mActivity ,mCircleDetail.getPostID()
                ,mCircleDetail.getBodyContent() ,mCircleDetail.getImgUrl()
                ,mCircleDetail.getCategoryTitle(), REQUEST_CODE_CIRCLE_FROM_TRANSPORT);
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

        mCommentPopup.displayDialog(articleID, commentLevel, parentId);
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
        para.put("Code", articleID);
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
     * 关注、取消关注
     */
    private void changeFocusState(){
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_FOCUS);
            return ;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("FollowType" , "1") ;
        paramMap.put("ResourceId" , mCircleDetail.getCreateUserId()) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, paramMap
                , new RequestObjectCallBack<FocusResult>("changeFocusState" , mContext ,FocusResult.class) {
                    @Override
                    public void onSuccessResult(FocusResult bean) {
                        mCircleDetail.setIsFollow(bean);

                        updateFocusState() ;

                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if(loadingDialog != null){
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

    private void updateFocusState(){
        boolean isFocused = (1 == mCircleDetail.getIsFollow()) ;

        mAttentionTv.setText(isFocused ? "已关注" : "关注");
        mAttentionTv.setTextColor(mContext.getResources().getColor(isFocused
                ? R.color.color_grey_88
                : R.color.color_theme));
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
        para.put("Code", articleID);
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
     * 获取圈子详情
     */
    private void requestCircleDetail() {
        Map<String,String> para = ParaUtils.getParaNece(mContext);
        para.put("Code", articleID);

        NetUtils.getDataFromServerByPost(mContext,Urls.CIRCLE_DETAILS, false, para,
                new RequestObjectCallBack<CircleDetail>("getCircleDetails", mContext, CircleDetail.class) {
                    @Override
                    public void onSuccessResult(CircleDetail bean) {
                        if(null == bean){
                            showErrorPage() ;
                        }else{
                            mCircleDetail = bean;
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
        para.put("Keyword", articleID);
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
        showSuccessPage() ;

        //基础信息
        String writerName = mCircleDetail.getCreateUserName() ;
        String writerPhoto = mCircleDetail.getCreateUserHeadIcon() ;
        final String content = mCircleDetail.getBodyContent() ;
        String data = mCircleDetail.getReleaseTime() ;
        String companyName = mCircleDetail.getCompanyName() ;
        String postName = mCircleDetail.getPostText() ;
        int seeCount = mCircleDetail.getClickNumber() ;

        boolean isV = mCircleDetail.isIndustryV() ;
        mAutherVIv.setVisibility(isV ? View.VISIBLE : View.INVISIBLE) ;

        mAutherNameTv.setText(writerName);
        mTimeTv.setText(data);
        mCompanyNameTv.setText(companyName);
        mPostNameTv.setText(postName);
        mSeeCountTv.setText("浏览" + seeCount + "次");

        if(!"".equals(content)){
            mContentTv.setText(content);
            mContentTv.setVisibility(View.VISIBLE) ;
        }else{
            mContentTv.setVisibility(View.GONE) ;
        }

        GlideUtils.loaderUser(writerPhoto , mAutherIv) ;

        updateFocusState() ;

        //如果是转载的
        if(mCircleDetail.isTransport()){
            mTransDtLay.setVisibility(View.VISIBLE);
            mTransportDeletedLay.setVisibility(View.GONE);

            final CircleTransInfo transInfo = mCircleDetail.getSourcePostInfo() ;
            if(transInfo != null){
                mTransDtLay.setVisibility(View.VISIBLE) ;

                String name = transInfo.getBodyContent() ;
                String imgUrl = transInfo.getImgUrl() ;
                String categoryName = mCircleDetail.getCategoryTitle() ;//理论上转载的分类跟被转载的是一致的

                mTransportTitleTv.setText(name);
                mTransportTypeTv.setText(categoryName);
                GlideUtils.loader(imgUrl ,mTransportIv) ;
            }else{
                mTransDtLay.setVisibility(View.GONE) ;
                mTransportDeletedLay.setVisibility(View.VISIBLE);
            }

            mTransDtLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDtIt = new Intent(mContext , CircleDetailActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID , transInfo.getPostID()) ;
                    startActivity(toDtIt) ;
                }
            });
        }else{
            final List<ImageThumb> thumbList = mCircleDetail.getAlbumsList() ;
            int size = thumbList.size() ;

            if(size <= 1){//单图
                if(size == 0){
                    mDtSingleIv.setVisibility(View.GONE);
                }else{
                    mDtSingleIv.setVisibility(View.VISIBLE);
                    final String thumb = thumbList.get(0).getThumbImgUrl() ;
                    Glide.with(MyApplication.getInstance()).asBitmap().load(thumb).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource
                                , @Nullable Transition<? super Bitmap> transition) {
                            int width = resource.getWidth();
                            int height = resource.getHeight();

                            //图片的 宽 / 高
                            float scale = (float) width / (float) height;
                            int screenWidth = ResourceUtils.getScreenWidth(MyApplication.getInstance()) ;
                            int imageWid ;
                            int imageHei ;

                            if(scale > 1F){//横图
                                imageWid = (int) (screenWidth / ImageWidthUtils.getSingleImageItemSelfRote(true));
                            }else{//竖图
                                imageWid = (int) (screenWidth / ImageWidthUtils.getSingleImageItemSelfRote(false));
                            }
                            imageHei = (int) (imageWid / scale);

                            if(imageHei > (ResourceUtils.getDeviceHeight(MyApplication.getInstance()) / 2)){
                                imageHei = ResourceUtils.getDeviceHeight(MyApplication.getInstance()) / 2 ;
                            }

                            ViewGroup.LayoutParams params = mDtSingleIv.getLayoutParams();
                            params.width = imageWid ;
                            params.height = imageHei;
                            mDtSingleIv.setLayoutParams(params);

                            GlideUtils.loader(thumb ,mDtSingleIv) ;
                        }
                    }) ;

                    mDtSingleIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SkipUtils.toImageShowActivity(mActivity ,thumb,0);
                        }
                    });
                }
            }else if(size == 4){//4图
                mDtFourLay.setVisibility(View.VISIBLE);

                int imgWidHei = (ResourceUtils.getScreenWidth(mContext)
                        - ResourceUtils.dip2px(mContext , 15) * 2 - ResourceUtils.dip2px(mContext , 2) * 2) / 3 ;
                mImageOneIv.getLayoutParams().width = imgWidHei ;
                mImageOneIv.getLayoutParams().height = imgWidHei ;
                mImageTwoIv.getLayoutParams().width = imgWidHei ;
                mImageTwoIv.getLayoutParams().height = imgWidHei ;
                mImageThreeIv.getLayoutParams().width = imgWidHei ;
                mImageThreeIv.getLayoutParams().height = imgWidHei ;
                mImageFourIv.getLayoutParams().width = imgWidHei ;
                mImageFourIv.getLayoutParams().height = imgWidHei ;

                String imgUrlOne = thumbList.get(0).getThumbImgUrl() ;
                String imgUrlTwo = thumbList.get(1).getThumbImgUrl() ;
                String imgUrlThree = thumbList.get(2).getThumbImgUrl() ;
                String imgUrlFour = thumbList.get(3).getThumbImgUrl() ;

                GlideUtils.loader(imgUrlOne ,mImageOneIv) ;
                GlideUtils.loader(imgUrlTwo ,mImageTwoIv) ;
                GlideUtils.loader(imgUrlThree ,mImageThreeIv) ;
                GlideUtils.loader(imgUrlFour ,mImageFourIv) ;

                mImageOneIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SkipUtils.toImageShowActivityWithThumb(mActivity ,thumbList ,0) ;
                    }
                });
                mImageTwoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SkipUtils.toImageShowActivityWithThumb(mActivity ,thumbList ,1) ;
                    }
                });
                mImageThreeIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SkipUtils.toImageShowActivityWithThumb(mActivity ,thumbList ,2) ;
                    }
                });
                mImageFourIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SkipUtils.toImageShowActivityWithThumb(mActivity ,thumbList ,3) ;
                    }
                });
            }else{//多图
                mImageRv.setVisibility(View.VISIBLE) ;

                GridLayoutManager gm = new GridLayoutManager(mContext , 3) ;
                gm.setAutoMeasureEnabled(true);
                mImageRv.setLayoutManager(gm) ;
                mImageRv.setFocusable(false) ;
                mImageRv.setAdapter(new AdapterCircleThreeImage(mActivity, thumbList));
            }
        }

        if(!"".equals(mCircleDetail.getApplyText())){
            mApplyTv.setText(mCircleDetail.getApplyText());
        }

        String comCount = "留言（" + StringUtils.convertString2Count(mCircleDetail.getCommentNumber()+"") + "）" ;

        mHeadCommentCountTv.setText(comCount);
        MyUtils.setBageShow(mTvCommentNumber, mCircleDetail.CommentNumber);
        MyUtils.setBageShow(mTvSupportNumber, mCircleDetail.LikeNumber);
        MyUtils.setBageShow(mTvTransNumber, mCircleDetail.getTransNumber());
        if (mCircleDetail.IsLike == 1) {
            mImgSupportIv.setImageResource(R.mipmap.ic_support_checked);
        } else {
            mImgSupportIv.setImageResource(R.mipmap.ic_support_nomal);
        }

        if (mCircleDetail.IsCollection == 1) {
            mImgCollectIv.setImageResource(R.mipmap.ic_collection_checked);
        } else {
            mImgCollectIv.setImageResource(R.mipmap.ic_collection_nomal);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_CIRCLE_FROM_TRANSPORT:
                if(RESULT_OK == resultCode){
                    int transCount = mCircleDetail.getTransNumber() ;
                    if(transCount == 0){
                        mTvTransNumber.setVisibility(View.VISIBLE);
                        mTvTransNumber.setText("" + (transCount + 1)) ;
                    }
                }
                break;
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
            case REQUEST_CODE_CIRCLE_TO_TRANSPORT:
                if(RESULT_OK == resultCode){
                    toTransportActivity() ;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getCircleDetails") ;
        NetUtils.cancelTag("supportCircle") ;
        NetUtils.cancelTag("collectionCircle") ;
        NetUtils.cancelTag("getCommentList") ;
    }
}
