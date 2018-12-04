package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/23.
 * 附近的人
 */

public class UserNear {
    public String CreateUserId ;
    public String CreateUserHeadIcon ;
    public String HeadIcon ;
    public String NickName ;
    public String PersonalitySignature ;
    public String Gender ;
    public String Age ;
    public double Distance=0.00;

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getNickName() {
        return StringUtils.convertNull(NickName);
    }

    public String getPersonalitySignature() {
        return StringUtils.convertNull(PersonalitySignature);
    }

    public String getCreateUserHeadIcon() {
        return null == HeadIcon ? StringUtils.convertNull(CreateUserHeadIcon) : HeadIcon;
    }

    public String getGender() {
        return StringUtils.convertNull(Gender);
    }

    public String getAge() {
        return StringUtils.convertNull(Age);
    }
}
