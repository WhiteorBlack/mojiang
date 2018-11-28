package cn.idcby.jiajubang.viewmodel;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ServiceAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.ScreenUtil;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.dialog.DoubleCityDownDialog;

public class ServiceListViewModel implements ViewModel {
    private BaseBindActivity activity;
    private ServiceAdapter adapter;
    private int mCurPage = 1;
    private String mSortType;
    private String mSearchKey;
    private String mCheckedCateId;
    private String mCategoryLayer;
    private boolean mIsInstallSer = false;
    private boolean mIsArrowUp = false;

    public ServiceListViewModel(BaseBindActivity activity, ServiceAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
        mCheckedCateId = activity.getIntent().getBundleExtra("bundle").getString(SkipUtils.INTENT_CATEGOTY_ID);
    }

    public void getData() {

        Map<String, String> paramMap = ParaUtils.getPara(activity);
        paramMap.put("TypeId", "" + mSortType);
        paramMap.put("Keyword", StringUtils.convertNull(mSearchKey));
        paramMap.put("CategoryId", StringUtils.convertNull(mCheckedCateId));
        paramMap.put("CategoryLevel", "" + mCategoryLayer);
        paramMap.put("Page", "" + mCurPage);
        paramMap.put("PageSize", "10");
        paramMap.put("AreaId", "" + MyApplication.getCurrentCityId());
        paramMap.put("AreaType", "" + MyApplication.getCurrentCityType());

        String url = mIsInstallSer ? Urls.SERVER_LIST_INSTALL : Urls.SERVER_LIST_SERVER;

        NetUtils.getDataFromServerByPost(activity, url, paramMap
                , new RequestListCallBack<ServiceList>("getServerList" + mSortType + "," + mIsArrowUp
                        , activity, ServiceList.class) {
                    @Override
                    public void onSuccessResult(List<ServiceList> bean) {
                        adapter.setPagingData(bean, mCurPage);
                        activity.refreshOk();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        ToastUtils.showToast(activity, str);
                        activity.refreshFail();
                    }

                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    public void loadMore() {
        mCurPage++;
        getData();
    }

    public void refresh() {
        mCurPage = 1;
        getData();
    }

    private DoubleCityDownDialog cityDialog;
    public void showCityDialog(View view){
        if (cityDialog==null){
            cityDialog=new DoubleCityDownDialog(activity);
            cityDialog.getFirstData();
        }
//        int[] point=new int[2];
//        view.getLocationInWindow(point);
        cityDialog.show();
//        cityDialog.showAtLocation(0,point[1]+ScreenUtil.dip2px(80));
    }

    public RecyclerView.OnItemTouchListener onItemTouchListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        };
    }

    @Override
    public void destory() {
        NetUtils.cancelTag("getServerList" + mSortType + "," + mIsArrowUp);
    }
}
