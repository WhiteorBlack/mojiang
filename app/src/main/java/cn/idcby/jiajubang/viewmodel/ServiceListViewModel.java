package cn.idcby.jiajubang.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.Address;
import cn.idcby.jiajubang.Bean.ServiceList;
import cn.idcby.jiajubang.activity.ServerActivity;
import cn.idcby.jiajubang.adapter.ServiceAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.DoubleSelectionInterface;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.ScreenUtil;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.dialog.DoubleCityDownDialog;

public class ServiceListViewModel extends BaseObservable implements ViewModel {
    private BaseBindActivity activity;
    private ServiceAdapter adapter;
    private int mCurPage = 1;
    private int mSortType=1;
    private String mSearchKey="";
    private String mCheckedCateId;
    private int mCategoryLayer=1;
    private boolean mIsInstallSer = false;
    private boolean mIsArrowUp = false;
    private String areaId;
    private String aredType="2";

    private String selectCity="";

    public ServiceListViewModel(BaseBindActivity activity, ServiceAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;

        setSelectCity(MyApplication.LOCATION_CITY);
        initData();
    }

    private void initData() {
        areaId=MyApplication.getCurrentCityId();
        mCheckedCateId = activity.getIntent().getExtras().getString(SkipUtils.INTENT_CATEGOTY_ID);
        mIsInstallSer = ServerActivity.SERVER_TYPE_INSTALL == activity.getIntent().getExtras().getInt(SkipUtils.INTENT_SERVER_TYPE) ;
        aredType=MyApplication.getCurrentCityType();
    }

    public void getData() {
        Map<String, String> paramMap = ParaUtils.getPara(activity);
        paramMap.put("TypeId", "" + mSortType);
        paramMap.put("Keyword", StringUtils.convertNull(mSearchKey));
        paramMap.put("CategoryId", StringUtils.convertNull(mCheckedCateId));
        paramMap.put("CategoryLevel", "" + mCategoryLayer);
        paramMap.put("Page", "" + mCurPage);
        paramMap.put("PageSize", "10");
        paramMap.put("AreaId", areaId);
        paramMap.put("AreaType", aredType);

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

    public void showCityDialog(View view) {
        if (cityDialog == null) {
            cityDialog = new DoubleCityDownDialog(activity, true);
            cityDialog.getFirstData();
            cityDialog.setDoubleSelectionInterface(new DoubleSelectionInterface() {
                @Override
                public void onSelection(Object post) {
                    if (post==null)
                        return;
                    Address address= (Address) post;
                    areaId=address.AreaId;
                    setSelectCity(address.AreaName);
                }
            });
        }
        int[] point = new int[2];
        view.getLocationInWindow(point);
//        cityDialog.show();
        cityDialog.showAtLocation(0, point[1] + ScreenUtil.dip2px(80));
    }

    public RecyclerView.OnItemTouchListener onItemTouchListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        };
    }

    @Bindable
    public String getSelectCity() {
        return selectCity;
    }

    public void setSelectCity(String selectCity) {
        this.selectCity = selectCity;
        notifyPropertyChanged(BR.selectCity);
    }

    @Override
    public void destory() {
        NetUtils.cancelTag("getServerList" + mSortType + "," + mIsArrowUp);
    }
}
