package cn.idcby.jiajubang.Bean;

import java.io.Serializable;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/26.
 * 福利
 */

public class WelfareList implements Serializable{
    public boolean isSelected = false ;
    public String Welfare ;
    public String WelfareText ;
    public String WelfareColorValue ;

    public WelfareList() {
    }

    public WelfareList(WelfareNomalBean nomalBean) {
        Welfare = nomalBean.ItemDetailId;
        WelfareText = nomalBean.ItemName;
        WelfareColorValue = nomalBean.ColorValue;
    }

    public String getWelfare() {
        return StringUtils.convertNull(Welfare);
    }

    public String getWelfareText() {
        return WelfareText;
    }

    public String getWelfareColorValue() {
        return "".equals(StringUtils.convertNull(WelfareColorValue)) ? "#B8B8B8" : WelfareColorValue;
    }
}
