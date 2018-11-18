package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
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
import cn.idcby.jiajubang.view.TopBarRightView;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 商品列表--搜索返回的
 */
public class SearchGoodListActivity extends BaseActivity {
    private String mStoreId ;
    private String mCategoryId ;
    private String mCheckedCateId ;
    private String mSearchKey ;
    private boolean mIsFromStore = false ;
    private int mSortType = 1 ; //1.全部 2.价格 3.销量 3.好评
    private boolean mIsArrowUp = true ;//升序

    private TopBarRightView mRightLay ;
    private TextView mSearchKeyTv ;
    private View mLoadingLay ;
    private TextView mNullTv ;
    private MaterialRefreshLayout mRefreshLay ;
    private RecyclerView mLv ;

    private TextView mTypeAllTv ;
    private TextView mTypeCountTv;
    private ImageView mTypeCountIv;
    private TextView mTypeCommentTv;
    private ImageView mTypeCommentIv;
    private TextView mTypePerfectTv;
    private ImageView mTypePerfectIv;

    private HeaderFooterAdapter<WstGoodsRecycleViewJpgAdapter> mAdapter ;
    private List<GoodList> mDataList = new ArrayList<>();

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;

    private TextView mFooterTv ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_search_good_list;
    }

    @Override
    public void initView() {
        mStoreId = getIntent().getStringExtra(SkipUtils.INTENT_STORE_ID) ;
        mCategoryId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID) ;
        mCheckedCateId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_CHILD_ID) ;
        mSearchKey = getIntent().getStringExtra(SkipUtils.INTENT_SEARCH_KEY) ;
        mIsFromStore = !"".equals(StringUtils.convertNull(mStoreId)) ;

        mRightLay = findViewById(R.id.acti_category_good_list_right_iv) ;
        View searchLay = findViewById(R.id.acti_category_good_list_search_lay) ;
        mSearchKeyTv = findViewById(R.id.acti_category_good_list_search_key_tv) ;
        mRefreshLay = findViewById(R.id.acti_category_good_list_refresh_lay) ;
        mLv = findViewById(R.id.acti_category_good_list_rv);
        mLoadingLay = findViewById(R.id.acti_category_good_list_loading_lay) ;
        mNullTv = findViewById(R.id.acti_category_good_list_null_tv) ;
        View mTypeAllLay = findViewById(R.id.acti_category_good_list_all_lay) ;
        mTypeAllTv = findViewById(R.id.acti_category_good_list_all_tv) ;
        View mTypeCountLay = findViewById(R.id.acti_category_good_list_count_lay) ;
        mTypeCountTv = findViewById(R.id.acti_category_good_list_count_tv) ;
        mTypeCountIv = findViewById(R.id.acti_category_good_list_count_iv) ;
        View mTypeCommentLay = findViewById(R.id.acti_category_good_list_comment_lay) ;
        mTypeCommentTv = findViewById(R.id.acti_category_good_list_comment_tv) ;
        mTypeCommentIv = findViewById(R.id.acti_category_good_list_comment_iv) ;
        View mTypePerfectLay = findViewById(R.id.acti_category_good_list_perfect_lay) ;
        mTypePerfectTv = findViewById(R.id.acti_category_good_list_perfect_tv) ;
        mTypePerfectIv = findViewById(R.id.acti_category_good_list_perfect_iv) ;

        mTypePerfectLay.setOnClickListener(this);
        mTypeCountLay.setOnClickListener(this);
        mTypeCommentLay.setOnClickListener(this);
        mTypeAllLay.setOnClickListener(this);
        searchLay.setOnClickListener(this);
        mRightLay.setOnClickListener(this);

        if(!"".equals(StringUtils.convertNull(mSearchKey))){
            mSearchKeyTv.setText(mSearchKey) ;
        }

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

    @Override
    public void initData() {
        getGoodList() ;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_category_good_list_right_iv == vId){
            SkipUtils.toMessageCenterActivity(mContext) ;
        }else if(R.id.acti_category_good_list_search_lay == vId){
            Intent toShIt = new Intent(mContext ,SearchNomalActivity.class) ;
            toShIt.putExtra(SkipUtils.INTENT_SEARCH_KEY , mSearchKey) ;
            mActivity.startActivityForResult(toShIt , 1003) ;
        }else if(R.id.acti_category_good_list_all_lay == vId){
            changeTypeTvStyle(1) ;
        }else if(R.id.acti_category_good_list_count_lay == vId){
            changeTypeTvStyle(2) ;
        }else if(R.id.acti_category_good_list_comment_lay == vId){
            changeTypeTvStyle(3) ;
        }else if(R.id.acti_category_good_list_perfect_lay == vId){
            changeTypeTvStyle(4) ;
        }
    }

    private void changeTypeTvStyle(int type){
        switch (mSortType){
            case 1:
                mTypeAllTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                break;
            case 2:
                mTypeCountTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                mTypeCountIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_nomal));
                break;
            case 3:
                mTypeCommentTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                mTypeCommentIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_nomal));

                break;
            case 4:
                mTypePerfectTv.setTextColor(getResources().getColor(R.color.color_nav_normal)) ;
                mTypePerfectIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_nomal));
                break;
            default:
                break;
        }

        if(1 == type){
            mIsArrowUp = true ;
        }else{
            if(mSortType != type){
                mIsArrowUp = true ;
            }

            mIsArrowUp = !mIsArrowUp ;
        }

        switch (type){
            case 1:
                mTypeAllTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;
                break;
            case 2:
                mTypeCountTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;

                if(mIsArrowUp){
                    mTypeCountIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_up));
                }else{
                    mTypeCountIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_down));
                }

                break;
            case 3:
                mTypeCommentTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;

                if(mIsArrowUp){
                    mTypeCommentIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_up));
                }else{
                    mTypeCommentIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_down));
                }
                break;
            case 4:
                mTypePerfectTv.setTextColor(getResources().getColor(R.color.color_nav_checked)) ;

                if(mIsArrowUp){
                    mTypePerfectIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_up));
                }else{
                    mTypePerfectIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up_down_down));
                }
                break;
            default:
                break;
        }

        mSortType = type ;
        changeNeedTypeAndGetList() ;
    }

    private void changeNeedTypeAndGetList(){
        NetUtils.cancelTag("getGoodList" + mSortType + "," + mIsArrowUp);

        mCurPage = 1 ;
        mIsMore = true ;

        showOrHiddenLoading(true);

        mDataList.clear();
        mAdapter.notifyDataSetChanged() ;

        getGoodList();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1003 == requestCode){
            if(RESULT_OK == resultCode && data != null){
                mSearchKey = data.getStringExtra(SkipUtils.INTENT_SEARCH_KEY);
                if(null == mSearchKey){
                    mSearchKey = "" ;
                }

                mSearchKeyTv.setText("".equals(mSearchKey) ? mContext.getResources().getString(R.string.nomal_search_def) : mSearchKey) ;
                changeNeedTypeAndGetList() ;
            }
        }
    }

    @Override
    public void onMessageCountChange(int count) {
        super.onMessageCountChange(count);

        mRightLay.setUnreadCount(count);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getGoodList" + mSortType + "," + mIsArrowUp);

    }
}
