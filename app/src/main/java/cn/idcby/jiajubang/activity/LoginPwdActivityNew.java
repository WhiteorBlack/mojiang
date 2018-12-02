package cn.idcby.jiajubang.activity;


import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.LoginInfo;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginPwdActivityNew extends BaseActivity implements CompoundButton.OnCheckedChangeListener, EasyPermissions.PermissionCallbacks {
    private EditText etPhone;
    private LoadingDialog loadingDialog;
    private String phone;

    @Override
    public int getLayoutID() {
        return R.layout.activity_login_pwd;
    }

    @Override
    public void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        ImmersionBar.with(this).statusBarColor(R.color.white).statusBarDarkFont(true).flymeOSStatusBarFontColor(R.color.black).init();
        etPhone = findViewById(R.id.acti_login_number_ev);
        phone = getIntent().getExtras().getString("phone");
    }

    @Override
    public void initListener() {
        findViewById(R.id.tv_forgetPwd).setOnClickListener(this);
        findViewById(R.id.tv_code).setOnClickListener(this);
        ((CheckBox) findViewById(R.id.chb_watch)).setOnCheckedChangeListener(this);
        findViewById(R.id.acti_login_sub_tv).setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.acti_login_sub_tv:
                login();
                break;
            case R.id.tv_forgetPwd:

                break;

            case R.id.tv_code:

                SkipUtils.goActivity(this, LoginSmsActivityNew.class, getIntent().getExtras());
                break;
        }
    }


    private void login() {
        if (!EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(this
                    , "登录功能需要获取设备信息权限，拒绝会导致登录失败", 1000, Manifest.permission.READ_PHONE_STATE);
            return;
        }

        final String pwd = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showToast(mContext, "请输入密码");
            return;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("UserAccount", phone);
        para.put("Password", pwd);
        NetUtils.getDataFromServerByPost(mContext, Urls.LOGIN, true, para,
                new RequestObjectCallBack<LoginInfo>("登录", mContext, LoginInfo.class) {
                    @Override
                    public void onSuccessResult(LoginInfo bean) {
                        SPUtils.newIntance(mContext).saveToken(bean.token);
                        if (bean.PersonalInfoPerfect) {
                            LoginHelper.saveUserLoginInfo(mContext, phone, pwd);
                            LoginHelper.login(mContext, bean);
                            EventBus.getDefault().post(new BusEvent.LocationUpdate(true));
                            getSelfInfo();
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("phone", phone);
                            bundle.putString("pwd", pwd);
                            SkipUtils.goActivity(mActivity, RegisterInfoActivity.class, bundle);
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        ToastUtils.showToast(mContext, "网络异常");
                    }
                });
    }

    private void getSelfInfo() {
        Map<String, String> paramMap = ParaUtils.getParaWithToken(mContext);
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getMyInfo", mContext, UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (bean != null) {
                            LoginHelper.saveUserInfoToLocal(mContext, bean);
                            goNextActivity(MainActivity.class);
                        } else {
                            ToastUtils.showToast(mContext, "网络异常");
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        ToastUtils.showToast(mContext, "网络异常");

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BusEvent.HxLoginStateEvent stateEvent){
        if (stateEvent!=null&&stateEvent.isReLogin()){
            onBackPressed();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            etPhone.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            etPhone.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtils.showToast(mContext, "拒绝了设备权限，可能导致登录异常");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
