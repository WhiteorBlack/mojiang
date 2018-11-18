package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 专场商品分类--一级分类
 * Created on 2018/8/28.
 */

public class SpecGoodCategory {
    public String RootCategoryID ;
    public String CategoryTitle ;

    public void setRootCategoryID(String rootCategoryID) {
        RootCategoryID = rootCategoryID;
    }

    public void setCategoryTitle(String categoryTitle) {
        CategoryTitle = categoryTitle;
    }

    public String getRootCategoryID() {
        return StringUtils.convertNull(RootCategoryID);
    }

    public String getCategoryTitle() {
        return StringUtils.convertNull(CategoryTitle);
    }
}
