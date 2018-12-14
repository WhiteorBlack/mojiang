package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/23.
 *
 * 2018-09-15 11:48:56
 * 添加转载相关内容
 */

public class UserActive {
    public String CategoryID ;
    public String PostID ;
    public String CreateUserId ;
    public String CreateUserName ;
    public String CreateUserHeadIcon ;
    public String ImgUrl ;
    public String H5Url ;
    public String Title ;
    public String BodyContent ;
    public String ReleaseTime ;
    public String CompanyName ;
    public String PostText ;
    public int ClickNumber = 0 ;
    public int CollectNumber = 0 ;
    public int LikeNumber = 0 ;
    public int CommentNumber = 0 ;


    public int IsLike ;
    public int IsFollow ;
    public String Gender ;
    public String Age ;
    public double Distance;

    public String IndustryVAuthentication ;

    public List<ImageThumb> AlbumsList ;
    public List<CommentCircleList> CommentList ;


    public String IsReprint ; //是否是转载
    public String CategoryTitle ;
    public int ReprintNumber = 0 ;//转载数量
    public CircleTransInfo SourcePostInfo ;//转载圈子信息

    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(CreateUserName);
    }

    public String getCreateUserHeadIcon() {
        return StringUtils.convertNull(CreateUserHeadIcon);
    }

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public String getBodyContent() {
        return StringUtils.convertNull(BodyContent);
    }
    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }
    public String getH5Url() {
        return StringUtils.convertNull(H5Url);
    }
    public String getReleaseTime() {
        return StringUtils.convertNull(ReleaseTime);
    }

    public String getCompanyName() {
        return StringUtils.convertNull(CompanyName);
    }

    public String getPostText() {
        return StringUtils.convertNull(PostText);
    }

    public int getClickNumber() {
        return ClickNumber;
    }

    public int getCollectNumber() {
        return CollectNumber;
    }

    public int getLikeNumber() {
        return LikeNumber;
    }

    public int getCommentNumber() {
        return CommentNumber;
    }
    public int getTransformNumber() {
        return ReprintNumber;
    }

    public List<ImageThumb> getAlbumsList() {
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList ;
    }
    public List<CommentCircleList> getCommentList() {
        return null == CommentList ? new ArrayList<CommentCircleList>() : CommentList;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public String getPostID() {
        return StringUtils.convertNull(PostID);
    }

    public String getGender() {
        return StringUtils.convertNull(Gender);
    }

    public String getAge() {
        return StringUtils.convertNull(Age);
    }

    public int getIsFollow() {
        return IsFollow;
    }

    public void setIsFollow(FocusResult result) {
        if(null == result){
            return ;
        }

        IsFollow = (1 == result.AddOrDelete ? 1 : 0) ;
    }

    public void setLikeNumber(int likeNumber) {
        LikeNumber = likeNumber;
    }
    public boolean isIndustryV() {
        return "1".equals(IndustryVAuthentication) ;
    }
    public boolean isReprint() {
        return "1".equals(IsReprint) ;
    }
    public boolean isSupport() {
        return 1 == IsLike ;
    }
    public void setSupportState(int like ,int count){
        IsLike = like ;
        LikeNumber = count ;
    }
    public void updateTransCount(){
        ReprintNumber += 1 ;
    }

    public String getCategoryTitle() {
        return StringUtils.convertNull(CategoryTitle) ;
    }

    public int getReprintNumber() {
        return ReprintNumber;
    }

    public CircleTransInfo getSourcePostInfo() {
        return SourcePostInfo;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        this.Distance = distance;
    }
}
