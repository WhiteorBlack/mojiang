package cn.idcby.jiajubang.Bean;

/**
 * Created on 2018/4/12.
 */

public class SortItem {
    public static final int ITEM = 0;//判断是否是普通item
    public static final int SECTION = 1;//判断是否是需要置顶悬停的item

    public final int type;//外部传入的类型，ITEM或者SECTION
    public final SortProvinceBean cityBean;//外部传入的数据

    public int sectionPosition;//头标记
    public int listPosition;//集合标记

    public SortItem(int type, SortProvinceBean cityBean) {
        this.type = type;
        this.cityBean = cityBean;
    }

    //获得其中保存的数据
    public SortProvinceBean getCityBean() {
        return cityBean;
    }
}