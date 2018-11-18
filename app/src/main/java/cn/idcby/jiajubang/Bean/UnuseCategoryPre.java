package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合闲置分类
 * Created on 2018/3/29.
 */

public class UnuseCategoryPre{
    private List<UnusedCategory> categoryList = new ArrayList<>();

    public List<UnusedCategory> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<UnusedCategory> categoryList) {
        this.categoryList.addAll(categoryList);
    }

    public void addCategoryToList(UnusedCategory category){
        this.categoryList.add(category) ;
    }
}
