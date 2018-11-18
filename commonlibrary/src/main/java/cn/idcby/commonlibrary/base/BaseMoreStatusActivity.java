package cn.idcby.commonlibrary.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.idcby.commonlibrary.R;
import cn.idcby.commonlibrary.utils.AppManager;
import cn.idcby.commonlibrary.utils.FlagUtils;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;


/**
 * Created on 2016/8/4.
 */
public abstract class BaseMoreStatusActivity extends AppCompatActivity implements View.OnClickListener {
    public Activity mActivity ;
    public Context mContext;

    public FrameLayout mFlContainer;
    private View loadingView;
    private View errorView;
    private View emptyView;
    private View successView;
    private View noNetView;
    private TextView mTvTitle;
    private TextView mTvRight;
    private ImageView mImgRight;
    private TextView mCountTv ;

    private LinearLayout mLlTopBar;

    private boolean mIsShowMsg = true ;//默认显示数量

    private LoginSuccessBroadCastReceiver loginSuccessBroadCastReceiver;
    private MsgCountChangeReceiver msgCountChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_base);

        StatusBarUtil.resetStatusBarColor(this) ;

        mActivity = this;
        mContext = this;
        mLlTopBar = (LinearLayout) findViewById(R.id.topbar);
        mFlContainer = (FrameLayout) findViewById(R.id.fl_container);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mImgRight =(ImageView)  findViewById(R.id.img_right);
        mCountTv = findViewById(R.id.tv_right_count);

        initTitle();
        initTopBar(mTvRight,mImgRight);
        addMoreStatusPage();
        dealCommon();
        initData();
        //登录成功的广播
        loginSuccessBroadCastReceiver = new LoginSuccessBroadCastReceiver();
        IntentFilter intentFilterToOftenAddress =
                new IntentFilter(FlagUtils.FLAG_BROADCAST_LOGIN_OR_LOGIN_OUT_SUCCESS);
        registerReceiver(loginSuccessBroadCastReceiver, intentFilterToOftenAddress);

        //消息数量变化的广播
        msgCountChangeReceiver = new MsgCountChangeReceiver();
        IntentFilter msgFilter =
                new IntentFilter(FlagUtils.FLAG_BROADCAST_MSG_COUNT_CHANGE);
        registerReceiver(msgCountChangeReceiver, msgFilter);
    }


    protected void addMoreStatusPage() {

        if (loadingView == null) {
            loadingView = createLoadingView();
            mFlContainer.addView(loadingView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
        }


        if (errorView == null) {
            errorView = createErrorView();
            mFlContainer.addView(errorView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (emptyView == null) {
            emptyView = createEmptyView();
            mFlContainer.addView(emptyView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (noNetView == null) {
            noNetView = createNoNetView();
            mFlContainer.addView(noNetView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
        }

        addSuccessView();
        initDefaultStatus();

    }


    private View createNoNetView() {
        View view = View.inflate(mContext, R.layout.page_no_net, null);
        view.findViewById(R.id.tv_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initData();
            }
        });
        return view;
    }

    private View createEmptyView() {
        return View.inflate(mContext, R.layout.page_empty, null);
    }

    private View createErrorView() {

        View view = View.inflate(mContext, R.layout.page_error, null);
        view.findViewById(R.id.tv_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initData();
            }
        });
        return view;
    }

    private View createLoadingView() {

        return View.inflate(mContext, R.layout.page_loading, null);
    }


    private void initDefaultStatus() {
        showLoadingPage();
    }

    private void addSuccessView() {
        successView = View.inflate(mContext, getSuccessViewId(), null);
        mFlContainer.addView(successView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
        init();
    }


    public void showLoadingPage() {
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        noNetView.setVisibility(View.GONE);
        if (successView != null)
            successView.setVisibility(View.GONE);
    }

    public void showErrorPage() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        noNetView.setVisibility(View.GONE);
        if (successView != null)
            successView.setVisibility(View.GONE);
    }

    public void showEmptyPage() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        noNetView.setVisibility(View.GONE);
        if (successView != null)
            successView.setVisibility(View.GONE);
    }

    public void showNetErrorPage() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        noNetView.setVisibility(View.VISIBLE);
        if (successView != null)
            successView.setVisibility(View.GONE);
    }


    public void showSuccessPage() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        noNetView.setVisibility(View.GONE);
        successView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.back) {
            finish();

        } else {
            dealOhterClick(view);

        }
    }


    private void dealCommon() {

        View view = (View) findViewById(R.id.back);

        if (view != null) {
            view.setOnClickListener(this);
        }


    }


    private void initData() {
        showLoadingPage();
        if (!MyUtils.isNetworkConnected(mContext)) {

            showNetErrorPage();
            return;
        }
        requestData();
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }

    private void initTitle() {
        if (setTitle() != null)
            mTvTitle.setText(setTitle());
    }


    public abstract void requestData();

    public abstract int getSuccessViewId();

    public abstract String setTitle();

    public abstract void initTopBar(TextView tvRight ,ImageView imgRight);

    public abstract void init();

    public abstract void dealOhterClick(View view);


    public void goNextActivity(Class activity) {
        Intent intent = new Intent(mContext, activity);
        startActivity(intent);
    }

    public void goNextActivity(Class activity ,int requestCode) {
        Intent intent = new Intent(mContext, activity);
        startActivityForResult(intent,requestCode);
    }

    public void goNextActivity(Class activity, Bundle data) {
        Intent intent = new Intent(mContext, activity);
        if(data != null){
            intent.putExtras(data);
        }
        startActivity(intent);
    }

    public void setTopBarIsShow(boolean isShow) {
        if (isShow) {
            mLlTopBar.setVisibility(View.VISIBLE);
        } else {
            mLlTopBar.setVisibility(View.GONE);
        }


    }

    public void setMessageCountShow(boolean isShow){
        this.mIsShowMsg = isShow ;
    }

    /***
     * 登录成功的广播
     */
    private class LoginSuccessBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.showLog("登录成功的广播>>>");
            requestData();

        }
    }

    /***
     * 消息数量变化的广播
     */
    private class MsgCountChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.showLog("MsgCountChangeReceiver--onReceive");

            if(intent != null && FlagUtils.FLAG_BROADCAST_MSG_COUNT_CHANGE.equals(intent.getAction())){
                //获取数量并且设置显隐
                int count = intent.getIntExtra(FlagUtils.FLAG_MSG_COUNT ,0) ;
                changeCountText(count) ;
            }
        }
    }

    private void changeCountText(int count){
        if(mImgRight.getVisibility() != View.VISIBLE || !mIsShowMsg){//两者配合使用
            return ;
        }

        if(mCountTv != null){
            if(count > 0){
                mCountTv.setText(count > 99 ? "99+" : ("" + count));
                if(mCountTv.getVisibility() != View.VISIBLE){
                    mCountTv.setVisibility(View.VISIBLE);
                }
            }else{
                mCountTv.setText("0");
                if(mCountTv.getVisibility() != View.GONE){
                    mCountTv.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setTitleText(String text){
        if(mTvTitle != null){
            mTvTitle.setText(null == text ? "" : text) ;
        }
    }

    public String getEditTextValue(EditText et) {
        return et.getText().toString().trim();
    }

    @Override
    protected void onResume() {
        super.onResume();

        changeCountText(SPUtils.newIntance(mContext).getUnreadMessageCountAll()) ;
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

        if (loginSuccessBroadCastReceiver != null){
            unregisterReceiver(loginSuccessBroadCastReceiver);
        }

        if (msgCountChangeReceiver != null){
            unregisterReceiver(msgCountChangeReceiver);
        }
    }
}
