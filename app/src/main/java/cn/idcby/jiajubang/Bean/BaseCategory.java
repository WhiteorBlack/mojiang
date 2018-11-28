package cn.idcby.jiajubang.Bean;

import android.databinding.BaseObservable;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/10.
 */

public class BaseCategory extends BaseObservable implements Serializable {
    public String ParentID ;
    public String CategoryTitle ;
    public String CategoryCode ;
    public String Layer ;
    public String ImgUrl ;
    public String IconUrl ;
    public String ColorValue ;

    public void setCategoryTitle(String categoryTitle) {
        CategoryTitle = categoryTitle;
    }

    public void setCategoryCode(String categoryCode) {
        CategoryCode = categoryCode;
    }

    public void setLayer(String layer) {
        Layer = layer;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public void setIconUrl(String iconUrl) {
        IconUrl = iconUrl;
    }

    public void setColorValue(String colorValue) {
        ColorValue = colorValue;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getParentID() {
        return StringUtils.convertNull(ParentID);
    }

    public String getCategoryTitle() {
        return StringUtils.convertNull(CategoryTitle);
    }

    public String getCategoryCode() {
        return CategoryCode;
    }

    public String getLayer() {
        return Layer;
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl) ;
    }
    public String getIconUrl() {
        return StringUtils.convertNull(IconUrl) ;
    }

    public String getColorValue() {
        return ColorValue;
    }
}
