package cn.idcby.jiajubang.Bean;

import com.google.gson.annotations.SerializedName;

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
 * Created by mrrlb on 2018/2/9.
 */

public class PersonApplyInfo {

    @SerializedName("PersonalAuthenticationId")
    public String PersonalAuthenticationId;
    @SerializedName("Name")
    public String name;
    @SerializedName("AliNumber")
    public String aliNumber;
    @SerializedName("HoldIDImg")
    public String holdIDImg;
    @SerializedName("IDNumber")
    public String iDNumber;
    @SerializedName("IDOppositeImg")
    public String idOppositeImg;
    @SerializedName("IDPositiveImg")
    public String idPositiveImg;

    @SerializedName("IndustryIds")
    public String IndustryIds;
    @SerializedName("PostText")
    public String PostText;
    @SerializedName("IndustryNames")
    public String IndustryNames;

    public String getPersonalAuthenticationId() {
        return StringUtils.convertNull(PersonalAuthenticationId);
    }

    public String getName() {
        return StringUtils.convertNull(name);
    }

    public String getAliNumber() {
        return StringUtils.convertNull(aliNumber);
    }

    public String getHoldIDImg() {
        return StringUtils.convertNull(holdIDImg);
    }

    public String getiDNumber() {
        return StringUtils.convertNull(iDNumber);
    }

    public String getIdOppositeImg() {
        return StringUtils.convertNull(idOppositeImg);
    }

    public String getIdPositiveImg() {
        return StringUtils.convertNull(idPositiveImg);
    }

    public String getIndustryIds() {
        return StringUtils.convertNull(IndustryIds);
    }

    public String getPostText() {
        return StringUtils.convertNull(PostText);
    }

    public String getIndustryNames() {
        return StringUtils.convertNull(IndustryNames);
    }
}
