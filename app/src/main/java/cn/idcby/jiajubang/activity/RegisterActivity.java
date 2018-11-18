package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseMoreStatusActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.BitmapToBase64Utils;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.LoginInfo;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.Bean.PhotoCode;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.CountDownTimerUtils;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 注册
 *
 * 2018-07-06 16:17:15
 * 隐藏注册码
 */
public class RegisterActivity extends BaseMoreStatusActivity {

    private String photoCodeId = "";
    private CountDownTimerUtils countDownTimerUtils;
    private LoadingDialog loadingDialog;

    private TextInputLayout mTilPhone;
    private TextInputEditText mTiePhone;
    private TextInputLayout mTilPhotoCode;
    private TextInputEditText mTiePhotoCode;
    private TextInputLayout mTilMsgCode;
    private TextInputEditText mTieMsgCode;
    private TextInputLayout mTilPwd;
    private TextInputEditText mTiePwd;
    private TextInputLayout mTilPwdAgain;
    private TextInputEditText mTiePwdAgain;
    private TextInputEditText mTieRecommendEv;

    private ImageView mImgPhotoCode;
    private TextView mTvGetMsgCode;
    private View mBtnRegister;
    private ImageView mAgreeCheckIv ;
    private TextView mRegTv ;
    private boolean mIsAgree = true ;//是否同意

    @Override
    public void requestData() {
        showSuccessPage();
        requestPhotoCode();
        getRegistTipsAndToWeb(false) ;
    }


    @Override
    public int getSuccessViewId() {
        return R.layout.activity_register;
    }

    @Override
    public String setTitle() {
        return "注册账号";
    }

    @Override
    public void initTopBar(TextView tvRight, ImageView imgRight) {

    }

    @Override
    public void init() {
        initView();
        initListenner();
    }


    private void initView() {
        loadingDialog = new LoadingDialog(mContext) ;

        mTilPhone = findViewById(R.id.til_phone);
        mTiePhone = findViewById(R.id.tie_phone);
        mTilPhotoCode = findViewById(R.id.til_photo_code);
        mTiePhotoCode = findViewById(R.id.tie_photo_code);
        mTilMsgCode = findViewById(R.id.til_msg_code);
        mTieMsgCode = findViewById(R.id.tie_msg_code);
        mTilPwd = findViewById(R.id.til_pwd);
        mTiePwd = findViewById(R.id.tie_pwd);
        mTilPwdAgain = findViewById(R.id.til_pwd_again);
        mTiePwdAgain = findViewById(R.id.tie_pwd_again);
        mTieRecommendEv = findViewById(R.id.tie_recommend_code);

        mImgPhotoCode = findViewById(R.id.img_photo_code);
        mTvGetMsgCode = findViewById(R.id.tv_get_msg_code);
        mBtnRegister = findViewById(R.id.btn_register);
        countDownTimerUtils = new CountDownTimerUtils(mTvGetMsgCode);

        mRegTv = findViewById(R.id.acti_register_regis_tip_tv) ;
        mAgreeCheckIv = findViewById(R.id.acti_register_regis_tip_iv) ;
        mRegTv.setOnClickListener(this);
        mAgreeCheckIv.setOnClickListener(this) ;
    }

    private void initListenner() {
        mImgPhotoCode.setOnClickListener(this);
        mTvGetMsgCode.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        switch (view.getId()) {
            case R.id.img_photo_code:
                requestPhotoCode();
                break;
            case R.id.tv_get_msg_code:
                requestMsgCode();
                break;
            case R.id.btn_register:
                requestRegister();
                break;
            case R.id.acti_register_regis_tip_iv:
                mIsAgree = !mIsAgree ;
                changeItemStyle() ;
                break;
            case R.id.acti_register_regis_tip_tv:
                getRegistTipsAndToWeb(true) ;
                break;
        }
    }


    /**
     * 根据sendType改变填写内容
     */
    private void changeItemStyle(){
        mAgreeCheckIv.setImageDrawable(getResources().getDrawable(mIsAgree
                ? R.mipmap.ic_check_checked_blue
                : R.mipmap.ic_check_nomal));
    }

