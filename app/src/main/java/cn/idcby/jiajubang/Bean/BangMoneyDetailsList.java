package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * <pre>
 *     author : hhh
 *     e-mail : xxx@xx
 *     time   : 2018/05/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BangMoneyDetailsList {

    public String CreateDate ;
    public String OperationAmount ;
    public String OperationIntegral ;
    public String OperationAppDescribe ;

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getOperationAmount() {
        return null == OperationIntegral ? StringUtils.convertNull(OperationAmount) : OperationIntegral;
    }

    public String getOperationAppDescribe() {
        return StringUtils.convertNull(OperationAppDescribe);
    }
}
