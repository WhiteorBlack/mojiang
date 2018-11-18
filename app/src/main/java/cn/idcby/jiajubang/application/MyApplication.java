package cn.idcby.jiajubang.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.multidex.MultiDex;

import com.baidu.location.BDLocation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.youku.cloud.player.YoukuPlayerConfig;

import java.util.HashMap;
import java.util.Map;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.BuildConfig;
import cn.idcby.jiajubang.activity.MessageChatActivity;
import cn.idcby.jiajubang.hxmsg.VideoCallActivity;
import cn.idcby.jiajubang.hxmsg.VoiceCallActivity;
import cn.idcby.jiajubang.hxmsg.db.DemoDBManager;
import cn.idcby.jiajubang.service.LocationService;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.wxapi.Paramters;
import cn.jpush.android.api.JPushInterface;

/**
 * Created on 2018/2/9.
 */

public class MyApplication extends Application {
    private static Application mContext ;

    public static Context getInstance(){
        return mContext ;
    }

    public LocationService locationService;

    public static Double Longitude = 0D;//经，纬度
    public static Double Latitude = 0D ;//经，纬度
    public static String LOCATION_PROVINCE = "";//当前定位的省
    public static String LOCATION_CITY = "";//当前定位的城市
    public static String LOCATION_DISTRICT = "";//当前定位的区

    //当前选择的城市--影响模块有：附近、直供、闲置、安装、服务、同城（圈子）、需求、招聘
    private static String CURRENT_LOCATION_ID ;
    private static String CURRENT_LOCATION_NAME ;
    private static int CURRENT_LOCATION_TYPE = 0;//1 省 2 市

    private static boolean mIsHxLoginSuccess = false ;

    public static boolean isVideoCalling ;
    public static boolean isVoiceCalling ;
    private static Map<String, EaseUser> contactList = new HashMap<>();
    private static Map<String, EaseUser> localContactList ;
    private static Map<String,Boolean> requestMap = new HashMap<>() ;//第一次请求状态
    private EaseUI easeUI ;

    private static IWXAPI msgApi;

    private static int jpushSequence = 1 ;

    private static int mScreenWidth ;
    private static int mScreenHeight ;

    public static int getScreenWidth() {
        return mScreenWidth;
    }

    public static int getScreenHeight() {
        return mScreenHeight;
    }

    public static void setHxLoginSuccess(boolean loginState){
        mIsHxLoginSuccess = loginState ;
    }
    public static boolean isHxLoginFail() {
        return !mIsHxLoginSuccess;
    }

    private static boolean mIsServerHidden = false ;

    public static boolean isServerHidden() {
        return mIsServerHidden;
    }

    public static void setServerHidden(boolean hidden) {
        MyApplication.mIsServerHidden = hidden;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        UMConfigure.init(this,"5ae3e7baa40fa30ef400013e"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0

        init() ;
    }

    private void init(){
        mContext = this ;

        initScreenWidHei() ;

        PlatformConfig.setWeixin(Paramters.WX_APP_ID, "b9bfc6fa56eb6d1eaea1fc57e61269c3");
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());

        //注意：优酷不支持64位，如果第三方sdk有 arm64,则略过该文件夹

        //初始化环信
        initHx() ;
        initWx() ;
        initJPush() ;
        initYoukuPlayer() ;
    }

    private void initYoukuPlayer(){
        /*请修改成你自己的clientId和clientSecret*/
        YoukuPlayerConfig.setClientIdAndSecret("b1ca84490a38f8e3","bb3f5b8f7f01eada083d1874080c0a6d");
        YoukuPlayerConfig.onInitial(this);
        YoukuPlayerConfig.setLog(true);
    }

    private void initScreenWidHei(){
        mScreenWidth = ResourceUtils.getScreenWidth(this) ;
        mScreenHeight = ResourceUtils.getScreenHeight(this) ;
    }

