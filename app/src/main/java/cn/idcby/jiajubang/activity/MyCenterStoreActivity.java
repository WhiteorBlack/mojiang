package cn.idcby.jiajubang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.Bean.StoreIndexInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterGoodOrderListPage;
import cn.idcby.jiajubang.adapter.MyGreenIndicatorAdapter;
import cn.idcby.jiajubang.adapter.WstGoodsRecycleViewJpgAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.fragment.FragmentGoodOrder;
import cn.idcby.jiajubang.fragment.FragmentMyOrderAfterSale;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
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
 * 厂家直供（我的店铺、我卖出的）
 * Created on 2018/5/18.
 */

public class MyCenterStoreActivity extends BaseActivity {
    private TextView mTypeStoreTv;
    private TextView mTypeOrderTv;

    private boolean mIsStore = true ;//我的店铺

    //我卖出的************************************************************************
    private View mOrderLay;
    private MagicIndicator mOrderIndicator;
    private ViewPager mOrderViewPager;

    //我的店铺************************************************************************
    private MaterialRefreshLayout mRefreshLay;
    private RecyclerView mRecyclerView;

    //header
    private ImageView mStoreLogoIv ;
    private TextView mStoreNameTv ;
    private TextView mStoreTypeCompanyTv;
    private TextView mStoreTypeShopyTv;
    private TextView mSupportCountTv ;

    private View mOpRecomDv;//推荐商品
    private View mOpAllDv;//全部商品
    private View mOpInfoDv;//店铺信息

    private WebViewNoScroll mWebView ;

    private View mLoadingLay ;
    private View mNullTv ;
    private View mLoadingPb ;

    private List<GoodList> mGoodList = new ArrayList<>();
    private HeaderFooterAdapter<WstGoodsRecycleViewJpgAdapter> mAdapter;

    private String mStoreId ;
    private StoreIndexInfo mStoreInfo ;
    private int mRecommendType = 1 ;// 1推荐 2全部 3详细

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private static final int REQUEST_CODE_SEARCH = 1002 ;

