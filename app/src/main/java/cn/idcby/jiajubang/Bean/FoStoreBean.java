package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018-05-7.
 * ShopName ShopImg Fans

 */

public class FoStoreBean {
    public String ShopId ;
    public String ShopName ;
    public String ShopImg ;
    public String Fans ;

    public String getShopInfoId() {
        return StringUtils.convertNull(ShopId);
    }

    public String getName() {
        return StringUtils.convertNull(ShopName);
    }

    public String getShopImg() {
        return StringUtils.convertNull(ShopImg);
    }

    public String getCount() {
        return StringUtils.convertString2Count(Fans) + "";
    }
}
