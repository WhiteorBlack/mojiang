package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/3.
 */

public class UserBond {
    public int InstallIsPay ;
    public String InstallBond ;
    public int ServiceIsPay ;
    public String ServiceBond ;
    public int ShopInfoisPay ;
    public String ShopInfoBond ;
    public String InstallOrderID ;
    public String InstallOrderCode ;
    public String InstallMoney ;
    public String ServiceOrderID ;
    public String ServiceOrderCode ;
    public String ServiceMoney ;
    public String ShopOrderID ;
    public String ShopOrderCode ;
    public String ShopMoney ;

    public String getInstallBond() {
        return StringUtils.convertStringNoPoint(InstallBond);
    }

    public String getServiceBond() {
        return StringUtils.convertStringNoPoint(ServiceBond);
    }

    public String getShopInfoBond() {
        return StringUtils.convertStringNoPoint(ShopInfoBond);
    }

    public String getInstallOrderID() {
        return InstallOrderID;
    }

    public String getInstallOrderCode() {
        return InstallOrderCode;
    }

    public String getInstallMoney() {
        return InstallMoney;
    }

    public String getServiceOrderID() {
        return ServiceOrderID;
    }

    public String getServiceOrderCode() {
        return ServiceOrderCode;
    }

    public String getServiceMoney() {
        return ServiceMoney;
    }

    public String getShopOrderID() {
        return ShopOrderID;
    }

    public String getShopOrderCode() {
        return ShopOrderCode;
    }

    public String getShopMoney() {
        return ShopMoney;
    }

    public boolean isInstallIsPay(){
        return 1 == InstallIsPay ;
    }
    public boolean isServiceIsPay(){
        return 1 == ServiceIsPay ;
    }
    public boolean isShopInfoisPay(){
        return 1 == ShopInfoisPay ;
    }
}
