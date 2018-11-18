package cn.idcby.jiajubang.fragment;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.GoodsDetailsActivity;
import cn.idcby.jiajubang.adapter.WstGoodsRecycleViewJpgAdapter;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvGridManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的收藏--商品
 */

public class MyCollectionGoodFragment extends BaseFragment {
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mRecyclerView;
    private HeaderFooterAdapter<WstGoodsRecycleViewJpgAdapter> mAdapter ;
    private List<GoodList> mDataList = new ArrayList<>();

    private int mCollectionType ;
    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;

    private View mNullTv ;


    public static MyCollectionGoodFragment getInstance(int collectionType) {
        MyCollectionGoodFragment fragment = new MyCollectionGoodFragment() ;
        fragment.mCollectionType = collectionType ;
        return fragment ;
    }

    @Override
    protected void requestData() {
        loadPage.showLoadingPage();

        if(!mIsReload){
            mIsReload = true ;

            if(getUserVisibleHint()){
                getCollectionGoodList() ;
            }
        }
    }

    @Override
    protected void initView(View view) {
        mNullTv = view.findViewById(R.id.frag_collection_good_null_tv) ;
        mRefreshLay = view.findViewById(R.id.frag_collection_good_refresh_lay);
        mRecyclerView = view.findViewById(R.id.frag_collection_good_rv);

        WstGoodsRecycleViewJpgAdapter goodAdapter = new WstGoodsRecycleViewJpgAdapter(mContext
                , mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    GoodList good = mDataList.get(position) ;
                    if(good != null){
                        SkipUtils.toGoodDetailsActivity(mContext,good.getProductID()) ;
                    }
                }
            }
        }) ;

        mAdapter = new HeaderFooterAdapter<>(goodAdapter) ;
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
        mRecyclerView.addItemDecoration(new RvGridManagerItemDecoration(mContext ,0,1
                , ResourceUtils.dip2px(mContext ,4)
                ,getResources().getDrawable(R.drawable.drawable_white_trans)));
        mRecyclerView.setAdapter(mAdapter) ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_collection_good;
    }

    @Override
    protected void initListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && ViewUtil.isSlideToBottom(mRecyclerView)){
                    getCollectionGoodList();
                }
            }
        });

        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mCurPage = 1 ;

                getCollectionGoodList() ;
            }
        });
    }


    /**
     * 列表
     */
    private void getCollectionGoodList(){
        if(mNullTv.getVisibility() != View.GONE){
            mNullTv.setVisibility(View.GONE);
        }

        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("Type" , "" + mCollectionType) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_COLLECTION_LIST_BY_TYPE, false, paramMap
                , new RequestListCallBack<GoodList>("getCollectionGoodList"
                        , mContext ,GoodList.class) {
                    @Override
                    public void onSuccessResult(List<GoodList> bean) {
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
            getCollectionGoodList() ;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getCollectionGoodList");

    }

}
