package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/2/23.
 */

public class CircleDetail {
    //基本信息
    public String PostID;
    public String ImgUrl;
    public int LikeNumber;
    public int ClickNumber;
    public int CommentNumber;
    public int IsCollection;
    public int IsLike;
    public int IsFollow;
    public String H5Url;
    public String ApplyText;
    public List<ImageThumb> AlbumsList;

    public String CreateUserId ;
    public String CreateUserName ;
    public String CreateUserHeadIcon ;
    public String BodyContent ;
    public String ReleaseTime ;
    public String CompanyName ;
    public String PostText ;
    public String IndustryVAuthentication ;

    //转载需要
    public String CategoryTitle ;
    public int ReprintNumber = 0;
    public String IsReprint ;
    public CircleTransInfo SourcePostInfo ;


    public String getCreateUserId() {
        return StringUtils.convertNull(CreateUserId);
    }

    public String getCreateUserName() {
        return StringUtils.convertNull(CreateUserName);
    }

    public String getCreateUserHeadIcon() {
        return StringUtils.convertNull(CreateUserHeadIcon);
    }

    public String getBodyContent() {
        return StringUtils.convertNull(BodyContent);
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

    public boolean isIndustryV() {
        return "1".equals(IndustryVAuthentication) ;
    }
    public boolean isTransport() {
        return "1".equals(IsReprint) ;
    }

    public CircleTransInfo getSourcePostInfo() {
        return SourcePostInfo;
    }

    public String getCategoryTitle() {
        return StringUtils.convertNull(CategoryTitle);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public String getPostID() {
        return StringUtils.convertNull(PostID);
    }

    public int getLikeNumber() {
        return LikeNumber;
    }

    public int getCommentNumber() {
        return CommentNumber;
    }
    public int getClickNumber() {
        return ClickNumber;
    }

    public int getTransNumber() {
        return ReprintNumber;
    }

    public String getApplyText() {
        return StringUtils.convertNull(ApplyText);
    }

    public String getH5Url() {
        return StringUtils.convertNull(H5Url);
    }

    public List<ImageThumb> getAlbumsList() {
        return null == AlbumsList ? new ArrayList<ImageThumb>() : AlbumsList;
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
}
