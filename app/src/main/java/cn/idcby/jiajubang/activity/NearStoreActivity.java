package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.HomeStore;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.NearStoreListAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.RvLinearManagerItemDecoration;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 附近店铺
 * Created on 2018-04-27.
 *
 * 2018-08-24 14:01:45
 * 改：布局改为类似资讯三图样式
 */

public class NearStoreActivity extends BaseActivity {
    private MaterialRefreshLayout materialRefreshLayout;
    private RecyclerView mRecycleView;
    private TextView mNullTv ;

    private int mPage = 1;//页码

    private List<HomeStore> mDataList = new ArrayList<>() ;
    private NearStoreListAdapter mAdapter;
    private boolean mIsMore = true;
    private boolean mIsLoading = false;

    private boolean mIsSpec = false ;//true是推荐

    public static void launch(Context context , boolean isSpec){
        Intent toLiIt = new Intent(context ,NearStoreActivity.class) ;
        toLiIt.putExtra("isSpecStore" ,isSpec) ;
        context.startActivity(toLiIt) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_near_store_list;
    }

    @Override
    public void initView() {
        mIsSpec = getIntent().getBooleanExtra("isSpecStore",mIsSpec) ;

        if(mIsSpec){
            TextView mTitleTv = findViewById(R.id.acti_near_store_title_tv) ;
            mTitleTv.setText("店铺推荐");
        }

        materialRefreshLayout = findViewById(R.id.acti_near_store_refresh_lay);
        mRecycleView = findViewById(R.id.acti_near_store_rv);
        mNullTv = findViewById(R.id.acti_near_store_null_tv);

        mAdapter = new NearStoreListAdapter(mContext ,mDataList);
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecycleView.addItemDecoration(new RvLinearManagerItemDecoration(mContext,ResourceUtils.dip2px(mContext
                ,ResourceUtils.getXmlDef(mContext,R.dimen.nomal_divider_height))
                ,getResources().getDrawable(R.drawable.drawable_white_trans)));
        mRecycleView.setAdapter(mAdapter);

        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mPage = 1 ;
                getListDatas();
            }
        });

        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mIsLoading && mIsMore && mDataList.size() > 5 && ViewUtil.isRecycleView2Bottom(mRecycleView)) {
                    getListDatas();
                }
            }
        });
    }


    private void getListDatas() {
        if(mNullTv.getVisibility() == View.VISIBLE){
            mNullTv.setVisibility(View.GONE);
        }

        mIsLoading = true;
        Map<String, String> para = ParaUtils.getParaNece(mContext);
        para.put("PageSize", "10");
        para.put("Page", "" + mPage);
        if(!mIsSpec){
            para.put("Longitude", MyApplication.getLongitude());
            para.put("Latitude", MyApplication.getLatitude());
            para.put("AreaId", MyApplication.getCurrentCityId());
            para.put("AreaType", "" + MyApplication.getCurrentCityType());
        }

        NetUtils.getDataFromServerByPost(mContext, mIsSpec ? Urls.API_HOME_COMMENT_STORE : Urls.API_NEAR_SHOP_LIST, false, para,
                new RequestListCallBack<HomeStore>("getListDatas", mContext, HomeStore.class) {
            @Override
            public void onSuccessResult(List<HomeStore> bean) {
                if(1 == mPage){
                    mDataList.clear();
                }

                mDataList.addAll(bean) ;
                mAdapter.notifyDataSetChanged() ;

                if (bean.size() <= 0) {
                    mIsMore = false;
                } else {
                    mIsMore = true;
                    mPage++;
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

    @Override
    public void initData() {
        getListDatas();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {

    }

    /**
     * 完成请求
     */
    private void finishRequest() {
        mIsLoading = false;
        materialRefreshLayout.finishRefresh();

        if(mDataList.size() == 0){
            mNullTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getListDatas") ;
    }
}

