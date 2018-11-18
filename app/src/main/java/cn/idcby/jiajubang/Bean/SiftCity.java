package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/9/12.
 * 筛选使用的城市相关
 */

public class SiftCity {
    private boolean isSelected = false ;

    public String ParentId ;
    public String AreaId ;
    public String AreaName ;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public SiftCity(String areaId, String areaName) {
        AreaId = areaId;
        AreaName = areaName;
    }

    public SiftCity() {
    }

    public String getParentId() {
        return StringUtils.convertNull(ParentId);
    }

    public String getAreaId() {
        return StringUtils.convertNull(AreaId);
    }

    public String getAreaName() {
        return StringUtils.convertNull(AreaName);
    }
}
