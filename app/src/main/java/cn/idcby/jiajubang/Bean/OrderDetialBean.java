package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018-04-26.
 */

public class OrderDetialBean {
    public ReceiveAddress uaeEntity;
    public String deliveryFee;
    public String Quantity;
    public String TotalPrice;
    public String totalPrice;
    public List<CartModelListBean> cartModelList;

    public ReceiveAddress getUaeEntity() {
        return uaeEntity;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public String getQuantity() {
        return Quantity;
    }

    public String getTotalPrice() {
        return null == totalPrice
                ? StringUtils.convertStringNoPoint(TotalPrice)
                : StringUtils.convertStringNoPoint(totalPrice) ;
    }

    public List<CartModelListBean> getCartModelList() {
        return cartModelList;
    }

    public static class CartModelListBean {
        public String CartID;
        public String SkuID;
        public String Quantity;
        public String ProductID;
        public String ProductTitle;
        public String SpecText;
        public String ImgUrl;
        public String ProductNO;
        public String MarketPrice;
        public String SalePrice;
        public String ExpressFee;
        public String MerchantID;
        public String Name;
        public String ShopImg;

        public String getCartID() {
            return CartID;
        }

        public String getSkuID() {
            return SkuID;
        }

        public String getQuantity() {
            return StringUtils.convertNull(Quantity);
        }

        public String getProductID() {
            return StringUtils.convertNull(ProductID);
        }

        public String getProductTitle() {
            return ProductTitle;
        }

        public String getSpecText() {
            return SpecText;
        }

        public String getImgUrl() {
            return StringUtils.convertNull(ImgUrl);
        }

        public String getProductNO() {
            return ProductNO;
        }

        public String getMarketPrice() {
            return MarketPrice;
        }

        public String getSalePrice() {
            return StringUtils.convertStringNoPoint(SalePrice);
        }

        public String getExpressFee() {
            return ExpressFee;
        }

        public String getMerchantID() {
            return StringUtils.convertNull(MerchantID);
        }

        public String getName() {
            return StringUtils.convertNull(Name);
        }

        public String getShopImg() {
            return StringUtils.convertNull(ShopImg);
        }
    }
}
