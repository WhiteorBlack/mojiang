package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.BitmapToBase64Utils;
import cn.idcby.commonlibrary.utils.MyUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.PhotoCode;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.utils.CountDownTimerUtils;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 修改：手机号、登录密码、支付密码
 * Created on 2018/4/16.
 */

public class ChangeLoginActivity extends BaseActivity {
    public static final int CHANGE_TYPE_PHONE = 1;
    public static final int CHANGE_TYPE_LOGIN_PASS = 2;
    public static final int CHANGE_TYPE_PAY_PASS = 3;
    public static final int SET_TYPE_PAY_PASS = 4;
    public static final int CHANGE_TYPE_LOGIN_PASS_FORGET = 5;

    private EditText mPhoneEt;
    private EditText mImageCodeEv;
    private ImageView mImageCodeIv;
    private EditText mPhoneCodeEv;
    private TextView mGetCodeTv;
    private EditText mContentEv;
    private EditText mCheckEv;


    private int mChangeType = 1;

    private String photoCodeId = "";
    private CountDownTimerUtils countDownTimerUtils;
    private LoadingDialog mDialog;

    public static void launch(Activity context, int type, int requestCode) {
        Intent toCiIt = new Intent(context, ChangeLoginActivity.class);
        toCiIt.putExtra(SkipUtils.INTENT_CHANGE_PHONE_TYPE, type);
        context.startActivityForResult(toCiIt, requestCode);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_change_login_info;
    }

    @Override
    public void initView() {
        mChangeType = getIntent().getIntExtra(SkipUtils.INTENT_CHANGE_PHONE_TYPE, CHANGE_TYPE_PHONE);

        TextView mTitleTv = findViewById(R.id.acti_change_login_info_title_tv);
        mPhoneEt = findViewById(R.id.acti_change_login_info_phone_ev);
        mImageCodeEv = findViewById(R.id.acti_change_login_info_img_code_ev);
        mImageCodeIv = findViewById(R.id.acti_change_login_info_img_code_iv);
        mPhoneCodeEv = findViewById(R.id.acti_change_login_info_phone_code_ev);
        mGetCodeTv = findViewById(R.id.acti_change_login_info_phone_code_tv);
        mContentEv = findViewById(R.id.acti_change_login_info_content_ev);
        mCheckEv = findViewById(R.id.acti_change_login_info_check_ev);
        TextView mSubmitTv = findViewById(R.id.acti_change_login_info_submit_tv);

        mPhoneEt.setText(SPUtils.newIntance(mContext).getUserAccount());

        String title = "";
        String contentHint = "";
        String checkHint = "";
        if (CHANGE_TYPE_PHONE == mChangeType) {//修改手机号
            title = "修改手机号";
            contentHint = "请输入新手机号";
            checkHint = "确认新手机号";

            mContentEv.setInputType(InputType.TYPE_CLASS_NUMBER);
            mCheckEv.setInputType(InputType.TYPE_CLASS_NUMBER);
            InputFilter[] filters = {new InputFilter.LengthFilter(11)};
            mContentEv.setFilters(filters);
            mCheckEv.setFilters(filters);

            tvshowbystyle(true);
        } else if (CHANGE_TYPE_LOGIN_PASS == mChangeType || CHANGE_TYPE_LOGIN_PASS_FORGET == mChangeType) {
            if(CHANGE_TYPE_LOGIN_PASS_FORGET == mChangeType){
                title = "找回密码";
                tvshowbystyle(false);
            }else{
                title = "修改登录密码";
                tvshowbystyle(true);
            }

            contentHint = "请输入新密码";
            checkHint = "确认新密码";
            mContentEv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mCheckEv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else if (SET_TYPE_PAY_PASS == mChangeType || CHANGE_TYPE_PAY_PASS == mChangeType) {
            if(SET_TYPE_PAY_PASS == mChangeType){
                title = "设置支付密码";
            }else{
                title = "修改支付密码";
            }

            contentHint = "请输入新密码";
            checkHint = "确认新密码";
            mContentEv.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mCheckEv.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

            InputFilter[] filters = {new InputFilter.LengthFilter(6)};
            mContentEv.setFilters(filters);
            mCheckEv.setFilters(filters);
            tvshowbystyle(true);
        }
        mTitleTv.setText(title);
        mContentEv.setHint(contentHint);
        mCheckEv.setHint(checkHint);

        mImageCodeIv.setOnClickListener(this);
        mGetCodeTv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);

        countDownTimerUtils = new CountDownTimerUtils(mGetCodeTv);
    }

