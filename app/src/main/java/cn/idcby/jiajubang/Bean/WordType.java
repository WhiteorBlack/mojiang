package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

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

public class WordType implements Serializable{
    public boolean isSelected = false ;
    public String ItemDetailId;
    public String ItemId;
    public String ItemName;
    public String ItemValue;
    public String SortCode;
    public String ColorValue;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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
