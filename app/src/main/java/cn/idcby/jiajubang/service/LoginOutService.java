package cn.idcby.jiajubang.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Map;

import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 退出登录service
 * Created on 2018/6/1.
 * 退出登录之后，需要给后台说一声
 */

public class LoginOutService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logoutApp() ;
        return START_NOT_STICKY;
    }


    /**
     * 退出登录
     */
    private void logoutApp(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(MyApplication.getInstance()) ;
        NetUtils.getDataFromServerByPost(MyApplication.getInstance(), Urls.LOGIN_OUT, paramMap
                , new RequestObjectCallBack<String>("logoutApp",MyApplication.getInstance() ,String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        stopSelf() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        stopSelf() ;
                    }
                    @Override
                    public void onFail(Exception e) {
                        stopSelf() ;
                    }
                });
    }


}
