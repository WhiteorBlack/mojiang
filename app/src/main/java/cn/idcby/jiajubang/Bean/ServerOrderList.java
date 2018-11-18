package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/12.
 */

public class ServerOrderList {
    public String ServiceOrderId ;
    public String OrderNO ;
    public String ServiceUserName ;
    public int OrderStatus ;
    public String OrderStatusName ;
    public String Serviceintroduce ;
    public String ServiceAddress ;
    public String CreateDate ;
    public String Contacts ;
    public String ProvinceName ;
    public String CityName ;
    public String ServiceAmount ;
    public String ContactsPhone ;//客户电话
    public String ServiceUserAccount ;//服务电话

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

    public String getServiceintroduce() {
        return StringUtils.convertNull(Serviceintroduce);
    }

    public String getServiceAddress() {
        return StringUtils.convertNull(ServiceAddress);
    }

    public String getServiceDetailAddress(){
        return StringUtils.convertNull(ProvinceName) + StringUtils.convertNull(CityName) + StringUtils.convertNull(ServiceAddress) ;
    }

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getContacts() {
        return Contacts;
    }

    public String getContactsPhone() {
        return StringUtils.convertNull(ContactsPhone);
    }
    public String getServiceUserAccount() {
        return StringUtils.convertNull(ServiceUserAccount);
    }

    public String getServiceAmount() {
        return StringUtils.convertStringNoPoint(ServiceAmount);
    }
}
