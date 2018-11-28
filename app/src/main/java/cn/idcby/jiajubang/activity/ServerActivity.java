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
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNomalCategoryHori;
import cn.idcby.jiajubang.adapter.AdapterServerRecommentList;
import cn.idcby.jiajubang.application.MyApplication;
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
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 服务--安装、行业
 * Created on 2018/4/10.
 *
 * 2018-05-10 11:54:46
 * 分类改成单行滑动效果
 *
 * 2018-05-11 15:33:18
 * 当前服务列表获取，不再使用专场接口，使用常规列表接口（根据城市 时间倒序读取）
 *
 * 2018-05-24 21:54:04
 * 进入服务首页，还是读取专场列表
 */

public class ServerActivity extends BaseActivity {
    public static final int SERVER_TYPE_SERVER = 1 ;
    public static final int SERVER_TYPE_INSTALL = 2 ;

    private MaterialRefreshLayout mRefreshLay ;
    private ListView mLv ;

    private Banner mBanner ;
    private RecyclerView mCateRv;

    private AdapterServerRecommentList mAdapter ;
    private List<ServiceList> mDataList = new ArrayList<>() ;

    private List<AdvBanner> mTopBannerList = new ArrayList<>() ;
    private AdapterNomalCategoryHori mCategoryAdapter ;
    private List<ServerCategory> mServerCateList = new ArrayList<>() ;

    private int mServerType = SERVER_TYPE_SERVER ;
    private boolean mIsInstall = false ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private boolean mIsRefreshing = false ;

    private TextView mFooterTv ;
    private View mToTopIv ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_server;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTransparentForImageView(this,null);

        mServerType = getIntent().getIntExtra(SkipUtils.INTENT_SERVER_TYPE ,SERVER_TYPE_SERVER) ;

        mIsInstall = (SERVER_TYPE_SERVER != mServerType) ;

