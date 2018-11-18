package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/6/7.
 */

public class MyReceiveOrderList {
    public String OrderId ;
    public String OrderNO ;
    public String OrderStatus ;
    public String StatusName ;
    public String CreateDate ;
    public String OrderType ; //1 商品 2闲置 3服务 4 安装服务
    public String OrderTypeName ;
    public String Description ;
    public String ImageUrl ;
    public String Name ;
    public String OrderAmount ;
    public String OrderItemCount ;

    public String PayUserId ;
    public String PayUserName ;

    public List<GoodOrderGood> OrderItem ;

    public String getOrderId() {
        return StringUtils.convertNull(OrderId);
    }

    public String getOrderNO() {
        return StringUtils.convertNull(OrderNO);
    }

    public String getOrderStatus() {
        return StringUtils.convertNull(OrderStatus);
    }

    public String getStatusName() {
        return StringUtils.convertNull(StatusName);
    }

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getOrderType() {
        return StringUtils.convertNull(OrderType);
    }

    public boolean isGood(){
        return SkipUtils.ORDER_TYPE_GOOD.equals(OrderType) ;
    }
    public boolean isUnuse(){
        return SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType) ;
    }

    public boolean isServer(){
        return SkipUtils.ORDER_TYPE_SERVER.equals(OrderType)  ;
    }

    public String getOrderTypeName() {
        return StringUtils.convertNull(OrderTypeName);
    }

    public String getDescription() {
        return StringUtils.convertNull(Description);
    }

    public String getImageUrl() {
        return StringUtils.convertNull(ImageUrl);
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public String getOrderAmount() {
        return StringUtils.convertStringNoPoint(OrderAmount);
    }

    public String getOrderItemCount() {
        return StringUtils.convertNull(OrderItemCount);
    }

    public String getPayUserId() {
        return StringUtils.convertNull(PayUserId);
    }

    public String getPayUserName() {
        return StringUtils.convertNull(PayUserName);
    }

    public boolean canDelete(){
        return "4".equals(OrderStatus) || "5".equals(OrderStatus) || "7".equals(OrderStatus) ;
    }

    public boolean canAfterSale(){
        //2018-06-14 10:53:31 暂定 商品付款之后确认收货之前，服务付款之后完成服务之前才能申请售后（主要是钱的问题）
        return StringUtils.convertString2Count(OrderStatus) > 1 && StringUtils.convertString2Count(OrderStatus) < 4 ;
//        return StringUtils.convertString2Count(OrderStatus) > 1 && StringUtils.convertString2Count(OrderStatus) < 6 ;
    }

    public List<GoodOrderGood> getOrderItem() {
        return null == OrderItem ? new ArrayList<GoodOrderGood>() : OrderItem ;
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
