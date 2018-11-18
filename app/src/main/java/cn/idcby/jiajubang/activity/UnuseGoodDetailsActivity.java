package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.jiajubang.Bean.CollectionResult;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.Bean.UnuseCommentList;
import cn.idcby.jiajubang.Bean.UnuseDetails;
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterUnuseComment;
import cn.idcby.jiajubang.adapter.AdapterUnuseSimple;
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
import cn.idcby.jiajubang.view.AddCommentPopup;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.TopBarRightPopup;

/**
 * 闲置详细
 * Created on 2018/3/29.
 *
 * 2018-09-13 13:20:15
 * 添加相似闲置
 */

public class UnuseGoodDetailsActivity extends BaseActivity {
    private WebView mUnuseWv ;
    private TextView mCommentCountTv ;
    private View mCommentMoreTv ;
    private View mCommentMoreIv ;
    private View mCommentNullTv ;
    private ListView mCommentLv ;
    private RecyclerView mUnuseGoodRv ;

    private ImageView mSupportIv ;
    private TextView mSupportCountTv;
    private ImageView mCollectionIv ;
    private TextView mCommentNumTv;
    private TextView mConnTv ;
    private TextView mSendTv ;

    private String mUnuseId;
    private UnuseDetails mDetails ;

    private List<UnuseCommentList> mCommentList = new ArrayList<>() ;
    private AdapterUnuseComment mCommentAdapter ;
    private boolean mIsSelf = false ;

    private LoadingDialog mLoadingDialog ;

    private static final int REQUEST_CODE_COLLECTION = 1001 ;
    private static final int REQUEST_CODE_SUPPORT = 1002 ;
    private static final int REQUEST_CODE_COMMENT = 1003 ;
    public static final int REQUEST_CODE_BUY = 1005 ;
    private static final int REQUEST_CODE_CONNECTION = 1007 ;
    private static final int REQUEST_CODE_COMMENT_REPLY = 1008 ;
    private static final int REQUEST_CODE_COMMENT_MORE = 1009 ;

    //评论相关
    private int mCurPosition ;
    private AddCommentPopup mCommentPopup ;

    //相似闲置
    private List<UnuseGoodList> mUnuseGoodList = new ArrayList<>() ;
    private AdapterUnuseSimple mUnuseAdapter ;

    private View mToTopIv ;

    private boolean mIsWvFinish = false ;

    private View mRightView ;
    private TopBarRightPopup mRightPopup ;

