package cn.idcby.jiajubang.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.jiajubang.Bean.JobsCollectionList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterCollectionJob;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的收藏--职位（招聘）
 */

public class MyCollectionJobsFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;

    private AdapterCollectionJob mAdapter;
    private List<JobsCollectionList> mList = new ArrayList<>() ;

    private int mCollectionType ;
    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;

    public static MyCollectionJobsFragment getInstance(int collectionType) {
       MyCollectionJobsFragment fragment = new MyCollectionJobsFragment() ;
       fragment.mCollectionType = collectionType ;
       return fragment ;
    }

    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                getDataList() ;
            }
        }
    }

    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.lay_refresh_rv_list_rv);
        mRefreshLay = view.findViewById(R.id.lay_refresh_rv_refresh_lay) ;
        mNullTv = view.findViewById(R.id.lay_refresh_rv_null_tv) ;

        mAdapter = new AdapterCollectionJob(mContext, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext)) ;
        mRecyclerView.setAdapter(mAdapter) ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.lay_refresh_rv;
    }

    @Override
    protected void initListener() {
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;
                mIsMore = true ;

                getDataList() ;
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && ViewUtil.isSlideToBottom(recyclerView)){
                    getDataList();
                }
            }
        });
    }


    /**
     * 获取列表
     */
    private void getDataList(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Type" , "" + mCollectionType) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_COLLECTION_LIST_BY_TYPE, false, paramMap
                , new RequestListCallBack<JobsCollectionList>("getDataList" +  mCollectionType , mContext ,JobsCollectionList.class) {
                    @Override
                    public void onSuccessResult(List<JobsCollectionList> bean) {
                        if(1 == mCurPage){
                            mList.clear();
                        }

                        mList.addAll(bean) ;
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

    private void finishRequest(){
        mRefreshLay.finishRefresh() ;
        mIsLoading = false ;
        loadPage.showSuccessPage();

        if(mList.size() == 0){
            if(mNullTv.getVisibility() != View.VISIBLE){
                mNullTv.setVisibility(View.VISIBLE);
            }
        }else{
            if(mNullTv.getVisibility() != View.GONE){
                mNullTv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mIsReload && isVisibleToUser && mList != null && mList.size() == 0){
            mIsMore = true ;
            getDataList() ;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getDataList" + mCollectionType);

    }

}
