package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/6/7.
 */

public class MyAfterSaleOrderList {
    public String ImageUrl ;
    public String Name ;
    public String OrderAfterSaleId ;
    public String OrderId ;
    public String OrderNO ;
    public String ServiceType ;
    public String ServiceTypeName ;
    public String Status ;
    public String StatusName ;
    public String Description ;
    public String OrderType ;//2018-09-14 14:02:32 统一为： 1 直供 2 闲置 3 服务 4 安装

    public String getOrderId() {
        return StringUtils.convertNull(OrderId);
    }

    public String getOrderNO() {
        return StringUtils.convertNull(OrderNO);
    }

    public String getImageUrl() {
        return StringUtils.convertNull(ImageUrl);
    }

    public String getName() {
        return StringUtils.convertNull(Name);
    }

    public String getOrderAfterSaleId() {
        return StringUtils.convertNull(OrderAfterSaleId);
    }

    public String getServiceTypeName() {
        return StringUtils.convertNull(ServiceTypeName);
    }

    public String getStatus() {
        return StringUtils.convertNull(Status);
    }

    public String getStatusName() {
        return StringUtils.convertNull(StatusName);
    }

    public String getDescription() {
        return StringUtils.convertNull(Description);
    }

    public boolean isGood(){
        return SkipUtils.ORDER_TYPE_GOOD.equals(OrderType) ;
    }
    public boolean isUnuse(){
        return SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType) ;
    }
    public boolean isServer(){
        return SkipUtils.ORDER_TYPE_SERVER.equals(OrderType) ;
    }

    public boolean isReturenMoneyOnly(){//仅退款
        return SkipUtils.AFTER_SERVER_TYPE_MONEY.equals(ServiceType) ;
    }

    //买家
    public boolean canEdit(){
        return "1".equals(Status) || "3".equals(Status) ;
    }

    public boolean canCancel(){
        if(SkipUtils.ORDER_TYPE_GOOD.equals(OrderType)
                || SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType)){//商品订单
            //当卖家同意时，如果是仅退款，则不必再取消
            return "1".equals(Status) || ("2".equals(Status) && !SkipUtils.AFTER_SERVER_TYPE_MONEY.equals(ServiceType)) || "3".equals(Status) ;
        }

        return "1".equals(Status) || "3".equals(Status) ;
    }

    public boolean canExpress(){//物流只针对商品，而且选的是 退货退款
        return (SkipUtils.ORDER_TYPE_GOOD.equals(OrderType)
                || SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType))
                && "2".equals(Status) && !SkipUtils.AFTER_SERVER_TYPE_MONEY.equals(ServiceType);
    }

    //卖家
    public boolean canAgreeOrDis(){
        return "1".equals(Status) ;
    }

    public boolean canFinish(){
        //只有商品才有确认收货，而且选了 退货退款
        return (SkipUtils.ORDER_TYPE_GOOD.equals(OrderType)
                || SkipUtils.ORDER_TYPE_UNUSE.equals(OrderType))
                && "5".equals(Status) && !SkipUtils.AFTER_SERVER_TYPE_MONEY.equals(ServiceType) ;
    }
}
