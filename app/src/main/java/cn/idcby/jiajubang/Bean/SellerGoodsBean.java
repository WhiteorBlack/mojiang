package cn.idcby.jiajubang.Bean;

/**
 * Created by Administrator on 2018-04-25.
 */

public class SellerGoodsBean {

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getPImgUrl() {
        return PImgUrl;
    }

    public void setPImgUrl(String PImgUrl) {
        this.PImgUrl = PImgUrl;
    }

    public String getProductTitle() {
        return ProductTitle;
    }

    public void setProductTitle(String productTitle) {
        ProductTitle = productTitle;
    }

    public String getShowPrice() {
        return ShowPrice;
    }

    public void setShowPrice(String showPrice) {
        ShowPrice = showPrice;
    }

    public String getPSaleNumber() {
        return PSaleNumber;
    }

    public void setPSaleNumber(String PSaleNumber) {
        this.PSaleNumber = PSaleNumber;
    }

    public String ProductID;//	商品Id
    public String PImgUrl;//	商品主图
    public String ProductTitle;//	商品标题
    public String ShowPrice;//	售价
    public String PSaleNumber;//	销售量

}
