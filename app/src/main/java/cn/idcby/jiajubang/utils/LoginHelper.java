package cn.idcby.jiajubang.utils;

import android.content.Context;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.jiajubang.Bean.LoginInfo;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.events.BusEvent;
import cn.jpush.android.api.JPushInterface;

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

public class LoginHelper {

    public static void login(Context context, LoginInfo loginInfo){
        if(JPushInterface.isPushStopped(context)){
            JPushInterface.resumePush(context) ;
        }

        SPUtils.newIntance(context).saveToken(loginInfo.token);
        saveUserHxInfo(context , loginInfo.getHxName() ,loginInfo.getHxPwd()) ;
    }
    public static void logout(Context context){
        SPUtils.newIntance(context).clearAll();
    }

    public static void saveUserLoginInfo(Context context,String id,String pass){
        SPUtils.newIntance(context).saveUserLoginInfo(id ,pass) ;
    }

    public static boolean isSelf(Context context ,String userId){
        return SPUtils.newIntance(context).getUserNumber().equals(userId) ;
    }

    public static boolean isNotLogin(Context context){
        return TextUtils.isEmpty(SPUtils.newIntance(context).getToken()) ;
    }

    public static void saveUserId(Context context, String userId){
        SPUtils.newIntance(context).saveUserNumber(userId);
    }

    public static void saveUserHxInfo(Context context, String name,String pass){
        SPUtils.newIntance(context).saveHxInfo(name ,pass);
    }

    public static boolean isPersonApplyAcross(Context context){
        return SkipUtils.APPLY_ACCESS_STATE == SPUtils.newIntance(context).getUserPersonApply();
    }

    public static void resetHxInfo(Context context){
        SPUtils.newIntance(context).saveHxInfo("" ,"") ;
    }

    public static boolean isHxCanLogin(Context context){
        return !isNotLogin(context) && !"".equals(SPUtils.newIntance(context).getHxName()) ;
    }

    public static boolean isHasEnoughIntegral(Context context ,String editIntegral){
        return StringUtils.convertString2Count(editIntegral)
                <= StringUtils.convertString2Count(SPUtils.newIntance(context).getUserIntegral()) ;
    }

    public static void saveUserInfoToLocal(Context context ,UserInfo mUserInfo){
        if(null == mUserInfo){
            return ;
        }

        JPushInterface.setAlias(context , MyApplication.getJpushSequence() ,mUserInfo.getUserId()) ;

        SPUtils utils = SPUtils.newIntance(context) ;
        utils.saveUserNumber(mUserInfo.UserId) ;
        utils.saveHxInfo(mUserInfo.HxName ,mUserInfo.HxPwd) ;
        utils.saveUserNickName(StringUtils.convertNull(mUserInfo.NickName)) ;
        utils.saveUserAvatar(StringUtils.convertNull(mUserInfo.HeadIcon)) ;
        utils.savePayPassInfos(mUserInfo.getIsHavePayPassWord());
        utils.savePhone(StringUtils.convertNull(mUserInfo.getMobile()));
        utils.saveUserAccount(StringUtils.convertNull(mUserInfo.getAccount()));
        utils.saveUserBalance(StringUtils.convertNull(mUserInfo.getBalance()));
        utils.saveUserIntegral(StringUtils.convertNull(mUserInfo.getIntegral()));
        utils.saveUserPersonApply(mUserInfo.PersonalAuthentication);
        utils.saveUserInstallApply(mUserInfo.InstallAuthentication);
        utils.saveUserServerApply(mUserInfo.ServiceAuthentication);
        utils.saveUserStoreApply(mUserInfo.ShopAuthentication);

        EventBus.getDefault().post(new BusEvent.HxLoginStateEvent(true));
    }

}
