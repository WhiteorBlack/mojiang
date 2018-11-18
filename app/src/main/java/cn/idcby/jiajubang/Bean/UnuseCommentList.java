package cn.idcby.jiajubang.Bean;

import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/29.
 */

public class UnuseCommentList extends BaseComment {
    public String ProductID ;
    public String ID ;
    public String ParentCommentID ;
    public List<UnuseCommentList> ChildList ;

    public String getProductID() {
        return StringUtils.convertNull(ProductID);
    }

    public String getID() {
        return StringUtils.convertNull(ID);
    }

    public String getParentCommentID() {
        return ParentCommentID;
    }

    public List<UnuseCommentList> getChildList() {
        return ChildList;
    }
}
