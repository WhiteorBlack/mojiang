package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.gyf.barlibrary.ImmersionBar;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseUI;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.utils.AppManager;
import cn.idcby.commonlibrary.utils.DialogUtils;
import cn.idcby.commonlibrary.utils.FlagUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.Address;
import cn.idcby.jiajubang.Bean.LoginInfo;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.Bean.WordType;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.fragment.CircleFragment;
import cn.idcby.jiajubang.fragment.FragmentHome;
import cn.idcby.jiajubang.fragment.MeFragment;
import cn.idcby.jiajubang.fragment.NearFragment;
import cn.idcby.jiajubang.fragment.NearNewFragment;
import cn.idcby.jiajubang.hxmsg.CallReceiver;
import cn.idcby.jiajubang.interf.OnCityChanged;
import cn.idcby.jiajubang.interf.OnLayClickListener;
import cn.idcby.jiajubang.interf.OnLocationRefresh;
import cn.idcby.jiajubang.receiver.MyReceiver;
import cn.idcby.jiajubang.service.LocationService;
import cn.idcby.jiajubang.service.LoginOutService;
import cn.idcby.jiajubang.update.UpdateManager;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.jpush.android.api.JPushInterface;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.Call;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 主页
 * <p>
 * 2018-06-08 13:21:25
 * 消息角标问题，暂时做成每次启动app就清空消息数量，
 * 即：角标只统计当前app打开的时间内的数量，当app重新打开了，就会重新计数
 * 2018-11-19
 * 修改附近页面
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks
        , OnCityChanged, OnLayClickListener, OnLocationRefresh, View.OnClickListener {

    private Activity mContext;
    private MsgCountChangeReceiver msgCountChangeReceiver;

    private LinearLayout mLlHome;
    private ImageView mImgHome;
    private TextView mTvHome;
    private LinearLayout mLlNear;
    private ImageView mImgNear;
    private TextView mTvNear;
    private LinearLayout mLlCircle;
    private ImageView mImgCircle;
    private TextView mTvCircle;
    private LinearLayout mLlMe;
    private ImageView mImgMe;
    private TextView mTvMe;
    private View mPublishIv;

    private FragmentHome mHomeFragment;
    //    private NearFragment mNearFragment;
    private NearNewFragment mNearFragment;
    private CircleFragment mCircleFragment;
    private MeFragment mMeFragment;
    private FragmentManager fragmentManager;

    private LocationService mLocationService;

    private static final int REQUEST_CODE_TO_ME = 1001;

    private boolean mIsLocateSuccess = false;//是否定位成功
    private boolean mIsCityIdGeted = false;//是否从定位获取了相关id
    private boolean mIsNearLocaChange = false;//是否从附近刷新定位的操作

    //    环信
    private CallReceiver callReceiver;

    //版本更新
    private UpdateManager mUpdateManager;

    private int mCurPosition = -1;
    private static final int INDEX_HOME = 0;
    private static final int INDEX_NEAR = 1;
    private static final int INDEX_CIRCLE = 2;
    private static final int INDEX_ME = 3;
    private static final String TAG_HOME = "indexTagHome";
    private static final String TAG_NEAR = "indexTagNear";
    private static final String TAG_CIRCLE = "indexTagCircle";
    private static final String TAG_ME = "indexTagMe";


    private boolean mCanExit = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (0 == msg.what) {
                mCanExit = false;
            }
        }
    };


    /***
     * 消息数量变化的广播
     */
    private class MsgCountChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.showLog("MsgCountChangeReceiver--onReceive");

            if (intent != null && FlagUtils.FLAG_BROADCAST_MSG_COUNT_CHANGE.equals(intent.getAction())) {
                //获取数量并且设置显隐
//                int count = intent.getIntExtra(FlagUtils.FLAG_MSG_COUNT ,0) ;
                onMessageCountChange(SPUtils.newIntance(mContext).getUnreadMessageCountAll());
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppManager.getAppManager().addActivity(this);

        initView(savedInstanceState != null);
    }

    private void initView(boolean reCreate) {
        mContext = this;

        //适配android 8.0消息通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(MyReceiver.CHANNEL_NOMAL_ID, MyReceiver.CHANNEL_NOMAL_NAME, importance);
        }

        EventBus.getDefault().register(this);

