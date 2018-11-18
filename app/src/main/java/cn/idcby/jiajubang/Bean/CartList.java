package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 购物车数据
 * Created on 2018/4/25.
 */

public class CartList {
    private boolean isSelected = false ;

    public String MerchantID ;//分组依据
    public String CartID ;
    public String ProductID ;
    public String SkuID ;
    public String Quantity ;
    public String Name ;
    public String SpecText ;
    public String Title ;
    public String ImgUrl ;
    public String SalePrice ;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getMerchantID() {
        return StringUtils.convertNull(MerchantID);
    }

    public String getCartID() {
        return StringUtils.convertNull(CartID);
    }

    public String getProductID() {
        return StringUtils.convertNull(ProductID);
    }

    public String getSkuID() {
        return SkuID;
    }

    public String getQuantity() {
        return StringUtils.convertNull(Quantity);
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public String getSpecText() {
        return StringUtils.convertNull(SpecText);
    }

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public String getSalePrice() {
        return StringUtils.convertStringNoPoint(SalePrice);
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}
