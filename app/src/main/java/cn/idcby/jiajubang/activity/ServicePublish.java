package cn.idcby.jiajubang.activity;

import android.databinding.DataBindingUtil;
import android.view.View;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.databinding.ActivityServicePublishBinding;

public class ServicePublish extends BaseBindActivity {
    private ActivityServicePublishBinding binding;

    @Override
    protected void initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_publish);
    }

    @Override
    public void initView() {
        super.initView();
        binding.toolbar.setTitle("完善服务安装信息");
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {

    }
}
