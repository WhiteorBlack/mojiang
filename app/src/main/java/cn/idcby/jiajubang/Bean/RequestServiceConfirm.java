package cn.idcby.jiajubang.Bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import cn.idcby.jiajubang.BR;
import java.io.Serializable;

public class RequestServiceConfirm extends BaseObservable implements Serializable {
    /**
     *  paramMap.put("ProvinceId", mProvinceId);
     *         paramMap.put("ProvinceName", mProvinceName);
     *         paramMap.put("CityId", mCityId);
     *         paramMap.put("CityName", mCityName);
     *         paramMap.put("Contacts", name);
     *         paramMap.put("ContactsPhone", phone);
     *         paramMap.put("OrderNO", "");
     *         paramMap.put("OrderStatus", "");
     *         paramMap.put("OrderStatusName", "");
     *         paramMap.put("ServiceAddress", "");
     *         paramMap.put("ServiceIntroduce", desc);
     *         paramMap.put("ServiceUserAccount", details.getHxName());
     *         paramMap.put("ServiceUserHeadIcon", details.getHeadIcon());
     *         paramMap.put("ServiceUserRealName", details.getNickName());
     *         paramMap.put("CreateDate", "");
     *         paramMap.put("CreateUserId", "");
     *         paramMap.put("CreateUserName", "");
     *         paramMap.put("ServiceTime", mTimeStr);
     *         paramMap.put("ServiceAmount", details.getPayMoney());
     *         paramMap.put("ServiceUserId", StringUtils.convertNull(mServerUserId));
     *         paramMap.put("ServiceOrderId", StringUtils.convertNull(mServerOrderId));
     *         paramMap.put("ServiceUserNickName", StringUtils.convertNull(mServerUserName));
     *         paramMap.put("OrderType", "" + mServerType);
     *         paramMap.put("IsCarry", isNeedBanYun + "");
     *         paramMap.put("IsHavePhone", "" + isHasDianTi);
     *         paramMap.put("CarryInstruction", CarryInstruction);
     *         paramMap.put("IsPullCart", "" + isNeedLaChe);
     *         paramMap.put("TruckNeed", "" + isTrunck);
     */
    private String ServiceTime;
    private String ServiceAmount;
    private String ProvinceName;
    private String ProvinceId;
    private String CityId;
    private String CityName;
    private String Contacts;
    private String ContactsPhone;
    private String OrderNO;
    private String OrderStatus;
    private String OrderStatusName;
    private String ServiceAddress;
    private String ServiceIntroduce;
    private String ServiceUserAccount;
    private String ServiceUserHeadIcon;
    private String ServiceUserRealName;
    private String CreateDate;
    private String CreateUserId;
    private String CreateUserName;
    private String ServiceUserId;
    private String ServiceOrderId;
    private String ServiceUserNickName;
    private String OrderType;
    private String IsCarry="1";
    private String IsHavePhone="1";
    private String CarryInstruction;
    private String IsPullCart="1";
    private String TruckNeed="1";

    public String getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(String provinceId) {
        ProvinceId = provinceId;
    }

    public String getServiceTime() {
        return ServiceTime;
    }

    public void setServiceTime(String serviceTime) {
        ServiceTime = serviceTime;
    }

    public String getServiceAmount() {
        return ServiceAmount;
    }

    public void setServiceAmount(String serviceAmount) {
        ServiceAmount = serviceAmount;
    }

    @Bindable
    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
        notifyPropertyChanged(BR.provinceName);
    }

    public String getCityId() {
        return CityId;
    }

    public void setCityId(String cityId) {
        CityId = cityId;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getContacts() {
        return Contacts;
    }

    public void setContacts(String contacts) {
        Contacts = contacts;
    }

    public String getContactsPhone() {
        return ContactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        ContactsPhone = contactsPhone;
    }

    public String getOrderNO() {
        return OrderNO;
    }

    public void setOrderNO(String orderNO) {
        OrderNO = orderNO;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getOrderStatusName() {
        return OrderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        OrderStatusName = orderStatusName;
    }

    @Bindable
    public String getServiceAddress() {
        return ServiceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        ServiceAddress = serviceAddress;
        notifyPropertyChanged(BR.serviceAddress);
    }

    public String getServiceIntroduce() {
        return ServiceIntroduce;
    }

    public void setServiceIntroduce(String serviceIntroduce) {
        ServiceIntroduce = serviceIntroduce;
    }

    public String getServiceUserAccount() {
        return ServiceUserAccount;
    }

    public void setServiceUserAccount(String serviceUserAccount) {
        ServiceUserAccount = serviceUserAccount;
    }

    public String getServiceUserHeadIcon() {
        return ServiceUserHeadIcon;
    }

    public void setServiceUserHeadIcon(String serviceUserHeadIcon) {
        ServiceUserHeadIcon = serviceUserHeadIcon;
    }

    public String getServiceUserRealName() {
        return ServiceUserRealName;
    }

    public void setServiceUserRealName(String serviceUserRealName) {
        ServiceUserRealName = serviceUserRealName;
    }

    @Bindable
    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
        notifyPropertyChanged(BR.createDate);
    }

    public String getCreateUserId() {
        return CreateUserId;
    }

    public void setCreateUserId(String createUserId) {
        CreateUserId = createUserId;
    }

    public String getCreateUserName() {
        return CreateUserName;
    }

    public void setCreateUserName(String createUserName) {
        CreateUserName = createUserName;
    }

    public String getServiceUserId() {
        return ServiceUserId;
    }

    public void setServiceUserId(String serviceUserId) {
        ServiceUserId = serviceUserId;
    }

    public String getServiceOrderId() {
        return ServiceOrderId;
    }

    public void setServiceOrderId(String serviceOrderId) {
        ServiceOrderId = serviceOrderId;
    }

    public String getServiceUserNickName() {
        return ServiceUserNickName;
    }

    public void setServiceUserNickName(String serviceUserNickName) {
        ServiceUserNickName = serviceUserNickName;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }

    @Bindable
    public String getIsCarry() {
        return IsCarry;
    }

    public void setIsCarry(String isCarry) {
        IsCarry = isCarry;
        notifyPropertyChanged(BR.isCarry);
    }

    @Bindable
    public String getIsHavePhone() {
        return IsHavePhone;
    }

    public void setIsHavePhone(String isHavePhone) {
        IsHavePhone = isHavePhone;
        notifyPropertyChanged(BR.isHavePhone);
    }

    public String getCarryInstruction() {
        return CarryInstruction;
    }

    public void setCarryInstruction(String carryInstruction) {
        CarryInstruction = carryInstruction;
    }

    @Bindable
    public String getIsPullCart() {
        return IsPullCart;
    }

    public void setIsPullCart(String isPullCart) {
        IsPullCart = isPullCart;
        notifyPropertyChanged(BR.isPullCart);
    }

    @Bindable
    public String getTruckNeed() {
        return TruckNeed;
    }

    public void setTruckNeed(String truckNeed) {
        TruckNeed = truckNeed;
    }
}
