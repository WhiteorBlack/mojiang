package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import cn.idcby.jiajubang.Bean.RequestServiceConfirm;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ImageSelectorResultAdapter;
import cn.idcby.jiajubang.databinding.ActivityServiceConfirmBinding;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.viewmodel.ServiceConfirmViewModel;

public class ServiceConfirmActivity extends BaseBindActivity {
    private ActivityServiceConfirmBinding binding;
    private ServiceConfirmViewModel viewModel;

    public static void launch(Activity context, RequestServiceConfirm details, int requestCode) {
        Intent toStIt = new Intent(context, ServiceConfirmActivity.class);
        toStIt.putExtra(SkipUtils.SERVICE_DETIALS, details);
        context.startActivityForResult(toStIt, requestCode);
    }

    @Override
    protected void initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_confirm);
        viewModel=new ServiceConfirmViewModel(this);
        binding.setViewModel(viewModel);
    }

    @Override
    public void initView() {
        super.initView();
        binding.toolbar.setTitle("预约订单");
        binding.rvPic.setLayoutManager(new GridLayoutManager(this, 3));
    }

    public void setAdapter(ImageSelectorResultAdapter imageSelectorResultAdapter) {
        binding.rvPic.setAdapter(imageSelectorResultAdapter);
    }

    @Override
    public void initListener() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void dealOhterClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }
}
