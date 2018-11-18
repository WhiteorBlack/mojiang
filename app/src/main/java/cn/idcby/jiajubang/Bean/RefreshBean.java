package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 直供--专场商品
 */

public class RefreshBean {

    public String getProductSpecialID() {
        return ProductSpecialID;
    }

    public void setProductSpecialID(String productSpecialID) {
        ProductSpecialID = productSpecialID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getColorValue() {
        return ColorValue;
    }

    public void setColorValue(String colorValue) {
        ColorValue = colorValue;
    }

    public List<GoodsListBean> getSpecialGoodList() {
        return specialGoodList;
    }

    public void setSpecialGoodList(List<GoodsListBean> specialGoodList) {
        this.specialGoodList = specialGoodList;
    }

    public String getRemark() {
        return StringUtils.convertNull(Remark);
    }

    public String ProductSpecialID;//	专场id;
    public String Title;//	专场标题
    public String Remark;//	描述
    public String Code;//	专场编码
    public String ImgUrl;//	图片路径
    public String ColorValue;//	色值
    public List<GoodsListBean> specialGoodList;//	专场商品

    public static class GoodsListBean {
        public GoodsListBean() {
        }

        public GoodsListBean(String imgUrl, String productTitle, String salePrice, String saleNumber) {
            ImgUrl = imgUrl;
            ProductTitle = productTitle;
            SalePrice = salePrice;
            SaleNumber = saleNumber;
        }

        public String getProductID() {
            return ProductID;
        }

        public void setProductID(String productID) {
            ProductID = productID;
        }

        public String getPImgUrl() {
            return ImgUrl;
        }

        public void setPImgUrl(String PImgUrl) {
            this.ImgUrl = PImgUrl;
        }

        public String getProductTitle() {
            return ProductTitle;
        }

        public void setProductTitle(String productTitle) {
            ProductTitle = productTitle;
        }

        public String getShowPrice() {
            return SalePrice;
        }

        public void setShowPrice(String showPrice) {
            SalePrice = showPrice;
        }

        public String getPSaleNumber() {
            return SaleNumber;
        }

        public void setPSaleNumber(String PSaleNumber) {
            this.SaleNumber = PSaleNumber;
        }

        public String ProductID;//	商品Id
        public String ImgUrl;//	商品主图
        public String ProductTitle;//	商品标题
        public String SalePrice;//	售价
        public String SaleNumber;//	销售量
    }

}
