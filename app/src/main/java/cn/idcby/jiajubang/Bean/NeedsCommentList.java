package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/29.
 */

public class NeedsCommentList extends BaseComment {
    public String NeedCommentID ;
    public String NeedID ;
    public String ParentCommentID ;
    public List<NeedsCommentList> ChildList ;

    public String getNeedCommentID() {
        return StringUtils.convertNull(NeedCommentID);
    }

    public String getNeedID() {
        return StringUtils.convertNull(NeedID);
    }

    public String getParentCommentID() {
        return ParentCommentID;
    }

    public List<NeedsCommentList> getChildList() {
        return ChildList;
    }
}
