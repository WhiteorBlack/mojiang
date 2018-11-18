package cn.idcby.jiajubang.Bean;

import java.util.List;

/**
 * Created on 2018/3/22.
 */

public class CommentCircleList extends BaseComment{
    public String ID ;
    public String PostID ;
    public List<CommentCircleList> ChildList ;

    public String getID() {
        return ID;
    }
    public String getPostID() {
        return PostID;
    }
    public List<CommentCircleList> getChildList() {
        return ChildList;
    }
}
