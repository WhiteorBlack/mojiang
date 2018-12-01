package cn.idcby.jiajubang.viewmodel;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.BR;
import cn.idcby.jiajubang.Bean.ResultBean;
import cn.idcby.jiajubang.activity.RegisterInfoActivity;
import cn.idcby.jiajubang.utils.CountDownTimerUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.Urls;

public class RegisterViewModel extends BaseObservable implements ViewModel {
    private BaseBindActivity activity;
    private LoadingDialog loadingDialog;
    private String phone;
    private CountDownTimerUtils countDownTimerUtils;
    private String code;
    private String pwd;
    private String pwdAgain;

    public RegisterViewModel(BaseBindActivity activity) {
        this.activity = activity;
        initData();
    }

    private void initData() {
        phone = activity.getIntent().getExtras().getString("phone");
    }

    public void commitData() {
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showToast(activity, "请输入验证码");
            return;
        }

        if (!TextUtils.equals(pwd, pwdAgain)) {
            ToastUtils.showToast(activity, "两次输入的密码不一致");
        }
        Map<String, String> para = ParaUtils.getPara(activity);
        para.put("Phone", phone);
        para.put("SmsCode", code);
        para.put("Password", pwd);
        NetUtils.getDataFromServerByPost(activity, Urls.REGISTER, false, para,
                new RequestObjectCallBack<ResultBean>("register", activity, ResultBean.class) {
                    @Override
                    public void onSuccessResult(ResultBean bean) {
                        Intent intent = new Intent(activity, RegisterInfoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("phone", phone);
                        bundle.putString("pwd", pwd);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
//                        login(phone, pwd);
                    }

                    @Override
                    public void onErrorResult(String str) {
                        ToastUtils.showToast(activity, str);
                    }

                    @Override
                    public void onFail(Exception e) {

                    }
                });
    }

    public void requestMsgCode(TextView textView) {
        if (countDownTimerUtils == null) {
            countDownTimerUtils = new CountDownTimerUtils(textView);
        }
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(activity);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getPara(activity);
        para.put("Phone", phone);
        NetUtils.getDataFromServerByPost(activity, Urls.GET_MSG_CODE_FOR_REGISTER, false, para,
                new RequestObjectCallBack<String>("注册获取短信验证码", activity, String.class) {
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

    @Bindable
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        notifyPropertyChanged(BR.code);
    }

    @Bindable
    public String getPwd() {
        return pwd;
    }


    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Bindable
    public String getPwdAgain() {
        return pwdAgain;
    }

    public void setPwdAgain(String pwdAgain) {
        this.pwdAgain = pwdAgain;
    }

    @Override
    public void destory() {
        NetUtils.cancelTag("注册获取短信验证码");
    }
}
