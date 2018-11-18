package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 商品评论
 * Created on 2018/5/24.
 */

public class GoodComment {
    public String OrderItemCommentID ;

    public String OrderEvaluateId ;
    public String BodyContent ;
    public String EvaluateLevel ;
    public String CreateDate ;
    public String CreateTimeText ;
    public String UserID ;
    public String RealName ;
    public String HeadIcon ;
    public String SpecText ;
    public String Star ;
    public String WuLiuFuWuStar ;
    public String FuWuTaiDuStar ;
    public List<CommentImage> AlbumsList ;

    //我的评价
    public String ProductID ;
    public String ProductTitle ;
    public String ImgUrl ;
    public String SalePrice ;

    //追评
    public String IsReview ;
    public String ChildCommentTime ;
    public GoodComment ChildInfo ;
    public List<CommentImage> ChildAlbumsList ;

    public String getOrderItemCommentID() {
        return StringUtils.convertNull(OrderItemCommentID);
    }
    public String getProductID() {
        return StringUtils.convertNull(ProductID);
    }

    public String getProductTitle() {
        return StringUtils.convertNull(ProductTitle);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }
    public String getSalePrice() {
        return StringUtils.convertStringNoPoint(SalePrice);
    }

    public String getEvaluateLevel() {
        return StringUtils.convertNull(EvaluateLevel);
    }

    public String getOrderEvaluateId() {
        return StringUtils.convertNull(OrderEvaluateId);
    }

    public String getBodyContent() {
        return StringUtils.convertNull(BodyContent);
    }

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }
    public String getCreateTimeText() {
        return StringUtils.convertNull(CreateTimeText);
    }

    public String getCreateUserId() {
        return StringUtils.convertNull(UserID);
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(RealName);
    }

    public String getCreateUserHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }
    public String getSpecText() {
        return StringUtils.convertNull(SpecText);
    }

    public int getStar() {
        return StringUtils.convertString2Count(Star);
    }

    public int getWuLiuFuWuStar() {
        return StringUtils.convertString2Count(WuLiuFuWuStar);
    }

    public int getFuWuTaiDuStar() {
        return StringUtils.convertString2Count(FuWuTaiDuStar);
    }

    public boolean isCanAddComment(){
        return "0".equals(IsReview) ;
    }

    public String getChildCommentTime() {
        return StringUtils.convertNull(ChildCommentTime);
    }

    public GoodComment getChildInfo() {
        return ChildInfo;
    }

    //手动删除追评
    public void removeChildInfo() {
        IsReview = "0" ;
        ChildInfo = null;
    }

    public List<ImageThumb> getImgList() {
        if(null == AlbumsList || AlbumsList.size() == 0){
            return new ArrayList<>() ;
        }else{
            List<ImageThumb> imageThumbs = new ArrayList<>(AlbumsList.size()) ;
            for(CommentImage image : AlbumsList){
                imageThumbs.add(new ImageThumb(image.getImgUrl())) ;
            }

            return imageThumbs ;
        }
    }

    public List<ImageThumb> getChildAlbumsList() {
        if(null == ChildAlbumsList || ChildAlbumsList.size() == 0){
            return new ArrayList<>() ;
        }else{
            List<ImageThumb> imageThumbs = new ArrayList<>(ChildAlbumsList.size()) ;
            for(CommentImage image : ChildAlbumsList){
                imageThumbs.add(new ImageThumb(image.getImgUrl())) ;
            }

            return imageThumbs ;
        }
    }

    public static class CommentImage{
        public String ImgUrl ;

        public String getImgUrl() {
            return StringUtils.convertNull(ImgUrl);
        }
    }
}
