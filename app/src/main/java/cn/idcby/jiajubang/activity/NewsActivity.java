package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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
import cn.idcby.jiajubang.Bean.NewsCategory;
import cn.idcby.jiajubang.Bean.NewsList;
import cn.idcby.jiajubang.Bean.NomalRvCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNomalOptionCategory;
import cn.idcby.jiajubang.adapter.HomeHotNewsAdapter;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 资讯
 * 2018-06-19 10:23:46
 *
 * 改：添加回到顶部、到底了提示
 *
 * 2018-06-26 15:10:34
 * 改：仿头条分类展示，右边加上按钮，点击加载全部分类，能点击
 *
 * 2018-08-01 15:54:55
 * 添加一个固定分类，推荐
 */
public class NewsActivity extends BaseActivity{
    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv ;

    private Banner mTopBanner ;
    private RecyclerView mCategoryRv ;

    private List<AdvBanner> mTopBannerList = new ArrayList<>() ;
    private ArrayList<NewsList> mNewsList = new ArrayList<>();
    private HomeHotNewsAdapter mAdapter;

    private List<NomalRvCategory> mShowRvCateList = new ArrayList<>() ;
    private List<NewsCategory> mNewsCategoryList = new ArrayList<>() ;
    private AdapterNomalOptionCategory mShowCateAdapter  ;
    private LinearLayoutManager mCateManager ;

    private int mCurPosition = 0 ;
    private String categoryID = "";
    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private boolean mIsRefreshing = false ;

    private TextView mFooterTv ;
    private View mToTopIv ;

    private static final int REQUEST_CODE_ALL_CATEGORY = 1000 ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_news;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTransparentForImageView(this,null) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mRefreshLay = findViewById(R.id.acti_news_parent_sv);
        mLv = findViewById(R.id.acti_news_lv) ;

