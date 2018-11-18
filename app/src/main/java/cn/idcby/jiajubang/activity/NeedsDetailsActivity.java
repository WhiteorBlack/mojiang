package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.CollectionResult;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.NeedsBidSeller;
import cn.idcby.jiajubang.Bean.NeedsBondPayResult;
import cn.idcby.jiajubang.Bean.NeedsCommentList;
import cn.idcby.jiajubang.Bean.NeedsDetails;
import cn.idcby.jiajubang.Bean.SupportResult;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNeedBidSeller;
import cn.idcby.jiajubang.adapter.AdapterNeedsComment;
import cn.idcby.jiajubang.adapter.AdapterNomalImage;
import cn.idcby.jiajubang.interf.AddCommentCallBack;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
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
import cn.idcby.jiajubang.view.FlowLayout;
import cn.idcby.jiajubang.view.RvGridManagerItemDecoration;
import cn.idcby.jiajubang.view.StationaryListView;
import cn.idcby.jiajubang.view.TopBarRightPopup;

/**
 * Created on 2018/3/29.
 * 2018-05-04 09:49:16
 * 招标把保证金展示出来，需求暂时不显示预算价
 *
 * 2018-05-22 14:09:16
 * 如果自己发布的需求，底部的操作隐藏，换成 完成 按钮 。
 * 参与商家每次加载3个，下面加一个更多图标，点击加载下一页数据
 *
 * 2018-06-30 10:22:08
 * 改：点击参与商家的头像时，根据当前需求的类型，跳转不同位置
 * 目前是：闲置和商品需求，跳转到 用户主页
 *          服务和安装需求，跳转到 安装工、服务工主页
 */

public class NeedsDetailsActivity extends BaseActivity {
    private ImageView mUserIv ;
    private TextView mUserNameTv ;
    private TextView mSeeCountTv ;
    private TextView mLocationTv ;
    private TextView mTimeTv ;
    private TextView mMoneyTv ;
    private TextView mMoneyTipsTv ;
    private TextView mMoneyTipTv ;

    private TextView mTopTitleTv ;

    private TextView mTypeTv ;
    private TextView mTitleTv ;
    private TextView mContentTv ;
    private RecyclerView mPicLay ;

    private TextView mSellerCountTv ;
    private StationaryListView mSellerLv ;
    private View mSellerMoreIv ;
    private View mSellerMoreTv ;

    private FlowLayout mApplyLay ;

    private TextView mCommentCountTv ;
    private ListView mCommentLv ;

    private View mBotLay ;
    private View mFinishTv ;
    private ImageView mSupportIv ;
    private TextView mSupportTv ;
    private ImageView mCollectionIv ;
    private TextView mCollectionTv ;
    private TextView mConnTv ;
    private TextView mSendTv ;

    private String mNeedId ;
    private String mPayOfferId ;

    private NeedsDetails mDetails ;
    private int mNeedType ;
    private boolean mIsBonded = false ;//是否已经选标了
    private boolean mIsNotComit = false ;
    private boolean mIsBidType = false ;//是否是招标
    private boolean mIsSelfSend = false ;//当前需求是否是自己发布的
    private int mNeedOfferState = 1 ;//默认是1 2是已缴纳保证金未报价 3已报价

    private AdapterNomalImage mPicAdapter ;
    private List<ImageThumb> mPicList = new ArrayList<>() ;

    private int mSellerPage = 1 ;
    private List<NeedsBidSeller> mSellerList = new ArrayList<>() ;
    private AdapterNeedBidSeller mSellerAdapter ;

    private List<NeedsCommentList> mCommentList = new ArrayList<>() ;
    private AdapterNeedsComment mCommentAdapter ;
    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private LoadingDialog mLoadingDialog ;

