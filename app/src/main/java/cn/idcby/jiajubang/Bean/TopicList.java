package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 话题
 * Created on 2018/3/22.
 */
public class TopicList {
    public String Title ;
    public String ImgUrl ;
    public String BodyContent ;
    public String CommentNumber ;
    public String PostID ;
    public String CategoryTitle ;
    public String CategoryImgUrl ;
    public String CategoryID ;

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public String getBodyContent() {
        return StringUtils.convertNull(BodyContent);
    }

    public String getPostID() {
        return StringUtils.convertNull(PostID);
    }

    public String getCommentNumber() {
        return CommentNumber;
    }

    public String getCategoryTitle() {
        return StringUtils.convertNull(CategoryTitle);
    }

    public String getCategoryImgUrl() {
        return StringUtils.convertNull(CategoryImgUrl);
    }

    public String getCategoryID() {
        return StringUtils.convertNull(CategoryID);
    }
}
