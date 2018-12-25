package cn.idcby.jiajubang.fragment;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.ServerDetailActivity;
import cn.idcby.jiajubang.adapter.AdapterServerList;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RvItemViewClickListener;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;

/**
 * 附近--服务、安装工
 * Created on 2018/4/14.
 * 20181119
 * 去除安装工
 */

public class NearServerListFragment extends BaseFragment {
    private boolean mIsInstallSer = true;

    private View mLoadingLay;
    private ListView mLv;
    private MaterialRefreshLayout mRefreshLay;

    private AdapterServerList mAdapter;
    private List<ServiceList> mDataList = new ArrayList<>();

    private int mCurPage = 1;
    private boolean mIsMore = true;
    private boolean mIsLoading = false;

    private String mCategoryId;


    @Override
    protected void requestData() {
        if (mDataList.isEmpty()) {
            getServerList();
        }
    }

    @Override
    protected void initView(View view) {
//        mCategoryId = getIntent().getStringExtra(SkipUtils.INTENT_CATEGOTY_ID) ;
//        int serverType = getIntent().getIntExtra(SkipUtils.INTENT_SERVER_TYPE ,ServerActivity.SERVER_TYPE_SERVER) ;
//        mIsInstallSer = ServerActivity.SERVER_TYPE_INSTALL == serverType ;

        mRefreshLay = view.findViewById(R.id.acti_near_server_list_refresh_lay);
        mLv = view.findViewById(R.id.acti_near_server_list_lv);
        mLoadingLay = view.findViewById(R.id.acti_near_server_list_loading_lay);


        mAdapter = new AdapterServerList(mContext, mDataList, mIsInstallSer, new RvItemViewClickListener() {
            @Override
            public void onItemClickListener(int type, int position) {
                if (0 == type) {
                    ServiceList serverList = mDataList.get(position);
                    if (serverList != null) {
                        String userId = serverList.getCreateUserId();
                        ServerDetailActivity.launch(mContext, userId, false);
                    }
                }
            }
        });
        mLv.setAdapter(mAdapter);

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount
                    , int totalItemCount) {

                if (!mIsLoading && mIsMore && totalItemCount > 5
                        && visibleItemCount + firstVisibleItem >= totalItemCount) {
                    getServerList();
                }
            }
        });
        mRefreshLay.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mIsMore = true;
                mCurPage = 1;
                getServerList();
            }
        });
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_near_server_list;
    }

    @Override
    public void initListener() {
    }


    private void showOrHiddenLoading(boolean isShow) {
        mLoadingLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取列表
     */
    private void getServerList() {
        mIsLoading = true;

        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("Page", "" + mCurPage);
        paramMap.put("PageSize", "10");
        paramMap.put("Longitude", MyApplication.getLongitude());
        paramMap.put("Latitude", MyApplication.getLatitude());
        paramMap.put("AreaId", MyApplication.getCurrentCityId());
        paramMap.put("AreaType", "" + MyApplication.getCurrentCityType());
//        paramMap.put("CategoryId", StringUtils.convertNull(mCategoryId));

        //附近服务，直接调用服务列表即可，以后接口会调整，所有信息都是经纬度倒序排列，附近的相关接口可能会废弃
//        String url = mIsInstallSer ? Urls.API_NEAR_SERVER_INSTALL : Urls.API_NEAR_SERVER ;
        String url = Urls.API_NEAR_SERVER_INSTALL;

        NetUtils.getDataFromServerByPost(mContext, url, paramMap
                , new RequestListCallBack<ServiceList>("getServerList", mContext, ServiceList.class) {
                    @Override
                    public void onSuccessResult(List<ServiceList> bean) {
                        if (1 == mCurPage) {
                            mDataList.clear();
                        }
                        mDataList.addAll(bean);
                        mAdapter.notifyDataSetChanged();

                        if (bean.size() == 0) {
                            mIsMore = false;
                        } else {
                            mCurPage++;
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

    /**
     * 完成请求
     */
    private void finishRequest() {
        mIsLoading = false;
        mRefreshLay.finishRefresh();

        showOrHiddenLoading(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getServerList");

    }
}
