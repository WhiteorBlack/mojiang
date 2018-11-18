package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/27.
 */

public class JobRecAdv {
    public String ItemName ;
    public String IconUrl ;

    public String getItemName() {
        return StringUtils.convertNull(ItemName);
    }

    public String getIconUrl() {
        return StringUtils.convertNull(IconUrl);
    }
}
