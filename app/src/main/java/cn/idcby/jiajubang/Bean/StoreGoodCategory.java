package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺商品分类--一级以及旗下二级
 * Created on 2018/4/27.
 */

public class StoreGoodCategory {
    public String CategoryID ;
    public String CategoryTitle ;
    public List<StoreGoodCategory> childCategoryList;

    public String getCategoryID() {
        return CategoryID;
    }

    public String getCategoryTitle() {
        return CategoryTitle;
    }

    public List<StoreGoodCategory> getChildCategoryList() {
        return null == childCategoryList ? new ArrayList<StoreGoodCategory>() : childCategoryList;
    }
}
