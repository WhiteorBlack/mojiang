package cn.idcby.commonlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created on 2016/8/10.
 */
public class SPUtils {

    private static final String SP_FILE_NAME = "config";//注意：会在退出登录时清空
    private static final String SP_FILE_NORMAL = "normal";//通用的设置，退出登录时不会清空
    private static final String TOKEN = "token";
    private static final String ACCOUNT = "Account";
    private static final String BLANCE = "blance";
    private static final String INTEGRAL = "integral";//积分
    private static String DEV_IDENTITY = "dev_identity";
    private static String IS_FIRST = "is_first";
    private static String IS_VIP = "is_vip";
    private static String USER_NAME = "user_name";
    private static String USER_NICK_NAME = "user_nick_name";
    private static String USER_NUMBER = "user_number";
    private static String USER_AVATAR = "user_avatar";
    private static String PHONE = "phone";
    private static String USER_HX_NAME = "hx_one";
    private static String USER_HX_PASS = "hx_two";
    private static final String ISHAVEPAYPASS = "ishavepaypass";//是否有支付密码
    private static final String APPLY_PERSON = "person_apply";//实名状态
    private static final String APPLY_INSTALL = "install_apply";//安装认证状态
    private static final String APPLY_SERVER = "server_apply";//服务认证状态
    private static final String APPLY_STORE = "store_apply";//店铺认证状态

    private static final String USER_LOGIN_ID = "l_id";
    private static final String USER_LOGIN_PASS = "l_ps";

    private static final String USER_ALIAS = "user_alias";//极光推送别名设置状态

    //未读消息相关
    private static final String USER_UNREAD_MESSAGE_COUNT = "user_unread_count";//未读消息数量
    private static final String UNREAD_MSG_COUNT_NEED_ORDER = "unread_count_need_order" ;
    private static final String UNREAD_MSG_COUNT_GOOD_ORDER = "unread_count_good_order" ;
    private static final String UNREAD_MSG_COUNT_SERVER_ORDER = "unread_count_server_order" ;
    private static final String UNREAD_MSG_COUNT_CIRCLE = "unread_count_circle" ;
    private static final String UNREAD_MSG_COUNT_COMMENT = "unread_count_comment" ;
    public static final int MSG_TYPE_NEED = 0 ;
    public static final int MSG_TYPE_SERVER = 1 ;
    public static final int MSG_TYPE_GOOD = 2 ;
    public static final int MSG_TYPE_CIRCLE = 3 ;
    public static final int MSG_TYPE_COMMENT = 4 ;


    private Context mContext;
    private static SPUtils instance = null;


    private SPUtils(Context context) {
        mContext = context;
    }

    public synchronized static SPUtils newIntance(Context context) {

        if (instance == null) {
            instance = new SPUtils(context);
        }

        return instance;
    }

    public void saveUserLoginInfo(String id ,String ps){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(USER_LOGIN_ID ,id).putString(USER_LOGIN_PASS ,ps).apply();
    }

    public String getUserLoginId(){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(USER_LOGIN_ID ,"") ;
    }
    public String getUserLoginPass(){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(USER_LOGIN_PASS ,"") ;
    }

    public void setUserAliasState(boolean aliasState){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(USER_ALIAS ,aliasState).apply();
    }

    public boolean getUserAliasState(){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(USER_ALIAS ,false) ;
    }

