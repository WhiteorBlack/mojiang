package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 报价详细
 * Created on 2018/3/30.
 */

public class NeedsBidDetails {
    public String CategoryID ;
    public String OfferId ;
    public String NeedId ;
    public String WorkDays;
    public String TotalOffer ;
    public String OfferDescription ;
    public String CreateUserId ;
    public String HxName ;
    public List<MyNeedsOrderList> NeedOrderList ;
    public List<ImageThumb> AlbumsList ;

    public String getOfferId() {
        return StringUtils.convertNull(OfferId);
    }

    public String getNeedId() {
        return StringUtils.convertNull(NeedId);
    }

    public String getWorkDays() {
        return StringUtils.convertNull(WorkDays);
    }

    public String getTotalOffer() {
        return StringUtils.convertStringNoPoint(TotalOffer);
    }

    public String getOfferDescription() {
        return StringUtils.convertNull(OfferDescription) ;
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public int getCategoryStyle(){
        return StringUtils.convertString2Count(CategoryID);
    }

    public String getHxName() {
        return StringUtils.convertNull(HxName);
    }

    public List<ImageThumb> getAlbumsList() {
        return AlbumsList;
    }

    public List<MyNeedsOrderList> getNeedOrderList() {
        return NeedOrderList;
    }
}
