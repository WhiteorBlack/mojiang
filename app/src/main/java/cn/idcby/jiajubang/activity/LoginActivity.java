package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.LoginInfo;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.jpush.android.api.JPushInterface;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    private LoadingDialog loadingDialog;

    private EditText mPhoneEv ;
    private EditText mPassEv ;
    private TextView mBtnLogin;
    private TextView mTvRegister;
    private TextView mTvForgetPwd;
    private TextView mRegTv ;

    private boolean isGoMain = true;


    @Override
    public int getLayoutID() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        setTopBarIsImmerse(true);
        mPhoneEv = findViewById(R.id.acti_login_number_ev);
        mPassEv = findViewById(R.id.acti_login_pass_ev);
        mBtnLogin = findViewById(R.id.acti_login_sub_tv);
        mTvRegister = findViewById(R.id.acti_login_regist_tv);
        mTvForgetPwd = findViewById(R.id.acti_login_forget_tv);
        mRegTv = findViewById(R.id.acti_login_regist_tips_tv);
    }

    @Override
    public void initData() {
        //先清空别名
        JPushInterface.deleteAlias(mContext , MyApplication.getJpushSequence());

        Intent intent = getIntent();
        if (intent != null) {
            isGoMain = intent.getBooleanExtra("isGoMain", true);
        }

        if(!EasyPermissions.hasPermissions(mContext , Manifest.permission.READ_PHONE_STATE)){
            EasyPermissions.requestPermissions(LoginActivity.this,"",1000 ,Manifest.permission.READ_PHONE_STATE);
        }

        getRegistTipsAndToWeb(false) ;
    }

    @Override
    public void initListener() {
        mBtnLogin.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mTvForgetPwd.setOnClickListener(this);
        mRegTv.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        if (i == R.id.acti_login_sub_tv) {
            login();
        } else if (i == R.id.acti_login_regist_tv) {
            Intent toReIt = new Intent(mActivity, RegisterActivity.class);
            startActivityForResult(toReIt, 1002);
        } else if (i == R.id.acti_login_forget_tv) {
            ChangeLoginActivity.launch(mActivity, ChangeLoginActivity.CHANGE_TYPE_LOGIN_PASS_FORGET, 1001);
        } else if (i == R.id.acti_login_regist_tips_tv) {
            getRegistTipsAndToWeb(true) ;
        }
    }

    private void login() {
        if(!EasyPermissions.hasPermissions(mContext , Manifest.permission.READ_PHONE_STATE)){
            EasyPermissions.requestPermissions(LoginActivity.this
                    ,"登录功能需要获取设备信息权限，拒绝会导致登录失败",1000 ,Manifest.permission.READ_PHONE_STATE);
            return ;
        }

        final String phone = mPhoneEv.getText().toString().trim();
        if (!MyUtils.isRightPhone(phone)) {
            ToastUtils.showToast(mContext , "手机号有误");
            return;
        }

        final String pwd = mPassEv.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showToast(mContext , "请输入密码");
            mPassEv.requestFocus() ;
            mPassEv.setText("") ;
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
                        LoginHelper.saveUserLoginInfo(mContext ,phone ,pwd) ;
                        LoginHelper.login(mContext, bean);
                        EventBus.getDefault().post(new BusEvent.LocationUpdate(true));

                        getSelfInfo();
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
                        ToastUtils.showToast(mContext,"网络异常");
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
                            LoginHelper.saveUserInfoToLocal(mContext,bean);

                            if (isGoMain) {
                                goNextActivity(MainActivity.class);
                            }
                            setResult(RESULT_OK) ;
                            finish();
                        }else {
                            ToastUtils.showToast(mContext,"网络异常");
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
                        ToastUtils.showToast(mContext,"网络异常");

                    }
                });
    }

    /**
     * 获取用户注册协议，并且跳转
     */
    private void getRegistTipsAndToWeb(final boolean show){
        if(show){
            if (loadingDialog == null)
                loadingDialog = new LoadingDialog(mContext);
            loadingDialog.show();
        }

        Map<String,String> paramMap = ParaUtils.getAgreementTipsParam(mContext,ParaUtils.PARAM_AGREE_TIPS_USER) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.ARTICLE_DETAIL_BY_CODE, paramMap
                , new RequestObjectCallBack<NewsDetail>("getRegistTips" ,mContext ,NewsDetail.class) {
                    @Override
                    public void onSuccessResult(NewsDetail bean) {
                        if(show){
                            loadingDialog.dismiss() ;
                        }

                        if(bean != null){
                            String title = bean.getTitle() ;
                            if("".equals(title)){
                                title = "用户协议" ;
                            }
                            mRegTv.setText(String.format(getResources().getString(R.string.login_tips_def) ,title)) ;

                            if(show){
                                String contentUrl = bean.getContentH5Url() ;
                                SkipUtils.toShowWebActivity(mContext , title ,contentUrl);
                            }
                        }
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if(show){
                            loadingDialog.dismiss() ;
                        }

                    }
                    @Override
                    public void onFail(Exception e) {
                        if(show){
                            loadingDialog.dismiss() ;
                        }

                    }
                });
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtils.showToast(mContext ,"拒绝了设备权限，可能导致登录异常");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode , permissions , grantResults ,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(1002 == requestCode){
            if(RESULT_OK == resultCode){
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getRegistTips") ;

    }
}
