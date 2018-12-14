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
    private String CompanyName;
    private String PostText;
    private String LastVisit;
    private String Description;

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public void setHeadIcon(String headIcon) {
        HeadIcon = headIcon;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public void setFollowerNumber(int followerNumber) {
        FollowerNumber = followerNumber;
    }

    public void setIsFollow(int isFollow) {
        IsFollow = isFollow;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getPostText() {
        return PostText;
    }

    public void setPostText(String postText) {
        PostText = postText;
    }

    public String getLastVisit() {
        return LastVisit;
    }

    public void setLastVisit(String lastVisit) {
        LastVisit = lastVisit;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

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
