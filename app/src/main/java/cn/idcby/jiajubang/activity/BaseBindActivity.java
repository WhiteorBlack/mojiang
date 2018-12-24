package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.gyf.barlibrary.ImmersionBar;

import cn.idcby.commonlibrary.R;
import cn.idcby.commonlibrary.utils.AppManager;
import cn.idcby.commonlibrary.utils.FlagUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;


/**
 * Created on 2016/8/4.
 */
public abstract class BaseBindActivity extends AppCompatActivity implements View.OnClickListener {
    public Activity mActivity ;
    public Context mContext;

    private MyAppNomalReceiver msgCountChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppManager.getAppManager().addActivity(this);
        //LogUtils.showLog("result?>>>" + result);
        initBinding();
        initSDK();
//        setContentView(getLayoutID());
//        StatusBarUtil.resetStatusBarColor(this) ;

        //消息数量变化的广播
        msgCountChangeReceiver = new MyAppNomalReceiver();
        IntentFilter msgFilter =
                new IntentFilter(FlagUtils.FLAG_BROADCAST_MSG_COUNT_CHANGE);
        registerReceiver(msgCountChangeReceiver, msgFilter);
        mActivity = this;
        mContext = this;
        initView();
        initTitle();
        initData();
        initListener();

        dealCommon();
    }

    public void WhiteStatuBar(View view){
        view.getLayoutParams().height=StatusBarUtil.getStatusBarHeight(this);
        ImmersionBar.with(this).statusBarColor(cn.idcby.jiajubang.R.color.transparent).statusBarDarkFont(true).flymeOSStatusBarFontColor(cn.idcby.jiajubang.R.color.black).keyboardEnable(false).init();

    }

    public void refreshOk(){

    }

    public void refreshFail(){

    }

    protected abstract void initBinding();

    public void initSDK() {
    }


//    public abstract int getLayoutID();

    public void initView(){

    }

    public void initTitle() {
    }


    public void initData(){

    }


    public abstract void initListener();


    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.back) {//处理返回键
            finish();
        } else {
            dealOhterClick(view);

        }
    }

    /**
     * 处理除了返回键之外的其他点击事件
     *
     * @param view
     */
    public abstract void dealOhterClick(View view);

    /**
     * 处理相同的返回上一页逻辑
     */
    private void dealCommon() {

        View view = (View) findViewById(R.id.back);
        if (view != null) {
            view.setOnClickListener(this);
        }

    }

    public void setTopBarIsImmerse(boolean isImmerse) {
        if (isImmerse) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }


    public void goNextActivity(Class activity) {
        Intent intent = new Intent(mContext, activity);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void goNextActivity(Class activity, Bundle data) {
        Intent intent = new Intent(mContext, activity);
        intent.putExtras(data);
        mContext.startActivity(intent, data);
    }


    public void setViewNoUse(View view, int bgId) {
        view.setBackgroundResource(bgId);
        view.setEnabled(false);
        view.setClickable(false);
    }

    public void setViewCanUse(View view, int bgId) {
        view.setBackgroundResource(bgId);
        view.setEnabled(true);
        view.setClickable(true);
    }

    public void onMessageCountChange(int count){

    }

    /***
     * 的广播
     */
    private class MyAppNomalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.showLog("MyAppNomalReceiver--onReceive");

            if(intent != null){
                String action = intent.getAction() ;

                if(FlagUtils.FLAG_BROADCAST_MSG_COUNT_CHANGE.equals(action)){//消息数量变化
                    //获取数量并且设置显隐
//                int count = intent.getIntExtra(FlagUtils.FLAG_MSG_COUNT ,0) ;
                    onMessageCountChange(SPUtils.newIntance(mContext).getUnreadMessageCountAll()) ;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int allCount = SPUtils.newIntance(mContext).getUnreadMessageCountAll() ;
        onMessageCountChange(allCount) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1234 == requestCode){
            if(RESULT_OK != resultCode){//被其他用户挤掉了，但是没有登陆成功，则需要跳转到主页
                //注意：MainActivity没有添加到AppManager里面，所以可以这样写，如果加入了，就不能这么写了
                AppManager.getAppManager().finishAllActivity() ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (msgCountChangeReceiver != null){
            unregisterReceiver(msgCountChangeReceiver);
        }

        AppManager.getAppManager().finishActivity(this) ;
    }
}
