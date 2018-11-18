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
import cn.idcby.jiajubang.adapter.AdapterMyCollectNeedsList;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 我的收藏--需求
 */

public class MyCollectionNeedsFragment extends BaseFragment {
    private ListView mListView;
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;

    private AdapterMyCollectNeedsList mAdapter;
    private List<NeedsList> mList = new ArrayList<>() ;

    private int mCollectionType ;
    private int mCurPage = 1 ;
    private boolean mIsLoading = false ;
    private boolean mIsMore = true ;

    private boolean mIsReload = false ;

    public static MyCollectionNeedsFragment getInstance(int collectionType) {
       MyCollectionNeedsFragment fragment = new MyCollectionNeedsFragment() ;
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
        mListView = view.findViewById(R.id.lay_refresh_lv_list_lv);
        mRefreshLay = view.findViewById(R.id.lay_refresh_lv_refresh_lay) ;
        mNullTv = view.findViewById(R.id.lay_refresh_lv_null_tv) ;

        mAdapter = new AdapterMyCollectNeedsList(mActivity, mList);
        mListView.setAdapter(mAdapter) ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.lay_refresh_lv;
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
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!mIsLoading && mIsMore && i2 > 5 && i + i1 >= i2){
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
                , new RequestListCallBack<NeedsList>("getDataList" +  mCollectionType , mContext ,NeedsList.class) {
                    @Override
                    public void onSuccessResult(List<NeedsList> bean) {
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
