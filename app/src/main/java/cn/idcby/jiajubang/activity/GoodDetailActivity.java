package cn.idcby.jiajubang.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.CollectionResult;
import cn.idcby.jiajubang.Bean.GoodComment;
import cn.idcby.jiajubang.Bean.GoodDetails;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterGoodDetailsGood;
import cn.idcby.jiajubang.adapter.AdapterGoodParam;
import cn.idcby.jiajubang.adapter.AdapterGoodService;
import cn.idcby.jiajubang.adapter.GoodDetailsCommentAdapter;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.FlowLayout;
import cn.idcby.jiajubang.view.MyScrollView;
import cn.idcby.jiajubang.view.TopBarRightPopup;
import cn.idcby.jiajubang.view.WebViewNoScroll;

/**
 * 商品详细
 * 仿淘宝原生界面
 * 2018-08-15 15:40:01
 */

public class GoodDetailActivity extends BaseActivity {
    private MyScrollView mScrolView ;
    private Banner mImageBanner ;
    private TextView mPriceTv ;
    private TextView mPriceOldTv ;
    private TextView mNameTv ;
    private TextView mSaleCountTv ;
    private TextView mStoreAreaTv ;
    private TextView mDeliveTypeTv ;//配送方式
    private TextView mGoodServiceTv ;
    private TextView mGoodSpecTv ;
    private TextView mGoodSpecCountTv ;
    private TextView mGoodParamTv ;
    private TextView mCommentCountTv ;
    private TextView mCommentAllTv ;
    private ListView mCommentLv ;
    private GridView mSampleGv ;
    private View mSampleHotTv ;
    private GridView mSampleHotGv ;

    private ImageView mStoreLogoIv ;
    private TextView mStoreNameTv ;
    private TextView mStoreSaleTv ;

    private View mDetailsWvTv ;
    private WebViewNoScroll mWebView;

    //图片
    private List<ImageThumb> mGoodImgList = new ArrayList<>() ;

    //服务、参数
    private AdapterGoodService mServiceAdapter ;
    private AdapterGoodParam mParamAdapter ;
    private List<GoodDetails.GoodService> mServiceList = new ArrayList<>() ;
    private List<GoodDetails.GoodParam> mParamList = new ArrayList<>() ;

    //评论
    private List<GoodComment> mGoodCommentList = new ArrayList<>() ;
    private GoodDetailsCommentAdapter mCommentAdapter ;

    //相关产品、推荐
    private List<GoodList> mSampleGoodList = new ArrayList<>() ;
    private AdapterGoodDetailsGood mSampleAdapter ;
    private List<GoodList> mSampleHotGoodList = new ArrayList<>() ;
    private AdapterGoodDetailsGood mSampleHotAdapter ;

    private LinearLayout mService;//客服
    private LinearLayout mCollection;//shoucang收藏
    private ImageView mCollectionIv;//shoucang收藏
    private LinearLayout mShop;//店铺
    private TextView mCarts;//购物车
    private TextView mBuy;//立即购买

    private boolean mIsFromStore = false ;
    private String mProductId;//当前商品的产品id
    private String mSkuId;//当前商品的产品SkuId，可为空
    private GoodDetails mDetails ;
    //是否是自己的店铺 2018-09-15 19:58:20 跟IOS一致，暂时可以让自己买自己的商品，把赋值的地方注释了
    private boolean mIsSelf = false ;

    private List<GoodDetails.GoodSkuList> mAllGoodList = new ArrayList<>();

    private LoadingDialog mLoadingDialog;

    //规格相关
    private View mGgParentLay ;
    private View mGgChildLay ;

    private View mGgSubmitTv ;
    private View mGgBotOpLay ;
    private View mGgAddCartTv ;
    private View mGgButNowTv ;

    private FlowLayout mGuigeFlowLay ;
    private ImageView mGgGoodIv ;//商品图片
    private TextView mGgGoodPriceTv ;//售价
    private TextView mGgGoodGgTv ;//规格
    private TextView mGgGoodStockTv ;//库存
    private EditText mCountEv;
    private boolean mIsSpec = false ;//是否是点击规格了
    private boolean mIsAddToCart = true ;//点击规格里面操作，默认加入购物车
    private boolean mIsLoadingAnim = false ;//是否正在显示动画

    private GoodDetails.GoodSkuList mChoosedGood ;
    private TextView mSelectedGgTv ;

    private static final int REQUEST_CODE_CUSTOMER = 1000 ;
    private static final int REQUEST_CODE_COLLECTION = 1001 ;
    private static final int REQUEST_CODE_ADD_CART = 1002 ;
    private static final int REQUEST_CODE_BUY_NOW = 1003 ;
    private static final int REQUEST_CODE_CART = 1004 ;
    private static final int REQUEST_CODE_BUT_SUBMIT = 1005 ;

    private View mRightView ;
    private TopBarRightPopup mRightPopup ;

    //滑动相关
    private View mStatusView ;
    private View mTitleLay ;
    private ImageView mTitleGoodIv ;
    private View mNavOneIv ;
    private TextView mNavOneTv ;
    private View mNavTwoIv ;
    private TextView mNavTwoTv ;
    private View mNavThreeIv ;
    private TextView mNavThreeTv ;
    private View mNavFourIv ;
    private TextView mNavFourTv ;

    private int mScrollY ;
    private int[] mCommentScr = new int[2] ;
    private int[] mDetailsScr = new int[2] ;
    private int[] mGoodScr = new int[2] ;
    private int mLimitY = 0 ;
    private int mScreenWidth = 0 ;
    private float mTopAlpha = 0F ;
    private int mCurType = 0 ;

    private View mParentView ;
    private PopupWindow mServicePup ;
    private PopupWindow mParamePup ;



