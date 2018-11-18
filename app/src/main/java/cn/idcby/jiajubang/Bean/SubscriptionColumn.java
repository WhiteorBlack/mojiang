package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/17.
 */

public class SubscriptionColumn {
    public String Coulmn ;
    public String CoulmnText ;
    public String TakeCoulmnId ;
    public String CreateUserId ;

    public String getCoulmn() {
        return Coulmn;
    }

    public String getCoulmnText() {
        return StringUtils.convertNull(CoulmnText);
    }

    public String getTakeCoulmnId() {
        return TakeCoulmnId;
    }

    public String getCreateUserId() {
        return CreateUserId;
    }
}
