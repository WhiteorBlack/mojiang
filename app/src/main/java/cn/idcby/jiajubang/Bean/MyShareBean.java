package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/5/2.
 */

public class MyShareBean {
    public String Title ;
    public String ImgUrl ;
    public String H5Url ;

    public String getTitle() {
        return StringUtils.convertNull(Title);
    }

    public String getImgUrl() {
        return StringUtils.convertNull(ImgUrl);
    }

    public String getH5Url() {
        return StringUtils.convertNull(H5Url);
    }
}
