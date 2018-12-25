package cn.idcby.jiajubang.Bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.BR;

/**
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2018/2/10.
 */

public class CategoryBean extends BaseObservable implements Serializable{
    public boolean isSelected = false ;
    public String ItemDetailId;
    public String ItemId;
    public String ItemName;
    public String ItemValue;
    public String SortCode;
    public String ColorValue;
    public String ParentId;

    public void setItemDetailId(String itemDetailId) {
        ItemDetailId = itemDetailId;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public void setItemValue(String itemValue) {
        ItemValue = itemValue;
    }

    public String getSortCode() {
        return SortCode;
    }

    public void setSortCode(String sortCode) {
        SortCode = sortCode;
    }

    public void setColorValue(String colorValue) {
        ColorValue = colorValue;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    @Bindable
    public boolean isSelected() {
        return isSelected;
    }


    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.selected);
    }

    public String getItemDetailId() {
        return StringUtils.convertNull(ItemDetailId);
    }

    public String getItemName() {
        return StringUtils.convertNull(ItemName);
    }

    public String getItemValue() {
        return ItemValue;
    }

    public String getColorValue() {
        return "".equals(StringUtils.convertNull(ColorValue)) ? "#B8B8B8" : ColorValue;
    }
}
