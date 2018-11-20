package cn.idcby.jiajubang.activity;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.NewsDetail;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 登录新页面
 */
public class LoginActivityNew extends BaseActivity {
    private EditText etPhone;
    private LoadingDialog loadingDialog;

    @Override
    public int getLayoutID() {
        return R.layout.activity_login_new;
    }

    @Override
    public void initView() {
        super.initView();
        etPhone = findViewById(R.id.acti_login_number_ev);

    }

    @Override
    public void initListener() {
        findViewById(R.id.acti_login_regist_tips_tv).setOnClickListener(this);
        findViewById(R.id.acti_login_sub_tv).setOnClickListener(this);
        findViewById(R.id.iv_weibo).setOnClickListener(this);
        findViewById(R.id.iv_wechat).setOnClickListener(this);
    }

    @Override
    public void dealOhterClick(View view) {
        int i = view.getId();
        if (i == R.id.acti_login_sub_tv) { //登录
            String phone = etPhone.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                ToastUtils.showToast(LoginActivityNew.this, "请输入手机号码");
                return;
            }
        } else if (i == R.id.acti_login_regist_tips_tv) {
            getRegistTipsAndToWeb(true);
        } else if (i == R.id.iv_wechat) {//微信登录

        } else if (i == R.id.iv_weibo) {//微博登录

        }
    }

    /**
     * 获取用户注册协议，并且跳转
     */
    private void getRegistTipsAndToWeb(final boolean show) {
        if (show) {
            if (loadingDialog == null)
                loadingDialog = new LoadingDialog(mContext);
            loadingDialog.show();
        }

        Map<String, String> paramMap = ParaUtils.getAgreementTipsParam(mContext, ParaUtils.PARAM_AGREE_TIPS_USER);
        NetUtils.getDataFromServerByPost(mContext, Urls.ARTICLE_DETAIL_BY_CODE, paramMap
                , new RequestObjectCallBack<NewsDetail>("getRegistTips", mContext, NewsDetail.class) {
                    @Override
                    public void onSuccessResult(NewsDetail bean) {
                        if (show) {
                            loadingDialog.dismiss();
                        }

                        if (bean != null) {
                            String title = bean.getTitle();
                            if ("".equals(title)) {
                                title = "用户协议";
                            }

                            if (show) {
                                String contentUrl = bean.getContentH5Url();
                                SkipUtils.toShowWebActivity(mContext, title, contentUrl);
                            }
                        }
                    }

                    @Override
                    public void onErrorResult(String str) {
                        if (show) {
                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        if (show) {
                            loadingDialog.dismiss();
                        }

                    }
                });
    }
}
