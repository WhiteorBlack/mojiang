package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/26.
 * 服务（行业服务）
 */

public class HomeServiceList {
    public String ServiceId ;
    public String CreateUserId ;
    public String NickName ;
    public String HeadIcon ;
    public String SingleAmount ;
    public String UserId ;

    public String getServiceId() {
        return StringUtils.convertNull(ServiceId);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getSingleAmount() {
        return StringUtils.convertNull(SingleAmount);
    }

    public String getCreateUserId() {
        return null == UserId ? StringUtils.convertNull(CreateUserId) : UserId;
    }

    public String getNickName() {
        return StringUtils.convertNull(NickName);
    }
}
