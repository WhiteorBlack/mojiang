package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/27.
 */

public class GoodOrderGood implements Serializable{
    public String OrderItemID ;
    public String ImgUrl ;
    public String ProductID ;
    public String ProductTitle ;
    public String SpecText ;
    public String Quantity ;
    public String MarketPrice ;
    public String SalePrice ;
    public String CommentStatus ;
    public String TotalPrice ;

    public String ReturnStatus ;//是否申请售后了
    public String AfterSaleStatus ;//售后状态
    public String AfterSaleStatusText ;//售后状态text，如果没申请，就显示申请按钮，否则就显示这个字段的值

    public boolean isGoodComment() {
        return "1".equals(CommentStatus);
    }

    public void setGoodComment(boolean goodComment) {
        CommentStatus = goodComment ? "1" : "0" ;
    }

    public String getOrderItemID() {
        return StringUtils.convertNull(OrderItemID);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public String getProductID() {
        return StringUtils.convertNull(ProductID);
    }

    public String getProductTitle() {
        return StringUtils.convertNull(ProductTitle);
    }

    public String getSpecText() {
        return StringUtils.convertNull(SpecText);
    }

    public String getQuantity() {
        return StringUtils.convertNull(Quantity);
    }

    public String getMarketPrice() {
        return MarketPrice;
    }
    public String getTotalPrice() {
        return StringUtils.convertStringNoPoint(TotalPrice);
    }

    public String getSalePrice() {
        return StringUtils.convertStringNoPoint(SalePrice);
    }
    public String getAfterSaleStatusText() {
        return StringUtils.convertNull(AfterSaleStatusText);
    }
    public String getAfterSaleStatus() {
        return StringUtils.convertNull(AfterSaleStatus);
    }

    public boolean canAfterSale(){
        return "0".equals(ReturnStatus) ;
    }
}
