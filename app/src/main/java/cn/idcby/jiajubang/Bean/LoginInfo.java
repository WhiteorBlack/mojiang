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
 * Created by mrrlb on 2018/2/8.
 */

public class LoginInfo implements Serializable{
    public String token;
    public String HxPwd;
    public String HxName;
    public boolean PersonalInfoPerfect;

    public String getToken() {
        return token;
    }

    public String getHxPwd() {
        return StringUtils.convertNull(HxPwd);
    }

    public String getHxName() {
        return StringUtils.convertNull(HxName);
    }

    public boolean isPersonalInfoPerfect() {
        return PersonalInfoPerfect;
    }

    public void setPersonalInfoPerfect(boolean personalInfoPerfect) {
        PersonalInfoPerfect = personalInfoPerfect;
    }
}
