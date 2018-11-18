package cn.idcby.jiajubang.Bean;

import java.util.List;

/**
 * 购物车商品--分组好的
 */

public class ShopCartBean {
    private boolean isSelected = false ;

    private String StoreId ;
    private String StoreName ;
    private List<CartList> cartGoodList ;

    public ShopCartBean() {
    }

    public ShopCartBean(String storeId) {
        StoreId = storeId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getStoreId() {
        return StoreId;
    }

    public String getStoreName() {
        return StoreName;
    }

    public List<CartList> getCartGoodList() {
        return cartGoodList;
    }

    public void setStoreId(String storeId) {
        StoreId = storeId;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public void setCartGoodList(List<CartList> cartGoodList) {
        this.cartGoodList = cartGoodList;
    }
}
