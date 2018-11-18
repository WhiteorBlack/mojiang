package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.Bean.UnuseSpecList;
import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNomalCategoryHori;
import cn.idcby.jiajubang.adapter.AdapterUnuseSpecGood;
import cn.idcby.jiajubang.adapter.UnuseSpecAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvDecorationHiddenCallBack;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearMgItemWithHeaaFoot;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 闲置
 * Created on 2018/3/28.
 *
 * 2018-05-10 13:26:13
 * 分类布局样式改为单排滑动
 * 2018-07-25 15:07:39
 * 专场改为只展示图片即可
 */

public class UnusedActivity extends BaseActivity {
    private Activity mContext;

    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mListView ;

    private Banner mTopBanner ;
//    private ViewPager mCategoryVp ;//分类
//    private LinearLayout mCategoryIndLay ;
    private RecyclerView mCategoryRv ;//分类

    private RecyclerView mHotRv;//推荐

    private int mUnuseType = 1 ;//1最新 2 附近
    private TextView mLatestTv ;
    private View mLatestDv ;
    private TextView mNearlyTv ;
    private View mNearlyDv ;

    private List<AdvBanner> mTopBannerList = new ArrayList<>() ;

    private List<UnusedCategory> mCateList = new ArrayList<>() ;
    private AdapterNomalCategoryHori mAdapterCategory ;

//    //分类
//    private PageAdapterUnuseCategory mCategoryAdapter ;
//    private List<UnuseCategoryPre> mCateList = new ArrayList<>() ;
//    private static final int MAX_SHOW_COUNT = 8 ;//每一屏数量
//    private List<ImageView> mRecommendShopBottomImages = new ArrayList<>();

    //专场
    private int mSpecCurPage = 1 ;
    private boolean mSpecIsMore = true ;
    private boolean mSpecIsLoading = false ;
    private List<UnuseSpecList> mSpecList = new ArrayList<>() ;
    private UnuseSpecAdapter mSpecAdapter ;

    private static final int REQUEST_CODE_CREATE_UNUSE = 1001 ;
    private static final int REQUEST_CODE_SEARCH = 1002 ;

    private HeaderFooterAdapter<AdapterUnuseSpecGood> mUnusedWrapAdapter ;
    private AdapterUnuseSpecGood mAdapter ;
    private List<UnuseGoodList> mUnuseGoodList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private boolean mIsRefreshing = false ;

    private TextView mFooterTv ;
    private View mToTopIv ;

    private int mScreenHeight ;
    private int mScrollY = 0 ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_unused;
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
        if (i == R.id.acti_unused_search_lay) {
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            startActivityForResult(toShIt , REQUEST_CODE_SEARCH) ;
        } else if (i == R.id.acti_unused_create_unused_tv) {
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mContext,REQUEST_CODE_CREATE_UNUSE);
            }else{
                Intent toCtIt = new Intent(mContext ,UnusedSendActivity.class) ;
                startActivity(toCtIt) ;
            }
        }else if(R.id.header_unused_nav_latest_lay == i){
            changeListByType(1) ;
        }else if(R.id.header_unused_nav_nearly_lay == i){
            changeListByType(2) ;
        }else if(R.id.acti_lv_to_top_iv == i){
            mListView.scrollToPosition(0);
            mToTopIv.setVisibility(View.GONE);
            mScrollY = 0 ;
        }
    }

    /**
     * 初始化
     */
    private void initBaseView(){
        mContext = this ;

        StatusBarUtil.setTransparentForImageView(this,null) ;

        mScreenHeight = ResourceUtils.getScreenHeight(mContext) ;

        mRefreshLay = findViewById(R.id.acti_unused_parent_sv);
        mListView = findViewById(R.id.acti_unused_lv) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        //header
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_unused_item,null) ;
        mTopBanner = headerView.findViewById(R.id.acti_unused_banner_lay);
