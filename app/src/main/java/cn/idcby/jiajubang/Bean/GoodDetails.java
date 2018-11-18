package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 商品详细
 * Created on 2018/4/25.
 */

public class GoodDetails {
    //原生-----start
    public String Title ;
    public String maxSalePrice ;
    public String minSalePrice ;
    public String minMarketPrice ;
    public String maxMarketPrice ;
    public String ProductSaleNumber ;
    public String PeiSongFangShiName ;//配送方式
    public List<ImageThumb> AlbumsList ;

    public List<GoodService> serviceList ;
    public List<GoodParam> paraList ;

    public String commCount ;
    public List<GoodComment> commList ;

    public String Address ;
    public String Name ;
    public String ShopImg ;
    public String shopOrderCount ;
    public String shopAddress ;

    public List<GoodList> shopProductList ;
    public List<GoodList> CategoryProductList ;

    public String getGoodName() {
        return StringUtils.convertNull(Title);
    }
    public String getPeiSongFangShiName() {
        return StringUtils.convertNull(PeiSongFangShiName);
    }

    public String getGoodPrice() {
        if(StringUtils.convertString2Float(minSalePrice) >= StringUtils.convertString2Float(maxSalePrice)){
            return StringUtils.convertStringNoPoint(minSalePrice) ;
        }

        return StringUtils.convertStringNoPoint(minSalePrice) + "-" + StringUtils.convertStringNoPoint(maxSalePrice);
    }

    public String getGoodPriceOld() {
        if(StringUtils.convertString2Float(minMarketPrice) >= StringUtils.convertString2Float(maxMarketPrice)){
            return StringUtils.convertStringNoPoint(minMarketPrice) ;
        }

        return StringUtils.convertStringNoPoint(minMarketPrice)+ "-" +StringUtils.convertStringNoPoint(maxMarketPrice);
    }

    public String getGoodSaleCount() {
        return StringUtils.convertString2Count(ProductSaleNumber) +"";
    }

    public List<ImageThumb> getGoodImgList() {
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList ;
    }

    public List<GoodService> getGoodServiceList() {
        return null == serviceList ? new ArrayList<GoodService>() : serviceList ;
    }

    public List<GoodParam> getGoodParamList() {
        return null == paraList ? new ArrayList<GoodParam>() : paraList ;
    }

    public String getCommentCount() {
        return StringUtils.convertString2Count(commCount)+"";
    }

    public List<GoodComment> getCommentList() {
        return null == commList ? new ArrayList<GoodComment>() : commList ;
    }

    public String getStoreName() {
        return StringUtils.convertNull(Name);
    }
    public String getShopAddress() {
        return StringUtils.convertNull(shopAddress);
    }

    public String getStoreLogo() {
        return StringUtils.convertNull(ShopImg);
    }

    public String getStoreSaleCount() {
        return StringUtils.convertString2Count(shopOrderCount) +"";
    }

    public List<GoodList> getSampleGoodList() {
        return null == shopProductList ? new ArrayList<GoodList>() : shopProductList;
    }

    public List<GoodList> getSampleHotGoodList() {
        return null == CategoryProductList ? new ArrayList<GoodList>() : CategoryProductList ;
    }

    //原生-----end

    public String HxName ;
    public String H5Url ;
    public String ContentDetailH5 ;
    public String ImgUrl ;

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String MerchantID ;
    public String isFollowShop ;

    public String EnabledMark;//1-上架 0-下架

    public List<GoodSkuList> SkuList ;

    public String CreateUserID ;
    public String getCreateUserID() {
        return StringUtils.convertNull(CreateUserID);
    }

    public boolean isNotEnableMark(){
        return "0".equals(EnabledMark) ;
    }

    public void setIsFollowShop(String isFollowShop) {
        this.isFollowShop = isFollowShop;
    }

    public String getMerchantID() {
        return StringUtils.convertNull(MerchantID);
    }

    public String getHxName() {
        return StringUtils.convertNull(HxName);
    }

    public String getH5Url() {
        return StringUtils.convertNull(H5Url);
    }
    public String getContentDetailH5() {
        return StringUtils.convertNull(ContentDetailH5);
    }

    public List<GoodSkuList> getSkuList() {
        return null == SkuList ? new ArrayList<GoodSkuList>() : SkuList ;
    }

    public static class GoodSkuList{
        public String SkuID ;
        public String SpecText ;
        public String Stock ;
        public String SalePrice ;
        public String MarketPrice ;
        public String isCollected ;
        public String ImgUrl;

        public String getSkuID() {
            return StringUtils.convertNull(SkuID);
        }

        public String getImgUrl() {
            return StringUtils.convertNull(ImgUrl);
        }
        public String getSpecText() {
            return StringUtils.convertNull(SpecText);
        }

        public int getStock() {
            return StringUtils.convertString2Count(Stock);
        }

        public String getSalePrice() {
            return StringUtils.convertStringNoPoint(SalePrice);
        }
        public String getMarketPrice() {
            return StringUtils.convertStringNoPoint(MarketPrice);
        }

        public boolean isCollection(){
            return "1".equals(isCollected) ;
        }

        public void setIsCollected(String isCollected) {
            this.isCollected = isCollected;
        }
    }

    public static class GoodParam{
        public String ParaTitle ;
        public String ParentParaTitle ;

        public String getParaTitle() {
            return StringUtils.convertNull(ParaTitle);
        }

        public String getParentParaTitle() {
            return StringUtils.convertNull(ParentParaTitle);
        }
    }

    public static class GoodService{
        public String ServiceTitle ;
        public String ServiceDescribe ;
        public String ColorValue ;

        public GoodService() {
        }

        public GoodService(String serviceTitle, String serviceDescribe, String colorValue) {
            ServiceTitle = serviceTitle;
            ServiceDescribe = serviceDescribe;
            ColorValue = colorValue;
        }

        public String getServiceTitle() {
            return StringUtils.convertNull(ServiceTitle);
        }
        public String getServiceDescribe() {
            return StringUtils.convertNull(ServiceDescribe);
        }
        public String getColorValue() {
            return "".equals(StringUtils.convertNull(ColorValue)) ? "#B8B8B8" : ColorValue;
        }
    }
}
