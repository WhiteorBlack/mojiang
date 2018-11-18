package cn.idcby.jiajubang.Bean;

/**
 * Created on 2018/3/31.
 */

public class NeedsBondPayResult {
    public String OrderID ;
    public String OrderCode ;
    public String PayableAmount ;
    public String OrderAmount ;

    public String getOrderAmount() {
        return null == OrderAmount ? PayableAmount : OrderAmount ;
    }
}
