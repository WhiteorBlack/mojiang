package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNeedsList;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.TopBarRightView;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created on 2018/3/29.
 */

public class NeedsListActivity extends BaseActivity {
    private int mNeedsType = 1 ; //1 全部 2 最近
    private String mCategoryId ;
    private String mSearchKey ;

    private TopBarRightView mRightLay ;
    private View mLoadingLay ;
    private TextView mSearchKeyTv ;
    private ListView mLv ;
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;

    private TextView mTypeAllTv ;
    private View mTypeAllDv ;
    private TextView mTypeNearlayTv ;
    private View mTypeNearlayDv ;

    private AdapterNeedsList mAdapter ;
    private List<NeedsList> mNeedsList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private TextView mFooterTv ;
    private View mToTopIv ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_needs_list;
    }

    @Override
    public void initView() {
        mCategoryId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID) ;
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;

        mToTopIv = findViewById(R.id.acti_lv_to_top_iv) ;
        mToTopIv.setOnClickListener(this);

        mRightLay = findViewById(R.id.acti_needs_list_right_iv) ;
        View searchLay = findViewById(R.id.acti_needs_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_needs_list_search_key_tv) ;
        mRefreshLay = findViewById(R.id.acti_needs_list_refresh_lay) ;
        mLv = findViewById(R.id.acti_needs_list_lv);
        mLoadingLay = findViewById(R.id.acti_needs_list_loading_lay) ;
        mNullTv = findViewById(R.id.acti_needs_list_null_tv) ;
        View mTypeAllLay = findViewById(R.id.acti_needs_list_all_lay) ;
        mTypeAllTv = findViewById(R.id.acti_needs_list_all_tv) ;
        mTypeAllDv = findViewById(R.id.acti_needs_list_all_dv) ;
        View mTypeNearlayLay = findViewById(R.id.acti_needs_list_nearly_lay) ;
        mTypeNearlayTv = findViewById(R.id.acti_needs_list_nearly_tv) ;
        mTypeNearlayDv = findViewById(R.id.acti_needs_list_nearly_dv) ;
        mTypeNearlayLay.setOnClickListener(this);
        mTypeAllLay.setOnClickListener(this);
        searchLay.setOnClickListener(this);
        mRightLay.setOnClickListener(this);

        if(mSearchKey != null && !"".equals(mSearchKey)){
            mSearchKeyTv.setText(mSearchKey);
        }

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mLv.addFooterView(mFooterTv) ;

        mAdapter = new AdapterNeedsList(mActivity , mNeedsList) ;
        mLv.setAdapter(mAdapter) ;

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount
                    , int totalItemCount) {

                if(!mIsLoading && mIsMore && totalItemCount > 5
                        && visibleItemCount + firstVisibleItem >= totalItemCount){
                    getNeedList();
                }

                ViewUtil.setViewVisible(mToTopIv ,firstVisibleItem > 10);
            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;
                getNeedList();
            }
        });
    }

    @Override
    public void initData() {
        getNeedList();
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_needs_list_right_iv == vId){
            SkipUtils.toMessageCenterActivity(mContext) ;
        }else if(R.id.acti_needs_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            mActivity.startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_needs_list_all_lay == vId){
            changeTypeTvStyle(1) ;
        }else if(R.id.acti_needs_list_nearly_lay == vId){
            changeTypeTvStyle(2) ;
        }else if(R.id.acti_lv_to_top_iv == vId){
            mToTopIv.setVisibility(View.GONE);
            mLv.setSelection(0) ;
        }
    }

    private void changeTypeTvStyle(int type){
        if(type == mNeedsType){
            return ;
        }

        switch (mNeedsType){
            case 1:
                mTypeAllTv.setTextColor(getResources().getColor(R.color.color_nav_normal_two)) ;
                mTypeAllDv.setVisibility(View.GONE);
                break;
            case 2:
                mTypeNearlayTv.setTextColor(getResources().getColor(R.color.color_nav_normal_two)) ;
                mTypeNearlayDv.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        switch (type){
            case 1:
                mTypeAllTv.setTextColor(getResources().getColor(R.color.color_nav_checked_two)) ;
                mTypeAllDv.setVisibility(View.VISIBLE);
                break;
            case 2:
                mTypeNearlayTv.setTextColor(getResources().getColor(R.color.color_nav_checked_two)) ;
                mTypeNearlayDv.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        mNeedsType = type ;
        changeNeedTypeAndGetList() ;
    }

    private void changeNeedTypeAndGetList(){
        NetUtils.cancelTag("getNeedList" + mNeedsType);

        mToTopIv.setVisibility(View.GONE);
        mLv.setSelection(0) ;

        mCurPage = 1 ;
        mIsMore = true ;

        showOrHiddenLoading(true);

        mNeedsList.clear();
        mAdapter.notifyDataSetChanged() ;

        getNeedList();
    }

    private void showOrHiddenLoading(boolean isShow){
        mLoadingLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取需求列表
     */
    private void getNeedList(){
        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string)) ;
        }

        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getPara(mContext) ;
        paramMap.put("TypeId" , "" + mNeedsType) ;
        paramMap.put("CategoryId" , StringUtils.convertNull(mCategoryId)) ;
        paramMap.put("Keyword" , StringUtils.convertNull(mSearchKey)) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Longitude" , MyApplication.getLongitude()) ;
        paramMap.put("Latitude" , MyApplication.getLatitude()) ;
        paramMap.put("AreaId" , MyApplication.getCurrentCityId()) ;
        paramMap.put("AreaType" , "" + MyApplication.getCurrentCityType()) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.NEEDS_LIST, paramMap
                , new RequestListCallBack<NeedsList>("getNeedList" , mContext , NeedsList.class) {
                    @Override
                    public void onSuccessResult(List<NeedsList> bean) {
                        if(1 == mCurPage){
                            mNeedsList.clear();
                        }
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
        mRefreshLay.finishRefresh() ;

        if(!mIsMore){
            mFooterTv.setText(getResources().getString(R.string.footer_no_string));
        }

        if(mNeedsList.size() == 0){
            mFooterTv.setText("") ;
            mNullTv.setVisibility(View.VISIBLE);
        }

        showOrHiddenLoading(false);
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
        }

    }

    @Override
    public void onMessageCountChange(int count) {
        super.onMessageCountChange(count);

        mRightLay.setUnreadCount(count);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getNeedList" + mNeedsType);

    }
}