    private void tvshowbystyle(boolean istv) {
        mPhoneEt.setEnabled(!istv);
    }

    @Override
    public void initData() {
        mDialog = new LoadingDialog(mContext);
        mDialog.show();

        requestPhotoCode();
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId();

        if (R.id.acti_change_login_info_img_code_iv == vId) {
            requestPhotoCode();
        } else if (R.id.acti_change_login_info_phone_code_tv == vId) {
            requestMsgCode();
        } else if (R.id.acti_change_login_info_submit_tv == vId) {
            requestSubmit();
        }

    }

    /**
     * 获取图形验证码
     */
    private void requestPhotoCode() {
        NetUtils.getDataFromServerByGet(mContext, Urls.GET_PHOTO_CODE,
                new RequestObjectCallBack<PhotoCode>("注册获取图像验证码", mContext, PhotoCode.class) {
                    @Override
                    public void onSuccessResult(PhotoCode bean) {
                        mDialog.dismiss();

                        photoCodeId = bean.imageID;
                        Glide.with(MyApplication.getInstance())
                                .load(BitmapToBase64Utils.base64ToBitmap(bean.base64String))
                                .apply(new RequestOptions()
                                        .placeholder(R.mipmap.default_icon)
                                        .error(R.mipmap.default_icon))
                                .into(mImageCodeIv);
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();

                        Glide.with(MyApplication.getInstance())
                                .load(R.mipmap.default_icon)
                                .apply(new RequestOptions()
                                        .placeholder(R.mipmap.default_icon)
                                        .error(R.mipmap.default_icon))
                                .into(mImageCodeIv);
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        Glide.with(MyApplication.getInstance())
                                .load(R.mipmap.default_icon)
                                .apply(new RequestOptions()
                                        .placeholder(R.mipmap.default_icon)
                                        .error(R.mipmap.default_icon))
                                .into(mImageCodeIv);
                    }
                });
    }


