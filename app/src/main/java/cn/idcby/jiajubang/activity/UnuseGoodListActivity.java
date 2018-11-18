package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterUnuseSpecGood;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvDecorationHiddenCallBack;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearMgItemWithHeaaFoot;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 闲置商品列表
 * Created on 2018/3/27.
 *
 * 2018-06-07 13:55:59
 * 闲置商品列表，筛选改为  最新 最近 2个选项卡
 */

public class UnuseGoodListActivity extends BaseActivity {

    private boolean mIsSpec = false ;//是否是专场
    private int mUnuseType = 1 ;//1最新 2 附近

    private TextView mLatestTv ;
    private View mLatestDv ;
    private TextView mNearlyTv ;
    private View mNearlyDv ;

    private String mCategoryId ;
    private String mSearchKey ;

    private View mLoadingLay ;
    private TextView mSearchKeyTv ;
    private TextView mNullTv ;
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mLv ;

    private HeaderFooterAdapter<AdapterUnuseSpecGood> mAdapter ;
    private List<UnuseGoodList> mUnuseGoodList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private int mScreenHeight ;
    private int mScrollY = 0 ;

    private TextView mFooterTv ;
    private View mToTopIv ;

    @Override
    public int getLayoutID() {
        return R.layout.activity_unuse_good_list;
    }

    @Override
    public void initView() {
        mIsSpec = getIntent().getBooleanExtra(SkipUtils.INTENT_UNUSE_TYPE_SPEC,mIsSpec) ;
        mCategoryId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID) ;
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;

        View rightIv = findViewById(R.id.acti_unuse_good_list_right_iv) ;
        View searchLay = findViewById(R.id.acti_unuse_good_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_unuse_good_list_search_key_tv) ;
        mRefreshLay = findViewById(R.id.acti_unuse_good_list_refresh_lay) ;
        mLv = findViewById(R.id.acti_unuse_good_list_lv);
        mLoadingLay = findViewById(R.id.acti_unuse_good_list_loading_lay) ;
        mNullTv = findViewById(R.id.acti_unuse_good_list_null_tv) ;

        mScreenHeight = ResourceUtils.getScreenHeight(mContext) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        View mNavLay = findViewById(R.id.acti_unuse_good_list_nav_lay) ;
        View mNearlyLay = findViewById(R.id.acti_unuse_good_list_nav_nearly_lay) ;
        mNearlyTv = findViewById(R.id.acti_unuse_good_list_nav_nearly_tv) ;
        mNearlyDv = findViewById(R.id.acti_unuse_good_list_nav_nearly_dv) ;
        View mLatestLay = findViewById(R.id.acti_unuse_good_list_nav_latest_lay) ;
        mLatestTv = findViewById(R.id.acti_unuse_good_list_nav_latest_tv) ;
        mLatestDv = findViewById(R.id.acti_unuse_good_list_nav_latest_dv) ;
        mNearlyLay.setOnClickListener(this);
        mLatestLay.setOnClickListener(this);
        mNavLay.setVisibility(mIsSpec ? View.GONE : View.VISIBLE);


        searchLay.setOnClickListener(this);
        rightIv.setOnClickListener(this);

        if(mSearchKey != null && !"".equals(mSearchKey)){
            mSearchKeyTv.setText(mSearchKey);
        }

        AdapterUnuseSpecGood adapter = new AdapterUnuseSpecGood(mActivity, mUnuseGoodList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                UnuseGoodList goodList = mUnuseGoodList.get(position) ;
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

        mAdapter = new HeaderFooterAdapter<>(adapter) ;
        mLv.setAdapter(mAdapter);
        mLv.setLayoutManager(new LinearLayoutManager(mContext));
        RvDecorationHiddenCallBack hiddenCallBack = new RvDecorationHiddenCallBack() {
            @Override
            public boolean isHidden(int position) {
                int allCount = mUnuseGoodList.size() + 1 ;//加footer

                if(position >= allCount - 2){
                    return true ;
                }
                return false;
            }
        } ;
        RvLinearMgItemWithHeaaFoot itemDecoration = new RvLinearMgItemWithHeaaFoot(mContext
                , ResourceUtils.dip2px(mContext ,ResourceUtils.getXmlDef(mContext,R.dimen.nomal_divider_height))
                ,getResources().getDrawable(R.drawable.drawable_white_trans)
                ,hiddenCallBack) ;
        mLv.addItemDecoration(itemDecoration);

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mAdapter.addFooter(mFooterTv);

        mLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsSpec && !mIsLoading && mIsMore
                        && mUnuseGoodList.size() > 5
                        && ViewUtil.isSlideToBottom(mLv)){
                    getUnuseGoodList();
                }

