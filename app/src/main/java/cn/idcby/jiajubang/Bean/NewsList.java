package cn.idcby.jiajubang.Bean;

import java.util.List;

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

public class NewsList {
    public String ArticleType;
    public String CategoryID;
    public String Description;
    public String ImgUrl;
    public String ArticleImgUrl;
    public int LikeNumber;

    public String LinkUrl;
    public String ReleaseTime;
    public String Title;
    public String ArticleSource;
    public String ArticleID;
    public int CommentNumber;
    public int AlbumsType ;//1一张大图 2一张小图 3多张图片
    public List<NewsThumb> AlbumsList ;

    //视频相关
    private int videoPosition = 0 ;//秒
    private int videoAllTime = 0 ;//秒
    private boolean isVideoPlay = false ;
    private boolean isVideoFinish = false ;

    public int getVideoAllTime() {
        return videoAllTime;
    }

    public void setVideoAllTime(int videoAllTime) {
        this.videoAllTime = videoAllTime;
    }

    public int getVideoPosition() {
        return videoPosition;
    }

    public void setVideoPosition(int videoPosition) {
        this.videoPosition = videoPosition;
    }

    public boolean isVideoFinish() {
        return isVideoFinish;
    }

    public void setVideoFinish(boolean videoFinish) {
        isVideoFinish = videoFinish;
    }

    public boolean isVideoPlay() {
        return isVideoPlay;
    }

    public void setVideoPlay(boolean videoPlay) {
        isVideoPlay = videoPlay;
    }

    public String getLinkUrl() {
        return StringUtils.convertNull(LinkUrl);
    }

    public String getReleaseTime() {
        return ReleaseTime;
    }

    public String getTitle() {
        return Title;
    }

    public String getArticleSource() {
        return ArticleSource;
    }

    public String getArticleID() {
        return ArticleID;
    }

    public int getCommentNumber() {
        return CommentNumber;
    }

    public int getAlbumsType() {
        return AlbumsType;
    }

    public String getImgUrl() {
        return null == ImgUrl ? StringUtils.convertNull(ArticleImgUrl) : StringUtils.convertNull(ImgUrl);
    }

    public boolean isVideo(){
        return "2".equals(ArticleType) ;
    }

    public List<NewsThumb> getAlbumsList() {
        return AlbumsList;
    }
}
