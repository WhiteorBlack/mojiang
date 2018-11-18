package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/8/22.
 */

public class GoodParam {
    private boolean isSelected = false ;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public String ParaId ;
    public String Title ;
    public String ParentId ;

    public String getParaId() {
        return StringUtils.convertNull(ParaId);
    }

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public String getParentId() {
        return StringUtils.convertNull(ParentId);
    }
}