//        StatusBarUtil.setTransparentForImageView(mContext ,null);
//        StatusBarUtil.setColor(this,getResources().getColor(R.color.white));
        ImmersionBar.with(this).statusBarColor(R.color.white).statusBarDarkFont(true).flymeOSStatusBarFontColor(R.color.black).init();
        //消息数量变化的广播
        msgCountChangeReceiver = new MsgCountChangeReceiver();
        IntentFilter msgFilter =
                new IntentFilter(FlagUtils.FLAG_BROADCAST_MSG_COUNT_CHANGE);
        registerReceiver(msgCountChangeReceiver, msgFilter);


        mLlHome = findViewById(R.id.ll_home);
        mImgHome = findViewById(R.id.img_home);
        mTvHome = findViewById(R.id.tv_home);
        mLlNear = findViewById(R.id.ll_near);
        mImgNear = findViewById(R.id.img_near);
        mTvNear = findViewById(R.id.tv_near);
        mLlCircle = findViewById(R.id.ll_circle);
        mImgCircle = findViewById(R.id.img_circle);
        mTvCircle = findViewById(R.id.tv_circle);
        mLlMe = findViewById(R.id.ll_me);
        mImgMe = findViewById(R.id.img_me);
        mTvMe = findViewById(R.id.tv_me);
        mPublishIv = findViewById(R.id.img_home_publish);

        SPUtils.newIntance(mContext).resetUnreadMessage();
        ShortcutBadger.applyCount(mContext, 0);

