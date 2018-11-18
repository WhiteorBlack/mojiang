package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/10.
 */

public class ServerCategory extends BaseCategory{
    private boolean isSelected = false ;

    public String ServiceCategoryID ;

    private ArrayList<ServerCategory> selectedCategory ;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ArrayList<ServerCategory> getSelectedCategory() {
        return null == selectedCategory ? new ArrayList<ServerCategory>() : selectedCategory;
    }

    public void setSelectedCategory(ArrayList<ServerCategory> selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getServiceCategoryID() {
        return StringUtils.convertNull(ServiceCategoryID);
    }
}
