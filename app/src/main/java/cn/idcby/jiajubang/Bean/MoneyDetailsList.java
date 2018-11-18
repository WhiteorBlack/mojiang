package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 账单
 * Created on 2018/4/14.
 */

public class MoneyDetailsList {
    public String CreateDate ;
    public String OperationAmount ;
    public String OperationAppDescribe ;
    public String IsPlus ;  //0-负数 1-正数

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getOperationAmount() {
        return StringUtils.convertNull(OperationAmount);
    }

    public String getOperationAppDescribe() {
        return StringUtils.convertNull(OperationAppDescribe);
    }

    public boolean isAdd(){
        return "1".equals(IsPlus) ;
    }
}