//        //模拟消息数量
//        SPUtils spUtils = SPUtils.newIntance(mContext) ;
//        spUtils.modifyUnreadMessageCount(SPUtils.MSG_TYPE_NEED ,true ,100) ;
//        spUtils.modifyUnreadMessageCount(SPUtils.MSG_TYPE_SERVER ,true ,50) ;
//        spUtils.modifyUnreadMessageCount(SPUtils.MSG_TYPE_GOOD ,true ,10) ;
//        spUtils.modifyUnreadMessageCount(SPUtils.MSG_TYPE_CIRCLE ,true ,6) ;
//        spUtils.modifyUnreadMessageCount(SPUtils.MSG_TYPE_COMMENT ,true ,2) ;
        initListener();
        initData(reCreate);
    }

    private void initData(boolean reCreate) {
        checkAppVersion();

        initFragment(reCreate);
        startLocations();
        loginWithSel();

        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {

                String curHxId = "";
                //当前正在显示的activity就是聊天窗口
                if (AppManager.getAppManager().currentActivity()
                        .getClass().getName().equals(MessageChatActivity.class.getName())) {
                    MessageChatActivity chatActivity = (MessageChatActivity) AppManager.getAppManager().currentActivity();
                    curHxId = chatActivity.getCurrentChatUserId();
                }

//                LogUtils.showLog("testMessageReceive" ,"onMessageReceived---curHxId=" + curHxId );

                for (EMMessage message : list) {
//                    LogUtils.showLog("testMessageReceive" ,"onMessageReceived---fromId=" + message.getFrom() );
                    if (!message.getFrom().equals(curHxId)) {
                        EaseUI.getInstance().getNotifier().onNewMsgWithNotify(message);
                        EventBus.getDefault().post(new BusEvent.UnreadMsgEvent(true));
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {
            }

            @Override
            public void onMessageRead(List<EMMessage> list) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {
            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initFragment(boolean reCreate) {
        fragmentManager = getSupportFragmentManager();

        if (reCreate) {
            fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentByTag(TAG_HOME))
                    .remove(fragmentManager.findFragmentByTag(TAG_NEAR))
                    .remove(fragmentManager.findFragmentByTag(TAG_CIRCLE))
                    .remove(fragmentManager.findFragmentByTag(TAG_ME))
                    .commitAllowingStateLoss();
        }

        if (null == mHomeFragment) {
            mHomeFragment = new FragmentHome();
            mHomeFragment.setCityChangeListener(this);
            mHomeFragment.setLayClickListener(this);
        }
        if (null == mNearFragment) {
//            mNearFragment = new NearFragment();
            mNearFragment = new NearNewFragment();
            mNearFragment.setLocationRefreshListener(this);
        }
        if (null == mCircleFragment) {
            mCircleFragment = new CircleFragment();
        }
        if (null == mMeFragment) {
            mMeFragment = new MeFragment();
        }

        fragmentManager.beginTransaction()
                .add(R.id.fl_container, mHomeFragment, TAG_HOME)
                .add(R.id.fl_container, mNearFragment, TAG_NEAR)
                .add(R.id.fl_container, mCircleFragment, TAG_CIRCLE)
                .add(R.id.fl_container, mMeFragment, TAG_ME)
                .commit();

        changeTabShow(INDEX_HOME);
    }

    private void setBottomItemSelect(boolean flagHome, boolean flagNear, boolean flagCircle,
                                     boolean flagMe) {
        mImgHome.setSelected(flagHome);
        mTvHome.setSelected(flagHome);

        mImgNear.setSelected(flagNear);
        mTvNear.setSelected(flagNear);

        mImgCircle.setSelected(flagCircle);
        mTvCircle.setSelected(flagCircle);

        mImgMe.setSelected(flagMe);
        mTvMe.setSelected(flagMe);
    }


    private void initListener() {
        mLlHome.setOnClickListener(this);
        mLlNear.setOnClickListener(this);
        mLlCircle.setOnClickListener(this);
        mLlMe.setOnClickListener(this);
        mPublishIv.setOnClickListener(this);

        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        callReceiver = new CallReceiver();
        registerReceiver(callReceiver, callFilter);

        //2018-08-23 14:55:13 由于莫名会各种挤掉帐号，所以不用环信的功能，直接用接口的
//        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
//            @Override
//            public void onConnected() {
//            }
//            @Override
//            public void onDisconnected(final int error) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(error == EMError.USER_REMOVED){
//                            // 显示帐号已经被移除
//                        }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
//                            LogUtils.showLog("环信登录状态" ,"当前账号被挤掉了---id="
//                                    + SPUtils.newIntance(mContext).getHxName() + "**") ;
//
//                            //退出环信登录，不然会间歇性的执行这个回调
//                            EMClient.getInstance().logout(true, new EMCallBack() {
//                                @Override
//                                public void onSuccess() {
//                                    //帐号在其他设备登陆，此时清空登陆信息
//                                    LoginHelper.logout(mContext) ;
//                                    MyApplication.setHxLoginSuccess(false);
//                                    LoginHelper.resetHxInfo(mContext) ;
//                                    JPushInterface.stopPush(MyApplication.getInstance()) ;
//                                }
//                                @Override
//                                public void onProgress(int progress, String status) {
//                                }
//                                @Override
//                                public void onError(int code, String message) {
//                                    //帐号在其他设备登陆，此时清空登陆信息
//                                    LoginHelper.logout(mContext) ;
//                                    MyApplication.setHxLoginSuccess(false);
//                                    LoginHelper.resetHxInfo(mContext) ;
//                                    JPushInterface.stopPush(MyApplication.getInstance()) ;
//                                }
//                            });
//
//                            showTokenOutTimeDialog() ;
//                        } else {
//                        }
//                    }
//                });
//            }
//        });
    }

    private void showTokenOutTimeDialog() {
        final Activity context;
        if (null == AppManager.getAppManager().currentActivity()) {
            context = mContext;
        } else {
            context = AppManager.getAppManager().currentActivity();
        }

        DialogUtils.showCustomViewDialog(context, "温馨提示", "您的帐号已经在其他设备登录", null
                , "重新登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Intent toLoIt = new Intent(context, LoginActivity.class);
                        toLoIt.putExtra("isGoMain", false);
                        context.startActivityForResult(toLoIt, 1234);
                    }
                }, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        EventBus.getDefault().post(new BusEvent.LoginOutEvent(true));

                        AppManager.getAppManager().finishOtherActivity(MainActivity.class);

//                        Intent toMiIt = new Intent(context , MainActivity.class) ;
//                        toMiIt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        toMiIt.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        context.startActivity(toMiIt) ;
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_home:

                changeTabShow(INDEX_HOME);

                break;
            case R.id.ll_near:

                changeTabShow(INDEX_NEAR);

                break;
            case R.id.ll_circle:

                changeTabShow(INDEX_CIRCLE);

//                toCircleActivity() ;

                break;
            case R.id.ll_me:

                changeTabShow(INDEX_ME);

                break;
            case R.id.img_home_publish:

                Intent toPbIt = new Intent(mContext, PublishActivity.class);
                startActivity(toPbIt);

                break;
        }
    }

    /**
     * 切换显示
     *
     * @param index index
     */
    private void changeTabShow(int index) {
//        if(mCurPosition == index){
//            return ;
//        }

        switch (index) {
            case 0:
                fragmentManager.beginTransaction()
                        .show(mHomeFragment)
                        .hide(mNearFragment)
                        .hide(mCircleFragment)
                        .hide(mMeFragment)
                        .commit();
                setBottomItemSelect(true, false, false, false);
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .show(mNearFragment)
                        .hide(mHomeFragment)
                        .hide(mCircleFragment)
                        .hide(mMeFragment)
                        .commit();
                setBottomItemSelect(false, true, false, false);
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .show(mCircleFragment)
                        .hide(mNearFragment)
                        .hide(mHomeFragment)
                        .hide(mMeFragment)
                        .commit();
                setBottomItemSelect(false, false, true, false);
                break;
            case 3:
                if (LoginHelper.isNotLogin(mContext)) {
                    SkipUtils.toLoginActivityForResult(mContext, REQUEST_CODE_TO_ME);
                } else {
                    fragmentManager.beginTransaction()
                            .show(mMeFragment)
                            .hide(mNearFragment)
                            .hide(mCircleFragment)
                            .hide(mHomeFragment)
                            .commit();
                    setBottomItemSelect(false, false, false, true);
                }
                break;
            default:
                break;
        }

        mCurPosition = index;
    }

    private void checkAppVersion() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(mContext, permissions)) {
            EasyPermissions.requestPermissions(MainActivity.this
                    , "应用需要存储权限，拒绝会导致部分功能异常", 1001
                    , permissions);
            return;
        }
        //版本检查
        mUpdateManager = new UpdateManager(this, false);
        mUpdateManager.checkUpdateInfo();
    }

    private void startLocations() {
        if (!EasyPermissions.hasPermissions(mContext
                , Manifest.permission.READ_PHONE_STATE
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            EasyPermissions.requestPermissions(MainActivity.this
                    , "应用需要定位权限来获取当前位置，拒绝会导致部分功能异常", 1000
                    , Manifest.permission.READ_PHONE_STATE
                    , Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }

        // -----------location config ------------
        mLocationService = ((MyApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        mLocationService.registerListener(mListener);
        //注册监听
        mLocationService.setLocationOption(mLocationService.getDefaultLocationClientOption());
        mLocationService.start();
    }

    //发送当前位置
    private void UpdatePosition() {
        if (LoginHelper.isNotLogin(mContext)) {
            return;
        }
        Map<String, String> param = ParaUtils.getParaNece(mContext);
        param.put("ProvinceName", MyApplication.LOCATION_PROVINCE);
        param.put("CityName", MyApplication.LOCATION_CITY);
        param.put("CountyName", MyApplication.LOCATION_DISTRICT);
        param.put("Longitude", MyApplication.Longitude + "");
        param.put("Latitude", MyApplication.Latitude + "");

        NetUtils.getDataFromServerByPost(mContext, Urls.UPLOAD_LOCATION, param, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.showLog("uploadLocation", "e=" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.showLog("uploadLocation", "response=" + response);
            }
        });
    }

    /**
     * 根据定位城市，获取id
     */
    private void getCityIdByName(final String cityName) {
        Map<String, String> param = ParaUtils.getPara(mContext);
        param.put("AreaName", cityName);
        param.put("AreaType", MyApplication.getCurrentCityType());

        NetUtils.getDataFromServerByPost(mContext, Urls.API_HOME_GET_CITY_ID_BY_NAME, param
                , new RequestObjectCallBack<Address>("getCityIdByName", mContext, Address.class) {
                    @Override
                    public void onSuccessResult(Address bean) {
                        if (bean != null) {
                            mIsCityIdGeted = true;
                            MyApplication.updateCurrentCityId(bean.getAreaId());
                        }

                        if (!mIsNearLocaChange) {
                            mHomeFragment.setCurLocation(cityName);
                        }

                        mIsNearLocaChange = false;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (!mIsNearLocaChange) {
                            mHomeFragment.setCurLocation(cityName);
                        }
                        mIsNearLocaChange = false;
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (!mIsNearLocaChange) {
                            mHomeFragment.setCurLocation(cityName);
                        }
                        mIsNearLocaChange = false;
                    }
                });
    }

    /**
     * 静默登录
     */
    private void loginWithSel() {
        String userId = StringUtils.convertNull(SPUtils.newIntance(mContext).getUserLoginId());
        String userPass = StringUtils.convertNull(SPUtils.newIntance(mContext).getUserLoginPass());

        if (!"".equals(userId)
                && !"".equals(userPass)) {
            Map<String, String> para = ParaUtils.getPara(mContext);
            para.put("UserAccount", userId);
            para.put("Password", userPass);
            NetUtils.getDataFromServerByPost(mContext, Urls.LOGIN, true, para,
                    new RequestObjectCallBack<LoginInfo>("登录", false, mContext, LoginInfo.class) {
                        @Override
                        public void onSuccessResult(LoginInfo bean) {
                            LoginHelper.login(mContext, bean);
                            EventBus.getDefault().post(new BusEvent.LocationUpdate(true));

                            getSelfInfo();
                        }

                        @Override
                        public void onErrorResult(String str) {
                        }

                        @Override
                        public void onFail(Exception e) {
                        }
                    });
        }
    }

    private void getSelfInfo() {
        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getMyInfo", false, mContext, UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        if (bean != null) {
                            LoginHelper.saveUserInfoToLocal(mContext, bean);
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                    }

                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    /**
     * 检查服务显示状态
     */
    private void checkServerShowState() {
        Map<String, String> paramMap = ParaUtils.getPara(mContext);
        paramMap.put("Code", "IsShowService");

        NetUtils.getDataFromServerByPost(mContext, Urls.GET_ENTIRY_BY_CODE, paramMap
                , new RequestObjectCallBack<WordType>("checkServerShowState", false, mContext, WordType.class) {
                    @Override
                    public void onSuccessResult(WordType bean) {
                        if (bean != null) {
                            String value = bean.getItemValue();
                            MyApplication.setServerHidden("0".equals(value));
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                    }

                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }

    /**
     * 判断环信登录状态，如果异常，重试
     */
    private void checkHxLoginState() {
        boolean isReloadHx = (MyApplication.isHxLoginFail() && LoginHelper.isHxCanLogin(mContext));

        LogUtils.showLog("testHxLogin", "---checkHxLoginState=" + isReloadHx);

        if (isReloadHx) {
            //可能帐号异常，先退出登录
            EMClient.getInstance().logout(true, new EMCallBack() {
                @Override
                public void onSuccess() {
                    Log.d("main", "退出聊天服务器成功！");
                    loginHx();
                }

                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(int code, String message) {
                    Log.d("main", "退出聊天服务器失败！code=" + code + ",message=" + message);
                }
            });
        }
    }

    /**
     * 登录环信
     */
    private void loginHx() {
        LogUtils.showLog("testHxLogin", "---loginHx--");

        EMClient.getInstance().login(SPUtils.newIntance(mContext).getHxName()
                , SPUtils.newIntance(mContext).getHxPass(), new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();

                        Log.d("main", "登录聊天服务器成功！");
                        MyApplication.setHxLoginSuccess(true);
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d("main", "登录聊天服务器失败！code=" + code + ",message=" + message);
                        MyApplication.setHxLoginSuccess(false);
                    }
                });
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (1000 == requestCode) {
            startLocations();
        } else if (1001 == requestCode) {
            checkAppVersion();
        }
//        else if(UpdateManager.INSTALL_PACKAGES_REQUESTCODE== requestCode){
//            if(mUpdateManager != null){
//                mUpdateManager.startUploadApk(true) ;
//            }
//        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (1000 == requestCode) {
            ToastUtils.showToast(mContext, "拒绝了相关权限，会导致定位功能失败");
        } else if (1001 == requestCode) {
            ToastUtils.showToast(mContext, "拒绝了相关权限，会导致部分功能异常");
        }
//        else if(UpdateManager.INSTALL_PACKAGES_REQUESTCODE== requestCode){
//            if(mUpdateManager != null){
//                mUpdateManager.startUploadApk(false) ;
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (UpdateManager.INSTALL_PACKAGES_REQUESTCODE == requestCode) {
            //有注册权限且用户允许安装
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mUpdateManager != null) {
                    mUpdateManager.startUploadApk(true);
                }
            } else {
                if (Build.VERSION.SDK_INT >= 26) {
                    DialogUtils.showCustomViewDialog(mContext, "温馨提示"
                            , "需要系统允许安装未知来源权限，请设置之后，重启app", null
                            , "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    //将用户引导至安装未知应用界面。
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                    startActivity(intent);
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    if (mUpdateManager != null) {
                                        mUpdateManager.startUploadApk(false);
                                    }
                                }
                            });
                }
            }
        } else {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }

                LogUtils.showLog("BaiduLocationApiDem", sb.toString());

                if (location.getCity() != null && location.getProvince() != null) {
                    mIsLocateSuccess = true;

                    if (!mIsNearLocaChange) {
                        MyApplication.updateCurLocation(location);
                        MyApplication.updateCurrentCityNameAndType("", location.getCity(), SkipUtils.LOCATION_TYPE_CITY);
                    }
                    MyApplication.setLocationRoad(TextUtils.isEmpty(location.getStreet()) ? "无名路" : location.getStreet());

                    getCityIdByName(location.getCity());
                    UpdatePosition();

                    final String locationDesc;
                    List<Poi> poiList = location.getPoiList();
                    if (poiList.size() > 0) {
                        locationDesc = poiList.get(0).getName();
                    } else {
                        locationDesc = location.getLocationDescribe();
                    }
                    String road =TextUtils.isEmpty(location.getStreet()) ? "无名路" : location.getStreet();
                    mNearFragment.setCurLocation(road);

                    mLocationService.unregisterListener(mListener); //注销掉监听
                    mLocationService.stop(); //停止定位服务
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE_TO_ME == requestCode) {
            if (RESULT_OK == resultCode) {
                changeTabShow(INDEX_ME);
            }
        } else if (UpdateManager.REQUEST_CODE_INSTALL_APK == requestCode) {
            finish();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.UpdateEvent ev) {
        if (!ev.isNext()) {//取消了下载
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBusLogin(BusEvent.LoginOutEvent ev) {
        if (ev.isOut()) {
            MyApplication.setHxLoginSuccess(false);
            LoginHelper.resetHxInfo(mContext);
            JPushInterface.stopPush(MyApplication.getInstance());
            LoginHelper.logout(mContext);
            AppManager.getAppManager().finishActivity(MainActivity.this);
            changeTabShow(INDEX_HOME);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBusLocation(BusEvent.LocationUpdate ev) {
        if (ev.isUpdate()) {
            UpdatePosition();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateHxLoginState(BusEvent.HxLoginStateEvent ev) {
        if (ev.isReLogin()) {
            checkHxLoginState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("checkServerShowState");

        if (msgCountChangeReceiver != null) {
            unregisterReceiver(msgCountChangeReceiver);
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        if (!"".equals(SPUtils.newIntance(mContext).getToken())) {
            startService(new Intent(mContext, LoginOutService.class));
        }

        EventBus.getDefault().unregister(this);

        //2018-06-05 15:18:09 客户貌似要退出app了还有消息能接收，所以暂时不要清空未读消息
//        JPushInterface.clearAllNotifications(this) ;

        if (mLocationService != null) {
            mLocationService.unregisterListener(mListener); //注销掉监听
            mLocationService.stop(); //停止定位服务
        }

        if (callReceiver != null) {
            unregisterReceiver(callReceiver);
        }

        AppManager.getAppManager().AppExit(mContext);
    }

    @Override
    public void onCityChanged(int type, String cId, String content) {

        MyApplication.updateCurrentCityNameAndType(cId, content, type);

        mHomeFragment.updateCityChangeDisplay();
        mNearFragment.updateCityChangeDisplay();
        mCircleFragment.updateCityChangeDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsLocateSuccess && !mIsCityIdGeted) {
            getCityIdByName(MyApplication.getCurrentCityName());
        }

        int allCount = SPUtils.newIntance(mContext).getUnreadMessageCountAll();
        onMessageCountChange(allCount);

        checkHxLoginState();
        checkJpushAlisaState();
        checkServerShowState();
    }

    private void checkJpushAlisaState() {
        if (!SPUtils.newIntance(mContext).getUserAliasState()) {
            if (LoginHelper.isNotLogin(mContext)) {
                JPushInterface.deleteAlias(mContext, MyApplication.getJpushSequence());
            } else {
                JPushInterface.setAlias(mContext, MyApplication.getJpushSequence(), SPUtils.newIntance(mContext).getUserNumber());
            }
        }
    }

    @Override
    public void onLayClick(int type) {
        if (FragmentHome.LAY_TYPE_CIRCLE == type) {
//            toCircleActivity() ;

            mCircleFragment.changeItem(2);
            changeTabShow(INDEX_CIRCLE);
        }
    }

    @Override
    public void onRefresh() {
        mIsNearLocaChange = true;
        startLocations();
    }

    private void onMessageCountChange(int count) {
        if (mHomeFragment != null) {
            mHomeFragment.changeUnreadMessageCount(count);
        }
    }

    @Override
    public void onBackPressed() {

        if (!mCanExit) {
            ToastUtils.showToast(mContext, "再按一次退出");
            mCanExit = true;
            handler.sendEmptyMessageDelayed(0, 1500);
        } else {
            super.onBackPressed();
        }
    }


}
