package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 商品订单
 * Created on 2018/4/27.
 */

public class GoodOrderDetails {
    public String OrderId ;
    public String OrderCode ;
    public String OrderType ;//1直供商品 2 闲置商品
    public String CreateDate ;
    public String PayableAmount ;
    public String Status ;
    public String StatusText ;
    public String Message ;
    public List<GoodOrderGood> OrderItem ;

    public String MerchantID ;
    public String MerchantName ;

    public String UserID ;

    public String Reciever ;
    public String RecieverPhone ;
    public String ProvinceName ;
    public String CityName ;
    public String Address ;

    public String deliveryName ;
    public String deliveryMobile ;

    public String ExpressName ;
    public String ExpressNO ;

    public String DeliveryType ;
    public String DeliveryTypeName ;

    public String FinalExpressFee ;
    public String FinalProductAmount;//商品总额

    public String getDeliveryType() {
        return StringUtils.convertNull(DeliveryType);
    }

    public String getDeliveryTypeName() {
        return StringUtils.convertNull(DeliveryTypeName);
    }

    public String getFinalExpressFee() {
        return StringUtils.convertStringNoPoint(FinalExpressFee);
    }
    public String getFinalProductAmount() {
        return StringUtils.convertStringNoPoint(FinalProductAmount);
    }

    public String getMessage() {
        return StringUtils.convertNull(Message);
    }

    public String getReciever() {
        return StringUtils.convertNull(Reciever);
    }

    public String getRecieverPhone() {
        return StringUtils.convertNull(RecieverPhone);
    }

    public String getReceiverAddress(){
        return StringUtils.convertNull(ProvinceName)
                + StringUtils.convertNull(CityName)
                + StringUtils.convertNull(Address) ;
    }

    public String getDeliveryName() {
        return StringUtils.convertNull(deliveryName);
    }

    public String getDeliveryMobile() {
        return StringUtils.convertNull(deliveryMobile);
    }

    public String getMerchantID() {
        return StringUtils.convertNull(MerchantID);
    }

    public String getMerchantName() {
        return StringUtils.convertNull(MerchantName);
    }

    public String getOrderId() {
        return StringUtils.convertNull(OrderId);
    }

    public String getOrderCode() {
        return StringUtils.convertNull(OrderCode);
    }

    public String getOrderType() {
        return StringUtils.convertNull(OrderType);
    }

    public boolean isUnuse(){
        return SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType) ;
    }

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getPayableAmount() {
        return StringUtils.convertStringNoPoint(PayableAmount);
    }

    public String getStatus() {
        return StringUtils.convertNull(Status);
    }

    public String getStatusText() {
        return StringUtils.convertNull(StatusText);
    }

    public List<GoodOrderGood> getOrderItem() {
        return null == OrderItem ? new ArrayList<GoodOrderGood>() : OrderItem;
    }

    public String getExpressName() {
        return StringUtils.convertNull(ExpressName);
    }

    public String getExpressNum() {
        return StringUtils.convertNull(ExpressNO);
    }

    public String getUserId(){
        return StringUtils.convertNull(UserID) ;
    }

    public boolean isNotAllGoodAfterSale(){
        boolean isOk = false ;

        if(OrderItem != null && OrderItem.size() > 0){
            for(GoodOrderGood good : OrderItem){
                if(good.canAfterSale()){
                    isOk = true ;
                    break;
                }
            }
        }

        return isOk ;
    }

    public boolean isGoodNoAfterSale(){//没有未完成的售后商品
        boolean allOk = true ;

        if(OrderItem != null && OrderItem.size() > 0){
            for(GoodOrderGood good : OrderItem){
                if(!good.canAfterSale()
                        && !"6".equals(good.getAfterSaleStatus())){//售后了，而且没有处理完成
                    allOk = false ;
                    break;
                }
            }
        }

        return allOk ;
    }
}
