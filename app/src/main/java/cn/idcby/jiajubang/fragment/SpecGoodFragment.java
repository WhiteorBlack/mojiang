package cn.idcby.jiajubang.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.GoodSpecListAdapter;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 专场商品列表
 */

public class SpecGoodFragment extends BaseFragment{
    private String mSpecialId;
    private String mSpecialCategoryId;
    private String mSearchKey ;

    private View mLoadingLay ;
    private TextView mNullTv ;
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mLv ;

    private HeaderFooterAdapter<GoodSpecListAdapter> mAdapter ;
    private List<GoodList> mDataList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private TextView mFooterTv ;

    public static SpecGoodFragment getInstance(String specId
            ,String categoryId ,String key){
        SpecGoodFragment fragment = new SpecGoodFragment() ;
        fragment.mSpecialId = StringUtils.convertNull(specId) ;
        fragment.mSpecialCategoryId = StringUtils.convertNull(categoryId) ;
        fragment.mSearchKey = StringUtils.convertNull(key) ;
        return fragment ;
    }


    @Override
    protected void requestData() {
        loadPage.showSuccessPage();
    }

    @Override
    protected void initView(View view) {
        mRefreshLay = view.findViewById(R.id.frag_spec_good_list_refresh_lay) ;
        mLv = view.findViewById(R.id.frag_spec_good_list_rv);
        mLoadingLay = view.findViewById(R.id.frag_spec_good_list_loading_lay) ;
        mNullTv = view.findViewById(R.id.frag_spec_good_list_null_tv) ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_spec_good_list;
    }

    @Override
    protected void initListener() {
        GoodSpecListAdapter adapter = new GoodSpecListAdapter(mContext, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    GoodList good = mDataList.get(position) ;
                    if(good != null){
                        SkipUtils.toGoodDetailsActivity(mContext,good.getProductID()) ;
                    }
                }else if(1 == type){
                    GoodList good = mDataList.get(position) ;
                    if(good != null){
                        SkipUtils.toStoreIndexActivity(mContext,good.getStoreId()) ;
                    }
                }
            }
        });

        mAdapter = new HeaderFooterAdapter<>(adapter) ;

//        mLv.addItemDecoration(new RvLinearManagerItemDecoration(mContext , ResourceUtils.dip2px(mContext ,1))) ;
        mLv.setLayoutManager(new LinearLayoutManager(mContext));
        mLv.setAdapter(mAdapter);
        mLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore
                        && mDataList.size() > 5
                        && ViewUtil.isSlideToBottom(mLv)){
                    getGoodList();
                }
            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true ;
                mCurPage = 1 ;
                getGoodList();
            }
        });

        mFooterTv = ViewUtil.getLoadingLvFooterView(mContext) ;
        mAdapter.addFooter(mFooterTv) ;
    }


    private void showOrHiddenLoading(boolean isShow){
        mLoadingLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取列表
     */
    private void getGoodList(){
        if(mNullTv.getVisibility() != View.GONE){
            mNullTv.setVisibility(View.GONE);
        }

        if(1 == mCurPage){
            mFooterTv.setText(getResources().getString(R.string.footer_loading_string));
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("SpecialGoodsID" , StringUtils.convertNull(mSpecialId)) ;
        paramMap.put("RootCategoryID" , StringUtils.convertNull(mSpecialCategoryId)) ;
        paramMap.put("Keyword" , StringUtils.convertNull(mSearchKey)) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext,Urls.DIRECT_SPEC_GOOD_LIST, paramMap
                , new RequestListCallBack<GoodList>("getGoodList" + mSpecialId + "," + mSpecialCategoryId
                        , mContext , GoodList.class) {
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

        if(mDataList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE) ;
            mFooterTv.setText("") ;
        }
    }

    public void refreshList(String key){
        if(!mSearchKey.equals(StringUtils.convertNull(key))
                || mDataList.size() == 0){
            mSearchKey = StringUtils.convertNull(key) ;

            mCurPage = 1 ;
            getGoodList() ;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getGoodList" + mSpecialId + "," + mSpecialCategoryId);

    }

}
