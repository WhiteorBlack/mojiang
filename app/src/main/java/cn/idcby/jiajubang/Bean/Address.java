package cn.idcby.jiajubang.Bean;

import android.text.TextUtils;

import cn.idcby.jiajubang.utils.PingYinUtil;
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
 * Created by mrrlb on 2018/2/10.
 */

public class Address {
    public String AreaId;
    public String AreaName;
    public String AreaCode;
    public String ParentId;

    public String pinyi;

    public Address() {
    }

    public Address(String areaId, String areaName) {
        AreaId = areaId;
        AreaName = areaName;
    }

    public String getPinyi() {
        if(TextUtils.isEmpty(pinyi)){
            if("重庆".equals(AreaName)){
                return "chongqing" ;
            }
            return PingYinUtil.getPingYin(AreaName);
        }else {
            return pinyi;
        }
    }

    public String getAreaId() {
        return StringUtils.convertNull(AreaId);
    }

    public String getAreaName() {
        return StringUtils.convertNull(AreaName);
    }

    public String getAreaCode() {
        return StringUtils.convertNull(AreaCode);
    }

    public String getParentId() {
        return StringUtils.convertNull(ParentId);
    }
}