//        mCategoryVp = headerView.findViewById(R.id.header_unused_category_vp);
//        mCategoryIndLay = headerView.findViewById(R.id.header_unused_category_vp_indicator) ;
        mCategoryRv = headerView.findViewById(R.id.header_unused_category_rv) ;

        View mSearchLay = headerView.findViewById(R.id.acti_unused_search_lay) ;
        TextView mCreateNeedTv = headerView.findViewById(R.id.acti_unused_create_unused_tv) ;
        mHotRv = headerView.findViewById(R.id.header_unused_hot_rv) ;
        mHotRv.setFocusable(false);
        mHotRv.setNestedScrollingEnabled(false);

        mSearchLay.setOnClickListener(this);
        mCreateNeedTv.setOnClickListener(this);

        //分类
        mAdapterCategory = new AdapterNomalCategoryHori<>(mContext, true ,mCateList, 4.5F ,new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                UnusedCategory category = mCateList.get(position) ;
                if(category != null){
                    Intent toLiIt = new Intent(mContext , UnuseGoodListActivity.class) ;
                    toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID , category.getCategoryID()) ;
                    mContext.startActivity(toLiIt) ;
                }
            }
        }) ;
        LinearLayoutManager cateLayoutManager = new LinearLayoutManager(mContext) ;
        cateLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCategoryRv.setLayoutManager(cateLayoutManager);
        mCategoryRv.setAdapter(mAdapterCategory) ;

        //专场
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSpecAdapter = new UnuseSpecAdapter(mContext, mSpecList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                UnuseSpecList specList = mSpecList.get(position) ;
                if(specList != null){
                    String categoryId =specList.getProductSpecialID() ;
                    Intent toLiIt  =new Intent(mContext ,UnuseGoodListActivity.class) ;
                    toLiIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID,categoryId) ;
                    toLiIt.putExtra(SkipUtils.INTENT_UNUSE_TYPE_SPEC ,true) ;
                    startActivity(toLiIt) ;
                }
            }
        }) ;
        mHotRv.setLayoutManager(layoutManager);
        mHotRv.setAdapter(mSpecAdapter) ;

        //init top banner
        int screenWidth = ResourceUtils.getScreenWidth(mContext) ;
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

        View mNearlyLay = headerView.findViewById(R.id.header_unused_nav_nearly_lay) ;
        mNearlyTv = headerView.findViewById(R.id.header_unused_nav_nearly_tv) ;
        mNearlyDv = headerView.findViewById(R.id.header_unused_nav_nearly_dv) ;
        View mLatestLay = headerView.findViewById(R.id.header_unused_nav_latest_lay) ;
        mLatestTv = headerView.findViewById(R.id.header_unused_nav_latest_tv) ;
        mLatestDv = headerView.findViewById(R.id.header_unused_nav_latest_dv) ;
        mNearlyLay.setOnClickListener(this);
        mLatestLay.setOnClickListener(this);

        mAdapter = new AdapterUnuseSpecGood(mActivity, mUnuseGoodList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                UnuseGoodList goodList = mUnuseGoodList.get(position - 1) ;
                if(goodList != null){
                    if(0 == type){
                        Intent toDtIt = new Intent(mContext ,UnuseGoodDetailsActivity.class) ;
                        toDtIt.putExtra(SkipUtils.INTENT_UNUSE_ID,goodList.getProductID()) ;
                        startActivity(toDtIt) ;
                    }else if(1 == type){
                        SkipUtils.toOtherUserInfoActivity(mContext ,goodList.getCreateUserId());
                    }
                }
            }
        }) ;

        mListView.setLayoutManager(new LinearLayoutManager(mContext)) ;
        mUnusedWrapAdapter =new HeaderFooterAdapter<>(mAdapter) ;
        mListView.setAdapter(mUnusedWrapAdapter) ;

        RvDecorationHiddenCallBack hiddenCallBack = new RvDecorationHiddenCallBack() {
            @Override
            public boolean isHidden(int position) {
                int allCount = mUnuseGoodList.size() + 2 ;//header + footer

                if(0 == position || position >= allCount - 2){//包含了footer
                    return true ;
                }
                return false;
            }
        } ;
        RvLinearMgItemWithHeaaFoot itemDecoration = new RvLinearMgItemWithHeaaFoot(mContext
                , ResourceUtils.dip2px(mContext ,5) ,getResources().getDrawable(R.drawable.round_grey_f2_bg)
                ,hiddenCallBack) ;
        mListView.addItemDecoration(itemDecoration);
        mListView.setFocusable(false) ;
        mUnusedWrapAdapter.addHeader(headerView) ;

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mUnusedWrapAdapter.addFooter(mFooterTv) ;

        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && ViewUtil.isSlideToBottom(mListView)){
                    getUnuseList() ;
                }

                mScrollY += dy ;

                ViewUtil.setViewVisible(mToTopIv ,mScrollY > mScreenHeight);
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefreshing = true ;
                mSpecCurPage = 1 ;
                mCurPage = 1 ;
                getTopBanner() ;
            }
        });
    }

    /**
     * 切换
     */
    private void changeListByType(int type){
        if(mUnuseType == type){
            return ;
        }

        //还原之前
        switch (mUnuseType){
            case 1:
                mLatestTv.setTextColor(mContext.getResources().getColor(R.color.color_nav_normal_two)) ;
                mLatestDv.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mNearlyTv.setTextColor(mContext.getResources().getColor(R.color.color_nav_normal_two)) ;
                mNearlyDv.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

        //修改现在的
        switch (type){
            case 1:
                mLatestTv.setTextColor(mContext.getResources().getColor(R.color.color_nav_checked_two)) ;
                mLatestDv.setVisibility(View.VISIBLE);
                break;
            case 2:
                mNearlyTv.setTextColor(mContext.getResources().getColor(R.color.color_nav_checked_two)) ;
                mNearlyDv.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        mUnuseType = type ;

        mToTopIv.setVisibility(View.GONE);
        mScrollY = 0 ;

        mCurPage = 1 ;
        getUnuseList();
    }

    /**
     * 获取顶部
     */
    private void getTopBanner(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , "AppOldMallHead") ;

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
                            getUnuseSpec();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getUnuseSpec();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getUnuseSpec();
                        }
                    }
                });
    }

    /**
     * 获取专场
     */
    private void getUnuseSpec(){
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Keyword" , "2") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;

        mSpecIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_SPEC_LIST, paramMap
                , new RequestListCallBack<UnuseSpecList>("getUnuseSpec" , mContext , UnuseSpecList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseSpecList> bean) {
                        if(1 == mSpecCurPage){
                            mSpecList.clear();
                        }

                        mSpecList.addAll(bean) ;
                        mSpecAdapter.notifyDataSetChanged() ;

                        if(bean.size() == 0){
                            mSpecIsMore = false ;
                        }else{
                            mSpecIsMore = true ;
                            mSpecCurPage ++ ;
                        }

                        mSpecIsLoading = false ;

                        if(mIsRefreshing){
                            getUnuseList();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mSpecIsLoading = false ;

                        if(mIsRefreshing){
                            getUnuseList();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        mSpecIsLoading = false ;

                        if(mIsRefreshing){
                            getUnuseList();
                        }
                    }
                });
    }

    /**
     * 获取列表
     */
    private void getUnuseList(){
        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;

            mUnuseGoodList.clear();
            mUnusedWrapAdapter.notifyDataSetChanged() ;
        }

        String sortCode = "CreateDate" ;
        String sort = "desc" ;
        if(2 == mUnuseType){//附近的不传排序
            sortCode = "" ;
            sort = "" ;
        }

        //2018-04-27 10:18:58 注意：最新的和附近的，都是日期倒序
        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("SortCode" , sortCode) ;
        paramMap.put("Sort" , sort) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Longitude" , MyApplication.getLongitude()) ;
        paramMap.put("Latitude" , MyApplication.getLatitude()) ;
        paramMap.put("AreaId" , MyApplication.getCurrentCityId()) ;
        paramMap.put("AreaType" ,MyApplication.getCurrentCityType()) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_LIST, paramMap
                , new RequestListCallBack<UnuseGoodList>("getUnuseList" + mUnuseType , mContext , UnuseGoodList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseGoodList> bean) {
                        mUnuseGoodList.addAll(bean) ;
                        mUnusedWrapAdapter.notifyDataSetChanged() ;

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

        if(mIsRefreshing){
            mIsRefreshing = false ;
            mRefreshLay.finishRefresh() ;
        }

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mUnuseGoodList.size() == 0){
            mFooterTv.setText("暂无闲置") ;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTopBannerList.size() == 0 && !mIsRefreshing){
            getTopBanner() ;
        }
        if(mCateList.size() == 0 && !mIsRefreshing){
            getUnuseCategory() ;
        }

        if(mSpecList.size() == 0 && !mIsRefreshing){
            getUnuseSpec() ;
        }

        if(mUnuseGoodList.size() == 0 && !mIsRefreshing){
            getUnuseList() ;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_CREATE_UNUSE == requestCode){
            if(RESULT_OK == resultCode){
                Intent toCtIt = new Intent(mContext ,UnusedSendActivity.class) ;
                startActivity(toCtIt) ;
            }
        }else if(REQUEST_CODE_SEARCH == requestCode){
            if(RESULT_OK == resultCode && data != null){
                String mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                Intent toLiIt = new Intent(mContext ,UnuseGoodListActivity.class) ;
                toLiIt.putExtra(SkipUtils.INTENT_SEARCH_KEY ,mSearchKey) ;
                startActivity(toLiIt) ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getUnuseCategory");
        NetUtils.cancelTag("getTopBanner");
        NetUtils.cancelTag("getUnuseList" + mUnuseType);

    }
}
