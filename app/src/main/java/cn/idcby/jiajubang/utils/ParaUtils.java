package cn.idcby.jiajubang.utils;

import android.content.Context;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.idcby.commonlibrary.utils.SPUtils;

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

public class ParaUtils {

    public static final String PARAM_AGREE_TIPS_USER = "yonghuxieyi" ;//用户协议
    public static final String PARAM_AGREE_TIPS_INSTALL = "InstallAgreement" ;//安装服务协议
    public static final String PARAM_AGREE_TIPS_MALL = "NewMallAgreement" ;//厂家直供协议
    public static final String PARAM_AGREE_TIPS_SERVER = "ServerAgreement" ;//行业服务协议
    public static final String PARAM_AGREE_TIPS_UNUSE = "OldMallAgreement" ;//行业闲置协议
    public static final String PARAM_AGREE_TIPS_NEED = "xuqiuxieyi" ;//发布需求
    public static final String PARAM_AGREE_TIPS_BID = "zhaobiaoxieyi" ;//发布招标
    public static final String PARAM_AGREE_TIPS_REGIST = "RegisterAgreement" ;//注册协议
    public static final String PARAM_REVIEW_BOND = "BondDescribe" ;//保证金介绍
    public static final String PARAM_INDUSTRY_V_DESC = "hangyedakaguize" ;//大咖说明


    /**
     * 用户协议
     * @param context con
     * @param tips 对应协议id
     * @return param
     */
    public static Map<String,String> getAgreementTipsParam(Context context, String tips){
        LinkedHashMap<String, String> para = new LinkedHashMap<>();
        para.put("Code", StringUtils.convertNull(tips));
        return para ;
    }

    public static Map<String, String> getParaWithToken(Context context){
        LinkedHashMap<String, String> para = new LinkedHashMap<>();
        para.put("Token", SPUtils.newIntance(context).getToken());
        return para;
    }

    public static Map<String, String> getPara(Context context){
        return new LinkedHashMap<String, String>();
    }

    public static Map<String, String> getParaNece(Context context){
        LinkedHashMap<String, String> para = new LinkedHashMap<>();
        if(!"".equals(SPUtils.newIntance(context).getToken())){
            para.put("Token", SPUtils.newIntance(context).getToken());
        }
        return para;
    }


}
