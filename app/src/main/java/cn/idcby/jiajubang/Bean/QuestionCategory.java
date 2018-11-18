package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 行业问答分类
 */

public class QuestionCategory implements Serializable{
    private boolean isSelected = false ;

    public String IndustryCategoryID;
    public String ParentID;
    public String CategoryTitle;
    public String CategoryCode;
    public String IconUrl;
    public String ImgUrl;
    public String Layer;
    public String ColorValue;

    public QuestionCategory() {
    }

    public QuestionCategory(boolean isSelected, String categoryTitle) {
        this.isSelected = isSelected;
        this.CategoryTitle = categoryTitle;
    }

    public String getIndustryCategoryID() {
                return StringUtils.convertNull(IndustryCategoryID);
        }

    public String getParentID() {
            return ParentID;
    }

    public String getCategoryTitle() {
            return StringUtils.convertNull(CategoryTitle);
    }

    public String getCategoryCode() {
            return CategoryCode;
    }

    public String getIconUrl() {
            return IconUrl;
    }

    public String getImgUrl() {
            return ImgUrl;
    }

    public String getLayer() {
            return Layer;
    }

    public String getColorValue() {
            return ColorValue;
    }

    public boolean isSelected() {
            return isSelected;
    }

    public void setSelected(boolean selected) {
                isSelected = selected;
        }
}
