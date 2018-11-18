package cn.idcby.jiajubang.Bean;

import java.io.Serializable;
import java.util.List;

/**
 *訂閲列表
 */
public class SubListBeans implements Serializable{
    /**
     * NeedId : 6ddfb128-1a16-4e6f-a05d-550d6d986aa8
     * TypeId : 1
     * NeedTitle : 测试订阅
     * EndTime : 2018年4月23日
     * NeedExplain : 这是需求的说明
     * AlbumsList : []
     * Position : 河南省郑州市
     * LikeNumber : 0
     * CommentNumber : 0
     */

    private String NeedId;
    private int TypeId;
    private String NeedTitle;
    private String EndTime;
    private String NeedExplain;
    private String Position;
    private int LikeNumber;
    private int CommentNumber;
    private List<?> AlbumsList;

    public String getNeedId() {
        return NeedId;
    }

    public void setNeedId(String NeedId) {
        this.NeedId = NeedId;
    }

    public int getTypeId() {
        return TypeId;
    }

    public void setTypeId(int TypeId) {
        this.TypeId = TypeId;
    }

    public String getNeedTitle() {
        return NeedTitle;
    }

    public void setNeedTitle(String NeedTitle) {
        this.NeedTitle = NeedTitle;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public String getNeedExplain() {
        return NeedExplain;
    }

    public void setNeedExplain(String NeedExplain) {
        this.NeedExplain = NeedExplain;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String Position) {
        this.Position = Position;
    }

    public int getLikeNumber() {
        return LikeNumber;
    }

    public void setLikeNumber(int LikeNumber) {
        this.LikeNumber = LikeNumber;
    }

    public int getCommentNumber() {
        return CommentNumber;
    }

    public void setCommentNumber(int CommentNumber) {
        this.CommentNumber = CommentNumber;
    }

    public List<?> getAlbumsList() {
        return AlbumsList;
    }

    public void setAlbumsList(List<?> AlbumsList) {
        this.AlbumsList = AlbumsList;
    }


//    public String getNeedId() {
//        return NeedId;
//    }
//
//    public void setNeedId(String needId) {
//        NeedId = needId;
//    }
//
//    public String getTypeId() {
//        return TypeId;
//    }
//
//    public void setTypeId(String typeId) {
//        TypeId = typeId;
//    }
//
//    public String getNeedTitle() {
//        return NeedTitle;
//    }
//
//    public void setNeedTitle(String needTitle) {
//        NeedTitle = needTitle;
//    }
//
//    public String getEndTime() {
//        return EndTime;
//    }
//
//    public void setEndTime(String endTime) {
//        EndTime = endTime;
//    }
//
//    public String getNeedExplain() {
//        return NeedExplain;
//    }
//
//    public void setNeedExplain(String needExplain) {
//        NeedExplain = needExplain;
//    }
//
//    public String getOriginalImgUrl() {
//        return OriginalImgUrl;
//    }
//
//    public void setOriginalImgUrl(String originalImgUrl) {
//        OriginalImgUrl = originalImgUrl;
//    }
//
//    public String getThumbImgUrl() {
//        return ThumbImgUrl;
//    }
//
//    public void setThumbImgUrl(String thumbImgUrl) {
//        ThumbImgUrl = thumbImgUrl;
//    }
//
//    public String getPosition() {
//        return Position;
//    }
//
//    public void setPosition(String position) {
//        Position = position;
//    }
//
//    public int getLikeNumber() {
//        return LikeNumber;
//    }
//
//    public void setLikeNumber(int likeNumber) {
//        LikeNumber = likeNumber;
//    }
//
//    public int getAnswerNumber() {
//        return CommentNumber;
//    }
//
//    public void setCommentNumber(int commentNumber) {
//        CommentNumber = commentNumber;
//    }
//
//    public List<SubThumb> getAlbumsList() {
//        return AlbumsList;
//    }
//
//    public void setAlbumsList(List<SubThumb> albumsList) {
//        AlbumsList = albumsList;
//    }
//
//    public String NeedId;
//    public String TypeId;
//    public String NeedTitle;
//    public String EndTime;
//    public String NeedExplain;
//
//    public String OriginalImgUrl;
//    public String ThumbImgUrl;
//    public String Position;
//    public int LikeNumber;
//    public int CommentNumber;
//
//    public List<SubThumb> AlbumsList;
//
//    private class SubThumb {
//
//        public String getArticleID() {
//            return ArticleID;
//        }
//
//        public void setArticleID(String articleID) {
//            ArticleID = articleID;
//        }
//
//        public String ArticleID;
//    }


}
