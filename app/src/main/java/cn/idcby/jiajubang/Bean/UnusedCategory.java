package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-04-19.
 */

public class UnusedCategory extends BaseCategory{
    private boolean isSelected = false ;
    private ArrayList<UnusedCategory> selectedCategory ;

    public String CategoryID ;

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ArrayList<UnusedCategory> getSelectedCategory() {
        return null == selectedCategory ? new ArrayList<UnusedCategory>() : selectedCategory ;
    }

    public void setSelectedCategory(ArrayList<UnusedCategory> selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
}
