package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/10.
 */

public class CircleCategory extends BaseCategory {
    public String PostCategoryID ;

    public String getCategoryId() {
        return StringUtils.convertNull(PostCategoryID);
    }
}
