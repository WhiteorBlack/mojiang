package cn.idcby.jiajubang.Bean;

/**
 * 评论消息（需求消息、评论消息）
 * Created on 2018/4/21.
 */

public class MessageListComment {
    public String CreateUserHeadIcon ;
    public String CreateUserName ;
    public String CommentContent ;
    public String PostID ;
    public String NeedID ;
    public String CreateDate ;

    public String getCreateUserHeadIcon() {
        return CreateUserHeadIcon;
    }

    public String getCreateUserName() {
        return CreateUserName;
    }

    public String getCommentContent() {
        return CommentContent;
    }

    public String getPostID() {
        return null == PostID ? NeedID : PostID ;
    }

    public String getCreateDate() {
        return CreateDate;
    }
}
