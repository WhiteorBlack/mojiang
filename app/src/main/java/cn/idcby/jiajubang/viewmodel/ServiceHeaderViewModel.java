package cn.idcby.jiajubang.viewmodel;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.AdvBanner;
import cn.idcby.jiajubang.Bean.BaseCategory;
import cn.idcby.jiajubang.Bean.ServerCategory;
import cn.idcby.jiajubang.activity.ServerActivity;
import cn.idcby.jiajubang.activity.ServiceListNewActivity;
import cn.idcby.jiajubang.adapter.ServiceClassAdapter;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

public class ServiceHeaderViewModel extends BaseObservable implements ViewModel, OnBannerClickListener {
    private BaseBindActivity activity;
    private ServiceClassAdapter classAdapter;
    private Banner banner;
    private List<AdvBanner> mTopBannerList=new ArrayList<>();
    private String search="";

    public ServiceHeaderViewModel(BaseBindActivity activity, ServiceClassAdapter classAdapter, Banner banner) {
        this.activity = activity;
        this.classAdapter = classAdapter;
        this.banner=banner;
        this.banner.setOnBannerClickListener(this);
    }

    public void getData() {
        requestServerCategory();
        requestBanner();
    }

    /**
     * 顶部banner
     */
    private void requestBanner() {
        Map<String, String> para = ParaUtils.getPara(activity);
        para.put("Code", "AppServerHead");
        NetUtils.getDataFromServerByPost(activity, Urls.API_ADVERT, false, para,
                new RequestListCallBack<AdvBanner>("getTopBanner", activity, AdvBanner.class) {
                    @Override
                    public void onSuccessResult(List<AdvBanner> bean) {
                        mTopBannerList.clear();
                        mTopBannerList.addAll(bean) ;
                        List<String> imageUrl = new ArrayList<>(mTopBannerList.size()) ;
                        for(AdvBanner banner : mTopBannerList){
                            imageUrl.add(banner.getImgUrl()) ;
                        }
                        banner.update(imageUrl) ;
                    }
                    @Override
                    public void onErrorResult(String str) {

                    }
                    @Override
                    public void onFail(Exception e) {

                    }
                });
    }

    /**
     * 服务分类
     */
    private void requestServerCategory() {
        Map<String, String> paramMap = ParaUtils.getPara(activity);
        paramMap.put("id", "2");
        paramMap.put("Layer", "1");

        NetUtils.getDataFromServerByPost(activity, Urls.SERVER_CATEGORY, false, paramMap,
                new RequestListCallBack<ServerCategory>("getServerCategory", activity, ServerCategory.class) {
                    @Override
                    public void onSuccessResult(List<ServerCategory> bean) {
                        classAdapter.setNewData(bean);
                    }

                    @Override
                    public void onErrorResult(String str) {
                    }

                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    public RecyclerView.OnItemTouchListener onItemTouchListener() {
        return new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapters, View view, int position) {
                ServerCategory category=classAdapter.getData().get(position);
                Intent intent=new Intent(activity,ServiceListNewActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt(SkipUtils.INTENT_SERVER_TYPE, 1);
                bundle.putString(SkipUtils.INTENT_CATEGOTY_ID,category.getServiceCategoryID());
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        };
    }

    @Bindable
    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
        notifyPropertyChanged(BR.search);
    }

    @Override
    public void destory() {
        NetUtils.cancelTag("getServerCategory");
    }

    @Override
    public void OnBannerClick(int position) {
        SkipUtils.intentToOtherByAdvId(activity ,mTopBannerList.get(position - 1)) ;
    }
}
