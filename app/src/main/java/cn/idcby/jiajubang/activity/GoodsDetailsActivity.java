package cn.idcby.jiajubang.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import cn.idcby.jiajubang.Bean.GoodDetails;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.ShareUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.FlowLayout;
import cn.idcby.jiajubang.view.TopBarRightPopup;
import cn.idcby.jiajubang.view.WebViewNoScroll;

/**
 * Created on 2018-04-24.
 * 2018-06-26 10:07:51
 * 改：自己的店铺不允许加入购物车和立即购买
 * 目前是直接不让操作2个按钮，影响的是没法看到商品规格了
 * 如果需求变动为，可以看规格，但是不能买，可以只处理规格弹窗里的确定按钮。
 *
 * 2018-07-26 09:23:22
 * 改：商品规格不再使用spec了，直接从SkuList里面提取相关的规格，名字叫 请选择商品规格，
 * 规格从GoodSkuList里面的SpecText拿到并展示。
 *
 * 2018-08-15 15:40:15
 * 不用H5了，用原生的，仿淘宝
 * {@link GoodDetailActivity}代替
 */

@Deprecated
public class GoodsDetailsActivity extends BaseActivity {
    private WebViewNoScroll mWebView;
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
    private boolean mIsSelf = false ;//是否是自己的店铺

    private List<GoodDetails.GoodSkuList> mAllGoodList = new ArrayList<>();

    private LoadingDialog mLoadingDialog;

    //规格相关
    private View mGgParentLay ;
    private View mGgChildLay ;

    private FlowLayout mGuigeFlowLay ;
    private ImageView mGgGoodIv ;//商品图片
    private TextView mGgGoodPriceTv ;//售价
    private TextView mGgGoodGgTv ;//规格
    private TextView mGgGoodStockTv ;//库存
    private EditText mCountEv;
    private boolean mIsAddToCart = true ;//点击规格里面操作，默认加入购物车
    private boolean mIsLoadingAnim = false ;//是否正在显示动画

    private GoodDetails.GoodSkuList mChoosedGood ;
    private TextView mSelectedGgTv ;

    private static final int REQUEST_CODE_CUSTOMER = 1000 ;
    private static final int REQUEST_CODE_COLLECTION = 1001 ;
    private static final int REQUEST_CODE_ADD_CART = 1002 ;
    private static final int REQUEST_CODE_BUY_NOW = 1003 ;
    private static final int REQUEST_CODE_CART = 1004 ;

    private View mRightView ;
    private TopBarRightPopup mRightPopup ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_gooddetails;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTransparentForImageView(mActivity ,null);

        EventBus.getDefault().register(this);

        mIsFromStore = getIntent().getBooleanExtra(SkipUtils.INTENT_GOOD_FROM_STORE,mIsFromStore) ;
        mProductId = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_GOOD_ID)) ;
        mSkuId = StringUtils.convertNull(getIntent().getStringExtra(SkipUtils.INTENT_GOOD_SKU_ID)) ;

        mWebView = findViewById(R.id.goodsdetail_wvnoscroll);
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
        TextView mGgSubTv = findViewById(R.id.acti_good_details_gg_submit_tv) ;
        mRightView = findViewById(R.id.goods_detail_share_iv) ;
        View cartView = findViewById(R.id.goods_detail_cart_iv) ;

        mGgChildLay.getLayoutParams().height = (int) (ResourceUtils.getScreenHeight(mContext) * 0.8F);
        mCountEv.clearFocus() ;
        mCountEv.setOnClickListener(this);
        mGuigeFlowLay.setOnClickListener(this) ;

        mGgParentLay.setOnClickListener(this);
        mGgChildLay.setOnClickListener(this);
        addView.setOnClickListener(this);
        reduceView.setOnClickListener(this);
        mCloaseIv.setOnClickListener(this);
        mGgSubTv.setOnClickListener(this);
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

        mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;
        if(mDetails.isNotEnableMark() || mIsSelf){//下架了或者是自己
            mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
            mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
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

        initWebView() ;
        initGuige() ;
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
        String guige = mChoosedGood.getSpecText() ;

        mGgGoodPriceTv.setText("¥" + money);
        mGgGoodGgTv.setText("规格：" + guige);
        mGgGoodStockTv.setText("库存：" + stock);
    }

    /**
     * 初始化规格
     *
     * 思路：把每组的子规格单独找出来，避免了每次全局遍历获取子规格
     */
    private void initGuige(){
        //先把图片展示下
        String goodImgUrl = mDetails.getImgUrl() ;
        GlideUtils.loaderRoundBorder(goodImgUrl ,mGgGoodIv ,2,mContext.getResources().getColor(R.color.color_grey_f2));

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

        mWebView.loadUrl(StringUtils.convertHttpUrl(mDetails.getH5Url()));
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
        paramMap.put("ProId" , mProductId) ;
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
                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;

                if(mIsSelf){//是自己的店铺
                    mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    String hxName = mDetails.getHxName();
                    SkipUtils.toMessageChatActivity(mActivity, hxName);
                }
            }
        }else if(REQUEST_CODE_COLLECTION == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;

                if(mIsSelf){//是自己的店铺
                    mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }

                goCollection() ;
            }
        }else if(REQUEST_CODE_ADD_CART == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;

                if(mIsSelf){//是自己的店铺
                    mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
                }else{
                    mIsAddToCart = true ;
                    setGuigeLayShow(true);
                }
            }
        }else if(REQUEST_CODE_BUY_NOW == requestCode){
            if(RESULT_OK == resultCode){
                mIsSelf = LoginHelper.isSelf(mContext ,mDetails.getCreateUserID()) ;

                if(mIsSelf){//是自己的店铺
                    mCarts.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_left_unable));
                    mBuy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dt_bot_options_right_unable));
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
