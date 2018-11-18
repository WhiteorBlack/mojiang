package cn.idcby.jiajubang.Bean;

import cn.idcby.jiajubang.utils.StringUtils;

/**
 * Created on 2018-04-26.
 */

public class FoFunsBean {
    private boolean isFocused = true ;

    public String UserId;
    public String HeadIcon;
    public String NickName;
    public String PersonalitySignature;
    public String Postion;

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    public String getUserId() {
        return StringUtils.convertNull(UserId);
    }

    public String getHeadIcon() {
        return StringUtils.convertNull(HeadIcon);
    }

    public String getNickName() {
        return StringUtils.convertNull(NickName);
    }

    public String getPostion() {
        return StringUtils.convertNull(Postion);
    }

    public String getPersonalitySignature() {
        return StringUtils.convertNull(PersonalitySignature);
    }
}
