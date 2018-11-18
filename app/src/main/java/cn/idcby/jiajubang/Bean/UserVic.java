package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/23.
 * 行业大咖
 */

public class UserVic {
    public String UserId ;
    public String RealName ;
    public String NickName ;
    public String HeadIcon ;
    public String Position ;
    public int FollowerNumber ;
    public int IsFollow ;

    public String getUserId() {
        return StringUtils.convertNull(UserId);
    }

    public String getRealName() {
        return null == NickName ? StringUtils.convertNull(RealName) : NickName;
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getPosition() {
        return StringUtils.convertNull(Position);
    }

    public int getFollowerNumber() {
        return FollowerNumber;
    }

    public int getIsFollow() {
        return IsFollow;
    }
    public void setIsFollow(FocusResult result) {
        if(null == result){
            return ;
        }

        IsFollow = (1 == result.AddOrDelete ? 1 : 0) ;
        FollowerNumber = result.Number ;
    }
}