    /**
     * 显示右边popup
     */
    private void showRightPopup(){
        if(null == mRightPopup){
            mRightPopup = new TopBarRightPopup(mContext, mRightView, new TopBarRightPopup.TopRightPopupCallBack() {
                @Override
                public void onItemClick(int position) {
                    if(TopBarRightPopup.POPUP_ITEM_SHARE == position){//分享
                        shareToOtherPlant() ;
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
        String strTitle = mDetails.getTitle();
        String strUrl = mDetails.getH5Url();
        String strImgurl = mDetails.getImgUrl();
        String strSubscriotion = "";
        ShareUtils.shareWeb(mActivity, strTitle, strUrl, strImgurl, strSubscriotion);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_unuse_good_detail ;
    }

    @Override
    public void initView() {
        mUnuseId = getIntent().getStringExtra(SkipUtils.INTENT_UNUSE_ID) ;

        mRightView = findViewById(R.id.acti_unuse_dt_right_iv) ;

        mCommentLv = findViewById(R.id.acti_unuse_dt_comment_lv) ;
        mUnuseGoodRv = findViewById(R.id.acti_unuse_dt_good_rv) ;

        mUnuseWv = findViewById(R.id.acti_unuse_dt_wv) ;
        mUnuseWv.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                mIsWvFinish = true ;
                //此时再显示界面


            }
        });
        mCommentCountTv = findViewById(R.id.acti_unuse_dt_comment_count_tv) ;
        mCommentMoreTv = findViewById(R.id.acti_unuse_dt_comment_more_tv) ;
        mCommentMoreIv = findViewById(R.id.acti_unuse_dt_comment_more_iv) ;
        mCommentNullTv = findViewById(R.id.acti_unuse_dt_comment_null_tv) ;
        mCommentMoreTv.setOnClickListener(this);
        mCommentMoreIv.setOnClickListener(this);

        View mSupportLay = findViewById(R.id.acti_unuse_dt_support_lay) ;
        mSupportIv = findViewById(R.id.acti_unuse_dt_support_iv) ;
        mSupportCountTv = findViewById(R.id.acti_unuse_dt_support_number_tv) ;
        View mCollectionLay = findViewById(R.id.acti_unuse_dt_collection_lay) ;
        mCollectionIv = findViewById(R.id.acti_unuse_dt_collection_iv) ;
        View mCommentLay = findViewById(R.id.acti_unuse_dt_comment_lay) ;
        mCommentNumTv = findViewById(R.id.acti_unuse_dt_comment_number_tv) ;
        mConnTv = findViewById(R.id.acti_unuse_dt_connection_tv) ;
        mSendTv = findViewById(R.id.acti_unuse_dt_send_tv) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        mRightView.setOnClickListener(this) ;
        mSupportLay.setOnClickListener(this) ;
        mCollectionLay.setOnClickListener(this) ;
        mCommentLay.setOnClickListener(this) ;
        mConnTv.setOnClickListener(this) ;
        mSendTv.setOnClickListener(this) ;

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUnuseGoodRv.setLayoutManager(layoutManager);
        mUnuseGoodRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext , ResourceUtils.dip2px(mContext ,10)
                ,getResources().getDrawable(R.drawable.drawable_white_trans),RvLinearManagerItemDecoration.HORIZONTAL_LIST));
        mUnuseAdapter = new AdapterUnuseSimple(mContext, mUnuseGoodList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    UnuseGoodList goodList = mUnuseGoodList.get(position) ;
                    if(goodList != null){
                        Intent toDtIt = new Intent(mContext ,UnuseGoodDetailsActivity.class) ;
                        toDtIt.putExtra(SkipUtils.INTENT_UNUSE_ID,goodList.getProductID()) ;
                        startActivity(toDtIt) ;
                    }
                }
            }
        }) ;
        mUnuseGoodRv.setAdapter(mUnuseAdapter) ;
    }

    @Override
    public void initData() {
        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.show() ;

        mCommentAdapter = new AdapterUnuseComment(mCommentList, mActivity, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    mCurPosition = position ;

                    if(LoginHelper.isNotLogin(mContext)){
                        SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_COMMENT_REPLY);
                    }else{
                        UnuseCommentList commentList = mCommentList.get(position) ;
                        if(commentList != null){
                            addCommentToList(2 ,commentList.getID()) ;
                        }
                    }
                }
            }
        }) ;
        mCommentLv.setAdapter(mCommentAdapter) ;

        getUnuseDetails();
        getUnuseCommentList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_unuse_dt_right_iv == vId){
            showRightPopup() ;
        }else if(R.id.acti_unuse_dt_support_lay == vId){//赞
            goSupport() ;
        }else if(R.id.acti_unuse_dt_collection_lay == vId){//收藏
            goCollection() ;
        }else if(R.id.acti_unuse_dt_comment_lay == vId){//留言
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(UnuseGoodDetailsActivity.this , REQUEST_CODE_COMMENT);
                return;
            }

            addCommentToList(1,"") ;
        }else if(R.id.acti_unuse_dt_connection_tv == vId){//联系TA
            if(!mIsSelf){
                if(LoginHelper.isNotLogin(mContext)){
                    SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_CONNECTION);
                }else{
                    String userHxId = mDetails.getHxName() ;
                    SkipUtils.toMessageChatActivity(mActivity,userHxId) ;
                }
            }
        }else if(R.id.acti_unuse_dt_send_tv == vId){//我想要购买
            if(null == mDetails || mDetails.isNotEnableMark()){
                return ;
            }

            if(!mIsSelf){
                if(LoginHelper.isNotLogin(mContext)){
                    SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_BUY);
                }else{
                    toOrderConfirm() ;
                }
            }
        }else if(R.id.acti_unuse_dt_comment_more_tv == vId
                || R.id.acti_unuse_dt_comment_more_iv == vId){//更多评论
            Intent toMoIt = new Intent(mContext ,UnuseGoodCommentList.class) ;
            toMoIt.putExtra(SkipUtils.INTENT_UNUSE_ID ,mUnuseId) ;
            startActivityForResult(toMoIt,REQUEST_CODE_COMMENT_MORE) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mCommentLv.setSelection(0);
            mToTopIv.setVisibility(View.GONE);
        }
    }

    /**
     * 评论
     * @param commentLevel level
     * @param parentId parentId
     */
    private void addCommentToList(int commentLevel ,String parentId){
        if(null == mCommentPopup){
            mCommentPopup = new AddCommentPopup(mActivity, AddCommentPopup.COMMENT_TYPE_UNUSE
                    , findViewById(R.id.acti_unuse_good_dt_parent_lay), new AddCommentCallBack() {
                @Override
                public void commentCallBack(String commentNum) {
                    getUnuseCommentList();

                    mCommentCountTv.setText("留言（" + commentNum + "）") ;
                    mCommentNumTv.setText("" + commentNum ) ;

                    if(StringUtils.convertString2Count(commentNum) > 0){
                        mCommentNumTv.setVisibility(View.VISIBLE) ;
                    }else{
                        mCommentNumTv.setVisibility(View.GONE);
                    }
                }
            }) ;
        }

        mCommentPopup.displayDialog(mUnuseId, commentLevel, parentId);
    }

    /**
     * 跳转到确认订单
     */
    private void toOrderConfirm(){
        GoodOrderConfirmActivity.launch(mContext ,true,"","1",mDetails.getProductID()) ;
    }

    /**
     * 填充需求详细
     */
    private void updateNeedDetails(){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss() ;
        }

        if(null == mDetails){
            DialogUtils.showCustomViewDialog(mContext, "获取详情有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });
            return ;
        }

        mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
        if(mIsSelf || mDetails.isNotEnableMark()){
            mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));

            if(mIsSelf){
                mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
            }
        }

        boolean isSupported = mDetails.getIsLike() ;
        boolean isCollected = mDetails.getIsCollection() ;
        mSupportIv.setImageDrawable(getResources().getDrawable(isSupported
                ? R.mipmap.ic_support_checked
                : R.mipmap.ic_support_nomal));
        mCollectionIv.setImageDrawable(getResources().getDrawable(isCollected
                ? R.mipmap.ic_collection_checked
                : R.mipmap.ic_collection_nomal));

        int likeNum = mDetails.getLikeNumber() ;
        int commentCount = mDetails.getLeaveNumber() ;
        mSupportCountTv.setText(""+likeNum);
        mCommentNumTv.setText(""+commentCount);
        mSupportCountTv.setVisibility(likeNum > 0 ? View.VISIBLE : View.INVISIBLE);
        mCommentNumTv.setVisibility(commentCount > 0 ? View.VISIBLE : View.INVISIBLE);

        String commentNum = "留言（" + StringUtils.convertString2Count(mDetails.getLeaveNumber()+"") + ")";
        mCommentCountTv.setText(commentNum);

        mUnuseWv.loadUrl(mDetails.getH5Url()) ;
    }

    /**
     * 获取评论列表
     */
    private void getUnuseCommentList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Keyword" , StringUtils.convertNull(mUnuseId)) ;
        paramMap.put("Page" , "1") ;
        paramMap.put("PageSize" , "3") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_COMMENT_LIST, paramMap
                , new RequestListCallBack<UnuseCommentList>("getUnuseCommentList" ,mContext , UnuseCommentList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseCommentList> bean) {
                        mCommentList.clear();
                        mCommentList.addAll(bean) ;
                        mCommentAdapter.notifyDataSetChanged();
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
        if(mCommentList.size() == 0){
            mCommentNullTv.setVisibility(View.VISIBLE);
            mCommentMoreIv.setVisibility(View.GONE);
            mCommentMoreTv.setVisibility(View.GONE);
        }else{
            mCommentNullTv.setVisibility(View.GONE);
            mCommentMoreIv.setVisibility(View.VISIBLE);
            mCommentMoreTv.setVisibility(View.VISIBLE);
        }

        getUnuseGoodList() ;
    }

    /**
     * 获取相似闲置
     */
    private void getUnuseGoodList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mUnuseId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_LIST_SIMPLE, paramMap
                , new RequestListCallBack<UnuseGoodList>("getUnuseGoodList" , mContext , UnuseGoodList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseGoodList> bean) {
                        mUnuseGoodList.clear();
                        mUnuseGoodList.addAll(bean) ;
                        mUnuseAdapter.notifyDataSetChanged() ;
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
     * 获取详细
     */
    private void getUnuseDetails(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mUnuseId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_DETAILS, paramMap
                , new RequestObjectCallBack<UnuseDetails>("getUnuseDetails" , mContext , UnuseDetails.class) {
                    @Override
                    public void onSuccessResult(UnuseDetails bean) {
                        mDetails = bean ;
                        updateNeedDetails() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateNeedDetails() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateNeedDetails() ;
                    }
                });
    }

    /**
     * 点赞
     */
    private void goSupport(){
        if("".equals(SPUtils.newIntance(mContext).getToken())){
            SkipUtils.toLoginActivityForResult(UnuseGoodDetailsActivity.this ,REQUEST_CODE_SUPPORT) ;
            return ;
        }


        if (mLoadingDialog == null)
            mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("ProId" , mUnuseId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_SUPPORT, false, paramMap
                , new RequestObjectCallBack<SupportResult>("goSupport" , mContext ,SupportResult.class) {
                    @Override
                    public void onSuccessResult(SupportResult bean) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }

                        if(bean != null){
                            boolean isSupported = 2 == bean.AddOrDelete ;

                            int count = bean.LikeNumber ;
                            mSupportCountTv.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                            mSupportCountTv.setText(""+count);
                            mSupportIv.setImageDrawable(mContext.getResources().getDrawable(isSupported
                                    ? R.mipmap.ic_support_nomal
                                    : R.mipmap.ic_support_checked));
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 收藏
     */
    private void goCollection(){
        if("".equals(SPUtils.newIntance(mContext).getToken())){
            SkipUtils.toLoginActivityForResult(UnuseGoodDetailsActivity.this ,REQUEST_CODE_COLLECTION) ;
            return ;
        }

        if (mLoadingDialog == null)
            mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("ProId" , mUnuseId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_COLLECTION, false, paramMap
                , new RequestObjectCallBack<CollectionResult>("goCollection" , mContext ,CollectionResult.class) {
                    @Override
                    public void onSuccessResult(CollectionResult bean) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }

                        if(bean != null){
                            boolean isCollected = 2 == bean.AddOrDelete ;
                            mCollectionIv.setImageDrawable(mContext.getResources().getDrawable(isCollected
                                    ? R.mipmap.ic_collection_nomal
                                    : R.mipmap.ic_collection_checked));
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_SUPPORT == requestCode){
            if(RESULT_OK == resultCode){
                goSupport() ;
            }
        }else if(REQUEST_CODE_COLLECTION == requestCode){
            if(RESULT_OK == resultCode){
                goCollection() ;
            }
        }else if(REQUEST_CODE_CONNECTION == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelf = SPUtils.newIntance(mContext).getUserNumber().equals(mDetails.getCreateUserId()) ;
                if(mIsSelf){
                    mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    String userHxId = mDetails.getHxName() ;
                    SkipUtils.toMessageChatActivity(mActivity,userHxId) ;
                }
            }
        }else if(REQUEST_CODE_BUY == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelf = SPUtils.newIntance(mContext).getUserNumber().equals(mDetails.getCreateUserId()) ;
                if(mIsSelf){
                    mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    toOrderConfirm();
                }
            }
        }else if(REQUEST_CODE_COMMENT == requestCode){
            if(RESULT_OK == resultCode){
                addCommentToList(1 ,"") ;
            }
        }else if(REQUEST_CODE_COMMENT_REPLY == requestCode){
            if(RESULT_OK == resultCode){
                UnuseCommentList commentList = mCommentList.get(mCurPosition) ;
                if(commentList != null){
                    addCommentToList(2 ,commentList.getID()) ;
                }
            }
        }else if(REQUEST_CODE_COMMENT_MORE == requestCode){
            getUnuseCommentList() ;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getUnuseCommentList") ;
        NetUtils.cancelTag("getUnuseDetails") ;
        NetUtils.cancelTag("goSupport") ;
        NetUtils.cancelTag("goCollection") ;

    }

}
