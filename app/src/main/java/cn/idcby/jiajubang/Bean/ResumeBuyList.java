package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/4/12.
 */

public class ResumeBuyList {
    public String Amount ;
    public String Number ;
    public String SetMealId ;
    public String SetMealTitle ;
    public String WhenLong ;

    public String getAmount() {
        return StringUtils.convertStringNoPoint(Amount);
    }

    public String getNumber() {
        return StringUtils.convertNull(Number);
    }

    public String getSetMealId() {
        return SetMealId;
    }
    public String getWhenLong() {
        return StringUtils.convertNull(WhenLong);
    }

    public String getSetMealTitle() {
        return StringUtils.convertNull(SetMealTitle);
    }
}
