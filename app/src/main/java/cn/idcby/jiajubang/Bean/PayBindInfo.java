package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/22.
 */

public class PayBindInfo {
    public String WithdrawInfoId ;
    public String Type ;//账号类型   1支付宝 2微信 3银联
    public String ReceiveUserName ;
    public String ReceiveUserPhone ;
    public String ReceiveNumber ;
    public String ReceiveBankName ;

    public String getWithdrawInfoId() {
        return StringUtils.convertNull(WithdrawInfoId);
    }

    public String getType() {
        return StringUtils.convertNull(Type);
    }

    public String getReceiveUserName() {
        return StringUtils.convertNull(ReceiveUserName);
    }

    public String getReceiveUserPhone() {
        return StringUtils.convertNull(ReceiveUserPhone);
    }

    public String getReceiveNumber() {
        return StringUtils.convertNull(ReceiveNumber);
    }

    public String getReceiveBankName() {
        return StringUtils.convertNull(ReceiveBankName);
    }
}
