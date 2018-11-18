package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/16.
 */

public class CommentResult {
    public String CommentNumber ;
    public String LeaveNumber ;

    public String getCommentNumber() {
        return null == LeaveNumber ? StringUtils.convertNull(CommentNumber) : LeaveNumber ;
    }
}
