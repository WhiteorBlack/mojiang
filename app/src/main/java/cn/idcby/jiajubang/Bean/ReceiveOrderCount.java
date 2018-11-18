package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 订单数量
 * Created on 2018/6/11.
 */

public class ReceiveOrderCount {
    public String WaitPay ;//待付款
    public String WaitSend ;//待发货
    public String WaitReceive ;//待收货
    public String WaitEvaluate ;//待评价
    public String AfterSale ;//售后

    public int getWaitPay(){
        int count = StringUtils.convertString2Count(WaitPay) ;
        if (count > 99){
            count = 99 ;
        }
        return count ;
    }

    public int getWaitSend(){
        int count = StringUtils.convertString2Count(WaitSend) ;
        if (count > 99){
            count = 99 ;
        }
        return count ;
    }
    public int getWaitReceive(){
        int count = StringUtils.convertString2Count(WaitReceive) ;
        if (count > 99){
            count = 99 ;
        }
        return count ;
    }

    public int getWaitEvaluate(){
        int count = StringUtils.convertString2Count(WaitEvaluate) ;
        if (count > 99){
            count = 99 ;
        }
        return count ;
    }
    public int getAfterSale(){
        int count = StringUtils.convertString2Count(AfterSale) ;
        if (count > 99){
            count = 99 ;
        }
        return count ;
    }
}
