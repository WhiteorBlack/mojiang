package cn.idcby.jiajubang.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ServiceClassAdapter;
import cn.idcby.jiajubang.databinding.ActivityServicePublishCategoryBinding;
import cn.idcby.jiajubang.databinding.FooterServiceCatergoryBinding;
import cn.idcby.jiajubang.viewmodel.ServiceCategoryViewModel;

public class ServicePublishCategory extends BaseBindActivity {
    private ActivityServicePublishCategoryBinding binding;
    private ServiceClassAdapter firstAdapter, secondeAdapter;
    private ServiceCategoryViewModel viewModel;
    private FooterServiceCatergoryBinding footerBind;

    @Override
    protected void initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_publish_category);
        firstAdapter = new ServiceClassAdapter(R.layout.item_service_first);
        secondeAdapter = new ServiceClassAdapter(R.layout.item_service_type);
        viewModel = new ServiceCategoryViewModel(this, firstAdapter, secondeAdapter);
    }

    @Override
    public void initView() {
        super.initView();
        footerBind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.footer_service_catergory, null, false);
        footerBind.setContent("安装分类可以多选");
        initFirst(binding.rvFirst);
        initSecond(binding.rvSecond);
        viewModel.getFirst();
    }

    private void initSecond(RecyclerView rvSecond) {
        rvSecond.setLayoutManager(new GridLayoutManager(this, 3));
        rvSecond.setAdapter(secondeAdapter);
        rvSecond.addOnItemTouchListener(viewModel.secondListener());
        secondeAdapter.addFooterView(footerBind.getRoot());
    }

    private void initFirst(RecyclerView rvFirst) {
        rvFirst.setLayoutManager(new LinearLayoutManager(this));
        rvFirst.setAdapter(firstAdapter);
        rvFirst.addOnItemTouchListener(viewModel.firstListener());
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {
        switch (view.getId()) {
            case R.id.tv_commit:
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
