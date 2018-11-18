package cn.idcby.jiajubang.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.Bean.HomeStore;
import cn.idcby.jiajubang.Bean.HomeStoreParent;
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.Bean.UserNear;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.NearJobListActivity;
import cn.idcby.jiajubang.activity.NearServerListActivity;
import cn.idcby.jiajubang.activity.NearStoreActivity;
import cn.idcby.jiajubang.activity.NearUserListActivity;
import cn.idcby.jiajubang.activity.ServerActivity;
import cn.idcby.jiajubang.activity.ServerDetailActivity;
import cn.idcby.jiajubang.activity.ServerListActivity;
import cn.idcby.jiajubang.activity.UnuseGoodDetailsActivity;
import cn.idcby.jiajubang.activity.UnuseGoodListActivity;
import cn.idcby.jiajubang.adapter.AdapterNearServer;
import cn.idcby.jiajubang.adapter.AdapterNearUser;
import cn.idcby.jiajubang.adapter.AdapterNomalCategoryHori;
import cn.idcby.jiajubang.adapter.AdapterUnuseSpecGood;
import cn.idcby.jiajubang.adapter.HomeRecommendShopAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.OnLocationRefresh;
import cn.idcby.jiajubang.interf.RvDecorationHiddenCallBack;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.RvLinearMgItemWithHeaaFoot;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created on 2018/2/3.
 * 附近
 *
 * 2018-05-05 16:15:00
 * 调整：暂时隐藏商家
 *
 * 2018-07-25 15:08:27
 * 附近工作，只展示图片，不展示文字
 */

public class NearFragment extends BaseFragment implements View.OnClickListener{
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRv ;

    private TextView mLocationTv ;

    private RecyclerView mHotServerRv;
    private RecyclerView mInstallRv;
    private ImageView mInstallRefreshIv ;

    private ViewPager mHotStoreRv;
    private ImageView mHotStoreRefreshIv ;

    private RecyclerView mCircleUserRv;
    private ImageView mCircleUserRefreshIv ;

    //附近服务--展示服务分类
    private AdapterNomalCategoryHori mCategoryAdapter ;
    private List<ServerCategory> mServerCateList = new ArrayList<>() ;

    //附近安装大师
    private int mNearInstallPage = 1 ;
    private List<ServiceList> mNearInstallList = new ArrayList<>() ;
    private AdapterNearServer mNearInstallAdapter;

    //附近的人
    private int mNearUserPage = 1 ;
    private List<UserNear> mNearUserList = new ArrayList<>() ;
    private AdapterNearUser mNearUserAdapter ;


    //附近店铺
    private int mNearStorePage = 1 ;
    public static final int MAX_STORE_SHOW = 2 ;//首页一屏显示店铺个数
    private List<HomeStoreParent> mNearShopList = new ArrayList<>() ;
    private HomeRecommendShopAdapter mNearShopAdapter;

    //附近闲置
    private List<UnuseGoodList> mUnuseGoodList = new ArrayList<>() ;
    private HeaderFooterAdapter<AdapterUnuseSpecGood> mUnusedWrapAdapter;

    private boolean mIsInit = false ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private boolean mIsRefreshing = false ;

    private int mScrollY = 0 ;
    private int mScreenHeight ;

    private TextView mFooterTv ;
    private View mToTopIv ;


    private OnLocationRefresh mLocationRefreshListener;
    public void setLocationRefreshListener(OnLocationRefresh mLocationRefreshListener) {
        this.mLocationRefreshListener = mLocationRefreshListener;
    }

    public void setCurLocation(String locationDesc){
        if(locationDesc != null){
            mLocationTv.setText(locationDesc);
        }else{
            mLocationTv.setText("正在定位");
        }
    }

    @Override
    protected void requestData() {
        mIsInit = true ;

        loadPage.showSuccessPage();
    }

    @Override
    protected void initView(View view) {

        mScreenHeight = ResourceUtils.getScreenHeight(mContext) ;

        View statusView = view.findViewById(R.id.frag_near_status_view);
        statusView.getLayoutParams().height = ResourceUtils.getStatusBarHeight(mContext) ;
        statusView.setBackgroundColor(StatusBarUtil.DEFAULT_STATUS_BAR_COLOR);

        mRv = view.findViewById(R.id.frag_near_lv) ;
        mRefreshLay = view.findViewById(R.id.frag_near_refresh_lay) ;

        mToTopIv = view.findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        initHeader() ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_near;
    }

