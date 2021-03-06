package cn.idcby.jiajubang.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.Address;
import cn.idcby.jiajubang.Bean.ServerCategory;
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
import cn.idcby.jiajubang.view.dialog.ServiceTypeDownDialog;

public class ServiceListViewModel extends BaseObservable implements ViewModel {
    private BaseBindActivity activity;
    private ServiceAdapter adapter;
    private int mCurPage = 1;
    private int mSortType = 1;
    private String mSearchKey = "";
    private String mCheckedCateId;
    private int mCategoryLayer = 1;
    private boolean mIsInstallSer = false;
    private boolean mIsArrowUp = false;
    private String areaId;
    private String aredType = "1";
    private int selectPos = 0;  //0 城市 1综合  2销量   3好评 4 筛选
    private String selectCity = "";
    private String titleString;
    private String typeString;

    public ServiceListViewModel(BaseBindActivity activity, ServiceAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;

        setSelectCity(MyApplication.LOCATION_CITY);
        initData();
    }

    private void initData() {
        areaId = MyApplication.getCurrentCityId();
        mCheckedCateId = activity.getIntent().getExtras().getString(SkipUtils.INTENT_CATEGOTY_ID);
        mIsInstallSer = ServerActivity.SERVER_TYPE_INSTALL == activity.getIntent().getExtras().getInt(SkipUtils.INTENT_SERVER_TYPE);
        aredType = MyApplication.getCurrentCityType();
        titleString=activity.getIntent().getExtras().getString(SkipUtils.INTENT_TITLE);
        setTypeString("筛选");
        setSelectPos(1);
    }

    /**
     * TypeId  1.综合 2.销量 3.好评
     */
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

    public void sellCount() {
        mSortType = 2;
        mCurPage = 1;
        setSelectPos(2);
        getData();
    }

    public void praise() {
        mSortType = 3;
        mCurPage = 1;
        setSelectPos(3);
        getData();
    }

    public void total() {
        mSortType = 1;
        setSelectPos(1);
        mCurPage = 1;
        getData();
    }

    private ServiceTypeDownDialog typeDownDialog;

    public void others(View view) {
        mSortType = 1;
        setSelectPos(4);
        if (typeDownDialog == null) {
            typeDownDialog = new ServiceTypeDownDialog(activity, true);
            typeDownDialog.setTitle(TextUtils.isEmpty(titleString)?"其他类型":titleString.substring(0,2)+"类型");
            typeDownDialog.getCategoryList(mCheckedCateId);
            typeDownDialog.setDoubleSelectionInterface(new DoubleSelectionInterface() {
                @Override
                public void onSelection(Object post) {
                    if (post==null){
                        return;
                    }
                    ServerCategory category= (ServerCategory) post;
                    setTypeString(category.getCategoryTitle());
                }
            });
        }
        int[] point = new int[2];
        view.getLocationInWindow(point);
        typeDownDialog.showAtLocation(0, point[1]+ ScreenUtil.dip2px(60));
    }

    private DoubleCityDownDialog cityDialog;

    public void showCityDialog(View view) {
        if (cityDialog == null) {
            cityDialog = new DoubleCityDownDialog(activity, true);
            cityDialog.getFirstData();
            cityDialog.setDoubleSelectionInterface(new DoubleSelectionInterface() {
                @Override
                public void onSelection(Object post) {
                    if (post == null)
                        return;
                    Address address = (Address) post;
                    areaId = address.AreaId;
                    setSelectCity(address.AreaName);
                    mSortType = 1;
                    mCurPage = 1;
                    setSelectPos(0);
                    getData();
                }
            });
        }
        int[] point = new int[2];
        view.getLocationInWindow(point);
//        cityDialog.show();
        cityDialog.showAtLocation(0, point[1] + ScreenUtil.dip2px(40));
    }

    public RecyclerView.OnItemTouchListener onItemTouchListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        };
    }

    @Bindable
    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
        notifyPropertyChanged(BR.typeString);
    }

    @Bindable
    public int getSelectPos() {
        return selectPos;
    }

    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
        notifyPropertyChanged(BR.selectPos);
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