    private LoadingDialog mLoadingDialog ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_my_center_zg;
    }

    @Override
    public void initView() {
        mLoadingDialog = new LoadingDialog(mContext) ;
        mLoadingDialog.setCancelable(false);

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

        mTypeStoreTv = findViewById(R.id.acti_my_zg_center_type_store_tv) ;
        mTypeOrderTv = findViewById(R.id.acti_my_zg_center_type_order_tv) ;
        mTypeStoreTv.setOnClickListener(this);
        mTypeOrderTv.setOnClickListener(this);

        mOrderLay = findViewById(R.id.acti_my_zg_center_order_lay) ;
        mOrderIndicator = findViewById(R.id.acti_my_zg_center_order_indicator) ;
        mOrderViewPager = findViewById(R.id.acti_my_zg_center_order_vp) ;

        mRefreshLay = findViewById(R.id.acti_my_zg_center_refresh_lay);
        mRecyclerView = findViewById(R.id.acti_my_zg_center_rv);

        initStore() ;
        initOrder() ;
    }


    /**
     * 初始化 提供商品商品
     */
    private void initOrder(){
        String[] titles = {"全部","待付款", "待发货", "待收货", "待评价", "售后"};
        MyGreenIndicatorAdapter myIndicatorAdapter = new MyGreenIndicatorAdapter(titles
                , mOrderViewPager, LinePagerIndicator.MODE_WRAP_CONTENT);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(false);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(myIndicatorAdapter);
        mOrderIndicator.setNavigator(commonNavigator);

        List<Fragment> fragments = new ArrayList<>(titles.length) ;
        fragments.add(FragmentGoodOrder.getInstance(0 , FragmentGoodOrder.GOOD_ORDER_TYPE_GOOD)) ;
        fragments.add(FragmentGoodOrder.getInstance(1 , FragmentGoodOrder.GOOD_ORDER_TYPE_GOOD)) ;
        fragments.add(FragmentGoodOrder.getInstance(2 , FragmentGoodOrder.GOOD_ORDER_TYPE_GOOD)) ;
        fragments.add(FragmentGoodOrder.getInstance(3 , FragmentGoodOrder.GOOD_ORDER_TYPE_GOOD)) ;
        fragments.add(FragmentGoodOrder.getInstance(4 , FragmentGoodOrder.GOOD_ORDER_TYPE_GOOD)) ;
        fragments.add(FragmentMyOrderAfterSale.getInstance(FragmentMyOrderAfterSale.ORDER_AFTER_SALE_FROM_MY
                ,FragmentMyOrderAfterSale.ORDER_ITEM_TYPE_GOOD)) ;

        AdapterGoodOrderListPage mSubsOrderPageAdapter = new AdapterGoodOrderListPage(getSupportFragmentManager()
                ,titles ,fragments) ;

        mOrderViewPager.setAdapter(mSubsOrderPageAdapter);
        ViewPagerHelper.bind(mOrderIndicator, mOrderViewPager);
    }

    /**
     * 初始化店铺
     */
    private void initStore(){
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
        mRecyclerView.addItemDecoration(new RvGridManagerItemDecoration(mContext ,1, ResourceUtils.dip2px(mContext ,1))) ;
        mAdapter = new HeaderFooterAdapter<>(adapter);
        mRecyclerView.setAdapter(mAdapter);

        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_store_index_my ,null) ;
        mStoreLogoIv = headerView.findViewById(R.id.header_store_index_logo_iv) ;
        mStoreNameTv = headerView.findViewById(R.id.header_store_index_store_name_tv) ;
        mStoreTypeCompanyTv = headerView.findViewById(R.id.header_store_index_store_type_tv) ;
        mStoreTypeShopyTv = headerView.findViewById(R.id.header_store_index_store_type_shop_tv) ;
        mSupportCountTv = headerView.findViewById(R.id.header_store_index_focus_count_tv) ;
        View mOpRecomLay = headerView.findViewById(R.id.header_store_index_op_recommend_lay) ;
        mOpRecomDv = headerView.findViewById(R.id.header_store_index_op_recommend_dv) ;
        View mOpAllLay = headerView.findViewById(R.id.header_store_index_op_all_lay) ;
        mOpAllDv = headerView.findViewById(R.id.header_store_index_op_all_dv) ;
        View mOpInfoLay = headerView.findViewById(R.id.header_store_index_op_info_lay) ;
        mOpInfoDv = headerView.findViewById(R.id.header_store_index_op_info_dv) ;

        mWebView = headerView.findViewById(R.id.header_store_index_info_wv) ;
        mWebView.setFocusable(false);

        mLoadingLay = headerView.findViewById(R.id.header_store_index_null_lay) ;
        mNullTv = headerView.findViewById(R.id.header_store_index_null_tv) ;
        mLoadingPb = headerView.findViewById(R.id.header_store_index_loading_pb) ;

        View searchLay = headerView.findViewById(R.id.header_store_index_my_search_lay) ;
        View shareIv = headerView.findViewById(R.id.header_store_index_my_share_iv) ;

        shareIv.setOnClickListener(this);
        searchLay.setOnClickListener(this);
        mOpRecomLay.setOnClickListener(this);
        mOpAllLay.setOnClickListener(this);
        mOpInfoLay.setOnClickListener(this);

        mAdapter.addHeader(headerView);

    }

    /**
     * 填充店铺信息
     */
    private void updateStoreInfo(){
        mLoadingDialog.dismiss();

        if(mStoreInfo != null){
            String name = mStoreInfo.getStoreName() ;
            String logo = mStoreInfo.getStoreLogo() ;
            String count = mStoreInfo.getSupportCount() ;

            mStoreNameTv.setText(name);
            GlideUtils.loader(logo ,mStoreLogoIv) ;
            mSupportCountTv.setText(count);


            if(mStoreInfo.isCompany()){
                mStoreTypeCompanyTv.setVisibility(View.VISIBLE);
            }
            if(mStoreInfo.isShop()){
                mStoreTypeShopyTv.setVisibility(View.VISIBLE);
            }

            if(!"".equals(mStoreInfo.getStoreH5())){
                mWebView.loadUrl(mStoreInfo.getStoreH5()) ;
            }

            getGoodList() ;
        }else{
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

    @Override
    public void initData() {
        if(mStoreId != null){
            getStoreDetails() ;
        }
    }

    @Override
    public void initListener() {
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                if(3 == mRecommendType){
                    mRefreshLay.finishRefresh() ;
                }else{
                    mCurPage = 1 ;
                    getGoodList();
                }
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

        if(R.id.acti_my_zg_center_type_store_tv == vId){
            changeSendType(true) ;
        }else if(R.id.acti_my_zg_center_type_order_tv == vId){
            changeSendType(false) ;
        }else if(R.id.header_store_index_op_recommend_lay == vId){
            changeGoodType(1) ;
        }else if(R.id.header_store_index_op_all_lay == vId){
            changeGoodType(2) ;
        }else if(R.id.header_store_index_op_info_lay == vId){
            changeGoodType(3) ;
        } else if (vId == R.id.header_store_index_my_search_lay) {
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            startActivityForResult(toShIt , REQUEST_CODE_SEARCH) ;
        } else if (vId == R.id.header_store_index_my_share_iv) {
            ShareUtils.shareWeb(mActivity ,mStoreInfo.getStoreName(),mStoreInfo.getShopShareUrl() ,mStoreInfo.getStoreLogo() ,"");
        }

    }

    /**
     *  切换类型
     *  @param isStore is mIsStore
     */
    private void changeSendType(boolean isStore){
        if(isStore == mIsStore){
            return ;
        }

        if(mIsStore){
            mTypeStoreTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeStoreTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }else{
            mTypeOrderTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeOrderTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }

        if(isStore){
            mTypeStoreTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeStoreTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }else{
            mTypeOrderTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeOrderTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }

        mIsStore = isStore ;

        mOrderLay.setVisibility(mIsStore ? View.GONE : View.VISIBLE) ;
        mRefreshLay.setVisibility(mIsStore ? View.VISIBLE : View.GONE) ;
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
        mLoadingDialog.show();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_SEARCH == requestCode){
            if(RESULT_OK == resultCode && data != null){
                String mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                SkipUtils.toSearchGoodList(mContext ,mSearchKey ,mStoreId);
            }
        }
    }
}