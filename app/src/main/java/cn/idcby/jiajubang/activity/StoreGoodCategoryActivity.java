package cn.idcby.jiajubang.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.jiajubang.Bean.StoreGoodCategory;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterStoreGoodCategory;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;

/**
 * 店铺商品分类（店铺宝贝）
 * Created on 2018/4/27.
 */

public class StoreGoodCategoryActivity extends BaseMoreStatusActivity {
    private RecyclerView mRecyclerView ;

    private List<StoreGoodCategory> mDataList = new ArrayList<>() ;
    private AdapterStoreGoodCategory mAdapter ;

    private String mStoreId ;

    private int mCurPage = 1 ;
    private boolean mIsMore = true ;
    private boolean mIsLoading = false ;



    @Override
    public void requestData() {
        getGoodCategory() ;
    }

    @Override
    public int getSuccessViewId() {
        return R.layout.activity_store_good_category ;
    }

    @Override
    public String setTitle() {
        return "店铺分类";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {
    }

    @Override
    public void init() {
        mStoreId = getIntent().getStringExtra(SkipUtils.INTENT_STORE_ID) ;

        mRecyclerView = findViewById(R.id.acti_store_good_category_rv) ;

        mAdapter = new AdapterStoreGoodCategory(mContext, mDataList, mStoreId) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mIsMore && mDataList.size() > 5
                        && ViewUtil.isSlideToBottom(mRecyclerView)){
                    getGoodCategory() ;
                }
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {

    }

    /**
     * 获取分类
     */
    private void getGoodCategory(){
        mIsLoading = true ;

        Map<String,String> paramMap = ParaUtils.getParaNece(mContext) ;
        paramMap.put("Page","" + mCurPage) ;
        paramMap.put("PageSize","10") ;
        paramMap.put("Keyword", StringUtils.convertNull(mStoreId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.STORE_GOOD_CATEGORY, paramMap
                , new RequestListCallBack<StoreGoodCategory>("getGoodCategory" ,mContext ,StoreGoodCategory.class) {
                    @Override
                    public void onSuccessResult(List<StoreGoodCategory> bean) {
                        mDataList.addAll(bean) ;
                        mAdapter.notifyDataSetChanged();

                        if(bean.size() == 0){
                            mIsMore = false ;
                        }else{
                            mIsMore = true ;
                            mCurPage ++ ;
                        }

                        showSuccessPage() ;
                        mIsLoading = false ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        showSuccessPage() ;
                        mIsLoading = false ;

                    }
                    @Override
                    public void onFail(Exception e) {
                        showSuccessPage() ;
                        mIsLoading = false ;

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getGoodCategory") ;

    }
}
