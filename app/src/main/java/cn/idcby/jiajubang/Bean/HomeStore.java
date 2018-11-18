package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/5.
 * 首页推荐店铺
 */

public class HomeStore {
    public String ShopID ;
    public String ShopImg ;
    public String SpecialTitle ;
    public String ShopName ;
    public String CreateUserId ;
    public String SortCode ;
    public String ProductCount ;
    public String ShopOrderCount ;
    public String ShopPostion ;
    public String isCompanyAuthentication ;
    public List<ImageThumb> ProductAlbumsList ;

    public int getSaleCount() {
        return StringUtils.convertString2Count(ShopOrderCount);
    }

    public String getShopPostion() {
        return StringUtils.convertNull(ShopPostion);
    }

    public String getType() {
        return "1".equals(isCompanyAuthentication) ? "企业" : "";
    }

    public String getShopID() {
        return ShopID;
    }

    public String getShopImg() {
        return ShopImg;
    }

    public String getSpecialTitle() {
        return SpecialTitle;
    }

    public String getShopName() {
        return ShopName;
    }

    public String getCreateUserId() {
        return CreateUserId;
    }

    public String getSortCode() {
        return SortCode;
    }

    public String getProductCount() {
        return ProductCount;
    }

    public List<ImageThumb> getProductAlbumsList() {
        return null == ProductAlbumsList ? new ArrayList<ImageThumb>() : ProductAlbumsList;
    }
}