    private void initJPush(){
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);
        JPushInterface.setLatestNotificationNumber(this, 99);//设置最多保留几条未读消息
        JPushInterface.stopCrashHandler(this) ;
        if(JPushInterface.isPushStopped(this)){
            JPushInterface.resumePush(this) ;
        }

//        JPushInterface.setDefaultPushNotificationBuilder(new MyJpushNotifyBuilder(mContext));
    }

    public static int getJpushSequence(){
        jpushSequence++ ;
        return jpushSequence;
    }

    private void initWx(){
        msgApi = WXAPIFactory.createWXAPI(this, null);
        msgApi.registerApp(Paramters.WX_APP_ID);
    }

    public boolean isWXAppInstalledAndSupported() {
        boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
                && msgApi.isWXAppSupportAPI();

        return sIsWXAppInstalledAndSupported;
    }

    public static IWXAPI getMsgApi() {
        return msgApi;
    }

    /**
     * 初始化环信
     */
    private void initHx(){
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setRequireAck(true);
        options.setRequireDeliveryAck(false);

        if(EaseUI.getInstance().init(mContext, options)){
            easeUI = EaseUI.getInstance() ;
            setEaseUIProviders();
        }
    }

    public static void updateCurLocation(BDLocation location){
        Longitude = location.getLongitude();
        Latitude = location.getLatitude();
        LOCATION_PROVINCE = location.getProvince();
        LOCATION_CITY = location.getCity();
        LOCATION_DISTRICT = location.getDistrict();
    }

    public static String getLongitude(){
        return Longitude > 0 ? (Longitude + "") : "0" ;
    }

    public static String getLatitude(){
        return Latitude > 0 ? (Latitude + "") : "0" ;
    }

    /**
     * 更新当前选择的位置的信息
     * @param id 与type配合，省id、市id
     * @param name 与type配合，省名字、市名字
     * @param type 1 省  2市
     */
    public static void updateCurrentCityNameAndType(String id ,String name ,int type){
        CURRENT_LOCATION_ID = id ;
        CURRENT_LOCATION_NAME = name ;
        CURRENT_LOCATION_TYPE = type ;
    }

    /**
     * 获取当前选择的区域名字
     * @return type = 1  省名字  type=2 市名字
     */
    public static String getCurrentCityName(){
        return StringUtils.convertNull(CURRENT_LOCATION_NAME) ;
    }

    /**
     * 获取当前选择的区域id
     * @return type = 1  省id  type=2 市id
     */
    public static String getCurrentCityId(){
        return StringUtils.convertNull(CURRENT_LOCATION_ID) ;
    }

    /**
     * 获取当前选择区域类型
     * @return 1 省  2 市
     */
    public static String getCurrentCityType(){
        return "" + CURRENT_LOCATION_TYPE ;
    }

    public static void updateCurrentCityId(String cityId){
        CURRENT_LOCATION_ID = StringUtils.convertNull(cityId) ;
    }

    protected void setEaseUIProviders() {
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        avatarOptions.setAvatarShape(2);//1 circle 2 round
        avatarOptions.setAvatarBorderColor(Color.parseColor("#E6E6E6"));
        avatarOptions.setAvatarBorderWidth(ResourceUtils.dip2px(mContext,1));
        avatarOptions.setAvatarRadius(ResourceUtils.dip2px(mContext,3));
        easeUI.setAvatarOptions(avatarOptions);

        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                EaseUser user = getUserInfo(username) ;
                if(null == user){
                    user = new EaseUser(username) ;
                    getUserInfoByHxName(username) ;
                }
                return user;
            }
        });

        //set options
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return true ;
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return true;
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return true;
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                return true;
            }
        });

        //set notification options, will use default if you don't set it
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
            @Override
            public String getTitle(EMMessage message) {
                return null;
            }
            @Override
            public int getSmallIcon(EMMessage message) {
                return 0;
            }
            @Override
            public String getDisplayedText(EMMessage message) {
                // be used on notification bar, different text according the message type.
                String ticker = EaseCommonUtils.getMessageDigest(message, mContext);
                if(message.getType() == EMMessage.Type.TXT){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }

                EaseUser user = getUserInfo(message.getFrom());
                if(user != null){
                    if(EaseAtMessageHelper.get().isAtMeMsg(message)){
                        return String.format("%s在群聊中@了你", user.getNick());
                    }
                    return user.getNick() + ": " + ticker;
                }else{
                    if(EaseAtMessageHelper.get().isAtMeMsg(message)){
                        return String.format("%s在群聊中@了你", message.getFrom());
                    }
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                Intent intent = new Intent(mContext, MessageChatActivity.class);
                if(isVideoCalling){
                    intent = new Intent(mContext, VideoCallActivity.class);
                }else if(isVoiceCalling){
                    intent = new Intent(mContext, VoiceCallActivity.class);
                }else{
                    EMMessage.ChatType chatType = message.getChatType();
                    if (chatType == EMMessage.ChatType.Chat) { // single chat message
                        intent.putExtra(SkipUtils.INTENT_USER_HX_ID, message.getFrom());
                    }
                }
                return intent;
            }
        });
    }

    /**
     * 暂定规则：先读取本地缓存，
     * 每次进入app先获取本地缓存，
     * 然后每个用户第一次联系时，
     * 先请求接口获取最新内容，
     * 然后放到application中（内存中），取数据时，除第一次从缓存拿
     * ，其他都是从内存中取（内存中有对应数据）
     * @param username hxname
     * @return userInfo
     */
    public static EaseUser getUserInfo(String username){
        if(null == localContactList){
            localContactList = DemoDBManager.getInstance().getContactList() ;
        }

        if(contactList.containsKey(username)){
            return contactList.get(username);
        }

        //此处主动调用一次网络请求
        getUserInfoByHxName(username);
        return localContactList.get(username);
    }

    /**
     * 保存信息到application
     */
    public static void saveContactToMap(UserInfo userInfo){
        if(contactList != null){
            EaseUser user = new EaseUser(userInfo.getHxName()) ;
            user.setNickname(userInfo.getNickName());
            user.setAvatar(userInfo.getHeadIcon());

            contactList.put(user.getUsername() ,user) ;
            DemoDBManager.getInstance().saveContact(user);
        }
    }

    private static void getUserInfoByHxName(final String hxName){
        //正在请求，返回
        if(requestMap.containsKey(hxName) && requestMap.get(hxName)){
            return ;
        }

        requestMap.put(hxName,true) ;

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        paramMap.put("ID" ,"1") ;
        paramMap.put("Code" ,hxName) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.USER_HX_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getUserInfos" + hxName ,mContext ,UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        requestMap.put(hxName,false) ;

                        if(bean != null){
                            saveContactToMap(bean) ;
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        requestMap.put(hxName,false) ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        requestMap.put(hxName,false) ;
                    }
                });
    }

    public static String getCurrentUsernName(){
        return SPUtils.newIntance(mContext).getUserNumber() ;
    }

}
