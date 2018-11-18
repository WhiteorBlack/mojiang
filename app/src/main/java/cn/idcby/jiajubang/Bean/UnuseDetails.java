package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/29.
 */

public class UnuseDetails {
    public String getCategoryID() {
        return StringUtils.convertNull(CategoryID);
    }

    public String getProductID() {
        return StringUtils.convertNull(ProductID);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getRealName() {
        return StringUtils.convertNull(RealName);
    }

    public String getSalePrice() {
        return StringUtils.convertNull(SalePrice);
    }

    public String getMarketPrice() {
        return StringUtils.convertNull(MarketPrice);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public String getReleaseTime() {
        return StringUtils.convertNull(ReleaseTime);
    }

    public int getLeaveNumber() {
        return LeaveNumber;
    }

    public String getHxName() {
        return StringUtils.convertNull(HxName);
    }

    public boolean getIsCollection() {
        return IsCollection == 1;
    }

    public boolean getIsLike() {
        return IsLike== 1;
    }

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public String getH5Url() {
        return StringUtils.convertNull(H5Url);
    }

    public List<ImageThumb> getAlbumsList() {
        return AlbumsList;
    }

    public String CategoryID;
    public String ProductID;
    public String HeadIcon;
    public String RealName;
    public String SalePrice;
    public String MarketPrice;
    public String ImgUrl;
    public int ClickNumber;
    public String Postion;
    public String ReleaseTime;
    public int LeaveNumber;
    public int LikeNumber;
    public String HxName;
    public int IsCollection;
    public int IsLike;
    public String Title;
    public String H5Url;
    public String CreateUserId;

    public String EnabledMark;//1-上架 0-下架

    public int getLikeNumber() {
        return LikeNumber;
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getExpressFee() {
        return StringUtils.convertNull(ExpressFee);
    }
    public String ExpressFee;

    public String getCategoryTitle() {
        return StringUtils.convertNull(CategoryTitle);
    }
    public String CategoryTitle;
    public String getAbstract() {
        return StringUtils.convertNull(Abstract);
    }
    public String Abstract;

    public List<ImageThumb> AlbumsList;


    public boolean isNotEnableMark(){
        return "0".equals(EnabledMark) ;
    }

}
