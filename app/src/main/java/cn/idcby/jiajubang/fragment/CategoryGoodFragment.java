package cn.idcby.jiajubang.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.base.HeaderFooterAdapter;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.WstGoodsRecycleViewJpgAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvGridManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 分类下的商品列表
 */

public class CategoryGoodFragment extends BaseFragment{
    private String mStoreId ;
    private String mCategoryId ;
    private String mCheckedCateId ;
    private String mSearchKey ;
    private boolean mIsFromStore = false ;
    private int mSortType = 1 ; //1.全部 2.价格 3.销量 4.好评
    private boolean mIsArrowUp = true ;//升序
    private String mGoodParams = "" ;//筛选规格，逗号隔开

    private View mLoadingLay ;
    private TextView mNullTv ;
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mLv ;


    private HeaderFooterAdapter<WstGoodsRecycleViewJpgAdapter> mAdapter ;
    private List<GoodList> mDataList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private TextView mFooterTv ;


    public static CategoryGoodFragment getInstance(String parentId
            , String categoryId , String key , String storeId,int sortType ,boolean isArrowUp,boolean fromStore){
        CategoryGoodFragment fragment = new CategoryGoodFragment() ;
        fragment.mCategoryId = StringUtils.convertNull(parentId);
        fragment.mCheckedCateId = StringUtils.convertNull(categoryId) ;
        fragment.mSearchKey = StringUtils.convertNull(key) ;
        fragment.mStoreId = StringUtils.convertNull(storeId) ;
        fragment.mSortType = sortType ;
        fragment.mIsArrowUp = isArrowUp ;
        fragment.mIsFromStore = fromStore ;
        return fragment ;
    }


    @Override
    protected void requestData() {
        loadPage.showSuccessPage();
    }

    @Override
    protected void initView(View view) {
        mRefreshLay = view.findViewById(R.id.frag_category_good_list_refresh_lay) ;
        mLv = view.findViewById(R.id.frag_category_good_list_rv);
        mLoadingLay = view.findViewById(R.id.frag_category_good_list_loading_lay) ;
        mNullTv = view.findViewById(R.id.frag_category_good_list_null_tv) ;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_category_good_list;
    }

    @Override
    protected void initListener() {
        WstGoodsRecycleViewJpgAdapter adapter = new WstGoodsRecycleViewJpgAdapter(mContext, mDataList, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if(0 == type){
                    GoodList good = mDataList.get(position) ;
                    if(good != null){
                        SkipUtils.toGoodDetailsActivity(mContext,good.getProductID()) ;
                    }
                }
            }
        });

        mAdapter = new HeaderFooterAdapter<>(adapter) ;
        mLv.setLayoutManager(new GridLayoutManager(mContext,2));
        mLv.addItemDecoration(new RvGridManagerItemDecoration(mContext ,0,1
                ,ResourceUtils.dip2px(mContext ,ResourceUtils.getXmlDef(mContext ,R.dimen.nomal_divider_height))
                ,getResources().getDrawable(R.drawable.drawable_white_trans)));
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

        String sort = "asc" ;
        String sortCode = "zonghe" ;
        if(2 == mSortType){//价格
            sortCode = "SalePrice" ;
            sort = mIsArrowUp ? "asc" : "desc" ;
        }else if(3 == mSortType){//销量
            sortCode = "SaleNumber" ;
            sort = mIsArrowUp ? "asc" : "desc" ;
        }else if(4 == mSortType){//好评
            sortCode = "Star" ;
            sort = mIsArrowUp ? "asc" : "desc" ;
        }

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("SortCode" , sortCode) ;
        paramMap.put("Sort" , sort) ;
        paramMap.put("Keyword" , StringUtils.convertNull(mSearchKey)) ;
        paramMap.put("RootCategoryID" , StringUtils.convertNull(mCategoryId)) ;
        paramMap.put("CategoryID" , StringUtils.convertNull(mCheckedCateId)) ;
        paramMap.put("paraIds" , mGoodParams) ;
        paramMap.put("Page" , "" + mCurPage) ;
        paramMap.put("PageSize" , "10") ;
        paramMap.put("AreaId" , MyApplication.getCurrentCityId()) ;
        paramMap.put("AreaType" ,MyApplication.getCurrentCityType()) ;

        if(mIsFromStore){
            paramMap.put("MerchantID" ,mStoreId) ;
            paramMap.put("MerchantIsRecommend" ,"0") ;
        }

        mIsLoading = true ;

        NetUtils.getDataFromServerByPost(mContext,Urls.UNUSE_DIRE_GOOD_LIST, paramMap
                , new RequestListCallBack<GoodList>("getGoodList" + mSortType + "," + mIsArrowUp , mContext , GoodList.class) {
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

    public void refreshList(String key ,int sortType ,boolean isArrowUp,String params){
        if(mDataList.size() == 0 || !mSearchKey.equals(StringUtils.convertNull(key))
                || sortType != mSortType || isArrowUp != mIsArrowUp
                || !mGoodParams.equals(StringUtils.convertNull(params))){
            mSearchKey = StringUtils.convertNull(key) ;
            mGoodParams = StringUtils.convertNull(params) ;
            mSortType = sortType ;
            mIsArrowUp = isArrowUp ;

            mCurPage = 1 ;
            getGoodList() ;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getGoodList" + mSortType + "," + mIsArrowUp+ "," + mCheckedCateId);

    }

}