        mToTopIv.setOnClickListener(this);

        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_news_item ,null) ;

        View mCateIv = headerView.findViewById(R.id.header_news_category_iv) ;
        mCateIv.setOnClickListener(this);

        mTopBanner = headerView.findViewById(R.id.header_news_banner_lay);
        mCategoryRv = headerView.findViewById(R.id.header_news_category_rv);

        int screenWidth = ResourceUtils.getScreenWidth(mContext) ;
        int height = (int) (screenWidth / ImageWidthUtils.nomalBannerImageRote());
        mTopBanner.getLayoutParams().height = height ;

        mLv.addHeaderView(headerView);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv) ;

        mAdapter = new HomeHotNewsAdapter(mContext, mNewsList);
        mLv.setAdapter(mAdapter);

        mLv.setFocusable(false) ;

        mShowCateAdapter = new AdapterNomalOptionCategory(mContext, mShowRvCateList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    if(mCurPosition == position){
                        return ;
                    }

                    mShowRvCateList.get(mCurPosition).setSelected(false);
                    mShowRvCateList.get(position).setSelected(true);
                    mShowCateAdapter.notifyDataSetChanged() ;

                    mCurPosition = position ;
                    resetLvContent() ;
                }
            }
        }) ;
        mCateManager = new LinearLayoutManager(mContext) ;
        mCateManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCategoryRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext ,ResourceUtils.dip2px(mContext ,10)
                ,getResources().getDrawable(R.drawable.drawable_white_trans)
                ,RvLinearManagerItemDecoration.HORIZONTAL_LIST)) ;
        mCategoryRv.setLayoutManager(mCateManager);
        mCategoryRv.setAdapter(mShowCateAdapter) ;

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                mIsRefreshing = true ;

                getTopBanner() ;
            }
        });

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount
                    , int totalItemCount) {

                if(!mIsLoading && mIsMore
                        && totalItemCount > 5 && visibleItemCount + firstVisibleItem >= totalItemCount){

                    getNewsList();
                }

                if(firstVisibleItem > 5){
                    if(mToTopIv.getVisibility() != View.VISIBLE){
                        mToTopIv.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(mToTopIv.getVisibility() != View.GONE){
                        mToTopIv.setVisibility(View.GONE);
                    }
                }
            }
        });

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
    }

    @Override
    public void initData() {
        getTopBanner();
        requestTopBarData();
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        if(R.id.acti_lv_to_top_iv == view.getId()){
            mLv.setSelection(0) ;
            mToTopIv.setVisibility(View.GONE);
        }else if(R.id.header_news_category_iv == view.getId()){
            NewsCategoryActivity.launch(mActivity ,mNewsCategoryList,mCurPosition ,REQUEST_CODE_ALL_CATEGORY);
        }
    }

    /**
     * 根据分类切换显示
     */
    private void resetLvContent(){
        if(mCurPosition < mNewsCategoryList.size()){
            categoryID = mNewsCategoryList.get(mCurPosition).getCategoryID() ;

            mCurPage = 1 ;
            getNewsList() ;
        }else{
           if(mIsRefreshing){
               mRefreshLay.finishRefresh() ;
           }
        }
    }

    /**
     * 获取顶部
     */
    private void getTopBanner(){
        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Code" , "AppNewsHead") ;
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
                            requestTopBarData();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            requestTopBarData();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            requestTopBarData();
                        }
                    }
                });
    }

    private void requestTopBarData() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", "News");
        NetUtils.getDataFromServerByPost(mContext, Urls.NEWS_CATEGORY_LIST, false, para,
                new RequestListCallBack<NewsCategory>("行业新闻标题栏", mContext, NewsCategory.class) {
                    @Override
                    public void onSuccessResult(List<NewsCategory> bean) {
                        mNewsCategoryList.clear();
                        mNewsCategoryList.add(new NewsCategory("" ,"推荐")) ;
                        mNewsCategoryList.addAll(bean) ;

                        mCurPosition = 0 ;
                        mShowRvCateList.clear();
                        int size = mNewsCategoryList.size() ;
                        for(int x = 0 ; x < size ; x ++){
                            NewsCategory category = mNewsCategoryList.get(x) ;
                            if(category.getCategoryID().equals(categoryID)){
                                mCurPosition = x ;
                            }
                            mShowRvCateList.add(new NomalRvCategory(category)) ;
                        }

                        mShowCateAdapter.notifyDataSetChanged() ;
                        if(mCurPosition < mShowRvCateList.size()){
                            mShowRvCateList.get(mCurPosition).setSelected(true) ;
                            ViewUtil.moveToPosition(mCateManager,mCategoryRv ,mCurPosition);
                        }

                        resetLvContent() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            resetLvContent() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            resetLvContent() ;
                        }
                    }
                });
    }

    /**
     * 获取列表
     */
    private void getNewsList(){
        mIsLoading = true ;

        if(1 == mCurPage){
            mNewsList.clear();
            mAdapter.notifyDataSetChanged();

            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Page", String.valueOf(mCurPage));
        paramMap.put("PageSize", "10");

        String url = Urls.NEWS_LIST ;
        if(!"".equals(categoryID)){
            paramMap.put("CategoryID", categoryID);
        }else{
            url = Urls.NEWS_LIST_HOT ;
        }

        NetUtils.getDataFromServerByPost(mContext, url, paramMap
                , new RequestListCallBack<NewsList>("requestNewsList" , mContext , NewsList.class) {
                    @Override
                    public void onSuccessResult(List<NewsList> bean) {
                        mNewsList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mCurPage ++ ;
                            mIsMore = true ;
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_ALL_CATEGORY == requestCode){
            if(RESULT_OK == resultCode && data != null){
                int position = data.getIntExtra(SkipUtils.INTENT_VP_INDEX ,mCurPosition) ;
                if(position < mNewsCategoryList.size()){
                    mShowRvCateList.get(mCurPosition).setSelected(false);
                    mShowRvCateList.get(position).setSelected(true);
                    mShowCateAdapter.notifyDataSetChanged() ;
                    mCategoryRv.scrollToPosition(position) ;

                    mCurPosition = position ;
                    resetLvContent() ;
                }
            }
        }

    }
}
