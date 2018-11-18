package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 商品订单
 * Created on 2018/4/27.
 */

public class GoodOrderList {
    public String OrderId ;
    public String OrderCode ;
    public String OrderType ;//1直供商品，2闲置商品 ，3 服务
    public String CreateDate ;
    public String PayableAmount ;
    public String FinalProductAmount;//商品总额
    public String FinalExpressFee ;//运费
    public String Status ;
    public String StatusText ;
    public List<GoodOrderGood> OrderItem ;


    public String Reciever ;
    public String RecieverPhone ;
    public String ProvinceName ;
    public String CityName ;
    public String Address ;

    public String deliveryName ;
    public String deliveryMobile ;

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


    public String getOrderId() {
        return StringUtils.convertNull(OrderId);
    }

    public String getOrderType() {
        return StringUtils.convertNull(OrderType);
    }

    public String getOrderCode() {
        return StringUtils.convertNull(OrderCode);
    }

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getPayableAmount() {
        return StringUtils.convertStringNoPoint(PayableAmount);
    }
    public String getFinalProductAmount() {
        return StringUtils.convertStringNoPoint(FinalProductAmount);
    }

    public String getFinalExpressFee() {
        return StringUtils.convertStringNoPoint(FinalExpressFee);
    }

    public String getStatus() {
        return StringUtils.convertNull(Status);
    }

    public String getStatusText() {
        return StringUtils.convertNull(StatusText);
    }

    public List<GoodOrderGood> getOrderItem() {
        return OrderItem;
    }

    public boolean canDelete(){
        return "4".equals(Status) || "5".equals(Status) || "7".equals(Status) ;
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

}
