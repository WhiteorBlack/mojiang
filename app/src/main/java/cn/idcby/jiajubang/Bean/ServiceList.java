package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/26.
 * 服务（行业服务）
 */

public class ServiceList{
    public String ServiceId ;
    public String CreateUserId ;
    public String UserId ;
    public String CreateUserName ;
    public String NickName ;
    public String HeadIcon ;
    public String SingleAmount ;
    public double Distance;
    public String Position ;
    public String PraiseRate ;
    public String ServiceDescription ;
    public String EnabledMark ;//发布的服务使用该字段，1 上架 0 下架
    public List<WordType> TypeList ;//注意：虽然用的字典项bean，但是服务分类已经不用字典项了，为了偷懒，要求的接口返回字典项同字段的返回
    public List<WordType> PromiseList ;
    public List<ImageThumb> AlbumsList ;
    private String PayMoney;

    public String getPayMoney() {
        return PayMoney;
    }

    public void setPayMoney(String payMoney) {
        PayMoney = payMoney;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public boolean isEnabledMark(){
        return "1".equals(EnabledMark) ;
    }

    public String getServiceId() {
        return StringUtils.convertNull(ServiceId);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getCreateUserId() {
        return null == UserId ? StringUtils.convertNull(CreateUserId) : UserId;
    }
    public String getCreateUserName() {
        return null == NickName ? StringUtils.convertNull(CreateUserName) : NickName ;
    }

    public String getSingleAmount() {
        return StringUtils.convertNull(SingleAmount);
    }

    public String getPosition() {
        return StringUtils.convertNull(Position);
    }

    public String getPraiseRate() {
        return StringUtils.convertNull(PraiseRate);
    }

    public String getServiceDescription() {
        return StringUtils.convertNull(ServiceDescription);
    }

    public List<WordType> getTypeList() {
        return TypeList;
    }

    public List<WordType> getPromiseList() {
        return PromiseList;
    }

    public List<ImageThumb> getAlbumsList() {
        return AlbumsList;
    }
}