    /**
     * 获取手机验证码
     */
    private void requestMsgCode() {
        String phone = mPhoneEt.getText().toString();
        if (CHANGE_TYPE_LOGIN_PASS_FORGET == mChangeType && !MyUtils.isRightPhone(phone.trim())){
            ToastUtils.showToast(mContext ,"请输入正确的手机号");
            mPhoneEt.requestFocus() ;
            mPhoneEt.setSelection(phone.length());
            return;
        }

        String photoCode = mImageCodeEv.getText().toString();
        if (TextUtils.isEmpty(photoCode.trim())) {
            ToastUtils.showToast(mContext, "请输入图形验证码");
            mImageCodeEv.setText("");
            return;
        }

        mDialog.show();
        Map<String, String> para ;
        if (CHANGE_TYPE_LOGIN_PASS == mChangeType
                || CHANGE_TYPE_LOGIN_PASS_FORGET == mChangeType){
            para = ParaUtils.getPara(mContext);
            para.put("Phone", phone);
            para.put("imageID", photoCodeId);
            para.put("imageCode", photoCode);
        }else {
            para =  ParaUtils.getParaWithToken(mContext);
            para.put("Phone", SPUtils.newIntance(mContext).getUserAccount());
            para.put("imageID", photoCodeId);
            para.put("imageCode", photoCode);
        }

        String url = Urls.GET_MSG_CODE_FOR_CHANGE_PHONE;
        if (CHANGE_TYPE_PHONE == mChangeType) {
            url = Urls.GET_MSG_CODE_FOR_CHANGE_PHONE;
        } else if (CHANGE_TYPE_LOGIN_PASS == mChangeType
                || CHANGE_TYPE_LOGIN_PASS_FORGET == mChangeType) {
            url = Urls.GET_MSG_CODE_FOR_CHANGE_LOGIN;
        } else if (CHANGE_TYPE_PAY_PASS == mChangeType || SET_TYPE_PAY_PASS == mChangeType) {
            url = Urls.GET_MSG_CODE_FOR_CHANGE_PAY;
        }

        NetUtils.getDataFromServerByPost(mContext, url, false, para,
                new RequestObjectCallBack<String>("注册获取短信验证码", mContext, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();
                        ToastUtils.showToast(mContext, "验证码发送成功");
                        countDownTimerUtils.start();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();

                        ToastUtils.showToast(mContext, "验证码发送失败");
                    }
                });
    }

    /**
     * 提交
     */
    private void requestSubmit() {
        String phoneup = "";
        if (CHANGE_TYPE_LOGIN_PASS == mChangeType
                || CHANGE_TYPE_LOGIN_PASS_FORGET == mChangeType){
            phoneup = mPhoneEt.getText().toString();
            if (!MyUtils.isRightPhone(phoneup.trim())) {
                ToastUtils.showToast(mContext ,"请输入正确的手机号");
                mPhoneEt.requestFocus() ;
                mPhoneEt.setSelection(phoneup.length());
                return;
            }
        }

        String photoCode = mImageCodeEv.getText().toString().trim();
        if (TextUtils.isEmpty(photoCode)) {
            ToastUtils.showToast(mContext, "请输入验证码");
            mImageCodeEv.requestFocus();
            mImageCodeEv.setText("");
            return;
        }

        String msgCode = mPhoneCodeEv.getText().toString().trim();
        if (TextUtils.isEmpty(msgCode)) {
            ToastUtils.showToast(mContext, "请输入手机验证码");
            mPhoneCodeEv.requestFocus();
            mPhoneCodeEv.setText("");
            return;
        }

        String content = mContentEv.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            if (CHANGE_TYPE_PHONE == mChangeType) {
                ToastUtils.showToast(mContext, "请输入新手机号");
            } else {
                ToastUtils.showToast(mContext, "请输入新密码");
            }

            mContentEv.requestFocus();
            mContentEv.setText("");
            return;
        }

        String pwdAgain = mCheckEv.getText().toString().trim();
        if (!content.equals(pwdAgain)) {
            ToastUtils.showToast(mContext, "两次输入不一致");
            mCheckEv.requestFocus();
            mCheckEv.setText("");
            return;
        }

        mDialog.show();

        Map<String, String> paramMap ;
        if (CHANGE_TYPE_LOGIN_PASS == mChangeType
                || CHANGE_TYPE_LOGIN_PASS_FORGET == mChangeType){
            paramMap =ParaUtils.getPara(mContext);
            paramMap.put("Phone", phoneup);
            paramMap.put("Password", content);
            paramMap.put("SmsCode", msgCode);
        }else if(CHANGE_TYPE_PHONE == mChangeType){//修改登录帐号
            paramMap = ParaUtils.getParaWithToken(mContext);
            paramMap.put("Phone", content);//新手机
            paramMap.put("Password", "");//可以为空
            paramMap.put("SmsCode", msgCode);
        }else {
            paramMap = ParaUtils.getParaWithToken(mContext);
            paramMap.put("Phone", SPUtils.newIntance(mContext).getUserAccount());
            paramMap.put("Password", content);
            paramMap.put("SmsCode", msgCode);
        }


        String url = Urls.CHANGE_LOGIN_NUMBER;
        if (CHANGE_TYPE_PHONE == mChangeType) {
            url = Urls.CHANGE_LOGIN_NUMBER;
        } else if (CHANGE_TYPE_LOGIN_PASS == mChangeType
                || CHANGE_TYPE_LOGIN_PASS_FORGET == mChangeType) {
            url = Urls.CHANGE_LOGIN_PASSWORD;
        } else if (CHANGE_TYPE_PAY_PASS == mChangeType
                || SET_TYPE_PAY_PASS == mChangeType) {
            url = Urls.CHANGE_PAY_PASSWORD;
        }

        NetUtils.getDataFromServerByPost(mContext, url, false, paramMap,
                new RequestObjectCallBack<String>("注册", mContext, String.class) {
                    @Override
                    public void onSuccessResult(String bean) {
                        mDialog.dismiss();
                        if (CHANGE_TYPE_PAY_PASS == mChangeType || SET_TYPE_PAY_PASS == mChangeType) {
                            SPUtils.newIntance(mContext).savePayPassInfos(1);
                        }

                        ToastUtils.showOkToast(mContext, "修改成功");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                    }
                });
    }

}
