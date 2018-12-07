package cn.idcby.jiajubang.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.application.MyApplication;

public class ServicePublishViewModel extends BaseObservable implements ViewModel {
    private BaseBindActivity activity;
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;
    private String address;
    private String serviceInfo;
    private String serviceTitle;
    private String price;

    public ServicePublishViewModel(BaseBindActivity activity) {
        this.activity = activity;
        initData();
    }

    private void initData() {
        setAddress(MyApplication.getCurrentCityName());
        cityId=MyApplication.getCurrentCityId();
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
        notifyPropertyChanged(BR.serviceInfo);
    }

    @Bindable
    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
        notifyPropertyChanged(BR.serviceTitle);
    }

    @Override
    public void destory() {

    }
}
