package cn.idcby.jiajubang.activity;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.Bean.UserNear;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNearUserList;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 附近的人
 * Created on 2018/5/10.
 */

public class NearUserListActivity extends BaseActivity {
    private View mLoadingLay ;
    private MaterialRefreshLayout mRefreshLay ;

    private AdapterNearUserList mAdapter ;
    private List<UserNear> mDataList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_near_user_list;
    }

    @Override
    public void initView() {
        mRefreshLay = findViewById(R.id.acti_near_user_list_refresh_lay) ;
        ListView mLv = findViewById(R.id.acti_near_user_list_lv);
        mLoadingLay = findViewById(R.id.acti_near_user_list_loading_lay) ;

        mAdapter = new AdapterNearUserList(mContext , mDataList) ;
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
                    getUserList();
                }
            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;
                getUserList();
            }
        });
    }

    @Override
    public void initData() {
        getUserList();
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {

    }

    private void showOrHiddenLoading(boolean isShow){
        mLoadingLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取列表
     */
    private void getUserList(){
        mIsLoading = true ;

        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Page", "" + mCurPage);
        para.put("PageSize", "10");
        para.put("Longitude", MyApplication.getLongitude());
        para.put("Latitude", MyApplication.getLatitude());
        para.put("AreaId", MyApplication.getCurrentCityName());
        para.put("AreaType", "" + MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.API_NEAR_USER, false, para,
                new RequestListCallBack<UserNear>("getUserList", mContext, UserNear.class) {
                    @Override
                    public void onSuccessResult(List<UserNear> bean) {
                        if(1 == mCurPage){
                            mDataList.clear();
                        }
                        mDataList.addAll(bean) ;
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

        showOrHiddenLoading(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getUserList");

    }
}
