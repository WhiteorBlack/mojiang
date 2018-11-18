package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 闲置商品列表
 * Created on 2018/4/23.
 */

public class UnuseGoodList {
    public String ProductID ;
    public String Title ;
    public String BodyContent ;
    public String Abstract ;
    public String ShowPrice ;
    public String SalePrice ;
    public String HeadIcon ;
    public String CreateUserId ;
    public String CreateDate ;
    public String PCreateDate ;
    public String Postion ;
    public String LikeNumber ;
    public String CommentNumber ;
    public String LeaveNumber ;

    public String NickName ;
    public String UserOnLine ;

    public String EnabledMark ; //我发布的使用该字段，1 上架 0 下架

    public List<ImageThumb> AlbumsList ;
    public List<UnuseCommentList> CommentList ;

    public String ImgUrl ;//相似闲置里面用到了

    public boolean isEnabledMark(){
        return "1".equals(EnabledMark) ;
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getProductID() {
        return StringUtils.convertNull(ProductID);
    }

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public String getBodyContent() {
        return null == BodyContent ? StringUtils.convertNull(Abstract) : BodyContent ;
    }

    public String getShowPrice() {
        return null == ShowPrice ? StringUtils.convertStringNoPoint(SalePrice) : StringUtils.convertStringNoPoint(ShowPrice);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getCreateDate() {
        return null == CreateDate ? StringUtils.convertNull(PCreateDate) : StringUtils.convertNull(CreateDate);
    }

    public String getUserOnLine(){
        return StringUtils.convertNull(UserOnLine) ;
    }
    public String getImgUrl(){
        return StringUtils.convertNull(ImgUrl) ;
    }

    public String getNickName() {
        return StringUtils.convertNull(NickName) ;
    }

    public String getPostion() {
        return StringUtils.convertNull(Postion);
    }

    public String getLikeNumber() {
        return StringUtils.convertNull(LikeNumber);
    }

    public String getCommentNumber() {
        return null == CommentNumber ? LeaveNumber : CommentNumber;
    }

    public List<ImageThumb> getAlbumsList() {
        return AlbumsList;
    }

    public List<UnuseCommentList> getCommentList() {
        return CommentList;
    }
}
