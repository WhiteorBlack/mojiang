package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/29.
 */

public class NeedsDetails {

    public String getH5Url() {
        return StringUtils.convertHttpUrl(H5Url);
    }

    public String H5Url;

    public String CategoryID ;
    public String CategoryName ;
    public String CategoryStyle ;
    public String NeedId ;
    public int TypeId ;
    public String CreateUserId ;
    public String HxName ;
    public String HeadIcon ;
    public String RealName ;
    public String Position ;
    public String ReleaseTime ;
    public String EndTime ;
    public int ClickNumber ;
    public int CommentNumber ;
    public int OfferNumber ;
    public String BudgetPrice ;
    public String NeedTitle ;
    public String NeedExplain ;
    public String BidDescription ;
    public String BidBond ;//保证金
    public int OrderStatus = 0 ; //1未缴纳保证金 2表示可报价 3已选标 4服务中 5已完成
    public int IsCollection ;
    public int IsLike ;

    //存在 招标 报价交了保证金，但是没填写报价
    public int NeedStatus = 1 ;//默认是1 2是已缴纳保证金未报价 3已报价
    public String NeedOfferId ;//当 NeedStatus==2 时，需要该字段

    public String AuthenticationText ;

    public String getApplyText() {
        return StringUtils.convertNull(AuthenticationText);
    }

    public List<ImageThumb> AlbumsList ;

    public String getHxName() {
        return StringUtils.convertNull(HxName);
    }

    public String getCategoryID() {
        return StringUtils.convertNull(CategoryID);
    }

    public String getCategoryName() {
        return StringUtils.convertNull(CategoryName);
    }

    public String getNeedId() {
        return NeedId;
    }

    public int getTypeId() {
        return TypeId;
    }

    public String getEndTime() {
        return StringUtils.convertNull(EndTime);
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getRealName() {
        return RealName;
    }

    public String getPosition() {
        return StringUtils.convertNull(Position);
    }

    public String getReleaseTime() {
        return StringUtils.convertNull(ReleaseTime);
    }

    public int getClickNumber() {
        return ClickNumber;
    }

    public int getCommentNumber() {
        return CommentNumber;
    }

    public int getOfferNumber() {
        return OfferNumber;
    }

    public String getBudgetPrice() {
        return BudgetPrice;
    }

    public String getNeedTitle() {
        return StringUtils.convertNull(NeedTitle);
    }

    public String getNeedExplain() {
        return StringUtils.convertNull(NeedExplain);
    }
    public String getBidDescription() {
        return StringUtils.convertNull(BidDescription);
    }

    public String getBidBond() {
        return StringUtils.convertNull(BidBond);
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public boolean isBid(){
        return OrderStatus >= 3 ;
    }

    //2018-06-30 10:47:39 把CategoryID改成1、2、3、4样式了，分别对应商品、闲置、安装、服务4个
    public int getCategoryStyle(){
        return StringUtils.convertString2Count(CategoryID) ;
    }

    public int getNeedStatus() {
        return NeedStatus;
    }

    public String getNeedOfferId() {
        return StringUtils.convertNull(NeedOfferId);
    }

    public int getIsCollection() {
        return IsCollection;
    }

    public int getIsLike() {
        return IsLike;
    }

    public List<ImageThumb> getAlbumsList() {
        return AlbumsList;
    }
}
