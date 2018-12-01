package cn.idcby.jiajubang.activity;

import android.databinding.DataBindingUtil;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.databinding.ActivityRegisterNewBinding;
import cn.idcby.jiajubang.viewmodel.RegisterViewModel;

public class RegisterNewActivity extends BaseBindActivity {
    private ActivityRegisterNewBinding binding;
    private RegisterViewModel viewModel;

    @Override
    protected void initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_new);
        viewModel = new RegisterViewModel(this);
        binding.setViewModel(viewModel);
    }

    @Override
    public void initView() {
        super.initView();
        binding.chbWatch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.actiLoginNumberEv.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    binding.actiLoginPwdTwo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        switch (view.getId()) {
            case R.id.tv_get_msg_code:
                viewModel.requestMsgCode(binding.tvGetMsgCode);
                break;
            case R.id.acti_login_sub_tv:
                viewModel.commitData();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.destory();
    }
}
