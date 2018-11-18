package cn.idcby.jiajubang.alpay;

/**
 * Created by hhh on 2017/3/15.
 */

public class ZFBPay{
    public String PrivateKey;//私钥
    public String partner;//商户号
    public String notify_url;//回调
    public String out_trade_no;//交易号
    public String subject;//
    public String seller_id;//
    public String total_fee;//总金额
    public String body;//描述

    public String getPrivateKey() {
        return PrivateKey;
    }

    public String getPartner() {
        return partner;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public String getSubject() {
        return subject;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public String getBody() {
        return body;
    }
}
