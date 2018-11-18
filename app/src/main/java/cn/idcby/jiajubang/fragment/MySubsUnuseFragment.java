package cn.idcby.jiajubang.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.UnuseGoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.SubClassificationActivity;
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
 * Created on 2018/5/26.
 */

public class MySubsUnuseFragment extends BaseFragment {
    private RecyclerView mRv;
    private MaterialRefreshLayout mRefreshLay ;
    private TextView mNullTv ;

    private AdapterUnuseSpecGood mAdapter ;
    private List<UnuseGoodList> mUnuseGoodList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;


    @Override
    protected void requestData() {
        if(getUserVisibleHint()){
            getUnuseGoodList() ;
        }
    }

    @Override
    protected void initView(View view) {
        mRefreshLay = view.findViewById(R.id.lay_loading_rv_refresh_lay) ;
        mRv = view.findViewById(R.id.lay_loading_rv_list_rv);
        mNullTv = view.findViewById(R.id.lay_loading_rv_null_tv) ;
        View mLoadingLay = view.findViewById(R.id.lay_loading_rv_progressBar) ;
        mLoadingLay.setVisibility(View.GONE);

        mAdapter = new AdapterUnuseSpecGood(mActivity, mUnuseGoodList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                UnuseGoodList goodList = mUnuseGoodList.get(position) ;
                if(goodList != null){
                    Intent toDtIt = new Intent(mContext ,UnuseGoodDetailsActivity.class) ;
                    toDtIt.putExtra(SkipUtils.INTENT_UNUSE_ID,goodList.getProductID()) ;
                    startActivity(toDtIt) ;
                }
            }
        }) ;
        mRv.setLayoutManager(new LinearLayoutManager(mContext));
        RvLinearManagerItemDecoration itemDecoration = new RvLinearManagerItemDecoration(mContext
                , ResourceUtils.dip2px(mContext ,ResourceUtils.getXmlDef(mContext,R.dimen.nomal_divider_height))
                ,getResources().getDrawable(R.drawable.drawable_white_trans)) ;
        mRv.addItemDecoration(itemDecoration);
        mRv.setAdapter(mAdapter);
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore
                        && mUnuseGoodList.size() > 5
                        && ViewUtil.isSlideToBottom(mRv)){
                    getUnuseGoodList();
                }
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
    protected int setSuccessViewId() {
        return R.layout.lay_loading_rv;
    }

    @Override
    protected void initListener() {

    }

    /**
     * 获取需求列表
     */
    private void getUnuseGoodList(){
        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }

        mIsLoading = true ;

        Map<String, String> paramMap = ParaUtils.getParaWithToken(getContext());
        paramMap.put("Type", "" + SubClassificationActivity.SUBS_TYPE_UNUSE);
        paramMap.put("Page", String.valueOf(mCurPage));
        paramMap.put("PageSize", "10");
        paramMap.put("Keyword", "");

        NetUtils.getDataFromServerByPost(mContext, Urls.SUB_SUBSCRIBE, paramMap
                , new RequestListCallBack<UnuseGoodList>("getUnuseGoodList" , mContext , UnuseGoodList.class) {
                    @Override
                    public void onSuccessResult(List<UnuseGoodList> bean) {
                        if(1 == mCurPage){
                            mUnuseGoodList.clear();
                        }
                        mUnuseGoodList.addAll(bean) ;
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

        if(mUnuseGoodList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && loadPage != null){
            if(mUnuseGoodList.size() == 0){
                loadPage.showLoadingPage() ;
                getUnuseGoodList();
            }
        }
    }
}
