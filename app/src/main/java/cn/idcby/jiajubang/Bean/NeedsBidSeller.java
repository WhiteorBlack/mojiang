package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/29.
 */

public class NeedsBidSeller {
    public String OfferId ;
    public String UserId ;
    public String HeadIcon ;
    public String RealName ;
    public String Position ;
    public String ReleaseTime ;
    public String IsBid ;

    public String getOfferId() {
        return StringUtils.convertNull(OfferId);
    }

    public String getUserId() {
        return StringUtils.convertNull(UserId);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getRealName() {
        return StringUtils.convertNull(RealName);
    }

    public String getPosition() {
        return StringUtils.convertNull(Position);
    }

    public String getReleaseTime() {
        return StringUtils.convertDateToDay(ReleaseTime);
    }

    public boolean isBid() {
        return "1".equals(IsBid);
    }
}
