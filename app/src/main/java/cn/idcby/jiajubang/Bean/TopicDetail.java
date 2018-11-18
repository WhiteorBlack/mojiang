package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/2/23.
 */

public class TopicDetail {
    public String PostID;
    public String Title;
    public String ImgUrl;
    public int ClickNumber;
    public int CollectNumber;
    public int LikeNumber;
    public int CommentNumber;
    public int IsCollection;
    public int IsLike;
    public String H5Url;
    public String ApplyText;
    public List<NewsThumb> AlbumsList;

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public String getPostID() {
        return StringUtils.convertNull(PostID);
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

    public int getIsLike() {
        return IsLike;
    }

    public String getApplyText() {
        return StringUtils.convertNull(ApplyText);
    }

    public String getH5Url() {
        return StringUtils.convertNull(H5Url);
    }

    public List<NewsThumb> getAlbumsList() {
        return AlbumsList;
    }
}
