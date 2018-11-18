package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/29.
 */

public class NeedsList {
    public String NeedId ;
    public int TypeId ;
    public String NeedTitle ;
    public String EndTime ;
    public String ReleaseTime ;
    public String NeedExplain ;
    public String Position ;

    //需求订单中使用
    public String IsHaveOffer ;//1允许修改和删除
    public String OrderStatus ;
    public String OrderStatusText ;

    //我发布的需求中只用
    public String EnabledMark ; // 1上架 0下架
    public List<MyNeedsBidSeller> NeedOfferList; // 参与商家列表

    public String LikeNumber ;
    public String CommentNumber ;
    public List<ImageThumb> AlbumsList ;


    public List<MyNeedsBidSeller> getNeedOfferList() {
        return NeedOfferList;
    }

    public boolean isEnabledMark(){
        return "1".equals(EnabledMark) ;
    }

    public String getNeedId() {
        return NeedId;
    }

    public int getTypeId() {
        return TypeId;
    }

    public String getNeedTitle() {
        return NeedTitle;
    }

    public String getEndTime() {
        return StringUtils.convertNull(EndTime);
    }

    public String getOrderStatusText() {
        return StringUtils.convertNull(OrderStatusText);
    }

    public String getReleaseTime() {
        return ReleaseTime;
    }

    public String getNeedExplain() {
        return NeedExplain;
    }

    public String getPosition() {
        return Position;
    }

    public String getLikeNumber() {
        return LikeNumber;
    }

    public String getCommentNumber() {
        return CommentNumber;
    }

    public boolean isHaveOffer() {
        return "1".equals(IsHaveOffer);
    }

    public boolean isPayBond(){
        return "1".equals(OrderStatus) ;
    }
    public boolean isFinishNeed(){
        return "3".equals(OrderStatus) || "4".equals(OrderStatus) ;//已选标和服务中可以完成需求
    }

    public boolean isUndownNeed(){
        return StringUtils.convertString2Count(OrderStatus)< 3 ;//已选标以后都不能上下架
    }

    public boolean isBonded(){
        return StringUtils.convertString2Count(OrderStatus) >= 3 ;
    }

    public List<ImageThumb> getAlbumsList() {
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList;
    }
}
