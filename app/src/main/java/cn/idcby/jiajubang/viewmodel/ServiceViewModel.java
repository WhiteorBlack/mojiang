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
import cn.idcby.jiajubang.utils.Urls;

public class ServiceViewModel implements ViewModel {
    private BaseBindActivity activity;
    private ServiceAdapter adapter;
    private int mCurPage = 1;

    public ServiceViewModel(BaseBindActivity activity, ServiceAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    public void getData() {
        getRecommendServer();
    }

    public void loadMore() {
        mCurPage++;
        getData();
    }

    public void refresh() {
        mCurPage=1;
        getData();
    }

    /**
     * 获取推荐
     */
    private void getRecommendServer() {

        Map<String, String> paramMap = ParaUtils.getPara(activity);
        paramMap.put("Page", "" + mCurPage);
        paramMap.put("PageSize", "10");
        paramMap.put("AreaId", "" + MyApplication.getCurrentCityId());
        paramMap.put("AreaType", "" + MyApplication.getCurrentCityType());

        String urls = Urls.SERVER_SERVER_LIST_RECOMMEND;


        NetUtils.getDataFromServerByPost(activity, urls, paramMap
                , new RequestListCallBack<ServiceList>("getRecommendServer"
                        , activity, ServiceList.class) {
                    @Override
                    public void onSuccessResult(List<ServiceList> bean) {
                        adapter.setPagingData(bean, mCurPage);
                        activity.refreshOk();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        activity.refreshFail();
                        ToastUtils.showToast(activity,str);
                    }

                    @Override
                    public void onFail(Exception e) {
                        activity.refreshFail();
                    }
                });
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
        NetUtils.cancelTag("getRecommendServer");
    }
}
