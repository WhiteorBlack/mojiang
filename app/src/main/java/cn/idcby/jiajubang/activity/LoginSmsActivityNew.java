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
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.kenny.separatededittext.SeparatedEditText;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.LoginInfo;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.CountDownTimerUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginSmsActivityNew extends BaseActivity implements CompoundButton.OnCheckedChangeListener, EasyPermissions.PermissionCallbacks {
    private EditText etPhone;
    private LoadingDialog loadingDialog;
    private String phone;
    private TextView tvPhone;
    private TextView tvCode;
    private String code;
    private CountDownTimerUtils countDownTimerUtils;
    private SeparatedEditText stCode;
    @Override
    public int getLayoutID() {
        return R.layout.activity_login_sms;
    }

    @Override
    public void initView() {
        super.initView();
        ImmersionBar.with(this).statusBarColor(R.color.white).statusBarDarkFont(true).flymeOSStatusBarFontColor(R.color.black).init();
        tvPhone = findViewById(R.id.tv_notify_phone);
        phone = getIntent().getExtras().getString("phone");
        tvPhone.setText(phone);
        tvCode=findViewById(R.id.tv_count);
        stCode=findViewById(R.id.edit_underline);
       requestMsgCode(tvCode);
    }

    public void requestMsgCode(TextView textView) {
        if (countDownTimerUtils == null) {
            countDownTimerUtils = new CountDownTimerUtils(textView);
        }
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getPara(this);
        para.put("Phone", phone);
        NetUtils.getDataFromServerByPost(this, Urls.GET_MSG_CODE_FOR_REGISTER, false, para,
                new RequestObjectCallBack<String>("注册获取短信验证码", this, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        countDownTimerUtils.start();
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
                    }
                });
    }


    @Override
    public void initListener() {
        ((CheckBox) findViewById(R.id.chb_watch)).setOnCheckedChangeListener(this);
        findViewById(R.id.acti_login_sub_tv).setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.acti_login_sub_tv:
                code=stCode.getText().toString();
                if (TextUtils.isEmpty(code)){
                    ToastUtils.showToast(this,"请输入验证码");
                    return;
                }
                login();
                break;

        }
    }


    private void login() {
        if (!EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(this
                    , "登录功能需要获取设备信息权限，拒绝会导致登录失败", 1000, Manifest.permission.READ_PHONE_STATE);
            return;
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("UserAccount", phone);
        para.put("SmsCode", code);
        NetUtils.getDataFromServerByPost(mContext, Urls.LOGIN, true, para,
                new RequestObjectCallBack<LoginInfo>("登录", mContext, LoginInfo.class) {
                    @Override
                    public void onSuccessResult(LoginInfo bean) {
                        if (bean.PersonalInfoPerfect) {

//                            LoginHelper.saveUserLoginInfo(mContext, phone, pwd);
                            LoginHelper.login(mContext, bean);
                            EventBus.getDefault().post(new BusEvent.LocationUpdate(true));
                            getSelfInfo();
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("phone", phone);
//                            bundle.putString("pwd", pwd);
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
        NetUtils.cancelTag("注册获取短信验证码");
    }
}