    private void requestRegister() {
        if(!mIsAgree){
            ToastUtils.showToast(mContext ,"请先同意用户注册协议");
            return ;
        }

        final String phone = mTiePhone.getText().toString().trim();
        if (!MyUtils.isRightPhone(phone)) {
            mTilPhone.setError("请输入正确的手机号");
            return;
        } else {
            mTilPhone.setErrorEnabled(false);
        }
        String photoCode = mTiePhotoCode.getText().toString().trim();
        if (TextUtils.isEmpty(photoCode)) {
            mTilPhotoCode.setError("请输入验证码");
            return;
        } else {
            mTilPhotoCode.setErrorEnabled(false);
        }

        String msgCode = mTieMsgCode.getText().toString().trim();
        if (TextUtils.isEmpty(msgCode)) {
            mTilMsgCode.setError("请输入手机验证码");
            return;
        } else {
            mTilMsgCode.setErrorEnabled(false);
        }

        final String pwd = mTiePwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            mTilPwd.setError("请输入密码");
            return;
        } else {
            mTilPwd.setErrorEnabled(false);
        }
        String pwdAgain = mTiePwdAgain.getText().toString().trim();
        if (TextUtils.isEmpty(pwdAgain)) {
            mTilPwdAgain.setError("请输入确认密码");
            return;
        } else {
            if (!pwd.equals(pwdAgain)) {
                mTilPwdAgain.setError("两次输入密码不一致");
                return;
            } else {
                mTilPwdAgain.setErrorEnabled(false);
            }

        }

        String recommendCode = mTieRecommendEv.getText().toString().trim() ;

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Phone", phone);
        para.put("Password", pwd);
        para.put("SmsCode", msgCode);
        para.put("ReferralCode", recommendCode);
        NetUtils.getDataFromServerByPost(mContext, Urls.REGISTER, false, para,
                new RequestObjectCallBack<String>("注册", mContext, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        login(phone,pwd) ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onFail(Exception e) {
                        loadingDialog.dismiss();
                    }
                });

    }

    private void login(String phone ,String pwd) {
        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("UserAccount", phone);
        para.put("Password", pwd);
        NetUtils.getDataFromServerByPost(mContext, Urls.LOGIN, true, para,
                new RequestObjectCallBack<LoginInfo>("登录", mContext, LoginInfo.class) {
                    @Override
                    public void onSuccessResult(LoginInfo bean) {
                        LoginHelper.login(mContext, bean);
//                        getSelfInfo();

                        Intent toSiIt = new Intent(mContext ,UserInfoActivity.class) ;
                        startActivity(toSiIt);
                        setResult(RESULT_OK);
                        finish() ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        finish();
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
                            setResult(RESULT_OK) ;
                        }
                        finish();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        finish();
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        finish();
                    }
                });
    }

    private void requestMsgCode() {
        String phone = mTiePhone.getText().toString().trim();
        if (!MyUtils.isRightPhone(phone)) {
            mTilPhone.setError("请输入正确的手机号");
            return;
        } else {
            mTilPhone.setErrorEnabled(false);
        }
        String photoCode = mTiePhotoCode.getText().toString().trim();
        if (TextUtils.isEmpty(photoCode)) {
            mTilPhotoCode.setError("请输入验证码");
            return;
        } else {
            mTilPhotoCode.setErrorEnabled(false);
        }

        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(mContext);
        loadingDialog.show();

        Map<String, String> para = ParaUtils.getPara(mContext);
        para.put("Phone", phone);
        para.put("imageID", photoCodeId);
        para.put("imageCode", photoCode);
        NetUtils.getDataFromServerByPost(mContext, Urls.GET_MSG_CODE_FOR_REGISTER, false, para,
                new RequestObjectCallBack<String>("注册获取短信验证码", mContext, String.class) {
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

    private void requestPhotoCode() {

        NetUtils.getDataFromServerByGet(mContext, Urls.GET_PHOTO_CODE,
                new RequestObjectCallBack<PhotoCode>("注册获取图像验证码", mContext, PhotoCode.class) {
                    @Override
                    public void onSuccessResult(PhotoCode bean) {
                        photoCodeId = bean.imageID;
                        Glide.with(MyApplication.getInstance())
                                .load(BitmapToBase64Utils.base64ToBitmap(bean.base64String))
                                .apply(new RequestOptions()
                                        .placeholder(R.mipmap.default_icon)
                                        .error(R.mipmap.default_icon))
                                .into(mImgPhotoCode) ;
                    }

                    @Override
                    public void onErrorResult(String str) {
                        Glide.with(MyApplication.getInstance())
                                .load(R.mipmap.default_icon)
                                .apply(new RequestOptions()
                                        .placeholder(R.mipmap.default_icon)
                                        .error(R.mipmap.default_icon))
                                .into(mImgPhotoCode) ;
                    }

                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }


    /**
     * 获取用户注册协议，并且跳转
     */
    private void getRegistTipsAndToWeb(final boolean show){
        if(show){
            loadingDialog.setLoadingText("");
            loadingDialog.show() ;
        }

        Map<String,String> paramMap = ParaUtils.getAgreementTipsParam(mContext,ParaUtils.PARAM_AGREE_TIPS_REGIST) ;
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
                            mRegTv.setText(String.format(getResources().getString(R.string.regist_tips_def) ,title)) ;

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


}