        mRefreshLay = findViewById(R.id.acti_server_refresh_lay) ;
        mLv = findViewById(R.id.acti_server_lv) ;
        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        initHeader() ;

        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置轮播时间
        mBanner.setDelayTime(5000);
        mBanner.setImageLoader(new BannerImageLoader()) ;
        mBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                SkipUtils.intentToOtherByAdvId(mContext ,mTopBannerList.get(position - 1)) ;
            }
        });
    }

    /**
     * 初始化header
     */
    private void initHeader(){
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_server_lay , null) ;
        mLv.addHeaderView(headerView);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv);

        mBanner = headerView.findViewById(R.id.acti_server_banner_lay) ;
        View searchLay = headerView.findViewById(R.id.acti_server_search_lay) ;
        TextView createLay = headerView.findViewById(R.id.acti_server_create_server_tv) ;
        mCateRv = headerView.findViewById(R.id.acti_server_type_gv) ;

        createLay.setText(mIsInstall ? "成为安装工" : "成为服务工");
        searchLay.setOnClickListener(this);
        createLay.setOnClickListener(this);

        mAdapter = new AdapterServerRecommentList(mContext, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    ServiceList serviceList = mDataList.get(position) ;
                    if(serviceList != null){
                        String userId = serviceList.getCreateUserId() ;
                        ServerDetailActivity.launch(mContext ,userId,mIsInstall) ;
                    }
                }
            }
        }) ;
        mLv.setAdapter(mAdapter);

        mBanner.getLayoutParams().height = (int) (ResourceUtils.getScreenWidth(mContext) / ImageWidthUtils.nomalBannerImageRote()) ;

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCateRv.setLayoutManager(layoutManager);
        mCategoryAdapter = new AdapterNomalCategoryHori<>(mContext,!mIsInstall , mServerCateList, 4.5F ,new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                ServerCategory category = mServerCateList.get(position) ;
                if(category != null){
                    Intent toLtIt = new Intent(mContext ,ServerListActivity.class) ;
                    toLtIt.putExtra(SkipUtils.INTENT_SERVER_TYPE ,mServerType) ;
                    toLtIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,category.ServiceCategoryID) ;
                    startActivity(toLtIt) ;
                }
            }
        }) ;
        mCateRv.setAdapter(mCategoryAdapter);
        mCateRv.setNestedScrollingEnabled(false);
        mCateRv.setFocusable(false);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(mIsMore && !mIsLoading && i2 > 5 && i + i1 >= i2){
                    getRecommendServer() ;
                }

                ViewUtil.setViewVisible(mToTopIv ,i > 10) ;
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                mIsRefreshing = true ;
                requestBanner() ;
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_server_search_lay == vId){//搜索
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            mActivity.startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_server_create_server_tv == vId){//创建服务
//            Intent toCtIt  = new Intent(mContext ,ServerSendActivity.class) ;
//            toCtIt.putExtra(SkipUtils.INTENT_SERVER_TYPE ,mServerType) ;
//            startActivity(toCtIt) ;
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity ,1004) ;
            }else{
                if(mIsInstall){
                    Intent toCtIt  = new Intent(mContext ,InstallApplyActivity.class) ;
                    startActivity(toCtIt) ;
                }else{
                    Intent toCtIt  = new Intent(mContext ,ServerApplyActivity.class) ;
                    startActivity(toCtIt) ;
                }
            }
        }else if(R.id.acti_lv_to_top_iv == vId){
            mLv.setSelection(0) ;
            mToTopIv.setVisibility(View.GONE);
        }
    }

    /**
     * 顶部banner
     */
    private void requestBanner() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Code", mIsInstall ? "AppInstallHead" : "AppServerHead");
        NetUtils.getDataFromServerByPost(mContext, Urls.API_ADVERT, false, para,
                new RequestListCallBack<AdvBanner>("getTopBanner", mContext, AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mTopBannerList.clear();
                        mTopBannerList.addAll(bean) ;
                        List<String> imageUrl = new ArrayList<>(mTopBannerList.size()) ;
                        for(AdvBanner banner : mTopBannerList){
                            imageUrl.add(banner.getImgUrl()) ;
                        }
                        mBanner.update(imageUrl) ;

                        if(mIsRefreshing){
                            requestServerCategory();
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            requestServerCategory();
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            requestServerCategory();
                        }
                    }
                });
    }

    /**
     * 服务分类
     */
    private void requestServerCategory() {
        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("id" ,mIsInstall ? "1" : "2") ;
        paramMap.put("Layer" ,"1") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_CATEGORY, false, paramMap,
                new RequestListCallBack<ServerCategory>("getServerCategory", mContext, ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        mServerCateList.clear();
                        mServerCateList.addAll(bean) ;
                        mCategoryAdapter.notifyDataSetChanged();

                        if(mIsRefreshing){
                            getRecommendServer() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            getRecommendServer() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            getRecommendServer() ;
                        }
                    }
                });
    }

    /**
     * 获取推荐
     */
    private void getRecommendServer(){
        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("AreaId", "" + MyApplication.getCurrentCityId());
        paramMap.put("AreaType", "" + MyApplication.getCurrentCityType());

        String urls = mIsInstall ? Urls.SERVER_INSTALL_LIST_RECOMMEND : Urls.SERVER_SERVER_LIST_RECOMMEND;


        NetUtils.getDataFromServerByPost(mContext, urls , paramMap
                , new RequestListCallBack<ServiceList>("getRecommendServer"
                        , mContext ,ServiceList.class) {
                    @Override
                    public void onSuccessResult(List<ServiceList> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                        }

                        mDataList.addAll(bean) ;
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

    private void finishRequest(){
        mIsLoading = false ;
        if(mIsRefreshing){
            mIsRefreshing = false ;
            mRefreshLay.finishRefresh() ;
        }

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mDataList.size() == 0){
            mFooterTv.setText("暂无服务") ;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTopBannerList.size() == 0){
            requestBanner() ;
        }

        if(mServerCateList.size() == 0){
            requestServerCategory() ;
        }

        if(mDataList.size() == 0){
            getRecommendServer() ;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1003 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                String mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                Intent toLiIt = new Intent(mContext ,ServerListActivity.class) ;
                toLiIt.putExtra(SkipUtils.INTENT_SEARCH_KEY ,mSearchKey) ;
                toLiIt.putExtra(SkipUtils.INTENT_SERVER_TYPE ,mServerType) ;
                startActivity(toLiIt) ;
            }
        }else if(1004 == requestCode){
            if(RESULT_OK == resultCode){
                if(mIsInstall){
                    Intent toCtIt  = new Intent(mContext ,InstallApplyActivity.class) ;
                    startActivity(toCtIt) ;
                }else{
                    Intent toCtIt  = new Intent(mContext ,ServerApplyActivity.class) ;
                    startActivity(toCtIt) ;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getServerCategory");
        NetUtils.cancelTag("getTopBanner");
        NetUtils.cancelTag("getRecommendServer");

    }
}