    /**
     * 获取记录用户的Token
     *
     * @return
     */
    public String getToken() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        String userName = sp.getString(TOKEN, "");
        return userName;
    }


    /***
     * 保存用户Token
     */
    public void saveToken(String token) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(TOKEN, token).commit();
    }

    /**
     * 清除Token
     */
    public void clearToken() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(TOKEN).commit();
    }


    public void saveDevIdentity(String devIdentity) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NORMAL, Context.MODE_PRIVATE);
        sp.edit().putString(DEV_IDENTITY, devIdentity).commit();
    }


    public String getDevIdentity() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NORMAL, Context.MODE_PRIVATE);
        String devIdentity = sp.getString(DEV_IDENTITY, "");
        return devIdentity;
    }

    public void clearDevIdentiyt() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NORMAL, Context.MODE_PRIVATE);
        sp.edit().remove(DEV_IDENTITY).commit();
    }

    /**
     * 是否第一次打开app
     * @param isFrist isFirst
     */
    public void saveIsFirst(boolean isFrist) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NORMAL, Context.MODE_PRIVATE);
        sp.edit().putBoolean(IS_FIRST, isFrist).apply();
    }

    /**
     * 是否第一次打开app
     */
    public boolean getIsFirst() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NORMAL, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_FIRST, true);

    }


    public void saveIsVip(boolean isVip) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(IS_VIP, isVip).commit();
    }

    public boolean getIsVip() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_VIP, false);

    }

    public void clearIsVip() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(IS_VIP).commit();
    }


    public void saveUserName(String userName) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(USER_NAME, userName).commit();
    }


    public String getUserName() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        String userName = sp.getString(USER_NAME, "");
        return userName;
    }


    public void saveUserNickName(String userName) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(USER_NICK_NAME, userName).apply();
    }


    public String getUserNickName() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        String userName = sp.getString(USER_NICK_NAME, "");
        return userName;
    }

    public void saveUserAvatar(String avatar) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(USER_AVATAR, avatar).apply();
    }


    public String getUserAvatar() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        String avatar = sp.getString(USER_AVATAR, "");
        return avatar;
    }

    /**
     * userId
     * @param userNumber userId
     */
    public void saveUserNumber(String userNumber) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(USER_NUMBER, userNumber).apply();
    }


    public String getUserNumber() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(USER_NUMBER, "");
    }

    public void clearUserName() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(USER_NAME).commit();
    }


    public String getPhone() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        String userName = sp.getString(PHONE, "");
        return userName;
    }

    /***
     * 保存用户Token
     */
    public void savePhone(String phone) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(PHONE, phone).commit();
    }

    /**
     * 清除Token
     */
    public void clearPhone() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(PHONE).commit();
    }

    public void saveHxInfo(String hxName, String hxPass) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(USER_HX_NAME, hxName).putString(USER_HX_PASS, hxPass).apply();
    }

    public String getHxName() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(USER_HX_NAME, "");
    }

    public String getHxPass() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(USER_HX_PASS, "");
    }

    public void clearAll() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    public void savePayPassInfos(int isHavePayPassWord) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(ISHAVEPAYPASS, isHavePayPassWord);
        editor.commit();
    }

    public int getPayPassInfo() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(ISHAVEPAYPASS, 0);
    }

    public void saveUserAccount(String sAccount) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(ACCOUNT, sAccount).commit();
    }

    public String getUserAccount() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(ACCOUNT, "");
    }

    public void saveUserPersonApply(int applyState) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(APPLY_PERSON, applyState).apply();
    }

    public int getUserPersonApply() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(APPLY_PERSON, -1);
    }

    public void saveUserInstallApply(int applyState) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(APPLY_INSTALL, applyState).apply();
    }

    public int getUserInstallApply() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(APPLY_INSTALL, -1);
    }

    public void saveUserServerApply(int applyState) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(APPLY_SERVER, applyState).apply();
    }

    public int getUserServerApply() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(APPLY_SERVER, -1);
    }

    public void saveUserStoreApply(int applyState) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(APPLY_STORE, applyState).apply();
    }

    public int getUserStoreApply() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(APPLY_STORE, -1);
    }

    public void saveUserBalance(String blance) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(BLANCE, blance).commit();
    }

    public float getUserBalance() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);

        String blancestr=sp.getString(BLANCE,"");
        if (TextUtils.isEmpty(blancestr)){
            blancestr="0";
        }

        float fBlance = Float.parseFloat(blancestr);
