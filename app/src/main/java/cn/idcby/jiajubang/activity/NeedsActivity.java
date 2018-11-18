package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.NeedsCategory;
import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNeedsCategory;
import cn.idcby.jiajubang.adapter.AdapterNeedsList;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.StationaryGridView;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created on 2018/3/28.
 */

public class NeedsActivity extends BaseActivity{
    private Activity mContext;

    private MaterialRefreshLayout mRefreshLay ;
    private ListView mListView ;

    private Banner mTopBanner ;
    private StationaryGridView mNeedsCategoryGv;//需求分类

    private List<AdvBanner> mTopBannerList = new ArrayList<>() ;
    private List<NeedsCategory> mCateList = new ArrayList<>() ;
    private AdapterNeedsCategory mCategoryAdapter ;

    private static final int REQUEST_CODE_CREATE_NEEDS = 1001 ;
    private static final int REQUEST_CODE_SEARCH = 1002 ;


    private AdapterNeedsList mAdapter ;
    private List<NeedsList> mNeedsList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private boolean mIsRefreshing = false ;

    private int mNeedsType = 1 ;
    private TextView mLatestTv ;
    private View mLatestDv ;
    private TextView mNearlyTv ;
    private View mNearlyDv ;
    private TextView mFinishTv ;
    private View mFinishDv ;