                mScrollY += dy ;
                ViewUtil.setViewVisible(mToTopIv ,mScrollY > mScreenHeight) ;
            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;
                getUnuseGoodList();
            }
        });
    }

    @Override
    public void initData() {
        getUnuseGoodList();
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_unuse_good_list_right_iv == vId){
            if(LoginHelper.isNotLogin(mContext)){
                SkipUtils.toLoginActivityForResult(mActivity ,1009);
            }else{
                SkipUtils.toMessageCenterActivity(mContext) ;
            }
        }else if(R.id.acti_unuse_good_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            mActivity.startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_unuse_good_list_nav_latest_lay == vId){
            changeListByType(1) ;
        }else if(R.id.acti_unuse_good_list_nav_nearly_lay == vId){
            changeListByType(2) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mLv.scrollToPosition(0);
            mScrollY = 0 ;
            mToTopIv.setVisibility(View.GONE);
        }
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

        mCurPage = 1 ;
        changeNeedTypeAndGetList();
    }

    private void changeNeedTypeAndGetList(){
        mCurPage = 1 ;
        mIsMore = true ;

        mToTopIv.setVisibility(View.GONE);
        mScrollY = 0 ;

        showOrHiddenLoading(true);

        mUnuseGoodList.clear();
        mAdapter.notifyDataSetChanged() ;

        getUnuseGoodList();
    }

    private void showOrHiddenLoading(boolean isShow){
        mLoadingLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取列表
     */
    private void getUnuseGoodList(){
        if(mNullTv.getVisibility() != View.GONE){
            mNullTv.setVisibility(View.GONE);
        }

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        String sort = "desc" ;
        String sortCode = "CreateDate" ;
        if(2 == mUnuseType){//附近的，不传排序
            sortCode = "" ;
            sort = "" ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("SortCode" , sortCode) ;
        paramMap.put("Sort" , sort) ;
        paramMap.put("Keyword" ,StringUtils.convertNull(mSearchKey)) ;
        paramMap.put(mIsSpec ? "SpecialGoodsID" : "RootCategoryID" , StringUtils.convertNull(mCategoryId)) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        if(!mIsSpec){
            paramMap.put("Longitude" , MyApplication.getLongitude()) ;
            paramMap.put("Latitude" , MyApplication.getLatitude()) ;
            paramMap.put("AreaId" , MyApplication.getCurrentCityId()) ;
            paramMap.put("AreaType" ,MyApplication.getCurrentCityType()) ;
        }

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext,mIsSpec ? Urls.UNUSE_SPEC_GOOD_LIST : Urls.UNUSE_GOOD_LIST, paramMap
                , new RequestListCallBack<UnuseGoodList>("getUnuseGoodList" + mUnuseType , mContext , UnuseGoodList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseGoodList> bean) {
                        if(1 == mCurPage){
                            mUnuseGoodList.clear();
                        }
                        mUnuseGoodList.addAll(bean) ;
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
        mRefreshLay.finishRefresh() ;

        showOrHiddenLoading(false);

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mUnuseGoodList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE) ;
            mFooterTv.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1003 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                mSearchKeyTv.setText("".equals(mSearchKey) ? mContext.getResources().getString(R.string.nomal_search_def) : mSearchKey) ;
                changeNeedTypeAndGetList() ;
            }
        }else if(1009 == requestCode){
            if(RESULT_OK == resultCode){
                SkipUtils.toMessageCenterActivity(mContext) ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getUnuseGoodList" + mUnuseType);

    }
}