//        float fBlance = Float.parseFloat(TextUtils.isEmpty(sp.getString(BLANCE, ""))?"0.00":sp.getString(BLANCE, ""));
        return fBlance;
    }

    public void saveUserIntegral(String blance) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(INTEGRAL, blance).apply();
    }

    public String getUserIntegral() {
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(INTEGRAL,"0");
    }

    /*******************未读消息相关 start ******************/

    /**
     * 重置未读消息计数
     */
    public void resetUnreadMessage(){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(USER_UNREAD_MESSAGE_COUNT, 0).apply();
    }

    /**
     * 修改数量
     * @param isAdd true add
     * @return all count
     */
    public int changeUnreadMessage(boolean isAdd){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        int count = sp.getInt(USER_UNREAD_MESSAGE_COUNT ,0) ;
        if(isAdd){
            count = count + 1 ;
        }else{
            if(count > 0){
                count = count - 1 ;
            }
        }

        if(count > 99){
            count = 99 ;
        }

        sp.edit().putInt(USER_UNREAD_MESSAGE_COUNT, count).apply();

        return count ;
    }


    public int modifyUnreadMessageCount(int type ,boolean isAdd,int cnt){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);

        String spKey ;
        if(MSG_TYPE_NEED == type){
            spKey = UNREAD_MSG_COUNT_NEED_ORDER ;
        }else if(MSG_TYPE_SERVER == type){
            spKey = UNREAD_MSG_COUNT_SERVER_ORDER ;
        }else if(MSG_TYPE_GOOD == type){
            spKey = UNREAD_MSG_COUNT_GOOD_ORDER ;
        }else if(MSG_TYPE_CIRCLE == type){
            spKey = UNREAD_MSG_COUNT_CIRCLE ;
        }else{
            spKey = UNREAD_MSG_COUNT_COMMENT ;
        }

        int count = sp.getInt(spKey ,0) ;
        if(cnt > 0){
            if(isAdd){
                count = count + cnt ;
            }else{
                if(count > 0){
                    count = count - cnt ;
                }
            }
        }

        if(count > 99){
            count = 99 ;
        }

        sp.edit().putInt(spKey, count).apply();

        return count ;
    }

    public int getUnreadMessageCount(int type){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);

        String spKey ;
        if(MSG_TYPE_NEED == type){
            spKey = UNREAD_MSG_COUNT_NEED_ORDER ;
        }else if(MSG_TYPE_SERVER == type){
            spKey = UNREAD_MSG_COUNT_SERVER_ORDER ;
        }else if(MSG_TYPE_GOOD == type){
            spKey = UNREAD_MSG_COUNT_GOOD_ORDER ;
        }else if(MSG_TYPE_CIRCLE == type){
            spKey = UNREAD_MSG_COUNT_CIRCLE ;
        }else{
            spKey = UNREAD_MSG_COUNT_COMMENT ;
        }
        return sp.getInt(spKey ,0) ;
    }

    public int getUnreadMessageCountOrder(){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        int needCount = sp.getInt(UNREAD_MSG_COUNT_NEED_ORDER ,0) ;
        int serverCount = sp.getInt(UNREAD_MSG_COUNT_SERVER_ORDER ,0) ;
        int goodCount = sp.getInt(UNREAD_MSG_COUNT_GOOD_ORDER ,0) ;
        return needCount + serverCount + goodCount ;
    }

    public int getUnreadMessageCountAll(){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);

        int allCount = sp.getInt(USER_UNREAD_MESSAGE_COUNT ,0) ;
//        int needCount = sp.getInt(UNREAD_MSG_COUNT_NEED_ORDER ,0) ;
//        int serverCount = sp.getInt(UNREAD_MSG_COUNT_SERVER_ORDER ,0) ;
//        int goodCount = sp.getInt(UNREAD_MSG_COUNT_GOOD_ORDER ,0) ;
//        int circleCount = sp.getInt(UNREAD_MSG_COUNT_CIRCLE ,0) ;
//        int commentCount = sp.getInt(UNREAD_MSG_COUNT_COMMENT ,0) ;
//
//        return needCount + serverCount + goodCount + circleCount + commentCount ;
        return allCount ;
    }

    public void clearUnreadMessageCount(int type){
        SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);

        String spKey ;
        if(MSG_TYPE_NEED == type){
            spKey = UNREAD_MSG_COUNT_NEED_ORDER ;
        }else if(MSG_TYPE_SERVER == type){
            spKey = UNREAD_MSG_COUNT_SERVER_ORDER ;
        }else if(MSG_TYPE_GOOD == type){
            spKey = UNREAD_MSG_COUNT_GOOD_ORDER ;
        }else if(MSG_TYPE_CIRCLE == type){
            spKey = UNREAD_MSG_COUNT_CIRCLE ;
        }else{
            spKey = UNREAD_MSG_COUNT_COMMENT ;
        }
        sp.edit().putInt(spKey, 0).apply();
    }

    /*******************未读消息相关 end ******************/





}