    private TextView mFooterTv ;
    private View mToTopIv ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_needs;
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
    }

    /**
     * 初始化
     */
    private void initBaseView(){
        mContext = this ;

        StatusBarUtil.setTransparentForImageView(this ,null) ;

        mRefreshLay = findViewById(R.id.acti_needs_parent_sv);
        mListView = findViewById(R.id.acti_needs_lv) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(new ViewClickListener());

        //header
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_needs_item,null) ;
        mTopBanner = headerView.findViewById(R.id.acti_needs_banner_lay);

        View mSearchLay = headerView.findViewById(R.id.acti_needs_search_lay) ;
        TextView mCreateNeedTv = headerView.findViewById(R.id.acti_needs_create_needs_tv) ;
        mNeedsCategoryGv = headerView.findViewById(R.id.acti_needs_type_gv) ;
        mSearchLay.setOnClickListener(new ViewClickListener());
        mCreateNeedTv.setOnClickListener(new ViewClickListener());

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

        View mNearlyLay = headerView.findViewById(R.id.header_needs_nav_nearly_lay) ;
        mNearlyTv = headerView.findViewById(R.id.header_needs_nav_nearly_tv) ;
        mNearlyDv = headerView.findViewById(R.id.header_needs_nav_nearly_dv) ;
        View mLatestLay = headerView.findViewById(R.id.header_needs_nav_latest_lay) ;
        mLatestTv = headerView.findViewById(R.id.header_needs_nav_latest_tv) ;
        mLatestDv = headerView.findViewById(R.id.header_needs_nav_latest_dv) ;
        View mFinishLay = headerView.findViewById(R.id.header_needs_nav_finish_lay) ;
        mFinishTv = headerView.findViewById(R.id.header_needs_nav_finish_tv) ;
        mFinishDv = headerView.findViewById(R.id.header_needs_nav_finish_dv) ;
        mNearlyLay.setOnClickListener(new ViewClickListener());
        mLatestLay.setOnClickListener(new ViewClickListener());
        mFinishLay.setOnClickListener(new ViewClickListener());

        mListView.addHeaderView(headerView) ;

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mListView.addFooterView(mFooterTv) ;

        mAdapter = new AdapterNeedsList(mContext , mNeedsList) ;
        mListView.setAdapter(mAdapter) ;

        mListView.setFocusable(false) ;

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount
                    , int totalItemCount) {

                if(!mIsLoading && mIsMore
                        && totalItemCount > 5 && visibleItemCount + firstVisibleItem >= totalItemCount){
                    getNeedList();
                }

                ViewUtil.setViewVisible(mToTopIv ,firstVisibleItem > 10);
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefreshing = true ;

                mCurPage = 1 ;
                getTopBanner() ;
            }
        });
    }

    /**
     * 点击监听
     */
    private class ViewClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.acti_needs_search_lay) {
                Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
                startActivityForResult(toShIt , REQUEST_CODE_SEARCH) ;
            } else if (i == R.id.acti_needs_create_needs_tv) {
                if(LoginHelper.isNotLogin(mContext)){
                    SkipUtils.toLoginActivityForResult(mContext,REQUEST_CODE_CREATE_NEEDS);
                }else{
                    Intent toCtIt = new Intent(mContext ,NeedsSendActivity.class) ;
                    startActivity(toCtIt) ;
                }
            }else if(R.id.header_needs_nav_latest_lay == i){
                changeListByType(1) ;
            }else if(R.id.header_needs_nav_nearly_lay == i){
                changeListByType(2) ;
            }else if(R.id.header_needs_nav_finish_lay == i){
                changeListByType(3) ;
            }else if(R.id.acti_lv_to_top_iv == i){
               mToTopIv.setVisibility(View.GONE);
               mListView.setSelection(0);
            }
        }
    }

    /**
     * 切换
     */
    private void changeListByType(int type){
        if(mNeedsType == type){
            return ;
        }

        //还原之前
        switch (mNeedsType){
            case 1:
                mLatestTv.setTextColor(mContext.getResources().getColor(R.color.color_nav_normal_two)) ;
                mLatestDv.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mNearlyTv.setTextColor(mContext.getResources().getColor(R.color.color_nav_normal_two)) ;
                mNearlyDv.setVisibility(View.INVISIBLE);
                break;
            case 3:
                mFinishTv.setTextColor(mContext.getResources().getColor(R.color.color_nav_normal_two)) ;
                mFinishDv.setVisibility(View.INVISIBLE);
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
            case 3:
                mFinishTv.setTextColor(mContext.getResources().getColor(R.color.color_nav_checked_two)) ;
                mFinishDv.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        mNeedsType = type ;

        mToTopIv.setVisibility(View.GONE);
        mListView.setSelection(0) ;

        mCurPage = 1 ;
        getNeedList();
    }

    /**
     * 获取顶部
     */
    private void getTopBanner(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , "AppNeedHead") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, paramMap
                , new RequestListCallBack<AdvBanner>("getTopBanner" ,mContext , AdvBanner.class) {
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
                            getNeedsCategory() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getNeedsCategory() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getNeedsCategory() ;
                        }
                    }
                });
    }

    /**
     * 获取需求分类
     */
    private void getNeedsCategory(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_CATEGORY, paramMap
                , new RequestListCallBack<NeedsCategory>("getNeedsCategory" ,mContext , NeedsCategory.class) {
                    @Override
                    public void onSuccessResult(List<NeedsCategory> bean) {
                        mCateList.clear();
                        mCateList.addAll(bean) ;

                        if(null == mCategoryAdapter){
                            mCategoryAdapter = new AdapterNeedsCategory(mContext , mCateList) ;
                            mNeedsCategoryGv.setAdapter(mCategoryAdapter);
                            mNeedsCategoryGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    NeedsCategory category = mCateList.get(i) ;
                                    if(category != null){
                                        String cateId = category.NeedCategoryID ;

                                        Intent toLvIt = new Intent(mContext ,NeedsListActivity.class) ;
                                        toLvIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID , cateId) ;
                                        mContext.startActivity(toLvIt) ;
                                    }
                                }
                            });
                        }else{
                            mCategoryAdapter.notifyDataSetChanged() ;
                        }

                        if(mIsRefreshing){
                            getNeedList();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getNeedList();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getNeedList();
                        }
                    }
                });
    }

    /**
     * 获取需求列表
     */
    private void getNeedList(){
        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;

            mNeedsList.clear();
            mAdapter.notifyDataSetChanged() ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("TypeId" , "" + mNeedsType) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Longitude" , MyApplication.getLongitude()) ;
        paramMap.put("Latitude" , MyApplication.getLatitude()) ;
        paramMap.put("AreaId" , MyApplication.getCurrentCityId()) ;
        paramMap.put("AreaType" ,MyApplication.getCurrentCityType()) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_LIST, paramMap
                , new RequestListCallBack<NeedsList>("getNeedList" , mContext , NeedsList.class) {
                    @Override
                    public void onSuccessResult(List<NeedsList> bean) {

                        mNeedsList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged() ;

                        if(bean.size() < 10){
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
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mNeedsList.size() == 0){
            mFooterTv.setText("暂无需求");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTopBannerList.size() == 0 && !mIsRefreshing){
            getTopBanner() ;
        }
        if(mCateList.size() == 0 && !mIsRefreshing){
            getNeedsCategory() ;
        }
        if(mNeedsList.size() == 0 && !mIsRefreshing){
            getNeedList() ;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_CREATE_NEEDS == requestCode){
            if(RESULT_OK == resultCode){
                Intent toCtIt = new Intent(mContext ,NeedsSendActivity.class) ;
                startActivity(toCtIt) ;
            }
        }else if(REQUEST_CODE_SEARCH == requestCode){
            if(RESULT_OK == resultCode && data != null){
                String mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                Intent toLiIt = new Intent(mContext ,NeedsListActivity.class) ;
                toLiIt.putExtra(SkipUtils.INTENT_SEARCH_KEY ,mSearchKey) ;
                startActivity(toLiIt) ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getNeedsCategory");
        NetUtils.cancelTag("getTopBanner");

    }
}
