package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/2.
 */

public class NomalH5Bean {
    public String Title ;
    public String ContentH5Url ;

    public String getTitleText() {
        return StringUtils.convertNull(Title);
    }

    public String getH5Url() {
        return StringUtils.convertHttpUrl(ContentH5Url);
    }
}
