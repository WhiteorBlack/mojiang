package cn.idcby.jiajubang.Bean;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/8/22.
 */

public class GoodParamParent {
    private boolean isNoExpand = false ;

    public boolean isNoExpand() {
        return isNoExpand;
    }

    public void setNoExpand(boolean noExpand) {
        isNoExpand = noExpand;
    }

    public String ParaId ;
    public String Title ;
    public List<GoodParam> childList ;

    public String getParaId() {
        return StringUtils.convertNull(ParaId);
    }

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public List<GoodParam> getChildList() {
        return null == childList ? new ArrayList<GoodParam>() : childList ;
    }
}
