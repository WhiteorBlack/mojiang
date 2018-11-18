package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 系统消息
 * Created on 2018/4/21.
 */

public class MessageListSystem {
    public String NewsId ;
    public String H5Url ;
    public String CreateDate ;
    public String FullHead ;

    public String getNewsId() {
        return NewsId;
    }

    public String getH5Url() {
        return StringUtils.convertHttpUrl(H5Url);
    }

    public String getCreateDate() {
        return StringUtils.convertNull(CreateDate);
    }

    public String getFullHead() {
        return StringUtils.convertNull(FullHead);
    }
}
