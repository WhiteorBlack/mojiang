package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018/3/31.
 */

public class ResumeContactTa {
    public String phone1 ;
    public String phone2 ;
    public String weChat ;
    public String qq ;
    public String surplusNumber ;
    public String expiryTime ;
    public String isNumber ;

    public String getPhone1() {
        return StringUtils.convertNull(phone1);
    }

    public String getPhone2() {
        return StringUtils.convertNull(phone2);
    }

    public String getWeChat() {
        return StringUtils.convertNull(weChat);
    }

    public String getQq() {
        return StringUtils.convertNull(qq);
    }

    public int getSurplusNumber() {
        return StringUtils.convertString2Count(surplusNumber);
    }

    public String getExpiryTime() {
        return StringUtils.convertNull(expiryTime);
    }

    public boolean isNumber() {
        return "1".equals(isNumber);
    }
}
