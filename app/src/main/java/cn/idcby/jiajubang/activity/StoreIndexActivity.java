package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.CollectionResult;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.Bean.StoreIndexInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.WstGoodsRecycleViewJpgAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.events.BusEvent;
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
import cn.idcby.jiajubang.view.RvGridManagerItemDecoration;
import cn.idcby.jiajubang.view.WebViewNoScroll;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 店铺主页面
 * Created on 2018/4/24.
 *
 * 2018-05-31 17:58:09
 * 跟ios一致，隐藏消息入口
 *
 * 2018-07-02 17:07:16
 * 右边改成店铺分类图标；删除了消息相关内容；重写了关注按钮样式，跟个人主页一致。
 *
 * 2018-09-11 11:09:55
 * 关注按钮改为 分享图标 + 文字 ；点击关注店铺，执行关注、取消关注操作，字体颜色为 主题色 和 灰色 切换
 */

public class StoreIndexActivity extends BaseActivity {
    private MaterialRefreshLayout mRefreshLay;
    private RecyclerView mRecyclerView;

    //header
    private ImageView mStoreLogoIv ;
    private TextView mStoreNameTv ;
    private TextView mStoreTypeCompanyTv;
    private TextView mStoreTypeShopyTv;
    private TextView mSupportCountTv ;
    private TextView mSupportTv ;
    private View mShareLay;

    private View mOpRecomDv;//推荐商品
    private View mOpAllDv;//全部商品
    private View mOpInfoDv;//店铺信息

    private WebViewNoScroll mWebView ;

    private View mLoadingLay ;
    private View mNullTv ;
    private View mLoadingPb ;

    private View mFixLay;
    private int mScrollLimitHeight = 0;
    private int mScrollY = 0;

    private List<GoodList> mGoodList = new ArrayList<>();
    private HeaderFooterAdapter<WstGoodsRecycleViewJpgAdapter> mAdapter;

    private String mStoreId ;
    private StoreIndexInfo mStoreInfo ;
    private int mRecommendType = 1 ;// 1推荐 2全部 3详细
    private boolean mIsRefreshing = false ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private static final int REQUEST_CODE_COLLECTION = 1000 ;
    private static final int REQUEST_CODE_CUSTOMER = 1001 ;
    private static final int REQUEST_CODE_SEARCH = 1002 ;

    private LoadingDialog mLoadingDialog ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_store_index;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTransparentForImageView(mActivity ,null);

        mStoreId = getIntent().getStringExtra(SkipUtils.INTENT_STORE_ID);