    @Override
    protected void initListener() {

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsRefreshing = true ;
                mCurPage = 1 ;
                mNearInstallPage = 1 ;
                mNearUserPage = 1 ;
                mNearStorePage = 1 ;

                requestNearServerHead() ;
            }
        });

        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && !mIsRefreshing && mIsMore
                        && mUnuseGoodList.size() > 5 && ViewUtil.isSlideToBottom(mRv)){
                    getUnuseList() ;
                }

                mScrollY += dy ;
                ViewUtil.setViewVisible(mToTopIv ,mScrollY > mScreenHeight * 2) ;
            }
        });
    }

    private void initHeader(){
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_near_item ,null) ;

        View locationLay = headerView.findViewById(R.id.header_near_item_location_lay) ;
        mLocationTv = headerView.findViewById(R.id.frag_near_location_tv) ;
        locationLay.setOnClickListener(this);

        View serverLay = headerView.findViewById(R.id.frag_near_server_lay) ;
        mHotServerRv = headerView.findViewById(R.id.frag_near_server_rv) ;
        mHotServerRv.setFocusable(false);

        View serverInstallLay = headerView.findViewById(R.id.frag_near_server_install_lay) ;
        mInstallRv = headerView.findViewById(R.id.frag_near_server_install_rv) ;
        View installChangeLay = headerView.findViewById(R.id.frag_near_server_install_change_lay) ;
        mInstallRefreshIv = headerView.findViewById(R.id.frag_near_server_install_change_iv) ;
        mInstallRv.setFocusable(false);

        View hotStoreLay = headerView.findViewById(R.id.frag_near_hot_store_lay) ;
        mHotStoreRv = headerView.findViewById(R.id.frag_near_hot_store_rv) ;
        View storeChangeLay = headerView.findViewById(R.id.frag_near_hot_store_change_lay) ;
        mHotStoreRefreshIv = headerView.findViewById(R.id.frag_near_hot_store_change_iv) ;
        mHotStoreRv.setFocusable(false);

        View workLay = headerView.findViewById(R.id.frag_near_work_lay) ;
        ImageView halfWorkIv = headerView.findViewById(R.id.frag_near_work_half_iv) ;
        ImageView allWorkIv = headerView.findViewById(R.id.frag_near_work_all_iv) ;

        View circleUserLay = headerView.findViewById(R.id.frag_near_circle_user_lay) ;
        mCircleUserRv = headerView.findViewById(R.id.frag_near_circle_user_rv) ;
        View userChangeLay = headerView.findViewById(R.id.frag_near_circle_user_change_lay) ;
        mCircleUserRefreshIv = headerView.findViewById(R.id.frag_near_circle_user_change_iv) ;
        mCircleUserRv.setFocusable(false);

        View unusedLay = headerView.findViewById(R.id.frag_near_unused_lay) ;

        serverLay.setOnClickListener(this);
        serverInstallLay.setOnClickListener(this);
        installChangeLay.setOnClickListener(this);
        hotStoreLay.setOnClickListener(this);
        storeChangeLay.setOnClickListener(this);
        workLay.setOnClickListener(this);
        halfWorkIv.setOnClickListener(this);
        allWorkIv.setOnClickListener(this);
        circleUserLay.setOnClickListener(this);
        userChangeLay.setOnClickListener(this);
        unusedLay.setOnClickListener(this);

        int screenWidth = ResourceUtils.getScreenWidth(mContext) ;
        int imgWid = (screenWidth - ResourceUtils.dip2px(mContext ,10) * 3) / 2 ;
        int imgHei = (int) (imgWid / 2.5F);

        halfWorkIv.getLayoutParams().width = imgWid ;
        halfWorkIv.getLayoutParams().height = imgHei ;
        allWorkIv.getLayoutParams().width = imgWid ;
        allWorkIv.getLayoutParams().height = imgHei ;

        initHotServerData() ;
        initInstallServerData() ;
        initHomeRecommendShopData() ;
        initNearUserData() ;

        mRv.setLayoutManager(new LinearLayoutManager(mContext)) ;
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
                , ResourceUtils.dip2px(mContext ,ResourceUtils.getXmlDef(mContext ,R.dimen.nomal_divider_height))
                ,getResources().getDrawable(R.drawable.drawable_white_trans)
                ,hiddenCallBack) ;
        mRv.addItemDecoration(itemDecoration);
        AdapterUnuseSpecGood unusedAdapter = new AdapterUnuseSpecGood(mActivity
                , mUnuseGoodList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                UnuseGoodList goodList = mUnuseGoodList.get(position - 1) ;//有个header
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
        });
        mUnusedWrapAdapter = new HeaderFooterAdapter<>(unusedAdapter) ;
        mRv.setAdapter(mUnusedWrapAdapter);

        mUnusedWrapAdapter.addHeader(headerView) ;

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mUnusedWrapAdapter.addFooter(mFooterTv) ;
    }

    /**
     * 推荐店铺
     */
    private void initHomeRecommendShopData() {
        mNearShopAdapter = new HomeRecommendShopAdapter(mContext, mNearShopList);
        mHotStoreRv.setAdapter(mNearShopAdapter);
    }

    /**
     * 附近安装大师
     */
    private void initInstallServerData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mInstallRv.setLayoutManager(linearLayoutManager);
        mInstallRv.setHasFixedSize(true);
        mInstallRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext
                ,ResourceUtils.dip2px(mContext ,10)
                ,getResources().getDrawable(R.drawable.drawable_white_trans)
                ,RvLinearManagerItemDecoration.HORIZONTAL_LIST));

        mNearInstallAdapter = new AdapterNearServer(mContext, mNearInstallList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                ServiceList serviceList = mNearInstallList.get(position) ;
                if(serviceList != null){
                    if(MyApplication.isServerHidden()){
                        DialogUtils.showCustomViewDialog(mContext,
                                getResources().getString(R.string.server_hidden_tips)
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                        return ;
                    }

                    String userId = serviceList.getCreateUserId() ;
                    if(0 == type){
                        ServerDetailActivity.launch(mContext ,userId,true) ;
                    }
                }
            }
        });
        mInstallRv.setAdapter(mNearInstallAdapter);
    }

    /**
     * 附近服务
     */
    private void initHotServerData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) ;
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHotServerRv.setLayoutManager(layoutManager);
        mCategoryAdapter = new AdapterNomalCategoryHori<>(mContext,true , mServerCateList, 4.5F ,new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                ServerCategory category = mServerCateList.get(position) ;
                if(category != null){

                    if(MyApplication.isServerHidden()){
                        DialogUtils.showCustomViewDialog(mContext,
                                getResources().getString(R.string.server_hidden_tips)
                                , "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                        return ;
                    }

//                    Intent toNsIt = new Intent(mContext , NearServerListActivity.class) ;
//                    toNsIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID , category.getServiceCategoryID()) ;
//                    toNsIt.putExtra(SkipUtils.INTENT_SERVER_TYPE , ServerActivity.SERVER_TYPE_SERVER) ;
//                    startActivity(toNsIt) ;

                    Intent toLtIt = new Intent(mContext ,ServerListActivity.class) ;
                    toLtIt.putExtra(SkipUtils.INTENT_SERVER_TYPE ,ServerActivity.SERVER_TYPE_SERVER) ;
                    toLtIt.putExtra(SkipUtils.INTENT_CATEGOTY_ID ,category.getServiceCategoryID()) ;
                    startActivity(toLtIt) ;
                }
            }
        }) ;
        mHotServerRv.setAdapter(mCategoryAdapter);
        mHotServerRv.setNestedScrollingEnabled(false);
        mHotServerRv.setFocusable(false);
    }

    /**
     * 附近的人
     */
    private void initNearUserData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCircleUserRv.setLayoutManager(linearLayoutManager);
        mCircleUserRv.addItemDecoration(new RvLinearManagerItemDecoration(mContext
                ,ResourceUtils.dip2px(mContext ,10)
                ,getResources().getDrawable(R.drawable.drawable_white_trans)
                ,RvLinearManagerItemDecoration.HORIZONTAL_LIST));
        mCircleUserRv.setHasFixedSize(true);

        mNearUserAdapter = new AdapterNearUser(mContext, mNearUserList
                , new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    UserNear userNear = mNearUserList.get(position) ;
                    if(userNear != null){
                        SkipUtils.toOtherUserInfoActivity(mContext ,userNear.getCreateUserId()) ;
                    }
                }
            }
        });
        mCircleUserRv.setAdapter(mNearUserAdapter);
    }


    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.header_near_item_location_lay == vId){//定位
            if(mLocationRefreshListener != null){
                mLocationRefreshListener.onRefresh();
            }
        }else if(R.id.frag_near_server_lay == vId){//附近服务
//            Intent toNsIt = new Intent(mContext , NearServerListActivity.class) ;
//            toNsIt.putExtra(SkipUtils.INTENT_SERVER_TYPE , ServerActivity.SERVER_TYPE_SERVER) ;
//            startActivity(toNsIt) ;

            if(MyApplication.isServerHidden()){
                DialogUtils.showCustomViewDialog(mContext,
                        getResources().getString(R.string.server_hidden_tips)
                        , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                return ;
            }

            Intent toLtIt = new Intent(mContext ,ServerListActivity.class) ;
            toLtIt.putExtra(SkipUtils.INTENT_SERVER_TYPE ,ServerActivity.SERVER_TYPE_SERVER) ;
            startActivity(toLtIt) ;

        }else if(R.id.frag_near_server_install_lay == vId){//附近安装
            if(MyApplication.isServerHidden()){
                DialogUtils.showCustomViewDialog(mContext,
                        getResources().getString(R.string.server_hidden_tips)
                        , "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                return ;
            }

            Intent toNsIt = new Intent(mContext , NearServerListActivity.class) ;
            toNsIt.putExtra(SkipUtils.INTENT_SERVER_TYPE , ServerActivity.SERVER_TYPE_INSTALL) ;
            startActivity(toNsIt) ;
        }else if(R.id.frag_near_work_lay == vId){//附近工作--全部
            Intent toNsIt = new Intent(mContext , NearJobListActivity.class) ;
            toNsIt.putExtra(SkipUtils.INTENT_JOB_TYPE , 0) ;
            startActivity(toNsIt) ;
        }else if(R.id.frag_near_work_all_iv == vId){//附近工作--全职
            Intent toNsIt = new Intent(mContext , NearJobListActivity.class) ;
            toNsIt.putExtra(SkipUtils.INTENT_JOB_TYPE , 1) ;
            startActivity(toNsIt) ;
        }else if(R.id.frag_near_work_half_iv == vId){//附近工作--兼职
            Intent toNsIt = new Intent(mContext , NearJobListActivity.class) ;
            toNsIt.putExtra(SkipUtils.INTENT_JOB_TYPE , 2) ;
            startActivity(toNsIt) ;
        }else if(R.id.frag_near_unused_lay == vId){//附近闲置

            Intent toUnIt = new Intent(mContext , UnuseGoodListActivity.class) ;
            startActivity(toUnIt) ;

        }else if(R.id.frag_near_hot_store_lay == vId){//附近商家

            NearStoreActivity.launch(mContext ,false) ;

        }else if(R.id.frag_near_hot_store_change_lay == vId){//附近商家--换一批
            Animation animation = AnimationUtils.loadAnimation(mContext ,R.anim.anim_near_refresh) ;
            animation.setInterpolator(new LinearInterpolator());
            mHotStoreRefreshIv.startAnimation(animation);
            requestNearShopList();
        }else if(R.id.frag_near_server_install_change_lay == vId){//安装服务--换一批
            Animation animation = AnimationUtils.loadAnimation(mContext ,R.anim.anim_near_refresh) ;
            animation.setInterpolator(new LinearInterpolator());
            mInstallRefreshIv.startAnimation(animation);
            requestNearInstallHead();
        }else if(R.id.frag_near_circle_user_change_lay == vId){//附近的人--换一批
            Animation animation = AnimationUtils.loadAnimation(mContext ,R.anim.anim_near_refresh) ;
            animation.setInterpolator(new LinearInterpolator());
            mCircleUserRefreshIv.startAnimation(animation);
            requestNearCirlceUser();
        }else if(R.id.frag_near_circle_user_lay == vId){//附近的人
            Intent toUiIt = new Intent(mContext , NearUserListActivity.class) ;
            startActivity(toUiIt) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mScrollY = 0 ;
            mToTopIv.setVisibility(View.GONE);
            mRv.scrollToPosition(0) ;
        }
    }

    /**
     * 附近服务
     */
    private void requestNearServerHead() {
        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("id" ,"2") ;
        paramMap.put("Layer" ,"1") ;

        NetUtils.getDataFromServerByPost(mContext, Urls.SERVER_CATEGORY, false, paramMap,
                new RequestListCallBack<ServerCategory>("getServerCategory", mContext, ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        mServerCateList.clear();
                        mServerCateList.addAll(bean) ;
                        mCategoryAdapter.notifyDataSetChanged();

                        if(mIsRefreshing){
                            requestNearInstallHead() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(mIsRefreshing){
                            requestNearInstallHead() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        if(mIsRefreshing){
                            requestNearInstallHead() ;
                        }
                    }
                });
    }

    /**
     * 附近安装大师
     */
    private void requestNearInstallHead() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Page", "" + mNearInstallPage);
        para.put("PageSize", "10");
        para.put("Longitude", MyApplication.getLongitude());
        para.put("Latitude", MyApplication.getLatitude());
        para.put("AreaId", MyApplication.getCurrentCityId());
        para.put("AreaType", "" + MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.API_NEAR_SERVER_INSTALL, false, para,
                new RequestListCallBack<ServiceList>("requestNearInstallHead", mContext, ServiceList.class) {
                    @Override
                    public void onSuccessResult(List<ServiceList> bean) {
                        mNearInstallList.clear();
                        mNearInstallList.addAll(bean) ;
                        mNearInstallAdapter.notifyDataSetChanged() ;

                        if(mNearInstallList.size() == 10){
                            mNearInstallPage++ ;
                        }else{
                            mNearInstallPage = 1 ;
                        }

                        mInstallRefreshIv.clearAnimation() ;

                        if(mIsRefreshing){
                            requestNearCirlceUser() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mInstallRefreshIv.clearAnimation() ;

                        if(mIsRefreshing){
                            requestNearCirlceUser() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        mInstallRefreshIv.clearAnimation() ;

                        if(mIsRefreshing){
                            requestNearCirlceUser() ;
                        }
                    }
                });
    }

    /**
     * 附近的人
     */
    private void requestNearCirlceUser() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Page", "" + mNearUserPage);
        para.put("PageSize", "10");
        para.put("Longitude", MyApplication.getLongitude());
        para.put("Latitude", MyApplication.getLatitude());
        para.put("AreaId", MyApplication.getCurrentCityName());//注意：此处传name即可，字段名不变
        para.put("AreaType", "" + MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.API_NEAR_USER, false, para,
                new RequestListCallBack<UserNear>("requestNearCirlceUser", mContext, UserNear.class) {
                    @Override
                    public void onSuccessResult(List<UserNear> bean) {
                        mNearUserList.clear();
                        mNearUserList.addAll(bean) ;
                        mNearUserAdapter.notifyDataSetChanged() ;

                        if(mNearUserList.size() == 10){
                            mNearUserPage++ ;
                        }else{
                            mNearUserPage = 1 ;
                        }

                        mCircleUserRefreshIv.clearAnimation();

                        if(mIsRefreshing){
                            requestNearShopList() ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mCircleUserRefreshIv.clearAnimation();

                        if(mIsRefreshing){
                            requestNearShopList() ;
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        mCircleUserRefreshIv.clearAnimation();

                        if(mIsRefreshing){
                            requestNearShopList() ;
                        }
                    }
                });
    }


    /**
     * 附近店铺
     */
    private void requestNearShopList() {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("PageSize", "" + MAX_STORE_SHOW );//暂定只加载一屏的数据，切换时，用换一批来操作
        para.put("Page", "" + mNearStorePage);
        para.put("Longitude", MyApplication.getLongitude());
        para.put("Latitude", MyApplication.getLatitude());
        para.put("AreaId", MyApplication.getCurrentCityId());
        para.put("AreaType", "" + MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.API_NEAR_SHOP_LIST, false, para,
                new RequestListCallBack<HomeStore>("requestNearShopList", mContext, HomeStore.class) {
                    @Override
                    public void onSuccessResult(List<HomeStore> bean) {
//                        //模拟数据
//                        for(int x = 0 ; x < 10 ; x ++){
//                            List<ImageThumb> imgLIst = new ArrayList<>() ;
//                            imgLIst.add(new ImageThumb("http://123.56.158.181:33002/Resource/PhotoFile/20180604/d2d492b0-110c-4914-9378-c5278fc125bb.png")) ;
//                            imgLIst.add(new ImageThumb("http://123.56.158.181:33002/Resource/PhotoFile/20180604/d2d492b0-110c-4914-9378-c5278fc125bb.png")) ;
//                            imgLIst.add(new ImageThumb("http://123.56.158.181:33002/Resource/PhotoFile/20180604/d2d492b0-110c-4914-9378-c5278fc125bb.png")) ;
//                            bean.add(new HomeStore("店铺名字" + x ,"" + x ,imgLIst)) ;
//                        }

                        //处理分组
                        mNearShopList.clear();
                        mNearShopAdapter.notifyDataSetChanged();

                        int size = bean.size() ;
                        if(size <= MAX_STORE_SHOW){
                            if(size > 0){
                                HomeStoreParent parentStore = new HomeStoreParent() ;
                                parentStore.setStoreList(bean) ;
                                mNearShopList.add(parentStore);
                            }
                        }else{
                            HomeStoreParent parentStore = new HomeStoreParent();

                            for(int x = 0 ; x < size ; x ++){
                                if(x%MAX_STORE_SHOW == 0){
                                    if(x > 0){
                                        mNearShopList.add(parentStore) ;
                                    }

                                    parentStore = new HomeStoreParent();
                                    parentStore.addStoreToList(bean.get(x));
                                }else{
                                    parentStore.addStoreToList(bean.get(x)) ;
                                }

                                if(x == size - 1){//最后一个，而且不是开始
                                    mNearShopList.add(parentStore) ;
                                }
                            }
                        }

                        mNearShopAdapter.notifyDataSetChanged();
                        mHotStoreRefreshIv.clearAnimation();

                        if(bean.size() == MAX_STORE_SHOW){
                            mNearStorePage ++ ;
                        }else{
                            mNearStorePage = 1 ;
                        }

                        if(mIsRefreshing){
                            getUnuseList() ;
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mHotStoreRefreshIv.clearAnimation() ;

                        if(mIsRefreshing){
                            getUnuseList() ;
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        mHotStoreRefreshIv.clearAnimation() ;

                        if(mIsRefreshing){
                            getUnuseList() ;
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
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("SortCode" ,"") ;
        paramMap.put("Sort" ,"") ;
//        paramMap.put("SortCode" ,"CreateDate") ;
//        paramMap.put("Sort" ,"desc") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Longitude" , MyApplication.getLongitude()) ;
        paramMap.put("Latitude" , MyApplication.getLatitude()) ;
        paramMap.put("AreaId" , MyApplication.getCurrentCityId()) ;
        paramMap.put("AreaType" ,MyApplication.getCurrentCityType()) ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext, Urls.UNUSE_GOOD_LIST, paramMap
                , new RequestListCallBack<UnuseGoodList>("getUnuseList" , mContext , UnuseGoodList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseGoodList> bean) {
                        if(1 == mCurPage){
                            mUnuseGoodList.clear();
                        }

                        mUnuseGoodList.addAll(bean) ;
                        mUnusedWrapAdapter.notifyDataSetChanged() ;

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
            mFooterTv.setText(getResources().getString(R.string.footer_no_string)) ;
        }

        if(mUnuseGoodList.size() == 0){
            mFooterTv.setText("暂无闲置") ;
        }
    }

    /**
     * 刷新城市更换影响的模块
     */
    public void updateCityChangeDisplay(){
        if(mIsInit){
            mNearInstallList.clear();
            mNearInstallAdapter.notifyDataSetChanged() ;

            mNearUserList.clear();
            mNearUserAdapter.notifyDataSetChanged() ;

            mUnuseGoodList.clear();
            mUnusedWrapAdapter.notifyDataSetChanged() ;
        }
    }

    /**
     * 更新数据
     */
    private void updateNearInfo(){
        if(mServerCateList.size() == 0){
            requestNearServerHead();
        }
        if(mNearInstallList.size() == 0){
            requestNearInstallHead();
        }

        if(mNearUserList.size() == 0){
            requestNearCirlceUser() ;
        }
        if(mNearShopList.size() == 0){
            requestNearShopList() ;
        }

        if(mUnuseGoodList.size() == 0){
            mCurPage = 1 ;
            getUnuseList() ;
        }

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden && mIsInit){
            updateNearInfo() ;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mIsInit && !isHidden()){
            updateNearInfo() ;
        }
    }
}
