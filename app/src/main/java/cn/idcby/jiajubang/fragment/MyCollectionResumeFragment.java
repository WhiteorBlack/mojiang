package cn.idcby.jiajubang.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.jiajubang.Bean.ResumeList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterCollectionResume;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的收藏--简历
 */

public class MyCollectionResumeFragment extends BaseFragment {
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRecyclerView;
    private AdapterCollectionResume mAdapter;

    private List<ResumeList> mList = new ArrayList<>() ;
    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;


    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                getCollectionResumeList() ;
            }
        }
    }

    @Override
    protected void initView(View view) {
        mRefreshLay = view.findViewById(R.id.frag_collection_resume_refresh_lay);
        mRecyclerView = view.findViewById(R.id.frag_collection_resume_rv);

        mAdapter = new AdapterCollectionResume( mContext, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter) ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_collection_resume;
    }

    @Override
    protected void initListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && ViewUtil.isSlideToBottom(mRecyclerView)){
                    getCollectionResumeList();
                }
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;

                getCollectionResumeList() ;
            }
        });
    }


    /**
     * 列表
     */
    private void getCollectionResumeList(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.RESUME_COLLECTION_LIST, false, paramMap
                , new RequestListCallBack<ResumeList>("getCollectionResumeList"
                        , mContext ,ResumeList.class) {
                    @Override
                    public void onSuccessResult(List<ResumeList> bean) {
                        mRefreshLay.finishRefresh() ;
                        loadPage.showSuccessPage();

                        if(1 == mCurPage){
                            mList.clear();
                        }
                        mList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged() ;

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        mIsLoading = false ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mRefreshLay.finishRefresh() ;
                        mIsLoading = false ;
                        loadPage.showSuccessPage();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mRefreshLay.finishRefresh() ;
                        mIsLoading = false ;
                        loadPage.showSuccessPage();
                    }
                });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(mIsReload && isVisibleToUser && mList != null && mList.size() == 0){
            mIsMore = true ;
            loadPage.showLoadingPage() ;
            getCollectionResumeList() ;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getCollectionResumeList");

    }

}
