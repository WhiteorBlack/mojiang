package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/29.
 */

public class MyNeedsBidSeller {
    public String OfferId ;
    public String Position ;
    public String IsBid ;
    public String CreateDate ;
    public String HeadIcon ;
    public String CreateUserId ;
    public String CreateUserName ;

    public String getOfferId() {
        return StringUtils.convertNull(OfferId);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getPosition() {
        return StringUtils.convertNull(Position);
    }

    public boolean isBid() {
        return "1".equals(IsBid);
    }

    public String getCreateDate() {
        return StringUtils.convertDateToDay(CreateDate);
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(CreateUserName);
    }
}
