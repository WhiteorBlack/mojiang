package cn.idcby.jiajubang.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.jiajubang.Bean.CollectionResult;
import cn.idcby.jiajubang.Bean.FoStoreBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.FocusStoreAdapter;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.utils.ViewUtil;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * Created by Administrator on 2018-04-27.
 */

public class MyFocusStoreActivity extends BaseActivity {
    private MaterialRefreshLayout materialRefreshLayout;
    private RecyclerView mRecycleView;

    private int mPage = 1;//页码

    private FocusStoreAdapter mAdapter;
    private boolean mIsMore = true;
    private boolean mIsLoading = false;

    private TextView mTitleTV;

    private LoadingDialog mDialog ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_myfpllows;
    }

    @Override
    public void initView() {

        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.myfollow_mrl);
        mRecycleView = (RecyclerView) findViewById(R.id.myfollow_rv);
        mTitleTV = (TextView) findViewById(R.id.title_funs_tv);
        mTitleTV.setText("关注的店铺");

        mAdapter = new FocusStoreAdapter(mContext, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                FoStoreBean storeBean = mAdapter.getListData().get(position) ;
                if(storeBean != null){
                    String storeId = storeBean.getShopInfoId() ;

                    if(0 == type){
                        SkipUtils.toStoreIndexActivity(mContext ,storeId) ;
                    }else if(1 == type){
                        goCollection(storeId);
                    }
                }
            }
        });
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecycleView.setAdapter(mAdapter);

        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mPage = 1 ;
                getListDatas(true);
            }
        });
        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mIsLoading && mIsMore && ViewUtil.isRecycleView2Bottom(mRecycleView)) {
                    getListDatas(false);
                }
            }
        });
    }


    /**
     * 收藏
     */
    private void goCollection(String storeId){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
            mDialog.setCancelable(false);
        }
        mDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("FollowType" , "2") ;
        paramMap.put("ResourceId" , StringUtils.convertNull(storeId)) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.FOCUS_OR_CANCEL_USER, false, paramMap
                , new RequestObjectCallBack<CollectionResult>("goCollection" , mContext ,CollectionResult.class) {
                    @Override
                    public void onSuccessResult(CollectionResult bean) {
                        mDialog.dismiss();
                        mPage = 1 ;
                        getListDatas(true) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                    }
                });
    }

    private void getListDatas(final boolean isClear) {
        mIsLoading = true;
        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        paramMap.put("Page", mPage + "");
        paramMap.put("PageSize", "10");
        paramMap.put("ID", "2");
        NetUtils.getDataFromServerByPost(mContext, Urls.ME_GETMYCOLLECTIONMAN
                , paramMap, new RequestListCallBack<FoStoreBean>("getListDatas", mContext, FoStoreBean.class) {
            @Override
            public void onSuccessResult(List<FoStoreBean> bean) {
                if (bean.size() <= 0) {
                    mIsMore = false;
                } else {
                    mIsMore = true;
                    mPage++;
                }

                mAdapter.setDataRefresh(bean, isClear);
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
        getListDatas(false);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getListDatas") ;
    }
}