        if(null == mStoreId){
            DialogUtils.showCustomViewDialog(mContext, "店铺信息有误，请返回重试"
                    , "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish() ;
                }
            });
            return ;
        }

        mLoadingDialog = new LoadingDialog(mContext) ;
        mLoadingDialog.setCancelable(false);

        mScrollLimitHeight = ResourceUtils.dip2px(mContext, 50);

        mRefreshLay = findViewById(R.id.acti_store_index_refresh_lay);
        mRecyclerView = findViewById(R.id.acti_store_index_rv);
        mFixLay = findViewById(R.id.ll_top_fixed_main_view);

        View customerLay = findViewById(R.id.acti_store_index_bot_customer_lay) ;
        View rightIv = findViewById(R.id.acti_store_index_right_iv) ;
        View searchLay = findViewById(R.id.acti_store_index_search_lay) ;
        View categoryLay = findViewById(R.id.acti_store_index_bot_category_lay) ;

        rightIv.setOnClickListener(this);
        searchLay.setOnClickListener(this);
        categoryLay.setOnClickListener(this);
        customerLay.setOnClickListener(this);

        initHeader() ;
    }

    private void initHeader(){
        WstGoodsRecycleViewJpgAdapter adapter = new WstGoodsRecycleViewJpgAdapter(mContext, mGoodList
                , new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    GoodList good = mGoodList.get(position - 1) ;
                    if(good != null){
                        String goodId = good.getProductID() ;
                        SkipUtils.toGoodDetailsActivity(mContext,goodId,true) ;
                    }
                }
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecyclerView.addItemDecoration(new RvGridManagerItemDecoration(mContext ,1,ResourceUtils.dip2px(mContext
                ,ResourceUtils.getXmlDef(mContext ,R.dimen.nomal_divider_height)))) ;
        mAdapter = new HeaderFooterAdapter<>(adapter);
        mRecyclerView.setAdapter(mAdapter);

        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_store_index ,null) ;
        mStoreLogoIv = headerView.findViewById(R.id.header_store_index_logo_iv) ;
        mStoreNameTv = headerView.findViewById(R.id.header_store_index_store_name_tv) ;
        mStoreTypeCompanyTv = headerView.findViewById(R.id.header_store_index_store_type_tv) ;
        mStoreTypeShopyTv = headerView.findViewById(R.id.header_store_index_store_type_shop_tv) ;
        View supportLay = headerView.findViewById(R.id.header_store_index_focus_count_lay) ;
        mSupportCountTv = headerView.findViewById(R.id.header_store_index_focus_count_tv) ;
        mSupportTv = headerView.findViewById(R.id.header_store_index_focus_count_tips_tv) ;
        mShareLay = headerView.findViewById(R.id.header_store_index_share_lay) ;
        View mOpRecomLay = headerView.findViewById(R.id.header_store_index_op_recommend_lay) ;
        mOpRecomDv = headerView.findViewById(R.id.header_store_index_op_recommend_dv) ;
        View mOpAllLay = headerView.findViewById(R.id.header_store_index_op_all_lay) ;
        mOpAllDv = headerView.findViewById(R.id.header_store_index_op_all_dv) ;
        View mOpInfoLay = headerView.findViewById(R.id.header_store_index_op_info_lay) ;
        mOpInfoDv = headerView.findViewById(R.id.header_store_index_op_info_dv) ;
        supportLay.setOnClickListener(this);

        mWebView = headerView.findViewById(R.id.header_store_index_info_wv) ;
        mWebView.setFocusable(false);

        mLoadingLay = headerView.findViewById(R.id.header_store_index_null_lay) ;
        mNullTv = headerView.findViewById(R.id.header_store_index_null_tv) ;
        mLoadingPb = headerView.findViewById(R.id.header_store_index_loading_pb) ;

        View topLay = headerView.findViewById(R.id.header_store_index_top_lay) ;

        mShareLay.setOnClickListener(this);
        mOpRecomLay.setOnClickListener(this);
        mOpAllLay.setOnClickListener(this);
        mOpInfoLay.setOnClickListener(this);

        mAdapter.addHeader(headerView);

        //415*170 = 83*34
        topLay.getLayoutParams().height = (int) (ResourceUtils.getScreenWidth(mContext) * 34 / 83F);
    }

    /**
     * 填充店铺信息
     */
    private void updateStoreInfo(){
        mLoadingDialog.dismiss();

        if(mStoreInfo != null){
            String name = mStoreInfo.getStoreName() ;
            String logo = mStoreInfo.getStoreLogo() ;

            mStoreNameTv.setText(name);
            GlideUtils.loader(logo ,mStoreLogoIv) ;

            if(mStoreInfo.isCompany()){
                mStoreTypeCompanyTv.setVisibility(View.VISIBLE);
            }
            if(mStoreInfo.isShop()){
                mStoreTypeShopyTv.setVisibility(View.VISIBLE);
            }

            updateSupportStyle() ;

            if(!"".equals(mStoreInfo.getStoreH5())){
                mWebView.loadUrl(mStoreInfo.getStoreH5()) ;
            }

            if(!mIsRefreshing){
                getGoodList() ;
            }
        }else{
            if(!mIsRefreshing){
                DialogUtils.showCustomViewDialog(mContext, "店铺信息获取失败，请返回重试"
                        , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish() ;
                            }
                        });
            }
        }

        if(mIsRefreshing){
            if(3 == mRecommendType){
                mIsRefreshing = false ;
                mRefreshLay.finishRefresh() ;
            }else{
                mCurPage = 1 ;
                mScrollY = 0 ;

                getGoodList();
            }
        }
    }

    /**
     * 更新关注状态
     */
    private void updateSupportStyle(){
        boolean isSupport = mStoreInfo.isSupport() ;
        String count = mStoreInfo.getSupportCount();
        mSupportCountTv.setText(count);

        if(isSupport){
            mSupportTv.setTextColor(getResources().getColor(R.color.color_grey_text));
        }else{
            mSupportTv.setTextColor(getResources().getColor(R.color.color_theme));
        }
    }

    @Override
    public void initData() {
        getStoreDetails() ;
    }

    @Override
    public void initListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollY += dy;

                float alpha = (float) mScrollY / mScrollLimitHeight;
                if (alpha >= 0.5F) {
                    alpha = 0.5F ;
                }
                mFixLay.setAlpha(alpha);
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefreshing = true ;

                getStoreDetails();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && mGoodList.size() > 5
                        && ViewUtil.isSlideToBottom(mRecyclerView)){
                    getGoodList() ;
                }
            }
        });

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.header_store_index_share_lay == vId){
            shareStoreInfo() ;
        }else if(R.id.header_store_index_focus_count_lay == vId){
            goCollection() ;
        }else if(R.id.header_store_index_op_recommend_lay == vId){
            changeGoodType(1) ;
        }else if(R.id.header_store_index_op_all_lay == vId){
            changeGoodType(2) ;
        }else if(R.id.header_store_index_op_info_lay == vId){
            changeGoodType(3) ;
        }else if(R.id.acti_store_index_bot_customer_lay == vId){//客服
            toCustomer() ;
        }else if (vId == R.id.acti_store_index_right_iv
                || R.id.acti_store_index_bot_category_lay == vId) {//分类
            Intent toCtIt = new Intent(mContext ,StoreGoodCategoryActivity.class) ;
            toCtIt.putExtra(SkipUtils.INTENT_STORE_ID ,mStoreId) ;
            startActivity(toCtIt) ;
        } else if (vId == R.id.acti_store_index_search_lay) {
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            startActivityForResult(toShIt , REQUEST_CODE_SEARCH) ;
        }
    }

    /**
     * 分享
     */
    private void shareStoreInfo(){
        if(mStoreInfo != null){
            ShareUtils.shareWeb(mActivity ,mStoreInfo.getStoreName() ,mStoreInfo.getShopShareUrl()
                    ,mStoreInfo.getStoreLogo() ,mStoreInfo.getStoreName());
        }
    }

    /**
     * 客服
     */
    private void toCustomer(){
        if("".equals(mStoreInfo.getStoreHxName())){
            ToastUtils.showToast(mContext ,"当前店铺未设置客服人员");
            return ;
        }

        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_CUSTOMER);
        }else{
            SkipUtils.toMessageChatActivity(mActivity ,mStoreInfo.getStoreHxName()) ;
        }
    }

    /**
     * 切换type
     * @param type type
     */
    private void changeGoodType(int type){
        if(type != mRecommendType){
            //还原
            switch (mRecommendType){
                case 1:
                    mOpRecomDv.setVisibility(View.GONE);
                    break;
                case 2:
                    mOpAllDv.setVisibility(View.GONE);
                    break;
                case 3:
                    mOpInfoDv.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }

            //更新
            switch (type){
                case 1:
                    mOpRecomDv.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    mOpAllDv.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    mOpInfoDv.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

            mRecommendType = type ;
        }

        if(3 == mRecommendType){
            //显示店铺信息
            if(mWebView.getVisibility() != View.VISIBLE){
                mWebView.setVisibility(View.VISIBLE);

                mCurPage = 1 ;
                mGoodList.clear();
                mAdapter.notifyDataSetChanged();
                mNullTv.setVisibility(View.GONE);
            }
        }else{
            //隐藏店铺信息
            if(mWebView.getVisibility() != View.GONE){
                mWebView.setVisibility(View.GONE);
            }

            mCurPage = 1 ;

            showOrHiddenLoading(true ,false);
            getGoodList() ;
        }
    }

    /**
     * 获取店铺详细
     */
    private void getStoreDetails(){
        if(!mIsRefreshing){
            mLoadingDialog.show();
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Code" , StringUtils.convertNull(mStoreId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.STORE_DETAILS, paramMap
                , new RequestObjectCallBack<StoreIndexInfo>("getStoreDetails", mContext , StoreIndexInfo.class) {
                    @Override
                    public void onSuccessResult(StoreIndexInfo bean) {
                        mStoreInfo = bean ;
                        updateStoreInfo() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        updateStoreInfo() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        updateStoreInfo() ;
                    }
                });
    }

    /**
     * 获取列表
     */
    private void getGoodList(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Sort" , "desc") ;
        paramMap.put("SortCode" , "CreateDate") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("MerchantID" , StringUtils.convertNull(mStoreInfo.getStoreId())) ;
        paramMap.put("MerchantIsRecommend" , 1 == mRecommendType ? "1" : "0") ;
        paramMap.put("AreaId" , MyApplication.getCurrentCityId()) ;
        paramMap.put("AreaType" ,MyApplication.getCurrentCityType()) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_DIRE_GOOD_LIST, paramMap
                , new RequestListCallBack<GoodList>("getGoodList" + mRecommendType , mContext , GoodList.class) {
                    @Override
                    public void onSuccessResult(List<GoodList> bean) {
                        if(1 == mCurPage){
                            mGoodList.clear();
                        }
                        mGoodList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged() ;

                        if(bean.size() == 0){
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


    /**
     * 完成请求
     */
    private void finishRequest(){
        mIsRefreshing = false ;
        mIsLoading = false ;
        mRefreshLay.finishRefresh() ;
        showOrHiddenLoading(false,mGoodList.size() == 0);
    }

    /**
     * show hidden
     * @param showLoad load
     * @param showNull null
     */
    private void showOrHiddenLoading(boolean showLoad ,boolean showNull){
        mLoadingLay.setVisibility(showLoad || showNull ? View.VISIBLE : View.GONE);
        mLoadingPb.setVisibility(showLoad ? View.VISIBLE : View.GONE);
        mNullTv.setVisibility(showNull ? View.VISIBLE : View.GONE);
    }


    /**
     * 收藏
     */
    private void goCollection(){
        if("".equals(SPUtils.newIntance(mContext).getToken())){
            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_COLLECTION) ;
            return ;
        }

        mLoadingDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("FollowType" , "2") ;
        paramMap.put("ResourceId" , mStoreInfo.getStoreId()) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, paramMap
                , new RequestObjectCallBack<CollectionResult>("goCollection" , mContext ,CollectionResult.class) {
                    @Override
                    public void onSuccessResult(CollectionResult bean) {
                        mLoadingDialog.dismiss();

                        if(bean != null){
                            boolean isCollected = 2 == bean.AddOrDelete ;
                            mStoreInfo.setSupportCount("" + bean.Number) ;
                            mStoreInfo.setSupport(!isCollected) ;
                            EventBus.getDefault().post(new BusEvent.StoreSupportEvent(!isCollected));
                            updateSupportStyle();
                        }
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

        if(REQUEST_CODE_COLLECTION == requestCode){
            if(RESULT_OK == resultCode){
                goCollection() ;
            }
        }else if(REQUEST_CODE_CUSTOMER == requestCode){
            if(RESULT_OK == resultCode){
                toCustomer() ;
            }
        }else if(REQUEST_CODE_SEARCH == requestCode){
            if(RESULT_OK == resultCode && data != null){
                String mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                SkipUtils.toSearchGoodList(mContext ,mSearchKey,mStoreId) ;
            }
        }
    }
}