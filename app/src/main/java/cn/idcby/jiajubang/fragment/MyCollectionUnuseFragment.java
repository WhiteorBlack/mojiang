package cn.idcby.jiajubang.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.UnuseGoodDetailsActivity;
import cn.idcby.jiajubang.adapter.AdapterUnuseSpecGood;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
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
 * 我的收藏--闲置
 */

public class MyCollectionUnuseFragment extends BaseFragment {
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRecyclerView;
    private AdapterUnuseSpecGood mAdapter;

    private int mCollectionType ;
    private List<UnuseGoodList> mDataList = new ArrayList<>() ;
    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;

    private View mNullTv ;


    public static MyCollectionUnuseFragment getInstance(int collectionType) {
        MyCollectionUnuseFragment fragment = new MyCollectionUnuseFragment() ;
        fragment.mCollectionType = collectionType ;
        return fragment ;
    }

    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                getCollectionUnuseList() ;
            }
        }
    }

    @Override
    protected void initView(View view) {
        mNullTv = view.findViewById(R.id.frag_collection_unuse_null_tv) ;
        mRefreshLay = view.findViewById(R.id.frag_collection_unuse_refresh_lay);
        mRecyclerView = view.findViewById(R.id.frag_collection_unuse_rv);

        mAdapter = new AdapterUnuseSpecGood(mActivity, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                UnuseGoodList goodList = mDataList.get(position) ;
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

        RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(mContext
                , ResourceUtils.dip2px(mContext ,ResourceUtils.getXmlDef(mContext ,R.dimen.nomal_divider_height))
                ,getResources().getDrawable(R.drawable.drawable_white_trans)) ;
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter) ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_collection_unuse;
    }

    @Override
    protected void initListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && ViewUtil.isSlideToBottom(mRecyclerView)){
                    getCollectionUnuseList();
                }
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;

                getCollectionUnuseList() ;
            }
        });
    }

    /**
     * 列表
     */
    private void getCollectionUnuseList(){
        if(mNullTv.getVisibility() != View.GONE){
            mNullTv.setVisibility(View.GONE);
        }

        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("Type" , "" + mCollectionType) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_COLLECTION_LIST_BY_TYPE, false, paramMap
                , new RequestListCallBack<UnuseGoodList>("getCollectionUnuseList"
                        , mContext ,UnuseGoodList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseGoodList> bean) {
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

                        finishRequest();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        finishRequest();
                    }
                    @Override
                    public void onFail(Exception e) {
                        finishRequest();
                    }
                });
    }


    private void finishRequest(){
        mRefreshLay.finishRefresh() ;
        mIsLoading = false ;
        loadPage.showSuccessPage();

        if(mDataList.size() == 0){
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

        if(mIsReload && isVisibleToUser && mDataList != null && mDataList.size() == 0){
            mIsMore = true ;
            getCollectionUnuseList() ;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getCollectionUnuseList");

    }

}