    private static final int REQUEST_CODE_COLLECTION = 1001 ;
    private static final int REQUEST_CODE_SUPPORT = 1002 ;
    private static final int REQUEST_CODE_COMMENT = 1003 ;
    public static final int REQUEST_CODE_COMMENT_REPLY = 1004 ;
    private static final int REQUEST_CODE_PAY_BOND = 1005 ;
    private static final int REQUEST_CODE_PAY_BOND_RESULT = 1006 ;
    private static final int REQUEST_CODE_CONNECTION = 1007 ;
    private static final int REQUEST_CODE_SUBMIT = 1008 ;
    public static final int REQUEST_CODE_SUBMIT_BID_RESULT = 1009 ;

    //评论相关
    private int mCurPosition ;
    private AddCommentPopup mCommentPopup ;

    private TextView mFooterTv ;
    private View mToTopIv ;

    private TopBarRightPopup mRightPopup ;
    private View mRightView ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_needs_detail ;
    }

    @Override
    public void initView() {
        mNeedId = getIntent().getStringExtra(SkipUtils.INTENT_NEEDS_ID) ;
        mIsBonded = getIntent().getBooleanExtra(SkipUtils.INTENT_NEEDS_IS_BONDED,mIsBonded) ;

        mRightView = findViewById(R.id.acti_needs_dt_right_iv) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        mTopTitleTv = findViewById(R.id.acti_needs_details_title_tv) ;

        mCommentLv = findViewById(R.id.acti_needs_dt_lv) ;
        mBotLay = findViewById(R.id.acti_needs_dt_bot_lay) ;
        mFinishTv = findViewById(R.id.acti_needs_dt_finish_tv) ;
        View mSupportLay = findViewById(R.id.acti_needs_dt_support_lay) ;
        mSupportIv = findViewById(R.id.acti_needs_dt_support_iv) ;
        mSupportTv = findViewById(R.id.acti_needs_dt_support_tv) ;
        View mCollectionLay = findViewById(R.id.acti_needs_dt_collection_lay) ;
        mCollectionIv = findViewById(R.id.acti_needs_dt_collection_iv) ;
        mCollectionTv = findViewById(R.id.acti_needs_dt_collection_tv) ;
        View mCommentLay = findViewById(R.id.acti_needs_dt_comment_lay) ;
        mConnTv = findViewById(R.id.acti_needs_dt_connection_tv) ;
        mSendTv = findViewById(R.id.acti_needs_dt_send_tv) ;

        mFinishTv.setOnClickListener(this) ;
        mRightView.setOnClickListener(this) ;
        mSupportLay.setOnClickListener(this) ;
        mCollectionLay.setOnClickListener(this) ;
        mCommentLay.setOnClickListener(this) ;
        mConnTv.setOnClickListener(this) ;
        mSendTv.setOnClickListener(this) ;
        mCommentLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 5 && i + i1 >= i2){
                    getNeedCommentList() ;
                }

                ViewUtil.setViewVisible(mToTopIv ,i > 10);
            }
        });

        //initHeader
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_needs_dt , null) ;
        mCommentLv.addHeaderView(headerView) ;
        headerView.setOnClickListener(this);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mCommentLv.addFooterView(mFooterTv);

        mUserIv = headerView.findViewById(R.id.header_needs_dt_user_iv) ;
        mUserNameTv = headerView.findViewById(R.id.header_needs_dt_user_name_tv) ;
        mSeeCountTv = headerView.findViewById(R.id.header_needs_dt_see_count_tv) ;
        mLocationTv = headerView.findViewById(R.id.header_needs_dt_location_tv) ;
        mTimeTv = headerView.findViewById(R.id.header_needs_dt_time_tv) ;
        mMoneyTv = headerView.findViewById(R.id.header_needs_dt_money_tv) ;
        mMoneyTipsTv = headerView.findViewById(R.id.header_needs_dt_money_tips_tv) ;
        mMoneyTipTv= headerView.findViewById(R.id.header_needs_dt_money_tip_tv) ;

        mTypeTv = headerView.findViewById(R.id.header_needs_dt_type_tv) ;
        mTitleTv = headerView.findViewById(R.id.header_needs_dt_title_tv) ;
        mContentTv = headerView.findViewById(R.id.header_needs_dt_content_tv) ;
        mPicLay = headerView.findViewById(R.id.header_needs_dt_pic_lay) ;
        mApplyLay = findViewById(R.id.header_needs_dt_apply_lay) ;
        mSellerCountTv = headerView.findViewById(R.id.header_needs_dt_seller_count_tv) ;
        mSellerLv = headerView.findViewById(R.id.header_needs_dt_seller_lv) ;
        mSellerMoreIv = headerView.findViewById(R.id.header_needs_dt_seller_more_bot_iv) ;
        mSellerMoreTv = headerView.findViewById(R.id.header_needs_dt_seller_more_tv) ;
        View moreCommentIv = headerView.findViewById(R.id.header_needs_dt_comment_more_iv) ;
        View moreCommentTv = headerView.findViewById(R.id.header_needs_dt_comment_more_tv) ;
        mCommentCountTv = headerView.findViewById(R.id.header_needs_dt_comment_count_tv) ;
        mSellerMoreIv.setOnClickListener(this);
        mSellerMoreTv.setOnClickListener(this);
        moreCommentIv.setOnClickListener(this);
        moreCommentTv.setOnClickListener(this);
        mUserIv.setOnClickListener(this);

        //2018-05-28 18:11:24 苹果没写更多商家这个界面，为了统一，暂时隐藏这个
        mSellerMoreTv.setVisibility(View.GONE);

        mSellerAdapter = new AdapterNeedBidSeller(mContext , mSellerList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                NeedsBidSeller seller = mSellerList.get(position) ;
                //只能查看自己发布的报价详细
                if(seller != null){
                    if(0 == type){
                        if(mIsSelfSend || SPUtils.newIntance(mContext).getUserNumber().equals(seller.UserId)){
                            String offerId = seller.OfferId ;
                            Intent toCtIt = new Intent(mContext ,NeedsConfirmBidActivity.class) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_ID , mNeedId) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_OFFER_ID , offerId) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_IS_SELF , mIsSelfSend) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_IS_BONDED ,mIsBonded) ;
                            startActivityForResult(toCtIt,REQUEST_CODE_SUBMIT_BID_RESULT) ;
                        }
                    }else if(1 == type){
                        String userId = seller.getUserId() ;
                        int needStyle = mDetails.getCategoryStyle() ;
                        if(SkipUtils.NEED_STYLE_TYPE_SERVER == needStyle
                                || SkipUtils.NEED_STYLE_TYPE_INSTALL == needStyle){//服务需求、安装需求
                            ServerDetailActivity.launch(mContext ,userId,false) ;
                        }else if(SkipUtils.NEED_STYLE_TYPE_GOOD == needStyle
                                || SkipUtils.NEED_STYLE_TYPE_UNUSE == needStyle){//商品需求、闲置需求
                            SkipUtils.toOtherUserInfoActivity(mContext ,userId) ;
                        }
                    }
                }
            }
        }) ;
        mSellerLv.setAdapter(mSellerAdapter) ;
    }

    @Override
    public void initData() {
        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.show() ;

        mCommentAdapter = new AdapterNeedsComment(mCommentList, mActivity, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    mCurPosition = position ;
                    if(LoginHelper.isNotLogin(mContext)){
                        SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_COMMENT_REPLY);
                    }else{
                        NeedsCommentList commentList = mCommentList.get(position) ;
                        if(commentList != null){
                            addCommentToList(2 ,commentList.getNeedCommentID()) ;
                        }
                    }
                }
            }
        }) ;
        mCommentLv.setAdapter(mCommentAdapter) ;

        getNeedsDetails();
        getNeedCommentList() ;
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_needs_dt_right_iv == vId){
//
//            String imgUrl = "" ;
//            if(mDetails.getAlbumsList() != null && mDetails.getAlbumsList().size() > 0){
//                imgUrl = mDetails.getAlbumsList().get(0).getThumbImgUrl() ;
//            }
//            ShareUtils.shareWeb(mActivity,mDetails.getNeedTitle(),mDetails.getH5Url(),imgUrl,"");

            showRightPopup();

        }else if(R.id.acti_needs_dt_support_lay == vId){//赞
            goSupport() ;
        }else if(R.id.acti_needs_dt_collection_lay == vId){//收藏
            goCollection() ;
        }else if(R.id.acti_needs_dt_comment_lay == vId){//留言
            if (LoginHelper.isNotLogin(mContext)) {
                SkipUtils.toLoginActivityForResult(NeedsDetailsActivity.this , REQUEST_CODE_COMMENT);
                return;
            }

            addCommentToList(1,"");
        }else if(R.id.acti_needs_dt_connection_tv == vId){//联系TA

            toConnectionUser() ;

        }else if(R.id.acti_needs_dt_send_tv == vId){//报价
            if(!mIsNotComit && !mIsSelfSend){
                if(LoginHelper.isNotLogin(mContext)){
                    SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_SUBMIT) ;
                }else{
                    if(3 == mNeedOfferState){//报过价了

                    }else{
                        if(mIsBidType){//招标报价要求先交钱，再填信息
                            if(2 == mNeedOfferState){
                                Intent toCtIt = new Intent(mContext ,NeedsConfirmBidActivity.class) ;
                                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_ID , mNeedId) ;
                                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_TYPE , mNeedType) ;//招标是2
                                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_OFFER_ID , mPayOfferId) ;
                                startActivityForResult(toCtIt ,REQUEST_CODE_PAY_BOND_RESULT);
                            }else{
                                DialogUtils.showCustomViewDialog(mContext, "温馨提示", "需要缴纳保证金，是否前往"
                                        , null, "去缴纳", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();

                                                payBondMoney() ;
                                            }
                                        }, "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                            }
                        }else{
                            Intent toCtIt = new Intent(mContext ,NeedsConfirmBidActivity.class) ;
                            toCtIt.putExtra(SkipUtils.INTENT_NEEDS_ID , mNeedId) ;
                            startActivityForResult(toCtIt ,REQUEST_CODE_PAY_BOND_RESULT) ;
                        }
                    }
                }
            }
        }else if(R.id.header_needs_dt_seller_more_tv == vId){//更多商家
            Intent toSlIt = new Intent(mContext , NeedsBidSellerActivity.class) ;
            toSlIt.putExtra(SkipUtils.INTENT_NEEDS_ID ,mNeedId) ;
            toSlIt.putExtra(SkipUtils.INTENT_NEEDS_IS_BONDED ,mIsBonded) ;
            toSlIt.putExtra(SkipUtils.INTENT_NEEDS_IS_SELF ,mIsSelfSend) ;
            startActivity(toSlIt) ;
        }else if(R.id.header_needs_dt_comment_more_tv == vId
                || R.id.header_needs_dt_comment_more_iv == vId){//更多评论

        }else if(R.id.header_needs_dt_user_iv == vId){//发布人头像
            if(mDetails != null){
                SkipUtils.toOtherUserInfoActivity(mContext ,mDetails.getCreateUserId());
            }
        }else if(R.id.acti_needs_dt_finish_tv == vId){//完成需求
            DialogUtils.showCustomViewDialog(mContext, "完成需求", "完成该需求？", null
                    , "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            finishNeed(mNeedId) ;
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }else if(R.id.header_needs_dt_seller_more_bot_iv == vId){//商家列表下面的箭头
            getSellerList() ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mCommentLv.setSelection(0);
            mToTopIv.setVisibility(View.GONE);
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
            String imgUrl = "" ;
            if(mDetails.getAlbumsList() != null && mDetails.getAlbumsList().size() > 0){
                imgUrl = mDetails.getAlbumsList().get(0).getThumbImgUrl() ;
            }
            ShareUtils.shareWeb(mActivity,mDetails.getNeedTitle(),mDetails.getH5Url(),imgUrl,"");
    }

    private void toConnectionUser(){
        if(!mIsSelfSend){
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_CONNECTION);
            }else{
                String userHxId = mDetails.getHxName() ;
                SkipUtils.toMessageChatActivity(mActivity,userHxId) ;
            }
        }
    }

    /**
     * 评论
     * @param commentLevel level
     * @param parentId parentId
     */
    private void addCommentToList(int commentLevel ,String parentId){
        if(null == mCommentPopup){
            mCommentPopup = new AddCommentPopup(mActivity, AddCommentPopup.COMMENT_TYPE_NEED
                    , findViewById(R.id.acti_needs_dt_parent_lay), new AddCommentCallBack() {
                @Override
                public void commentCallBack(String commentNum) {
                    mCurPage = 1 ;
                    getNeedCommentList();

                    if(!"".equals(StringUtils.convertNull(commentNum))){
                        mCommentCountTv.setText("留言（" + commentNum + "）") ;
                    }
                }
            }) ;
        }

        mCommentPopup.displayDialog(mNeedId, commentLevel, parentId);
    }

    /**
     * 填充需求详细
     */
    private void updateNeedDetails(){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss() ;
        }

        if(null == mDetails){
            DialogUtils.showCustomViewDialog(mContext, "网络异常，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });
            return ;
        }

        mPayOfferId = mDetails.getNeedOfferId() ;
        mNeedOfferState = mDetails.getNeedStatus() ;
        mIsSelfSend = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
        mIsNotComit = (2 != mDetails.getOrderStatus()) ;
        mIsBonded = mDetails.isBid() ;

        updateBotStyle() ;

        if(3 == mNeedOfferState){
            mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
        }

        boolean isSupported = 1 == mDetails.IsLike ;
        boolean isCollected = 1 == mDetails.IsCollection ;
        mSupportIv.setImageDrawable(getResources().getDrawable(isSupported
                ? R.mipmap.ic_support_checked
                : R.mipmap.ic_support_nomal));
        mCollectionIv.setImageDrawable(getResources().getDrawable(isCollected
                ? R.mipmap.ic_collection_checked
                : R.mipmap.ic_collection_nomal));

        int offerNumber = mDetails.getOfferNumber() ;

        String userImgUrl = mDetails.HeadIcon ;
        String userName = mDetails.RealName ;
//        String clickNum = mDetails.ClickNumber + "浏览" ;
        String commentNum = "留言（" + mDetails.CommentNumber + "）";
        String location = mDetails.Position ;
        String time = mDetails.ReleaseTime ;

        //2018-05-28 18:12:02 跟苹果一致，暂时不显示更多按钮
//        mSellerMoreTv.setVisibility(offerNumber > 0 ? View.VISIBLE : View.GONE);

        //认证信息
        String applyText = mDetails.getApplyText() ;

        if(mApplyLay.getChildCount() > 0){
            mApplyLay.removeAllViews() ;
        }

        if(applyText.contains(",")){
            String[] applyStates = applyText.split(",") ;
            for(String state : applyStates){
                if(SkipUtils.APPLY_TYPE_CAR.equals(state)){
                    continue ;
                }

                View itemLay = LayoutInflater.from(mContext).inflate(R.layout.dt_apply_item ,null) ;
                ImageView icIv = itemLay.findViewById(R.id.dt_apply_icon_iv) ;
                TextView nameTv = itemLay.findViewById(R.id.dt_apply_name_tv) ;

                if(SkipUtils.APPLY_TYPE_PERSON_NO.equals(state)){
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.geren));
                    nameTv.setText(getResources().getString(R.string.apply_text_person_no)) ;
                }else if(SkipUtils.APPLY_TYPE_PERSON.equals(state)){
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.geren));
                    nameTv.setText(getResources().getString(R.string.apply_text_person)) ;
                }else if(SkipUtils.APPLY_TYPE_FACTORY.equals(state)){
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.fangzi));
                    nameTv.setText(getResources().getString(R.string.apply_text_factory)) ;
                }else if(SkipUtils.APPLY_TYPE_COMPANY.equals(state)){
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.baoxiao));
                    nameTv.setText(getResources().getString(R.string.apply_text_company)) ;
                }else if(SkipUtils.APPLY_TYPE_INSTALL.equals(state)){
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.anzhuang));
                    nameTv.setText(getResources().getString(R.string.apply_text_install)) ;
                }else if(SkipUtils.APPLY_TYPE_SERVER.equals(state)){
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.gongsi));
                    nameTv.setText(getResources().getString(R.string.apply_text_server)) ;
                }else if(SkipUtils.APPLY_TYPE_STORE.equals(state)){
                    icIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_apply_store));
                    nameTv.setText(getResources().getString(R.string.apply_text_store)) ;
                }

                mApplyLay.addView(itemLay) ;
            }
        }else{
            if(SkipUtils.APPLY_TYPE_PERSON.equals(applyText)
                    || SkipUtils.APPLY_TYPE_PERSON_NO.equals(applyText)){//通过了个人认证
                View perLay = LayoutInflater.from(mContext).inflate(R.layout.dt_apply_item ,null) ;
                ImageView icIv = perLay.findViewById(R.id.dt_apply_icon_iv) ;
                TextView nameTv = perLay.findViewById(R.id.dt_apply_name_tv) ;

                icIv.setImageDrawable(getResources().getDrawable(R.mipmap.geren));
                if(SkipUtils.APPLY_TYPE_PERSON_NO.equals(applyText)){
                    nameTv.setText(getResources().getString(R.string.apply_text_person_no)) ;
                }else{
                    nameTv.setText(getResources().getString(R.string.apply_text_person)) ;
                }
                mApplyLay.addView(perLay) ;
            }
        }

        mSellerCountTv.setText(""+ offerNumber);
        mCommentCountTv.setText(commentNum);
        mUserNameTv.setText(userName);