    @Override
    public int getLayoutID() {
        return R.layout.activity_good_detail;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTransparentForImageView(mActivity ,null);

        EventBus.getDefault().register(this);

        mIsFromStore = getIntent().getBooleanExtra(SkipUtils.INTENT_GOOD_FROM_STORE,mIsFromStore) ;
        mProductId = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_GOOD_ID)) ;
        mSkuId = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_GOOD_SKU_ID)) ;

        mParentView = findViewById(R.id.acti_good_details_parent_lay) ;
        mStatusView = findViewById(R.id.acti_good_details_top_status_view) ;
        mTitleLay = findViewById(R.id.acti_good_details_title_lay) ;
        mTitleGoodIv = findViewById(R.id.acti_good_details_top_good_iv) ;
        View mNavOneLay = findViewById(R.id.acti_good_details_top_nav_one_lay) ;
        mNavOneIv = findViewById(R.id.acti_good_details_top_nav_one_iv) ;
        mNavOneTv = findViewById(R.id.acti_good_details_top_nav_one_tv) ;
        View mNavTwoLay = findViewById(R.id.acti_good_details_top_nav_two_lay) ;
        mNavTwoIv = findViewById(R.id.acti_good_details_top_nav_two_iv) ;
        mNavTwoTv = findViewById(R.id.acti_good_details_top_nav_two_tv) ;
        View mNavThreeLay = findViewById(R.id.acti_good_details_top_nav_three_lay) ;
        mNavThreeIv = findViewById(R.id.acti_good_details_top_nav_three_iv) ;
        mNavThreeTv = findViewById(R.id.acti_good_details_top_nav_three_tv) ;
        View mNavFourLay = findViewById(R.id.acti_good_details_top_nav_four_lay) ;
        mNavFourIv = findViewById(R.id.acti_good_details_top_nav_four_iv) ;
        mNavFourTv = findViewById(R.id.acti_good_details_top_nav_four_tv) ;
        mNavOneLay.setOnClickListener(this);
        mNavTwoLay.setOnClickListener(this);
        mNavThreeLay.setOnClickListener(this);
        mNavFourLay.setOnClickListener(this);

        mScreenWidth = ResourceUtils.getScreenWidth(mContext) ;
        int statusHeight = ResourceUtils.getStatusBarHeight(mContext) ;
        mStatusView.getLayoutParams().height = statusHeight ;
        mLimitY = statusHeight
                + ResourceUtils.dip2px(mContext
                    ,ResourceUtils.getXmlDef(mContext ,R.dimen.titleBar_height))
                + ResourceUtils.dip2px(mContext,35) ;

        mScrolView = findViewById(R.id.acti_good_details_sv) ;
        mImageBanner = findViewById(R.id.acti_good_details_top_banner) ;
        mPriceTv = findViewById(R.id.acti_good_details_price_tv) ;
        mPriceOldTv = findViewById(R.id.acti_good_details_price_old_tv) ;
        mNameTv = findViewById(R.id.acti_good_details_name_tv) ;
        mSaleCountTv = findViewById(R.id.acti_good_details_sale_count_tv) ;
        mStoreAreaTv = findViewById(R.id.acti_good_details_store_address_tv) ;
        mDeliveTypeTv = findViewById(R.id.acti_good_details_delive_type_tv) ;
        mGoodServiceTv = findViewById(R.id.acti_good_details_service_tv) ;
        View mGoodSpecLay = findViewById(R.id.acti_good_details_spec_lay) ;
        mGoodSpecTv = findViewById(R.id.acti_good_details_spec_tv) ;
        mGoodSpecCountTv = findViewById(R.id.acti_good_details_spec_count_tv) ;
        mGoodParamTv = findViewById(R.id.acti_good_details_params_tv) ;
        mCommentCountTv = findViewById(R.id.acti_good_details_comment_count_tv) ;
        mCommentAllTv = findViewById(R.id.acti_good_details_comment_all_tv) ;
        mCommentLv = findViewById(R.id.acti_good_details_comment_lv) ;
        View toStoreTv = findViewById(R.id.acti_good_details_store_index_tv) ;
        mStoreNameTv = findViewById(R.id.acti_good_details_store_name_tv) ;
        mStoreSaleTv = findViewById(R.id.acti_good_details_store_sale_tv) ;
        mStoreLogoIv = findViewById(R.id.acti_good_details_store_logo_iv) ;
        mDetailsWvTv = findViewById(R.id.acti_good_details_good_wv_tv) ;
        mWebView = findViewById(R.id.acti_good_details_good_wv) ;
        mSampleGv = findViewById(R.id.acti_good_details_store_good_gv) ;
        mSampleHotTv = findViewById(R.id.acti_good_details_hot_good_tv) ;
        mSampleHotGv = findViewById(R.id.acti_good_details_hot_good_gv) ;
        mScrolView.setOnScrollChangedListener(mScrollListener);
        mStoreLogoIv.setOnClickListener(this);
        mStoreNameTv.setOnClickListener(this);
        toStoreTv.setOnClickListener(this);
        mGoodServiceTv.setOnClickListener(this);
        mGoodSpecLay.setOnClickListener(this);
        mGoodParamTv.setOnClickListener(this);
        mCommentAllTv.setOnClickListener(this);

        mSampleGv.setFocusable(false);
        mSampleHotGv.setFocusable(false);
        mWebView.setFocusable(false);
        mCommentLv.setFocusable(false);

        mImageBanner.getLayoutParams().height = mScreenWidth ;
        //设置banner动画效果
        mImageBanner.setBannerAnimation(Transformer.Default);
        mImageBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        });
        //设置轮播时间
        mImageBanner.isAutoPlay(false) ;
        mImageBanner.setImageLoader(new BannerImageLoader(false)) ;

        mService = findViewById(R.id.ll_customerservice);
        mCollection = findViewById(R.id.ll_collection);
        mCollectionIv = findViewById(R.id.acti_good_details_collection_iv);
        mShop = findViewById(R.id.ll_shop);
        mCarts = findViewById(R.id.add_carts_tv);
        mBuy = findViewById(R.id.add_buys_tv);

        //规格
        ImageView mCloaseIv = findViewById(R.id.acti_good_details_gg_child_close_iv) ;
        mGgParentLay = findViewById(R.id.acti_good_details_gg_parent_lay);
        mGgChildLay = findViewById(R.id.acti_good_details_gg_child_lay);
        mGuigeFlowLay = findViewById(R.id.acti_good_details_gg_flow_lay) ;
        mGgGoodIv = findViewById(R.id.acti_good_details_gg_child_good_iv) ;
        mGgGoodPriceTv = findViewById(R.id.acti_good_details_gg_child_good_money_tv) ;
        mGgGoodGgTv = findViewById(R.id.acti_good_details_gg_child_good_gg_tv) ;
        mGgGoodStockTv = findViewById(R.id.acti_good_details_gg_child_good_stock_tv) ;
        mCountEv = findViewById(R.id.acti_good_details_gg_count_ev) ;
        View addView = findViewById(R.id.acti_good_details_gg_count_add_iv) ;
        View reduceView = findViewById(R.id.acti_good_details_gg_count_reduce_iv) ;
        mGgSubmitTv = findViewById(R.id.acti_good_details_gg_submit_tv) ;
        mRightView = findViewById(R.id.goods_detail_share_iv) ;
        View cartView = findViewById(R.id.goods_detail_cart_iv) ;
        mGgBotOpLay = findViewById(R.id.acti_good_details_gg_submit_lay) ;
        mGgAddCartTv = findViewById(R.id.bot_gg_add_carts_tv) ;
        mGgButNowTv = findViewById(R.id.bot_gg_add_buys_tv) ;

        mGgChildLay.getLayoutParams().height = (int) (ResourceUtils.getScreenHeight(mContext) * 0.8F);
        mCountEv.clearFocus() ;
        mCountEv.setOnClickListener(this);
        mGuigeFlowLay.setOnClickListener(this) ;

        mGgParentLay.setOnClickListener(this);
        mGgChildLay.setOnClickListener(this);
        addView.setOnClickListener(this);
        reduceView.setOnClickListener(this);
        mCloaseIv.setOnClickListener(this);
        mGgSubmitTv.setOnClickListener(this);
        mGgAddCartTv.setOnClickListener(this);
        mGgButNowTv.setOnClickListener(this);
        mRightView.setOnClickListener(this);
        cartView.setOnClickListener(this);
    }

    @Override
    public void initData() {
        getGoodDetails() ;
    }

    @Override
    public void initListener() {
        mService.setOnClickListener(this);
        mCollection.setOnClickListener(this);
        mShop.setOnClickListener(this);
        mCarts.setOnClickListener(this);
        mBuy.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        if(mIsLoadingAnim){
            return ;
        }
        int i = view.getId();
        if (i == R.id.ll_customerservice) {
            if (mDetails != null) {
                String hxName = mDetails.getHxName();
                if("".equals(hxName)){
                    ToastUtils.showToast(mContext ,"当前店铺未设置客服人员");
                }else{
                    if(LoginHelper.isNotLogin(mContext)){
                        SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_CUSTOMER) ;
                    }else{
                        SkipUtils.toMessageChatActivity(mActivity, hxName);
                    }
                }
            }
        } else if (i == R.id.ll_collection) {
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_COLLECTION) ;
                return ;
            }
            goCollection();
        } else if (i == R.id.ll_shop) {
            intentToStoreIndex() ;
        } else if (i == R.id.add_carts_tv) {
            if(null == mDetails || mDetails.isNotEnableMark() || mIsSelf){
                return ;
            }

            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_ADD_CART) ;
                return ;
            }
            mIsAddToCart = true ;
            setGuigeLayShow(true);
        } else if (i == R.id.add_buys_tv) {
            if(null == mDetails || mDetails.isNotEnableMark() || mIsSelf){
                return ;
            }

            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_BUY_NOW) ;
                return ;
            }
            mIsAddToCart = false ;
            setGuigeLayShow(true);
        } else if(R.id.acti_good_details_gg_child_close_iv == i
                || R.id.acti_good_details_gg_parent_lay == i){
            setGuigeLayShow(false);
        } else if(R.id.acti_good_details_gg_child_lay == i
                || R.id.acti_good_details_gg_flow_lay == i){//关闭输入法
            mCountEv.clearFocus() ;
            DialogUtils.hideKeyBoard(mCountEv) ;
        } else if(R.id.acti_good_details_gg_count_add_iv == i){
            changeCount(true) ;
        } else if(R.id.acti_good_details_gg_count_reduce_iv == i){
            changeCount(false) ;
        }  else if(R.id.acti_good_details_gg_count_ev == i){
            mCountEv.requestFocus() ;
            mCountEv.setSelection(mCountEv.getText().length());
        } else if(R.id.acti_good_details_gg_submit_tv == i){
            submitGoodInfo() ;
        } else if(R.id.goods_detail_share_iv == i){//分享
//            ShareUtils.shareWeb(mActivity ,mDetails.getName(),mDetails.getH5Url() ,mDetails.getImgUrl() ,"");

            showRightPopup() ;
        } else if(R.id.goods_detail_cart_iv == i){//购物车
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_CART);
            }else{
                Intent toCtIt = new Intent(mContext ,ShoppingCartActivity.class) ;
                startActivity(toCtIt) ;
            }
        } else if(R.id.acti_good_details_store_index_tv == i
                || R.id.acti_good_details_store_name_tv == i
                || R.id.acti_good_details_store_logo_iv == i){//进店看看
            if(mDetails != null && !"".equals(mDetails.getMerchantID())){
                SkipUtils.toStoreIndexActivity(mContext ,mDetails.getMerchantID()) ;
            }
        } else if(R.id.acti_good_details_top_nav_one_lay == i){//宝贝
            scrllToPosition(0) ;
        } else if(R.id.acti_good_details_top_nav_two_lay == i){//评价
            scrllToPosition(1) ;
        } else if(R.id.acti_good_details_top_nav_three_lay == i){//详细
            scrllToPosition(2) ;
        } else if(R.id.acti_good_details_top_nav_four_lay == i){//推荐
            scrllToPosition(3) ;
        } else if(R.id.acti_good_details_service_tv == i){//服务
            showServicePopup() ;
        } else if(R.id.acti_good_details_params_tv == i){//参数
            showParamPopup() ;
        } else if(R.id.acti_good_details_spec_lay == i){//规格
            mIsSpec = true ;
            setGuigeLayShow(true);
        } else if(R.id.bot_gg_add_buys_tv == i){//立即购买--规格布局
            if(null == mDetails || mDetails.isNotEnableMark() || mIsSelf){
                return ;
            }

            mIsAddToCart = false ;
            setGuigeLayShow(false) ;

            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_BUT_SUBMIT);
            }else{
                submitGoodInfo() ;
            }
        } else if(R.id.bot_gg_add_carts_tv == i){//加入购物车--规格布局
            if(null == mDetails || mDetails.isNotEnableMark() || mIsSelf){
                return ;
            }

            mIsAddToCart = true ;
            setGuigeLayShow(false) ;

            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity,REQUEST_CODE_BUT_SUBMIT);
            }else{
                submitGoodInfo() ;
            }
        } else if(R.id.acti_good_details_comment_all_tv == i){//全部评价
            GoodCommentListActivity.launch(mContext ,mProductId,mDetails.getImgUrl()) ;
        }
    }

    /**
     * 服务
     */
    private void showServicePopup(){
        if(null == mServicePup){
            View view = LayoutInflater.from(mContext).inflate(R.layout.popup_good_details_server,null) ;
            mServicePup = new PopupWindow(view,ResourceUtils.getScreenWidth(mContext)
                    , (int) (ResourceUtils.getScreenHeight(mContext) * 0.75F)) ;

            mServicePup.setBackgroundDrawable(new BitmapDrawable());
            mServicePup.setTouchable(true);
            mServicePup.setOutsideTouchable(true);
            mServicePup.setAnimationStyle(R.style.add_comment_popup_anim_style);
            mServicePup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams layoutParams = mActivity.getWindow().getAttributes();
                    layoutParams.alpha=1.0f;
                    mActivity.getWindow().setAttributes(layoutParams);
                }
            });

            View okTv = view.findViewById(R.id.popup_good_details_content_tv) ;
            okTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mServicePup.dismiss() ;
                }
            });
            ListView lv = view.findViewById(R.id.popup_good_details_content_lv) ;
            mServiceAdapter = new AdapterGoodService(mContext ,mServiceList) ;
            lv.setAdapter(mServiceAdapter) ;
        }

        WindowManager.LayoutParams layoutParams = mActivity.getWindow().getAttributes();
        layoutParams.alpha=0.3f;
        mActivity.getWindow().setAttributes(layoutParams);
        mServicePup.showAtLocation(mParentView, Gravity.BOTTOM,0,0) ;
    }

    /**
     * 参数
     */
    private void showParamPopup(){
        if(null == mParamePup){
            View view = LayoutInflater.from(mContext).inflate(R.layout.popup_good_details_param,null) ;
            mParamePup = new PopupWindow(view,ResourceUtils.getScreenWidth(mContext)
                    , (int) (ResourceUtils.getScreenHeight(mContext) * 0.75F)) ;

            mParamePup.setBackgroundDrawable(new BitmapDrawable());
            mParamePup.setTouchable(true);
            mParamePup.setOutsideTouchable(true);
            mParamePup.setAnimationStyle(R.style.add_comment_popup_anim_style);
            mParamePup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams layoutParams = mActivity.getWindow().getAttributes();
                    layoutParams.alpha=1.0f;
                    mActivity.getWindow().setAttributes(layoutParams);
                }
            });

            View okTv = view.findViewById(R.id.popup_good_details_content_tv) ;
            okTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mParamePup.dismiss() ;
                }
            });
            ListView lv = view.findViewById(R.id.popup_good_details_content_lv) ;
            mParamAdapter = new AdapterGoodParam(mContext ,mParamList) ;
            lv.setAdapter(mParamAdapter) ;
        }

        WindowManager.LayoutParams layoutParams = mActivity.getWindow().getAttributes();
        layoutParams.alpha=0.3f;
        mActivity.getWindow().setAttributes(layoutParams);
        mParamePup.showAtLocation(mParentView, Gravity.BOTTOM,0,0) ;
    }

    /**
     * 滑动到相关位置
     * @param position 0 商品 1 评论 2 详情 3 推荐
     */
    private void scrllToPosition(int position){
        if(mTopAlpha == 0){
            return ;
        }
        switch (position){
            case 0:
                mScrolView.smoothScrollTo(0,0) ;
                break;
            case 1:
                mCommentCountTv.getLocationOnScreen(mCommentScr);
                int comPoY = mCommentScr[1] ;
                int comScrollDes = comPoY - mLimitY ;
                mScrolView.smoothScrollBy(0,comScrollDes) ;
                break;
            case 2:
                mDetailsWvTv.getLocationOnScreen(mDetailsScr);
                int detPoY = mDetailsScr[1] ;
                int detScrollDes = detPoY - mLimitY ;
                mScrolView.smoothScrollBy(0,detScrollDes) ;
                break;
            case 3:
                mSampleHotTv.getLocationOnScreen(mGoodScr);
                int goodPoY = mGoodScr[1] ;
                int goodScrollDes = goodPoY - mLimitY ;
                mScrolView.smoothScrollBy(0,goodScrollDes) ;
                break;
            default:
                break;
        }
    }

    /**
     * 滑动监听
     */
    private MyScrollView.OnScrollChangedListener mScrollListener = new MyScrollView.OnScrollChangedListener() {
        @Override
        public void ScrollChanged(int yPositon) {
            mScrollY = yPositon ;

            //更新各个模块的位置
            mCommentAllTv.getLocationOnScreen(mCommentScr) ;
            mDetailsWvTv.getLocationOnScreen(mDetailsScr);
            mSampleHotTv.getLocationOnScreen(mGoodScr);

            float alpha = (float) (mScrollY + mLimitY - mScreenWidth) / (mLimitY / 2) ;
            if(alpha > 1){
                if(mTopAlpha != 1){
                    mTopAlpha = 1 ;

                    mStatusView.setAlpha(mTopAlpha) ;
                    mTitleLay.setAlpha(mTopAlpha) ;
                }
            }else if(alpha <= 0){
                if(mTopAlpha != 0){
                    mTopAlpha = 0 ;

                    mStatusView.setAlpha(mTopAlpha) ;
                    mTitleLay.setAlpha(mTopAlpha) ;
                }
            }else{
                mTopAlpha = alpha ;

                mStatusView.setAlpha(mTopAlpha) ;
                mTitleLay.setAlpha(mTopAlpha) ;
            }

            int commY = mCommentScr[1] ;
            int detailY = mDetailsScr[1] ;
            int goodY = mGoodScr[1] ;

            if(goodY <= mLimitY){//显示 推荐
                changeTitleNavStyle(3) ;
            }else if(detailY <= mLimitY){
                changeTitleNavStyle(2) ;
            }else if(commY <= mLimitY){
                changeTitleNavStyle(1) ;
            }else{
                changeTitleNavStyle(0) ;
            }
        }
    } ;

    /**
     * 切换nav样式
     * @param type 0 商品 1 评价 2 详情 3 推荐
     */
    private void changeTitleNavStyle(int type){
        if(mCurType == type){
            return ;
        }

        switch (mCurType){
            case 0:
                mNavOneIv.setVisibility(View.INVISIBLE);
                mNavOneTv.setTextColor(getResources().getColor(R.color.color_nomal_text));
                break;
            case 1:
                mNavTwoIv.setVisibility(View.INVISIBLE);
                mNavTwoTv.setTextColor(getResources().getColor(R.color.color_nomal_text));
                break;
            case 2:
                mNavThreeIv.setVisibility(View.INVISIBLE);
                mNavThreeTv.setTextColor(getResources().getColor(R.color.color_nomal_text));
                break;
            case 3:
                mNavFourIv.setVisibility(View.INVISIBLE);
                mNavFourTv.setTextColor(getResources().getColor(R.color.color_nomal_text));
                break;
            default:
                break;
        }
        switch (type){
            case 0:
                mNavOneIv.setVisibility(View.VISIBLE);
                mNavOneTv.setTextColor(getResources().getColor(R.color.color_theme));
                break;
            case 1:
                mNavTwoIv.setVisibility(View.VISIBLE);
                mNavTwoTv.setTextColor(getResources().getColor(R.color.color_theme));
                break;
            case 2:
                mNavThreeIv.setVisibility(View.VISIBLE);
                mNavThreeTv.setTextColor(getResources().getColor(R.color.color_theme));
                break;
            case 3:
                mNavFourIv.setVisibility(View.VISIBLE);
                mNavFourTv.setTextColor(getResources().getColor(R.color.color_theme));
                break;
            default:
                break;
        }
        mCurType = type ;
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
                        ShareUtils.shareWeb(mActivity ,mDetails.getStoreName(),mDetails.getH5Url() ,mDetails.getImgUrl() ,"");
                    }else if(TopBarRightPopup.POPUP_ITEM_REQUEST == position){//投诉
                        SkipUtils.toRequestActivity(mContext,null) ;
                    }
                }
            }) ;
        }

        mRightPopup.displayDialog() ;
    }

    /**
     * 跳转到店铺主页
     */
    private void intentToStoreIndex(){
        if(mIsFromStore){
            finish() ;
            return ;
        }

        Intent toStIt = new Intent(mContext, StoreIndexActivity.class);
        toStIt.putExtra(SkipUtils.INTENT_STORE_ID ,mDetails.getMerchantID()) ;
        startActivity(toStIt);
    }

    /**
     * 提交
     */
    private void submitGoodInfo(){
        String count = mCountEv.getText().toString().trim() ;
        if(StringUtils.convertString2Count(count) > mChoosedGood.getStock()){
            ToastUtils.showToast(mContext ,"库存不足");
            return ;
        }

        setGuigeLayShow(false) ;

        if(mIsAddToCart){//加入购物车
            addGoodToCart() ;
        }else{//立即购买
            toBuyNow() ;
        }
    }

    /**
     * 修改数量
     * @param isAdd true
     */
    private void changeCount(boolean isAdd){
        int stock = mChoosedGood.getStock() ;
        int count = StringUtils.convertString2Count(mCountEv.getText().toString().trim()) ;
        if(isAdd){
            if(count + 1 > stock){
                ToastUtils.showToast(mContext ,"库存不足") ;
                return ;
            }
            mCountEv.setText((count + 1) + "");
        }else{
            if(count > 1){
                mCountEv.setText((count - 1) + "");
            }
        }
    }

    /**
     * 填充数据
     */
    private void updateDisplay(){
        if(null == mDetails){
            DialogUtils.showCustomViewDialog(mContext, "商品信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });
            return ;
        }

//        mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;
        if(mDetails.isNotEnableMark() || mIsSelf){//下架了或者是自己
            mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
            mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));

            mGgAddCartTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
            mGgButNowTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
        }

        final List<GoodDetails.GoodSkuList> goodList = mDetails.getSkuList() ;
        if(goodList != null && goodList.size() > 0){
            mAllGoodList.addAll(goodList) ;
            if(!"".equals(mSkuId)){
                for(GoodDetails.GoodSkuList good : goodList){
                    if(mSkuId.equals(good.getSkuID())){
                        mChoosedGood = good ;
                        break;
                    }
                }
            }

            if(null == mChoosedGood){
                mChoosedGood = mAllGoodList.get(0) ;
            }

            updateGoodInfo();
        }

        //填充信息
        String goodName = mDetails.getGoodName() ;
        String goodPrice = mDetails.getGoodPrice() ;
        String goodOldPrice = mDetails.getGoodPriceOld() ;
        String goodSaleCount = mDetails.getGoodSaleCount() ;
        String storeArea = mDetails.getShopAddress() ;
        String deliveType = mDetails.getPeiSongFangShiName() ;

        mPriceTv.setText(goodPrice);
        mPriceOldTv.setText(goodOldPrice) ;
        mNameTv.setText(goodName);
        mSaleCountTv.setText("销量" + goodSaleCount);
        mStoreAreaTv.setText(storeArea);
        mDeliveTypeTv.setText(deliveType);

        //店铺信息
        String storeName = mDetails.getStoreName() ;
        String storeLogo = mDetails.getStoreLogo() ;
        String storeSale = mDetails.getStoreSaleCount() ;
        mStoreNameTv.setText(storeName);
        mStoreSaleTv.setText("累计" + storeSale + "人付款");
        GlideUtils.loader(storeLogo,mStoreLogoIv) ;

        //商品图片
        mGoodImgList.clear();
        mGoodImgList.addAll(mDetails.getGoodImgList()) ;
        if(mGoodImgList.size() > 0){
            List<String> thumbList = new ArrayList<>(mGoodImgList.size()) ;
            for(ImageThumb thumb : mGoodImgList){
                thumbList.add(thumb.getOriginalImgUrl()) ;
            }

            mImageBanner.update(thumbList) ;
        }

        //服务、参数
        updateServiceParam() ;

        //评论
        updateGoodComment() ;

        //商品详情
        initWebView() ;

        //相关产品、相关产品推荐
        updateSampleGood() ;

        initGuige() ;
    }

    /**
     * 相关产品、推荐
     */
    private void updateSampleGood(){
        mSampleGoodList.clear();
        mSampleGoodList.addAll(mDetails.getSampleGoodList()) ;
        if(null == mSampleAdapter){
            mSampleAdapter = new AdapterGoodDetailsGood(mContext ,mSampleGoodList ,3) ;
            mSampleGv.setNumColumns(3) ;
            mSampleGv.setAdapter(mSampleAdapter);
        }else{
            mSampleAdapter.notifyDataSetChanged() ;
        }

        mSampleHotGoodList.clear();
        mSampleHotGoodList.addAll(mDetails.getSampleHotGoodList()) ;

        if(null == mSampleHotAdapter){
            mSampleHotAdapter = new AdapterGoodDetailsGood(mContext ,mSampleHotGoodList ,2) ;
            mSampleHotGv.setNumColumns(2) ;
            mSampleHotGv.setAdapter(mSampleHotAdapter);
        }else{
            mSampleHotAdapter.notifyDataSetChanged() ;
        }
    }

    /**
     * 商品评论
     */
    private void updateGoodComment(){
        final String count = mDetails.getCommentCount() ;
        mCommentCountTv.setText("（" + count + "）");

        mGoodCommentList.clear();
        mGoodCommentList.addAll(mDetails.getCommentList()) ;

        ViewUtil.setViewVisible(mCommentAllTv,mGoodCommentList.size() > 0);

        if(null == mCommentAdapter){
            mCommentAdapter = new GoodDetailsCommentAdapter(mActivity, mGoodCommentList, new RvItemViewClickListener() {
                @Override
                public void onItemClickListener(int type, int position) {
                    if(0 == type){
                        GoodComment comment = mGoodCommentList.get(position) ;
                        if(comment != null){
                            SkipUtils.toOtherUserInfoActivity(mContext ,comment.getCreateUserId()) ;
                        }
                    }
                }
            }) ;
            mCommentLv.setAdapter(mCommentAdapter) ;
        }else{
            mCommentAdapter.notifyDataSetChanged() ;
        }
    }

    /**
     * 服务、参数
     */
    private void updateServiceParam(){
        mServiceList.clear();
        mServiceList.addAll(mDetails.getGoodServiceList()) ;
        if(mServiceAdapter != null){
            mServiceAdapter.notifyDataSetChanged() ;
        }

        mParamList.clear();
        mParamList.addAll(mDetails.getGoodParamList()) ;
        if(mParamAdapter != null){
            mParamAdapter.notifyDataSetChanged() ;
        }

        String service = "" ;
        for(GoodDetails.GoodService ser : mServiceList){
            service += (ser.getServiceTitle() + "-") ;
        }
        if(service.endsWith("-")){
            service = service.substring(0,service.length() - 1) ;
        }
        mGoodServiceTv.setText(service) ;

        StringBuilder param = new StringBuilder() ;
        for(GoodDetails.GoodParam par : mParamList){
            param.append(par.getParentParaTitle())
                    .append("-")
                    .append(par.getParaTitle())
                    .append(" ");
        }
        mGoodParamTv.setText(param.toString()) ;
    }

    /**
     * 更新商品信息
     */
    private void updateGoodInfo(){
        if(null == mChoosedGood){
            return ;
        }

        boolean isCollection = mChoosedGood.isCollection() ;
        mCollectionIv.setImageDrawable(getResources().getDrawable(isCollection ? R.mipmap.ic_collection_checked : R.mipmap.ic_collection_nomal));

        //更新商品信息
        int stock = mChoosedGood.getStock() ;
        String money = mChoosedGood.getSalePrice() ;
        String oldPrice = mChoosedGood.getMarketPrice() ;
        String guige = mChoosedGood.getSpecText() ;
        GlideUtils.loaderRoundBorder(mChoosedGood.getImgUrl() ,mGgGoodIv
                ,2,mContext.getResources().getColor(R.color.color_grey_f2));

        mGgGoodPriceTv.setText("¥" + money);
        mGgGoodGgTv.setText("已选：" + guige);
        mGgGoodStockTv.setText("库存：" + stock);

        mGoodSpecTv.setText(guige);

        //暂时不影响主界面的价格显示
//        mPriceTv.setText(money);
//        mPriceOldTv.setText(oldPrice);
    }

    /**
     * 初始化规格
     * 思路：把每组的子规格单独找出来，避免了每次全局遍历获取子规格
     */
    private void initGuige(){
        //先把图片展示下
        String goodImgUrl = mDetails.getImgUrl() ;
        GlideUtils.loader(goodImgUrl ,mTitleGoodIv) ;
        GlideUtils.loaderRoundBorder(goodImgUrl ,mGgGoodIv ,2,mContext.getResources().getColor(R.color.color_grey_f2));

        int minWidth = ResourceUtils.dip2px(mContext ,40) ;

        //获取规格
        List<GoodDetails.GoodSkuList> skuLists = mDetails.getSkuList() ;
        for(final GoodDetails.GoodSkuList skuGood : skuLists){
            final String skuId = skuGood.getSkuID() ;
            String skuSpecName = skuGood.getSpecText() ;

            View cV = LayoutInflater.from(mContext).inflate(R.layout.lay_only_textview, null) ;
            final TextView valueTv = cV.findViewById(R.id.only_textview_tv) ;
            valueTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            valueTv.setText(skuSpecName) ;
            valueTv.setTag(skuId) ;
            valueTv.setMinWidth(minWidth) ;

            if(mChoosedGood != null && mChoosedGood.getSkuID().equals(skuId)){
                mSelectedGgTv = valueTv ;

                valueTv.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(R.drawable.bg_guige_checked)) ;
                valueTv.setTextColor(mContext.getResources().getColor(R.color.color_white));
            }else{
                valueTv.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(R.drawable.bg_guige_nomal)) ;
                valueTv.setTextColor(mContext.getResources().getColor(R.color.color_nomal_text));
            }

            valueTv.setPadding(ResourceUtils.dip2px(mContext, 10), ResourceUtils.dip2px(mContext, 5)
                    , ResourceUtils.dip2px(mContext, 10), ResourceUtils.dip2px(mContext, 5));

            valueTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if(mChoosedGood == null || mChoosedGood.getSkuID().equals(skuId)){
                        return ;
                    }

                    mChoosedGood = skuGood ;

                    //还原之前的
                    mSelectedGgTv.setBackgroundDrawable(mContext.getResources()
                            .getDrawable(R.drawable.bg_guige_nomal)) ;
                    mSelectedGgTv.setTextColor(mContext.getResources().getColor(R.color.color_nomal_text));

                    //更新现在的
                    valueTv.setBackgroundDrawable(mContext.getResources()
                            .getDrawable(R.drawable.bg_guige_checked)) ;
                    valueTv.setPadding(ResourceUtils.dip2px(mContext, 10), ResourceUtils.dip2px(mContext, 5)
                            , ResourceUtils.dip2px(mContext, 10), ResourceUtils.dip2px(mContext, 5));
                    valueTv.setTextColor(mContext.getResources().getColor(R.color.color_white));

                    mSelectedGgTv = valueTv ;
                    updateGoodInfo() ;
                }
            });

            mGuigeFlowLay.addView(cV);
        }
    }

    /**
     * 规格布局显示
     */
    private void setGuigeLayShow(boolean isShow){
        mCountEv.clearFocus() ;
        DialogUtils.hideKeyBoard(mCountEv);
        toGuigeChangeIt(isShow) ;
    }

    /**
     * 选择规格
     */
    private void toGuigeChangeIt(boolean isIn){
        if(isIn){//显示规格布局

            ViewUtil.setViewVisible(mGgBotOpLay,mIsSpec) ;
            if(!mIsSpec){
                if(mGgSubmitTv.getVisibility() != View.VISIBLE){
                    mGgSubmitTv.setVisibility(View.VISIBLE);
                }
            }else{
                if(mGgSubmitTv.getVisibility() != View.INVISIBLE){
                    mGgSubmitTv.setVisibility(View.INVISIBLE);
                }
            }

            mGgParentLay.setVisibility(View.VISIBLE) ;
            Animation myAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.in_bottomtop);
            mGgChildLay.startAnimation(myAnimation);
            myAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                    mIsLoadingAnim = true ;
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    mGgChildLay.setVisibility(View.VISIBLE) ;
                    mIsLoadingAnim = false ;
                }
            });
        }else{//隐藏规格布局

            int count = StringUtils.convertString2Count(mCountEv.getText().toString().trim()) ;
            if(count < 1){
                count = 1 ;
                mCountEv.setText("1") ;
            }

            mGoodSpecCountTv.setText(count + "件");

            mIsSpec = false ;

            Animation myAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.out_topbottom);
            mGgChildLay.startAnimation(myAnimation);
            myAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                    mIsLoadingAnim = true ;
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationEnd(Animation arg0) {
                    mGgParentLay.setVisibility(View.GONE) ;
                    mIsLoadingAnim = false ;
                }
            });
        }
    }

    private void initWebView(){
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

        mWebView.loadUrl(StringUtils.convertHttpUrl(mDetails.getContentDetailH5()));
    }

    /**
     * 立即购买
     */
    private void toBuyNow(){
        if(null == mChoosedGood){
            ToastUtils.showToast(mContext ,"所选商品信息有误，请重试");
            return ;
        }
        String count = mCountEv.getText().toString().trim() ;
        if(StringUtils.convertString2Count(count) <= 0){
            ToastUtils.showToast(mContext ,"请输入正确的数量");
            return ;
        }

        GoodOrderConfirmActivity.launch(mContext ,false ,mChoosedGood.getSkuID() ,""+count ,mProductId) ;
    }

    /**
     * 加入购物车
     */
    private void addGoodToCart(){
        if(null == mChoosedGood){
            ToastUtils.showToast(mContext ,"所选商品信息有误，请重试");
            return ;
        }
        String count = mCountEv.getText().toString().trim() ;
        if(StringUtils.convertString2Count(count) <= 0){
            ToastUtils.showToast(mContext ,"请输入正确的数量");
            return ;
        }

        if(null == mLoadingDialog){
            mLoadingDialog = new LoadingDialog(mContext) ;
        }
        mLoadingDialog.show() ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("SkuID" ,StringUtils.convertNull(mChoosedGood.getSkuID())) ;
        paramMap.put("Quantity" , count+"") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_ADD_CART , paramMap
                , new RequestObjectCallBack<GoodDetails>("addToCart",mContext ,GoodDetails.class) {
                    @Override
                    public void onSuccessResult(GoodDetails bean) {
                        ToastUtils.showToast(mContext,"加入购物车成功");
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mLoadingDialog.dismiss();

                    }

                    @Override
                    public void onFail(Exception e) {
                        mLoadingDialog.dismiss();

                        ToastUtils.showToast(mContext,"加入购物车失败");
                    }
                });
    }

    /**
     * 获取详细
     */
    private void getGoodDetails(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("ProId" , StringUtils.convertNull(mProductId)) ;
        paramMap.put("SkuID" ,StringUtils.convertNull(mSkuId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_DETAILS, paramMap
                , new RequestObjectCallBack<GoodDetails>("getGoodDetails",mContext ,GoodDetails.class) {
                    @Override
                    public void onSuccessResult(GoodDetails bean) {
                        mDetails = bean ;
                        updateDisplay() ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        updateDisplay() ;
                    }

                    @Override
                    public void onFail(Exception e) {
                        updateDisplay() ;
                    }
                });
    }

    /**
     * 收藏
     */
    private void goCollection(){
        if (mLoadingDialog == null)
            mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();

        final String chooseSkuId = mChoosedGood.getSkuID() ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ProId" , mProductId) ;
        paramMap.put("SkuID" , chooseSkuId) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_COLLECTION, false, paramMap
                , new RequestObjectCallBack<CollectionResult>("goCollection" , mContext ,CollectionResult.class) {
                    @Override
                    public void onSuccessResult(CollectionResult bean) {
                        if(mLoadingDialog != null){
                            mLoadingDialog.dismiss();
                        }

                        if(bean != null){
                            final boolean isCollected = 2 == bean.AddOrDelete ;
                            mChoosedGood.setIsCollected(isCollected ? "0" : "1");
                            updateGoodInfo();

                            //手动改下原始数据
                            for(GoodDetails.GoodSkuList goodSkuList : mAllGoodList){
                                if(chooseSkuId.equals(goodSkuList.getSkuID())){
                                    goodSkuList.setIsCollected(isCollected ? "0" : "1");
                                    break;
                                }
                            }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_CUSTOMER == requestCode){
            if(RESULT_OK == resultCode){
//                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;

                if(mIsSelf){//是自己的店铺
                    mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));

                    mGgAddCartTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mGgButNowTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    String hxName = mDetails.getHxName();
                    SkipUtils.toMessageChatActivity(mActivity, hxName);
                }
            }
        }else if(REQUEST_CODE_COLLECTION == requestCode){
            if(RESULT_OK == resultCode){
//                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;

                if(mIsSelf){//是自己的店铺
                    mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));

                    mGgAddCartTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mGgButNowTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }

                goCollection() ;
            }
        }else if(REQUEST_CODE_ADD_CART == requestCode){
            if(RESULT_OK == resultCode){
//                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;

                if(mIsSelf){//是自己的店铺
                    mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));

                    mGgAddCartTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mGgButNowTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    mIsAddToCart = true ;
                    setGuigeLayShow(true);
                }
            }
        }else if(REQUEST_CODE_BUY_NOW == requestCode){
            if(RESULT_OK == resultCode){
//                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;

                if(mIsSelf){//是自己的店铺
                    mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));

                    mGgAddCartTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mGgButNowTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    mIsAddToCart = true ;
                    setGuigeLayShow(true);
                }
            }
        }else if(REQUEST_CODE_CART == requestCode){
            if(RESULT_OK == resultCode){
                Intent toCtIt = new Intent(mContext ,ShoppingCartActivity.class) ;
                startActivity(toCtIt) ;
            }
        }else if(REQUEST_CODE_BUT_SUBMIT == requestCode){
            if(RESULT_OK == resultCode){
//                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;

                if(mIsSelf){//是自己的店铺
                    mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));

                    mGgAddCartTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mGgButNowTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    submitGoodInfo() ;
                }
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.StoreSupportEvent ev){
        mDetails.setIsFollowShop(ev.isSupport() ? "1" : "0") ;
    }

    @Override
    public void onBackPressed() {
        if(mIsLoadingAnim){
            return ;
        }

        if(mGgParentLay.getVisibility() == View.VISIBLE){
            setGuigeLayShow(false) ;
           return ;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        NetUtils.cancelTag("addToCart");
        NetUtils.cancelTag("goCollection");
        NetUtils.cancelTag("getGoodDetails");
    }
}
