package cn.idcby.jiajubang.Bean;

/**
 * Created on 2018/3/29.
 */

public class BaseComment {
    public String CommentContent ;
    public String CreateDate ;
    public String CreateUserId ;
    public String CreateUserName ;
    public String CreateUserHeadIcon ;

    public String getCommentContent() {
        return CommentContent;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public String getCreateUserId() {
        return CreateUserId;
    }

    public String getCreateUserName() {
        return CreateUserName;
    }

    public String getCreateUserHeadIcon() {
        return CreateUserHeadIcon;
    }

    public void setCommentContent(String commentContent) {
        CommentContent = commentContent;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public void setCreateUserId(String createUserId) {
        CreateUserId = createUserId;
    }

    public void setCreateUserName(String createUserName) {
        CreateUserName = createUserName;
    }

    public void setCreateUserHeadIcon(String createUserHeadIcon) {
        CreateUserHeadIcon = createUserHeadIcon;
    }
}