//        mSeeCountTv.setText(clickNum);
        mLocationTv.setText(location);
        mTimeTv.setText(time);
        GlideUtils.loaderUser(userImgUrl , mUserIv) ;

        mNeedType = mDetails.getTypeId() ;
        String needTitle = mDetails.getNeedTitle() ;
        String needContent = mDetails.getNeedExplain() ;

        mTitleTv.setText(needTitle);
        mContentTv.setText(needContent);
        if(1 == mNeedType){
            mTopTitleTv.setText("需求详情");

            mIsBidType = false ;
            mTypeTv.setText("需求");
            mTypeTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_needs_xq_bg));
        }else if(2 == mNeedType){
            float money = StringUtils.convertString2Float(mDetails.getBidBond()) ;
            if(money > 0){
                mMoneyTv.setText("" + money);
                mMoneyTv.setVisibility(View.VISIBLE);
                mMoneyTipTv.setVisibility(View.VISIBLE);
                mMoneyTipsTv.setVisibility(View.VISIBLE);
                mMoneyTipTv.setText("保证金");
            }

            mTopTitleTv.setText("招标详情");

            mIsBidType = true ;
            mTypeTv.setText("招标");
            mTypeTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_needs_zb_bg));
        }

        mPicLay.setNestedScrollingEnabled(false);
        mPicLay.setFocusable(false);

        mPicList.clear();
        mPicList.addAll(mDetails.getAlbumsList()) ;

        if(null == mPicAdapter){
            mPicLay.setLayoutManager(new GridLayoutManager(mContext ,3)) ;
            mPicLay.addItemDecoration(new RvGridManagerItemDecoration(mContext,ResourceUtils.dip2px(mContext ,5)));

            int offset = ResourceUtils.dip2px(mContext ,15) * 2 + ResourceUtils.dip2px(mContext ,5) * 2 ;
            mPicAdapter = new AdapterNomalImage(mActivity ,mPicList,offset ,3F) ;
            mPicLay.setAdapter(mPicAdapter);
        }else{
            mPicAdapter.notifyDataSetChanged() ;
        }

        mSellerPage = 1 ;
        getSellerList();
    }

    private void updateBotStyle(){
        mFinishTv.setVisibility(View.GONE);
        if(mIsSelfSend){
            //已选标或服务中可以完成需求
            if(mDetails.getOrderStatus() == 3 || mDetails.getOrderStatus() == 4){
                mFinishTv.setVisibility(View.VISIBLE) ;
                mBotLay.setVisibility(View.INVISIBLE);
            }else{
                mBotLay.setVisibility(View.GONE);
            }
        }else{
            mBotLay.setVisibility(View.VISIBLE);
        }

        mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right));
        mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left));

        if(mIsSelfSend || mIsNotComit){
            mSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
            if(mIsSelfSend){
                mConnTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
            }
        }
    }

    /**
     * 填充招标商家
     * @param sellerList list
     */
    private void updateNeedSeller(final List<NeedsBidSeller> sellerList){
        if(null == sellerList){
            mSellerMoreIv.setVisibility(View.GONE);
            return ;
        }

        if(1 == mSellerPage){
            mSellerList.clear();
        }

        if(sellerList.size() == 0
                || mDetails.getOfferNumber() == 0
                || mDetails.getOfferNumber() <= sellerList.size()){
            mSellerMoreIv.setVisibility(View.GONE);
        }else{
            mSellerMoreIv.setVisibility(View.VISIBLE);
            mSellerPage ++ ;
        }

        mSellerList.addAll(sellerList) ;
        mSellerAdapter.setIsSelf(mIsSelfSend) ;
    }

    /**
     * 招标--报价--缴纳保证金
     */
    private void payBondMoney(){
        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" , mNeedId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_ADD_BID, paramMap
                , new RequestObjectCallBack<NeedsBondPayResult>("payBondMoney" , mContext , NeedsBondPayResult.class) {
                    @Override
                    public void onSuccessResult(NeedsBondPayResult bean) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }

                        if(bean != null){
                            String moneys = bean.PayableAmount ;
                            mPayOfferId = bean.OrderID ;

                            //跳转到付款界面
                            Intent toPyIt = new Intent(mContext , ActivityBondPay.class) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_MONEY ,moneys) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_ID ,mPayOfferId) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_CODE ,bean.OrderCode) ;
                            toPyIt.putExtra(SkipUtils.INTENT_PAY_ORDER_TYPE , "" + SkipUtils.PAY_ORDER_TYPE_BOND_REQ_BID) ;
                            startActivityForResult(toPyIt ,REQUEST_CODE_PAY_BOND);
                        }else{
                            ToastUtils.showErrorToast(mContext , "缴纳失败，请到我的招标进行缴纳") ;
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
                        ToastUtils.showErrorToast(mContext , "缴纳失败") ;
                    }
                });
    }


    /**
     * 获取评论列表
     */
    private void getNeedCommentList(){
        mIsLoading  = true ;

        if(1 == mCurPage){
            mToTopIv.setVisibility(View.GONE);
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Keyword" , StringUtils.convertNull(mNeedId)) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_COMMENT_LIST, paramMap
                , new RequestListCallBack<NeedsCommentList>("getNeedCommentList" ,mContext , NeedsCommentList.class) {
                    @Override
                    public void onSuccessResult(List<NeedsCommentList> bean) {
                        if(1 == mCurPage){
                            mCommentList.clear();
                        }

                        mCommentList.addAll(bean) ;
                        mCommentAdapter.notifyDataSetChanged();

                        if(bean.size() < 10){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        finishReqeust() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishReqeust() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishReqeust() ;
                    }
                });
    }

    private void finishReqeust(){
        mIsLoading = false ;

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mCommentList.size() == 0){
            mFooterTv.setText("暂无留言");
        }
    }

    /**
     * 获取需求详细
     */
    private void getNeedsDetails(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mNeedId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_DETAILS, paramMap
                , new RequestObjectCallBack<NeedsDetails>("getNeedsDetails" , mContext , NeedsDetails.class) {
                    @Override
                    public void onSuccessResult(NeedsDetails bean) {
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
     * 获取需求商家列表
     */
    private void getSellerList(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("NeedId" , StringUtils.convertNull(mNeedId)) ;
        paramMap.put("Page" , "" + mSellerPage) ;
        paramMap.put("PageSize" , "6") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_SELLER_LIST, paramMap
                , new RequestListCallBack<NeedsBidSeller>("getSellerList" , mContext ,NeedsBidSeller.class) {
                    @Override
                    public void onSuccessResult(List<NeedsBidSeller> bean) {
                        updateNeedSeller(bean) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateNeedSeller(null) ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateNeedSeller(null) ;
                    }
                });
    }


    /**
     * 点赞
     */
    private void goSupport(){
        if("".equals(SPUtils.newIntance(mContext).getToken())){
            SkipUtils.toLoginActivityForResult(NeedsDetailsActivity.this ,REQUEST_CODE_SUPPORT) ;
            return ;
        }

        if (mLoadingDialog == null)
            mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , mNeedId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_SUPPORT, false, paramMap
                , new RequestObjectCallBack<SupportResult>("goSupport" , mContext ,SupportResult.class) {
                    @Override
                    public void onSuccessResult(SupportResult bean) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }

                        if(bean != null){
                            boolean isSupported = 2 == bean.AddOrDelete ;

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
            SkipUtils.toLoginActivityForResult(NeedsDetailsActivity.this ,REQUEST_CODE_COLLECTION) ;
            return ;
        }

        if (mLoadingDialog == null)
            mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , mNeedId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_COLLECTION, false, paramMap
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

    /**
     * 完成需求
     * @param optionId id
     */
    private void finishNeed(String optionId){
        if (mLoadingDialog == null)
            mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("Code" ,optionId) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_FINISH, paramMap
                , new RequestObjectCallBack<String>("finishNeed" ,mContext ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mLoadingDialog.dismiss();

                        mIsNotComit = false ;
                        mFinishTv.setVisibility(View.GONE) ;
                        mBotLay.setVisibility(View.GONE);
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLoadingDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_SUPPORT == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelfSend = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
                updateBotStyle() ;

                mLoadingDialog.show() ;
                goSupport() ;
            }
        }else if(REQUEST_CODE_COLLECTION == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelfSend = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
                updateBotStyle() ;

                mLoadingDialog.show() ;
                goCollection() ;
            }
        }else if(REQUEST_CODE_COMMENT == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelfSend = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
                updateBotStyle() ;


                addCommentToList(1,"");
            }
        }else if(REQUEST_CODE_COMMENT_REPLY == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelfSend = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
                updateBotStyle() ;

                NeedsCommentList commentList = mCommentList.get(mCurPosition) ;
                if(commentList != null){
                    addCommentToList(2 ,commentList.getNeedCommentID()) ;
                }
            }
        }else if(REQUEST_CODE_PAY_BOND == requestCode){//报价保证金提交结束
            if(RESULT_OK == resultCode){
                Intent toCtIt = new Intent(mContext ,NeedsConfirmBidActivity.class) ;
                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_ID , mNeedId) ;
                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_TYPE , mNeedType) ;//招标是2
                toCtIt.putExtra(SkipUtils.INTENT_NEEDS_OFFER_ID , mPayOfferId) ;
                startActivityForResult(toCtIt ,REQUEST_CODE_PAY_BOND_RESULT);
            }
        }else if(REQUEST_CODE_PAY_BOND_RESULT == requestCode){//报价结束，
            mLoadingDialog.show() ;

            getNeedsDetails();
            getNeedCommentList() ;
        }else if(REQUEST_CODE_CONNECTION == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelfSend = LoginHelper.isSelf(mContext ,mDetails.getCreateUserId()) ;
                updateBotStyle() ;


                toConnectionUser() ;
            }
        }else if(REQUEST_CODE_SUBMIT == requestCode){//登录成功
            if(RESULT_OK == resultCode){//更新状态
                mLoadingDialog.show() ;

                getNeedsDetails();
                getNeedCommentList() ;
            }
        }else if(REQUEST_CODE_SUBMIT_BID_RESULT == requestCode){//编辑报价了（选中了报价）
            if(RESULT_OK == resultCode){//更新状态
                mLoadingDialog.show() ;

                getNeedsDetails();
                getNeedCommentList() ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getNeedCommentList") ;
        NetUtils.cancelTag("getNeedsDetails") ;
        NetUtils.cancelTag("getSellerList") ;
        NetUtils.cancelTag("goSupport") ;
        NetUtils.cancelTag("goCollection") ;

    }

}
