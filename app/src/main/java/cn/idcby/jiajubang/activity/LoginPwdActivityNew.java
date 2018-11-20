package cn.idcby.jiajubang.activity;


import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class LoginPwdActivityNew extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private EditText etPhone;
    private LoadingDialog loadingDialog;

    @Override
    public int getLayoutID() {
        return R.layout.activity_login_pwd;
    }

    @Override
    public void initView() {
        super.initView();
        etPhone = findViewById(R.id.acti_login_number_ev);

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

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {

        }
    }
}
