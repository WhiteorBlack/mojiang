package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/26.
 */

public class StoreIndexInfo implements Serializable{
    public String ShopInfoId ;
    public String Name ;
    public String ShopImg ;
    public String Count ;
    public String InfoH5Url ;
    public String ShareH5Url ;
    public String HxName ;
    public String isFollowShop ;
    public String isCompanyAuthentication ;
    public String isShopAuthentication ;

    public String getStoreId() {
        return StringUtils.convertNull(ShopInfoId);
    }

    public String getStoreName() {
        return StringUtils.convertNull(Name);
    }

    public String getStoreLogo() {
        return StringUtils.convertNull(ShopImg);
    }

    public String getSupportCount() {
        return StringUtils.convertNull(Count);
    }

    public void setSupportCount(String supportCount) {
        this.Count = supportCount;
    }

    public boolean isCompany() {
        return "1".equals(isCompanyAuthentication);
    }

    public boolean isShop() {
        return "1".equals(isShopAuthentication);
    }

    public boolean isSupport() {
        return "1".equals(isFollowShop);
    }

    public void setSupport(boolean support) {
        isFollowShop = support ? "1" : "0" ;
    }

    public String getStoreH5() {
        return StringUtils.convertHttpUrl(InfoH5Url);
    }
    public String getShopShareUrl() {
        return StringUtils.convertHttpUrl(ShareH5Url);
    }

    public String getStoreHxName() {
        return StringUtils.convertNull(HxName);
    }
}
