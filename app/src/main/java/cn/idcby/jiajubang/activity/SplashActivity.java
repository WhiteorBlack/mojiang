package cn.idcby.jiajubang.activity;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.update.UpdateManager;
import cn.idcby.jiajubang.utils.FileUtil;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.LoginHelper;

public class SplashActivity extends BaseActivity {

    private final int GO_MAIN = 9;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_MAIN:
                    if(SPUtils.newIntance(mContext).getIsFirst()){
                        FirstGuideActivity.launch(mContext);
                    }else{
                        goNextActivity(MainActivity.class);
                    }
                    finish();
                    break;
            }
        }
    };


    @Override
    public int getLayoutID() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        //设置默认颜色
        StatusBarUtil.setDefaultStatusBarColor(this) ;

        new ClearCacheTask().execute() ;
        loginHx() ;
        handler.sendEmptyMessageDelayed(GO_MAIN, 3000);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {

    }

    @Override
    public void onBackPressed() {
    }

    private static class ClearCacheTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            GlideUtils.getInstance().clearImageAllCache() ;
            GlideUtils.clearAppCache() ;

            FileUtil.deleteFile(true ,FileUtil.getUploadCachePath()) ;
            FileUtil.deleteFile(true , UpdateManager.FILE_PATH) ;
            return null;
        }
    }

    /**
     * 登录环信
     */
    private void loginHx(){
        if(LoginHelper.isHxCanLogin(mContext)){
            EMClient.getInstance().login(SPUtils.newIntance(mContext).getHxName()
                    ,SPUtils.newIntance(mContext).getHxPass(),new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();

                    Log.d("main", "登录聊天服务器成功！");
                    MyApplication.setHxLoginSuccess(true) ;
                }
                @Override
                public void onProgress(int progress, String status) {
                }
                @Override
                public void onError(int code, String message) {
                    Log.d("main", "登录聊天服务器失败！code=" + code + ",message=" + message);
                    MyApplication.setHxLoginSuccess(false) ;
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null)
            handler.removeCallbacksAndMessages(null) ;

    }
}
