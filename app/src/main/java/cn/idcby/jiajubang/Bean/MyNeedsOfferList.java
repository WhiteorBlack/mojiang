package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 我的报价
 * Created on 2018/4/16.
 */

public class MyNeedsOfferList {
    public String NeedId ;
    public String OfferId ;
    public String OrderNO ;
    public String OrderAmount ;
    public String TypeId ;
    public String NeedTitle ;
    public String NeedExplain ;
    public String OrderStatus ;
    public String NeedOrderStatus ;//5的时候，不允许继续发起付款
    public String OrderStatusName ;
    public String CreateDate ;
    public List<ImageThumb> AlbumsList ;

    public String IsBid ;
    public String CreateUserId ;
    public String CreateUserName ;
    public String HeadIcon ;
    public String TotalOffer ;

    public boolean isBid(){
        return "1".equals(IsBid) ;
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }
    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(CreateUserName);
    }

    public String getTotalOffer() {
        return StringUtils.convertNull(TotalOffer);
    }

    public String getNeedId() {
        return StringUtils.convertNull(NeedId);
    }

    public String getOfferId() {
        return StringUtils.convertNull(OfferId);
    }

    public String getOrderNO() {
        return StringUtils.convertNull(OrderNO);
    }

    public String getOrderAmount() {
        return StringUtils.convertNull(OrderAmount);
    }

    public String getTypeId() {
        return StringUtils.convertNull(TypeId);
    }

    public String getNeedTitle() {
        return StringUtils.convertNull(NeedTitle);
    }

    public String getNeedExplain() {
        return StringUtils.convertNull(NeedExplain);
    }

    public String getOrderStatus() {
        return StringUtils.convertNull(OrderStatus);
    }
    public String getNeedOrderStatus() {
        return StringUtils.convertNull(NeedOrderStatus);
    }

    public boolean canOrderStartPay(){
        return "4".equals(OrderStatus) && !"5".equals(NeedOrderStatus) ;
    }

    public boolean canFinishNeed(){
        return false ;
    }

    public String getCreateDate() {
        return StringUtils.convertDateToDay(CreateDate);
    }

    public String getOrderStatusName() {
        return StringUtils.convertNull(OrderStatusName);
    }

    public List<ImageThumb> getAlbumsList() {
        return AlbumsList;
    }
}
