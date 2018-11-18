package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/9/15.
 *
 * 转载圈子上级帖子信息
 */

public class CircleTransInfo {
    public String PostID ;
    public String ImgUrl ;
    public String BodyContent ;

    public String getPostID() {
        return StringUtils.convertNull(PostID);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public String getBodyContent() {
        return StringUtils.convertNull(BodyContent);
    }
}
