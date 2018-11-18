package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 直供商品列表
 * Created on 2018/4/26.
 */

public class GoodList {
    public String ProductID ;
    public String ImgUrl ;
    public String Title ;
    public String ProductTitle ;
    public String SalePrice ;
    public String SaleNumber ;
    public String MerchantName ;
    public String MerchantID ;
    public String ShopPostion ;

    public List<GoodDetails.GoodService> serviceList ;

    public GoodList() {
    }

    public GoodList(String productID, String imgUrl, String title, String salePrice, String saleNumber) {
        ProductID = productID;
        ImgUrl = imgUrl;
        Title = title;
        SalePrice = salePrice;
        SaleNumber = saleNumber;
    }

    public String getProductID() {
        return StringUtils.convertNull(ProductID);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public String getTitle() {
        return null == Title ? StringUtils.convertNull(ProductTitle) : Title;
    }

    public String getSalePrice() {
        return StringUtils.convertStringNoPoint(SalePrice);
    }

    public String getSaleNumber() {
        return StringUtils.convertNull(SaleNumber);
    }
    public String getStorePositon() {
        return StringUtils.convertNull(ShopPostion);
    }
    public String getStoreName() {
        return StringUtils.convertNull(MerchantName);
    }
    public String getStoreId() {
        return StringUtils.convertNull(MerchantID);
    }

    public List<GoodDetails.GoodService> getServerList() {
        return null == serviceList ? new ArrayList<GoodDetails.GoodService>() : serviceList;
    }
}
