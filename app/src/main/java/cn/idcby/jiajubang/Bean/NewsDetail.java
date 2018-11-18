package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2018/2/23.
 */

public class NewsDetail {
    public String Abstract;
    public String ArticleCode;
    public String ArticleSource;
    public String ArticleID;
    public String CategoryID;
    public String ChannelID;
    public int ClickNumber;
    public int CollectNumber;
    public int CommentNumber;
    public String Description;
    public String ImgUrl;
    public int IsRecommend;

    public String getAbstract() {
        return StringUtils.convertNull(Abstract);
    }

    public void setAbstract(String anAbstract) {
        Abstract = anAbstract;
    }

    public String getArticleCode() {
        return ArticleCode;
    }

    public void setArticleCode(String articleCode) {
        ArticleCode = articleCode;
    }

    public String getArticleSource() {
        return ArticleSource;
    }

    public void setArticleSource(String articleSource) {
        ArticleSource = articleSource;
    }

    public String getArticleID() {
        return ArticleID;
    }

    public void setArticleID(String articleID) {
        ArticleID = articleID;
    }

    public String getCategoryID() {
        return StringUtils.convertNull(CategoryID);
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getChannelID() {
        return ChannelID;
    }

    public void setChannelID(String channelID) {
        ChannelID = channelID;
    }

    public int getClickNumber() {
        return ClickNumber;
    }

    public void setClickNumber(int clickNumber) {
        ClickNumber = clickNumber;
    }

    public int getCollectNumber() {
        return CollectNumber;
    }

    public void setCollectNumber(int collectNumber) {
        CollectNumber = collectNumber;
    }

    public int getCommentNumber() {
        return CommentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        CommentNumber = commentNumber;
    }

    public String getDescription() {
        return StringUtils.convertNull(Description);
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public int getIsRecommend() {
        return IsRecommend;
    }

    public void setIsRecommend(int isRecommend) {
        IsRecommend = isRecommend;
    }

    public int getLikeNumber() {
        return LikeNumber;
    }

    public void setLikeNumber(int likeNumber) {
        LikeNumber = likeNumber;
    }

    public String getLinkUrl() {
        return LinkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        LinkUrl = linkUrl;
    }

    public int getIsCollection() {
        return IsCollection;
    }

    public void setIsCollection(int isCollection) {
        IsCollection = isCollection;
    }

    public int getIsLike() {
        return IsLike;
    }

    public void setIsLike(int isLike) {
        IsLike = isLike;
    }

    public String getReleaseTime() {
        return StringUtils.convertNull(ReleaseTime);
    }

    public void setReleaseTime(String releaseTime) {
        ReleaseTime = releaseTime;
    }

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setContentH5Url(String contentH5Url) {
        ContentH5Url = contentH5Url;
    }

    public String getH5Url() {
        return StringUtils.convertHttpUrl(H5Url);
    }

    public void setH5Url(String h5Url) {
        H5Url = h5Url;
    }

    public int LikeNumber;
    public String LinkUrl;
    public int IsCollection;
    public int IsLike;
    public String ReleaseTime;
    public String Title;
    public String ContentH5Url;
    public String H5Url;
    public String ApplyText ;

    public String getApplyText() {
        return StringUtils.convertNull(ApplyText);
    }

    public String getContentH5Url() {
        return StringUtils.convertHttpUrl(ContentH5Url);
    }
}
