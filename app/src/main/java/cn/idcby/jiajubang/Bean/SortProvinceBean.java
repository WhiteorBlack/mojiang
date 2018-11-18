package cn.idcby.jiajubang.Bean;

import java.util.List;

/**
 * 排序的省份--仅选择定位功能使用
 */
public class SortProvinceBean {
    private int cityId;//城市id
    private String cityName;//城市名
    private int superiorId;//上级城市id
    private List<SortProvinceBean> subordinateList;//下级城市集合

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(int superiorId) {
        this.superiorId = superiorId;
    }

    public List<SortProvinceBean> getSubordinateList() {
        return subordinateList;
    }

    public void setSubordinateList(List<SortProvinceBean> subordinateList) {
        this.subordinateList = subordinateList;
    }
}
