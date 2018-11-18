package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/12.
 */

public class ServerOrderDetails {
    public String ServiceOrderId ;
    public String OrderNO ;
    public String ServiceUserId ;
    public String ServiceUserName ;
    public int OrderStatus ;
    public String OrderStatusName ;
    public String Serviceintroduce ;
    public String CreateDate ;
    public String ServiceTimes ;
    public String ServiceAmount ;
    public String CreateUserId ;
    public String ServiceUserAccount ;//服务电话
    public String OrderType ;

    public String ProvinceId ;
    public String ProvinceName ;
    public String CityId ;
    public String CityName ;
    public String Contacts ;
    public String ContactsPhone ;
    public String ServiceAddress ;

    public String getServiceUserId() {
        return StringUtils.convertNull(ServiceUserId);
    }
    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }
    public String getServiceOrderId() {
        return StringUtils.convertNull(ServiceOrderId);
    }

    public String getOrderNO() {
        return StringUtils.convertNull(OrderNO);
    }

    public String getServiceUserName() {
        return StringUtils.convertNull(ServiceUserName);
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public String getOrderStatusName() {
        return StringUtils.convertNull(OrderStatusName);
    }

    public String getServiceTime() {
        return StringUtils.convertDateToDay(ServiceTimes);
    }

    public String getServiceintroduce() {
        return StringUtils.convertNull(Serviceintroduce);
    }

    public String getServiceAddress() {
        return StringUtils.convertNull(ServiceAddress);
    }

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getContacts() {
        return StringUtils.convertNull(Contacts);
    }
    public String getServiceUserAccount() {
        return StringUtils.convertNull(ServiceUserAccount);
    }

    public String getContactsPhone() {
        return StringUtils.convertNull(ContactsPhone);
    }

    public String getServiceAmount() {
        return StringUtils.convertStringNoPoint(ServiceAmount);
    }

    public String getProvinceId() {
        return StringUtils.convertNull(ProvinceId);
    }

    public String getProvinceName() {
        return StringUtils.convertNull(ProvinceName);
    }

    public String getCityId() {
        return StringUtils.convertNull(CityId);
    }

    public String getCityName() {
        return StringUtils.convertNull(CityName);
    }

    public int getOrderType(){
        return StringUtils.convertString2Count(OrderType) ;
    }
}
