package cn.idcby.jiajubang.fragment;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.jiajubang.Bean.NeedsList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.SubClassificationActivity;
import cn.idcby.jiajubang.adapter.AdapterNeedsList;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created on 2018/5/26.
 */

public class MySubsNeedFragment extends BaseFragment {
    private ListView mLv ;
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;

    private AdapterNeedsList mAdapter ;
    private List<NeedsList> mNeedsList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    @Override
    protected void requestData() {
        if(getUserVisibleHint()){
            getNeedList() ;
        }
    }

    @Override
    protected void initView(View view) {
        mRefreshLay = view.findViewById(R.id.lay_loading_lv_refresh_lay) ;
        mLv = view.findViewById(R.id.lay_loading_lv_list_lv);
        View mLoadingLay = view.findViewById(R.id.lay_loading_lv_progressBar) ;
        mNullTv = view.findViewById(R.id.lay_loading_lv_null_tv) ;
        mLoadingLay.setVisibility(View.GONE);

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
    protected int setSuccessViewId() {
        return R.layout.lay_loading_lv;
    }

    @Override
    protected void initListener() {

    }

    /**
     * 获取需求列表
     */
    private void getNeedList(){
        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }

        mIsLoading = true ;

        Map<String, String> paramMap = ParaUtils.getParaWithToken(getContext());
        paramMap.put("Type", "" + SubClassificationActivity.SUBS_TYPE_NEED);
        paramMap.put("Page", String.valueOf(mCurPage));
        paramMap.put("PageSize", "10");
        paramMap.put("Keyword", "");

        NetUtils.getDataFromServerByPost(mContext, Urls.SUB_SUBSCRIBE, paramMap
                , new RequestListCallBack<NeedsList>("getNeedList" , mContext , NeedsList.class) {
                    @Override
                    public void onSuccessResult(List<NeedsList> bean) {
                        if(1 == mCurPage){
                            mNeedsList.clear();
                        }
                        mNeedsList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged() ;

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
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
        loadPage.showSuccessPage();

        mIsLoading = false ;
        mRefreshLay.finishRefresh() ;

        if(mNeedsList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && loadPage != null){
            if(mNeedsList.size() == 0){
                loadPage.showLoadingPage() ;
                getNeedList();
            }
        }
    }
}
