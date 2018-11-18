package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.DirectGoodList;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.Bean.GoodListParent;
import cn.idcby.jiajubang.Bean.HomeStore;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNomalCategoryHori;
import cn.idcby.jiajubang.adapter.DirectLatestGoodPageAdapter;
import cn.idcby.jiajubang.adapter.WstGoodsRecycleViewAdapter;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvGridManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 厂家直供
 * Created on 2018/3/28.
 *
 * 2018-07-25 15:37:41
 * 专场改为前2个只展示3个图片，不显示商品相关信息，第三个正常显示；
 * 前2个点击跳转到专场列表，第三个正常商品；
 *
 * 2018-08-17 18:29:44
 * 改界面
 *
 * 2018-10-25 11:13:16
 * 拼单专场暂时隐藏，xml里面设置了gone
 */
public class DirectSellingActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private Activity mContext;

    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRecyclerView;

    private Banner mTopBanner ;

    private List<AdvBanner> mTopBannerList = new ArrayList<>() ;
    private List<AdvBanner> mMidBannerList = new ArrayList<>() ;
    private List<UnusedCategory> mCateList = new ArrayList<>() ;
    private AdapterNomalCategoryHori mAdapterCategory ;

    private List<DirectGoodList> mAllGoodList = new ArrayList<>() ;//专场商品总集合

    private List<GoodList> mGoodList = new ArrayList<>();
    private HeaderFooterAdapter<WstGoodsRecycleViewAdapter> mDirectWrapAdapter;

    private List<HomeStore> mStoreList = new ArrayList<>() ;
    private List<GoodList> mPindanGoodList = new ArrayList<>() ;
    private List<GoodList> mHotGoodList = new ArrayList<>() ;
    private List<GoodList> mLatestGoodList = new ArrayList<>() ;

    private static final int REQUEST_CODE_OPEN_STORE = 1001 ;
    private static final int REQUEST_CODE_SEARCH = 1002 ;
    private static final int REQUEST_CODE_CART = 1003 ;

    private boolean mIsRefreshing = false ;

    //新品推荐
    public static final int MAX_GOOD_SHOW = 3 ;//一屏显示商品个数
    private List<GoodListParent> mLatestGoodParentList = new ArrayList<>() ;
    private DirectLatestGoodPageAdapter mLatestGoodPageAdapter;
    private List<ImageView> mLatestGoodBotPointList = new ArrayList<>() ;


    //header多组合商品相关
    private ImageView mMidBanIv ;

    //店铺
    private View mStoreMoreTv ;
    private View mStoreOneLay ;
    private TextView mStoreOneTitleTv ;
    private TextView mStoreOneCountTv ;
    private ImageView mStoreOneImageOneIv;
    private ImageView mStoreOneImageTwoIv ;
    private View mStoreTwoLay ;
    private TextView mStoreTwoTitleTv ;
    private TextView mStoreTwoCountTv ;
    private ImageView mStoreTwoImageOneIv;
    private ImageView mStoreTwoImageTwoIv ;

    //专场
    private View mFirstMoreTv;
    private TextView mFirstSpecTitleTv;
    private View mSecondMoreTv;
    private TextView mSecondSpecTitleTv;
    private View mThirdMoreTv;
    private TextView mThirdSpecTitleTv;
    private View mFourthMoreTv;
    private TextView mFourSpecTitleTv;

    private View mLeftGoodLay ;
    private ImageView mLeftGoodIv ;
    private TextView mLeftGoodTv ;
    private View mMidGoodLay ;
    private ImageView mMidGoodIv ;
    private TextView mMidGoodTv ;
    private View mRightGoodLay ;
    private ImageView mRightGoodIv ;
    private TextView mRightGoodTv ;

    private ImageView mHotLeftIv;
    private ImageView mHotMidIv;
    private ImageView mHotRightIv;

    //新品
    private ViewPager mLatestGoodVp;
    private LinearLayout mLatestGoodBotLay;

    private TextView mFooterTv ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_direct_selling;
    }

    @Override
    public void initView() {
        initBaseView() ;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        if (i == R.id.acti_direct_selling_search_lay) {
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            startActivityForResult(toShIt , REQUEST_CODE_SEARCH) ;
        } else if (i == R.id.acti_direct_selling_open_tv) {
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mContext, REQUEST_CODE_OPEN_STORE);
            }else{
                Intent toCtIt = new Intent(mContext ,OpenStoreActivity.class) ;
                startActivity(toCtIt) ;
            }
        } else if(R.id.title_name_item_one_all_tv == i || R.id.header_spec_item_one_lay == i){//专场一更多
            if(mAllGoodList.size() > 0){
                DirectGoodList goodList = mAllGoodList.get(0) ;
                if(goodList != null){
                    Intent toLiIt = new Intent(mContext ,DirectGoodListsActivity.class) ;
                    toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,goodList.getProductSpecialID()) ;
                    startActivity(toLiIt) ;
                }
            }
        }else if(R.id.title_name_item_hot_all_tv == i || R.id.adapter_direct_good_hot_lay == i){//专场二更多
            if(mAllGoodList.size() > 1){
                DirectGoodList goodList = mAllGoodList.get(1) ;
                if(goodList != null){
                    Intent toLiIt = new Intent(mContext ,DirectGoodListsActivity.class) ;
                    toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,goodList.getProductSpecialID()) ;
                    startActivity(toLiIt) ;
                }
            }
        }else if(R.id.title_name_item_news_all_tv == i){//专场三更多
            if(mAllGoodList.size() > 2){
                DirectGoodList goodList = mAllGoodList.get(2) ;
                if(goodList != null){
                    Intent toLiIt = new Intent(mContext ,DirectGoodListsActivity.class) ;
                    toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,goodList.getProductSpecialID()) ;
                    startActivity(toLiIt) ;
                }
            }
        }else if(R.id.title_name_item_sells_all_tv == i){//专场四更多
            if(mAllGoodList.size() > 3){
                DirectGoodList goodList = mAllGoodList.get(3) ;
                if(goodList != null){
                    Intent toLiIt = new Intent(mContext ,DirectGoodListsActivity.class) ;
                    toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,goodList.getProductSpecialID()) ;
                    startActivity(toLiIt) ;
                }
            }
        }else if(R.id.title_name_item_store_all_tv == i){//店铺更多

            NearStoreActivity.launch(mContext ,true) ;

        }else if (i == R.id.acti_good_cart_iv) {
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mContext, REQUEST_CODE_CART);
            }else{
                Intent toCtIt = new Intent(mContext ,ShoppingCartActivity.class) ;
                startActivity(toCtIt) ;
            }
        }
    }

    /**
     * 初始化
     */
    private void initBaseView(){
        mContext = this ;

        EventBus.getDefault().register(this) ;
        StatusBarUtil.setTransparentForImageView(this,null) ;

        mRefreshLay = findViewById(R.id.acti_direct_selling_parent_sv);
        mRecyclerView = findViewById(R.id.acti_direct_selling_lv) ;

        View cartIv = findViewById(R.id.acti_good_cart_iv) ;
        cartIv.setOnClickListener(this);

        //header
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_direct_selling_item,null) ;

        WstGoodsRecycleViewAdapter goodAdapter = new WstGoodsRecycleViewAdapter(mActivity, mGoodList
                , new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                GoodList goodList = mGoodList.get(position) ;
                if(goodList != null){
                    if(0 == type){
                        SkipUtils.toGoodDetailsActivity(mContext,goodList.getProductID()) ;
                    }
                }
            }
        }) ;

        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2)) ;
        mRecyclerView.addItemDecoration(new RvGridManagerItemDecoration(mContext ,1,1
                ,ResourceUtils.dip2px(mContext ,ResourceUtils.getXmlDef(mContext ,R.dimen.nomal_divider_height))
                ,getResources().getDrawable(R.drawable.drawable_white_trans))) ;
        mDirectWrapAdapter =new HeaderFooterAdapter<>(goodAdapter) ;
        mRecyclerView.setAdapter(mDirectWrapAdapter) ;
        mRecyclerView.setFocusable(false) ;
        mDirectWrapAdapter.addHeader(headerView) ;
        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mDirectWrapAdapter.addFooter(mFooterTv) ;

        mTopBanner = headerView.findViewById(R.id.acti_direct_selling_banner_lay);
        RecyclerView mCategoryRv = headerView.findViewById(R.id.header_direct_selling_category_rv) ;
        mCategoryRv.setFocusable(false);

        View mSearchLay = headerView.findViewById(R.id.acti_direct_selling_search_lay) ;
        TextView mOpenStoreTv = headerView.findViewById(R.id.acti_direct_selling_open_tv) ;
        mSearchLay.setOnClickListener(this);
        mOpenStoreTv.setOnClickListener(this);

        mMidBanIv = headerView.findViewById(R.id.header_direct_selling_mid_ban_iv) ;

        mFirstSpecTitleTv = headerView.findViewById(R.id.title_name_item_one);
        mFirstMoreTv = headerView.findViewById(R.id.title_name_item_one_all_tv);
        mSecondSpecTitleTv = headerView.findViewById(R.id.title_name_item_hot);
        mSecondMoreTv = headerView.findViewById(R.id.title_name_item_hot_all_tv);
        mThirdSpecTitleTv = headerView.findViewById(R.id.title_name_item_news);
        mThirdMoreTv = headerView.findViewById(R.id.title_name_item_news_all_tv);
        mFourSpecTitleTv = headerView.findViewById(R.id.title_name_item_sells);
        mFourthMoreTv = headerView.findViewById(R.id.title_name_item_sells_all_tv);

        mFourthMoreTv.setOnClickListener(this);
        mThirdMoreTv.setOnClickListener(this);
        mSecondMoreTv.setOnClickListener(this);
        mFirstMoreTv.setOnClickListener(this);

        View  mPindanLay = headerView.findViewById(R.id.header_spec_item_one_lay) ;
        mLeftGoodLay = headerView.findViewById(R.id.header_direct_selling_pindan_left_lay) ;
        mLeftGoodIv = headerView.findViewById(R.id.header_direct_selling_pindan_left_iv) ;
        mLeftGoodTv = headerView.findViewById(R.id.header_direct_selling_pindan_left_price_tv) ;
        mMidGoodLay = headerView.findViewById(R.id.header_direct_selling_pindan_mid_lay) ;
        mMidGoodIv = headerView.findViewById(R.id.header_direct_selling_pindan_mid_iv) ;
        mMidGoodTv = headerView.findViewById(R.id.header_direct_selling_pindan_mid_price_tv) ;
        mRightGoodLay = headerView.findViewById(R.id.header_direct_selling_pindan_right_lay) ;
        mRightGoodIv = headerView.findViewById(R.id.header_direct_selling_pindan_right_iv) ;
        mRightGoodTv = headerView.findViewById(R.id.header_direct_selling_pindan_right_price_tv) ;
        mPindanLay.setOnClickListener(this);

        View mHotMainLay = headerView.findViewById(R.id.adapter_direct_good_hot_lay) ;
        mHotLeftIv = headerView.findViewById(R.id.adapter_direct_good_hot_top_left_iv) ;
        mHotMidIv = headerView.findViewById(R.id.adapter_direct_good_hot_top_right_one_iv) ;
        mHotRightIv = headerView.findViewById(R.id.adapter_direct_good_hot_top_right_two_iv) ;
        mHotMainLay.setOnClickListener(this);

        mLatestGoodVp = headerView.findViewById(R.id.header_direct_selling_latest_good_vp) ;
        mLatestGoodBotLay = headerView.findViewById(R.id.header_direct_selling_latest_point_indicator) ;

        mStoreMoreTv = headerView.findViewById(R.id.title_name_item_store_all_tv) ;
        mStoreOneLay = headerView.findViewById(R.id.header_direct_selling_store_one_lay) ;
        mStoreOneTitleTv = headerView.findViewById(R.id.header_direct_selling_store_one_title_tv) ;
        mStoreOneCountTv = headerView.findViewById(R.id.header_direct_selling_store_one_count_tv) ;
        mStoreOneImageOneIv = headerView.findViewById(R.id.header_direct_selling_store_one_top_left_iv) ;
        mStoreOneImageTwoIv = headerView.findViewById(R.id.header_direct_selling_store_one_top_right_one_lay) ;
        mStoreTwoLay = headerView.findViewById(R.id.header_direct_selling_store_two_lay) ;
        mStoreTwoTitleTv = headerView.findViewById(R.id.header_direct_selling_store_two_title_tv) ;
        mStoreTwoCountTv = headerView.findViewById(R.id.header_direct_selling_store_two_count_tv) ;
        mStoreTwoImageOneIv = headerView.findViewById(R.id.header_direct_selling_store_two_top_left_iv) ;
        mStoreTwoImageTwoIv = headerView.findViewById(R.id.header_direct_selling_store_two_top_right_one_lay) ;
        mStoreMoreTv.setOnClickListener(this);

        int screenWidth = ResourceUtils.getScreenWidth(mContext) ;

        //mid Banner 365 : 70
        mMidBanIv.getLayoutParams().height = (int) ((screenWidth - ResourceUtils.dip2px(mContext ,5) * 2) / 5.2F);

        //专场一
        int lefGoodHei = (screenWidth - ResourceUtils.dip2px(mContext ,2)) / 3 ;
        mLeftGoodIv.getLayoutParams().height = (int) (lefGoodHei / 1F);
        mMidGoodIv.getLayoutParams().height = (int) (lefGoodHei / 1F) ;
        mRightGoodIv.getLayoutParams().height = (int) (lefGoodHei / 1F) ;

        //专场二
        int secHei = (screenWidth - ResourceUtils.dip2px(mContext ,2)) / 3 ;
        mHotLeftIv.getLayoutParams().height = (int) (secHei / 1F);
        mHotMidIv.getLayoutParams().height = (int) (secHei / 1F);
        mHotRightIv.getLayoutParams().height = (int) (secHei / 1F);

        //专场三
        mLatestGoodPageAdapter = new DirectLatestGoodPageAdapter(mContext, mLatestGoodParentList);
        mLatestGoodVp.setAdapter(mLatestGoodPageAdapter);
        mLatestGoodVp.setFocusable(false);
        mLatestGoodBotLay.setFocusable(false);
        mLatestGoodVp.addOnPageChangeListener(this);

        //店铺推荐
        int mImgWidHei = (int) ((screenWidth - ResourceUtils.dip2px(mContext ,15)
                - ResourceUtils.dip2px(mContext ,3)* 2) / 4F);
        mStoreOneImageOneIv.getLayoutParams().width = mImgWidHei ;
        mStoreOneImageOneIv.getLayoutParams().height = mImgWidHei;
        mStoreTwoImageOneIv.getLayoutParams().width = mImgWidHei ;
        mStoreTwoImageOneIv.getLayoutParams().height = mImgWidHei;

        //分类
        mAdapterCategory = new AdapterNomalCategoryHori<>(mContext,true, mCateList, 4.5F ,new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                UnusedCategory category = mCateList.get(position) ;
                if(category != null){
                    SkipUtils.toNomalGoodList(mContext,category.getCategoryID(),category.getCategoryTitle());
                }
            }
        }) ;
        LinearLayoutManager cateLayoutManager = new LinearLayoutManager(mContext) ;
        cateLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCategoryRv.setLayoutManager(cateLayoutManager);
        mCategoryRv.setAdapter(mAdapterCategory) ;

        //init top banner
        int height = (int) (screenWidth / ImageWidthUtils.nomalBannerImageRote());
        mTopBanner.getLayoutParams().height = height ;

        mTopBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                SkipUtils.intentToOtherByAdvId(mContext ,mTopBannerList.get(position - 1)) ;
            }
        });
        //设置banner动画效果
        mTopBanner.setBannerAnimation(Transformer.Default);
        //设置轮播时间
        mTopBanner.setDelayTime(5000);
        mTopBanner.setImageLoader(new BannerImageLoader()) ;

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefreshing = true ;
                getTopBanner() ;
            }
        });

    }


    private void addPointIndicator() {
        int size = mLatestGoodParentList.size();

        mLatestGoodBotPointList.clear();
        mLatestGoodBotLay.removeAllViews();

        int widHei = ResourceUtils.dip2px(mContext ,ResourceUtils.getXmlDef(mContext ,R.dimen.nomal_page_bot_point_size)) ;
        int itemMargin = ResourceUtils.dip2px(mContext ,ResourceUtils.getXmlDef(mContext ,R.dimen.nomal_page_bot_point_margin)) ;
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widHei, widHei);
            params.setMargins(itemMargin, 0, itemMargin, 0);
            imageView.setLayoutParams(params);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.shape_indicator_select);
            } else {
                imageView.setBackgroundResource(R.drawable.shape_indicator_no_select);
            }

            mLatestGoodBotPointList.add(imageView);
            //把指示作用的原点图片加入底部的视图中
            mLatestGoodBotLay.addView(imageView);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        int size = mLatestGoodBotPointList.size() ;
        position = position % size;
        for (int i = 0; i < size; i++) {
            if (i == position) {
                mLatestGoodBotPointList.get(i).setBackgroundResource(R.drawable.shape_indicator_select);
            } else {
                mLatestGoodBotPointList.get(i).setBackgroundResource(R.drawable.shape_indicator_no_select);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 填充推荐店铺
     */
    private void updateStoreInfo(){
        int storeSize = mStoreList.size() ;

        ViewUtil.setViewVisible(mStoreMoreTv,storeSize > 0) ;
        ViewUtil.setViewVisible(mStoreOneLay,storeSize > 0) ;
        ViewUtil.setViewVisible(mStoreTwoLay,storeSize > 1) ;

        if(storeSize > 0){
            HomeStore store = mStoreList.get(0) ;
            if(store != null) {
                final String storeId = store.getShopID();
                String storeName = store.getShopName();
                String storeCount = store.getProductCount();

                mStoreOneTitleTv.setText(storeName);
                mStoreOneCountTv.setText("共" + storeCount + "件商品");

                String imageOne = "";
                String imageTwo = "";
                List<ImageThumb> imgList = store.getProductAlbumsList();
                if (imgList != null && imgList.size() > 0) {
                    imageOne = imgList.get(0).getThumbImgUrl();
                    if (imgList.size() > 1) {
                        imageTwo = imgList.get(1).getThumbImgUrl();
                    }
                }

                GlideUtils.loaderGoodImage(imageOne, mStoreOneImageOneIv);
                GlideUtils.loaderGoodImage(imageTwo, mStoreOneImageTwoIv);

                mStoreOneLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SkipUtils.toStoreIndexActivity(mContext, storeId);
                    }
                });
            }
        }

        if(storeSize > 1){
            HomeStore store = mStoreList.get(1) ;
            if(store != null) {
                final String storeId = store.getShopID();
                String storeName = store.getShopName();
                String storeCount = store.getProductCount();

                mStoreTwoTitleTv.setText(storeName);
                mStoreTwoCountTv.setText("共" + storeCount + "件商品");

                String imageOne = "";
                String imageTwo = "";
                List<ImageThumb> imgList = store.getProductAlbumsList();
                if (imgList != null && imgList.size() > 0) {
                    imageOne = imgList.get(0).getThumbImgUrl();
                    if (imgList.size() > 1) {
                        imageTwo = imgList.get(1).getThumbImgUrl();
                    }
                }

                GlideUtils.loaderGoodImage(imageOne, mStoreTwoImageOneIv);
                GlideUtils.loaderGoodImage(imageTwo, mStoreTwoImageTwoIv);

                mStoreTwoLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SkipUtils.toStoreIndexActivity(mContext, storeId);
                    }
                });
            }
        }
    }

    /**
     * 填充顶部内容
      */
    private void updateTopGoodLay(){
        //专场一
        int pintuanSize = mPindanGoodList.size() ;

        ViewUtil.setViewVisible(mFirstMoreTv,pintuanSize > 0) ;
        ViewUtil.setViewVisible(mLeftGoodLay,pintuanSize > 0) ;
        ViewUtil.setViewVisible(mMidGoodLay,pintuanSize > 1) ;
        ViewUtil.setViewVisible(mRightGoodLay,pintuanSize > 2) ;

        if(pintuanSize > 0){
            final GoodList good = mPindanGoodList.get(0) ;
            mLeftGoodTv.setText("¥" + good.getSalePrice());
            GlideUtils.loaderGoodImage(good.getImgUrl(),mLeftGoodIv);

//            mLeftGoodLay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    SkipUtils.toGoodDetailsActivity(mContext ,good.getProductID()) ;
//                }
//            });
        }
        if(pintuanSize > 1){
            final GoodList good = mPindanGoodList.get(1) ;
            mMidGoodTv.setText("¥" + good.getSalePrice());
            GlideUtils.loaderGoodImage(good.getImgUrl(),mMidGoodIv);

//            mMidGoodLay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    SkipUtils.toGoodDetailsActivity(mContext ,good.getProductID()) ;
//                }
//            });
        }
        if(pintuanSize > 2){
            final GoodList good = mPindanGoodList.get(2) ;
            mRightGoodTv.setText("¥" + good.getSalePrice());
            GlideUtils.loaderGoodImage(good.getImgUrl(),mRightGoodIv);

//            mRightGoodLay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    SkipUtils.toGoodDetailsActivity(mContext ,good.getProductID()) ;
//                }
//            });
        }

        //专场二
        int hotSize = mHotGoodList.size() ;
        ViewUtil.setViewVisible(mSecondMoreTv,hotSize > 0) ;
        ViewUtil.setViewVisible(mHotLeftIv,hotSize > 0) ;
        ViewUtil.setViewVisible(mHotMidIv,hotSize > 1) ;
        ViewUtil.setViewVisible(mHotRightIv,hotSize > 2) ;

        if(hotSize > 0){
            final GoodList good = mHotGoodList.get(0) ;
            GlideUtils.loaderGoodImage(good.getImgUrl(), mHotLeftIv);
//            mHotLeftIv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    SkipUtils.toGoodDetailsActivity(mContext ,good.getProductID()) ;
//                }
//            });
        }

        if(hotSize > 1){
            final GoodList good = mHotGoodList.get(1) ;
            GlideUtils.loaderGoodImage(good.getImgUrl(), mHotMidIv);
//            mHotMidIv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    SkipUtils.toGoodDetailsActivity(mContext ,good.getProductID()) ;
//                }
//            });
        }
        if(hotSize > 2){
            final GoodList good = mHotGoodList.get(2) ;
            GlideUtils.loaderGoodImage(good.getImgUrl(), mHotRightIv);
//            mHotRightIv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    SkipUtils.toGoodDetailsActivity(mContext ,good.getProductID()) ;
//                }
//            });
        }

        //专场三
        //处理分组
        mLatestGoodParentList.clear();
        mLatestGoodPageAdapter.notifyDataSetChanged();

        int latestSize = mLatestGoodList.size() ;
        ViewUtil.setViewVisible(mThirdMoreTv,latestSize > 0) ;

        if(latestSize <= MAX_GOOD_SHOW){
            if(latestSize > 0){
                GoodListParent parentStore = new GoodListParent() ;
                parentStore.setChildGood(mLatestGoodList) ;
                mLatestGoodParentList.add(parentStore);
            }
        }else{
            GoodListParent parentStore = new GoodListParent();

            for(int x = 0 ; x < latestSize ; x ++){
                if(x% MAX_GOOD_SHOW == 0){
                    if(x > 0){
                        mLatestGoodParentList.add(parentStore) ;
                    }

                    parentStore = new GoodListParent();
                    parentStore.addGoodToList(mLatestGoodList.get(x));
                }else{
                    parentStore.addGoodToList(mLatestGoodList.get(x)) ;
                }

                if(x == latestSize - 1){//最后一个，而且不是开始
                    mLatestGoodParentList.add(parentStore) ;
                }
            }
        }

        mLatestGoodPageAdapter.notifyDataSetChanged();
        addPointIndicator();
    }

    /**
     * 获取顶部
     */
    private void getTopBanner(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , "AppNewMallHead") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, paramMap
                , new RequestListCallBack<AdvBanner>("行业闲置广告轮播" ,mContext , AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mTopBannerList.clear();
                        mTopBannerList.addAll(bean) ;

                        List<String> imageUrl = new ArrayList<>(bean.size()) ;
                        for(AdvBanner banner : mTopBannerList){
                            imageUrl.add(banner.getImgUrl()) ;
                        }

                        mTopBanner.update(imageUrl) ;

                        if(mIsRefreshing){
                            getMidBanner() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getMidBanner() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getMidBanner() ;
                        }
                    }
                });
    }

    /**
     * 获取Mid
     */
    private void getMidBanner(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , "AppProductMiddle") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, paramMap
                , new RequestListCallBack<AdvBanner>("getMidBanner" ,mContext , AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mMidBannerList.clear();
                        mMidBannerList.addAll(bean) ;

                        ViewUtil.setViewVisible(mMidBanIv,mMidBannerList.size() > 0);

                        if(mMidBannerList.size() > 0){
                            final AdvBanner banner = mMidBannerList.get(0) ;
                            if(banner != null){
                                GlideUtils.loaderRound(banner.getImgUrl(),mMidBanIv,10) ;
                                mMidBanIv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        SkipUtils.intentToOtherByAdvId(mContext ,banner) ;
                                    }
                                });
                            }
                        }

                        if(mIsRefreshing){
                            getUnuseCategory() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getUnuseCategory() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getUnuseCategory() ;
                        }
                    }
                });
    }

    /**
     * 获取分类
     */
    private void getUnuseCategory(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Keyword", "");
        paramMap.put("Id", "1");
        NetUtils.getDataFromServerByPost(mContext, Urls.GOOD_CATEGORY_LIST, paramMap
                , new RequestListCallBack<UnusedCategory>("闲置分类" ,mContext , UnusedCategory.class) {
                    @Override
                    public void onSuccessResult(List<UnusedCategory> bean) {
                        mCateList.clear();
                        mCateList.addAll(bean) ;
                        mAdapterCategory.notifyDataSetChanged() ;

                        if(mIsRefreshing){
                            requestAppStoreHead();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            requestAppStoreHead();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            requestAppStoreHead();
                        }
                    }
                });
    }

    /**
     * 推荐店铺
     */
    private void requestAppStoreHead() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("PageSize", "2");
        para.put("Page", "1");
        NetUtils.getDataFromServerByPost(mContext, Urls.API_HOME_COMMENT_STORE, false, para,
                new RequestListCallBack<HomeStore>("requestAppStoreHead", mContext, HomeStore.class) {
                    @Override
                    public void onSuccessResult(List<HomeStore> bean) {
                        //处理分组
                        mStoreList.clear();
                        mStoreList.addAll(bean) ;

                        updateStoreInfo() ;

                        if(mIsRefreshing){
                            getSpecGoodList() ;
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {

                        if(mIsRefreshing){
                            getSpecGoodList() ;
                        }
                    }

                    @Override
                    public void onFail(Exception e) {

                        if(mIsRefreshing){
                            getSpecGoodList() ;
                        }
                    }
                });
    }


    /**
     * 获取列表
     */
    private void getSpecGoodList(){
        mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;

        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("Page", "1");
        paramMap.put("PageSize", "18");
        paramMap.put("Keyword", "");
        NetUtils.getDataFromServerByPost(mContext, Urls.DIRECT_SPEC_LIST, paramMap
                , new RequestListCallBack<DirectGoodList>("getSpecGoodList", mContext , DirectGoodList.class) {
                    @Override
                    public void onSuccessResult(List<DirectGoodList> bean) {
                        mAllGoodList.clear() ;
                        mAllGoodList.addAll(bean) ;

                        //专场一
                        mPindanGoodList.clear();
                        if(mAllGoodList.size() > 0){
                            DirectGoodList goodList = mAllGoodList.get(0) ; 
                            if(goodList != null){
                                String title = goodList.getTitle() ;
                                mFirstSpecTitleTv.setText(title);
                                mPindanGoodList.addAll(goodList.getSpecialGoodList()) ;
                            }
                        }

                        mHotGoodList.clear();
                        if(mAllGoodList.size() > 1){
                            DirectGoodList goodList = mAllGoodList.get(1) ;
                            if(goodList != null){
                                String title = goodList.getTitle() ;
                                mSecondSpecTitleTv.setText(title);
                                mHotGoodList.addAll(goodList.getSpecialGoodList()) ;
                            }
                        }

                        mLatestGoodList.clear();
                        if(mAllGoodList.size() > 2){
                            DirectGoodList goodList = mAllGoodList.get(2) ;
                            if(goodList != null){
                                String title = goodList.getTitle() ;
                                mThirdSpecTitleTv.setText(title);
                                mLatestGoodList.addAll(goodList.getSpecialGoodList()) ;
                            }
                        }
                        updateTopGoodLay() ;

                        mGoodList.clear();
                        if(mAllGoodList.size() > 3){
                            DirectGoodList goodList = mAllGoodList.get(3) ;
                            if(goodList != null){
                                String title = goodList.getTitle() ;
                                mFourSpecTitleTv.setText(title);
                                List<GoodList> specList = goodList.getSpecialGoodList() ;
                                if(specList.size() > 4){
                                    for(int x = 0 ; x < 4 ; x ++){
                                        GoodList good = specList.get(x) ;
                                        mGoodList.add(good) ;
                                    }
                                }else{
                                    mGoodList.addAll(goodList.getSpecialGoodList()) ;
                                }
                            }
                        }
                        mDirectWrapAdapter.notifyDataSetChanged() ;

                        ViewUtil.setViewVisible(mFourthMoreTv ,mGoodList.size() > 0);

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
        mFooterTv.setText(getResources().getString(R.string.footer_no_string));

        if(mIsRefreshing){
            mIsRefreshing = false ;
            mRefreshLay.finishRefresh() ;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTopBannerList.size() == 0 && !mIsRefreshing){
            getTopBanner() ;
        }
        if(mMidBannerList.size() == 0 && !mIsRefreshing){
            getMidBanner() ;
        }
        if(mCateList.size() == 0 && !mIsRefreshing){
            getUnuseCategory() ;
        }

        if(mStoreList.size() == 0 && !mIsRefreshing){
            requestAppStoreHead() ;
        }

        if(mGoodList.size() == 0 && !mIsRefreshing){
            getSpecGoodList() ;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_OPEN_STORE == requestCode){
            if(RESULT_OK == resultCode){
                Intent toCtIt = new Intent(mContext ,OpenStoreActivity.class) ;
                startActivity(toCtIt) ;
            }
        }else if(REQUEST_CODE_SEARCH == requestCode){
            if(RESULT_OK == resultCode && data != null){
                String mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                SkipUtils.toSearchGoodList(mContext ,mSearchKey) ;
            }
        }else if(REQUEST_CODE_CART == requestCode){
            if(RESULT_OK == resultCode){
                Intent toCtIt = new Intent(mContext ,ShoppingCartActivity.class) ;
                startActivity(toCtIt) ;
            }
        }
    }

    //由于新品推荐，是ViewPager格式的，但是不知道为啥viewPager不执行onClick，所以只能item点击了
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateHxLoginState(BusEvent.DirectSellingLatestEvent ev){
        if(ev.isClicked()){
            if(mAllGoodList.size() > 2){
                DirectGoodList goodList = mAllGoodList.get(2) ;
                if(goodList != null){
                    Intent toLiIt = new Intent(mContext ,DirectGoodListsActivity.class) ;
                    toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,goodList.getProductSpecialID()) ;
                    startActivity(toLiIt) ;
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        NetUtils.cancelTag("getUnuseCategory");
        NetUtils.cancelTag("getTopBanner");
        NetUtils.cancelTag("getMidBanner");
        NetUtils.cancelTag("requestAppStoreHead");

    }
}
