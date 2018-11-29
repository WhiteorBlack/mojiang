package cn.idcby.jiajubang.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ServiceAdapter;
import cn.idcby.jiajubang.databinding.ActivityServiceListActivityBinding;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;
import cn.idcby.jiajubang.viewmodel.ServiceListViewModel;

public class ServiceListNewActivity extends BaseBindActivity {
    private ActivityServiceListActivityBinding binding;
    private ServiceAdapter adapter;
    private ServiceListViewModel viewModel;

    @Override
    protected void initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_list_activity);
        adapter = new ServiceAdapter(R.layout.item_service);
        viewModel = new ServiceListViewModel(this, adapter);
        binding.setViewModel(viewModel);
    }

    @Override
    public void initView() {
        super.initView();
        binding.viewBar.getLayoutParams().height=ResourceUtils.getStatusBarHeight(this);
        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(adapter);
        adapter.setEnableLoadMore(true);
        binding.rvList.addOnItemTouchListener(viewModel.onItemTouchListener());
        adapter.setOnLoadMoreListener(() -> viewModel.loadMore(), binding.rvList);
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.getData();
    }

    @Override
    public void initListener() {
        binding.refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                viewModel.refresh();
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search:

                break;
            case R.id.ll_city:
                viewModel.showCityDialog(view);
                break;
            case R.id.ll_total:

                break;
            case R.id.ll_sell:

                break;
            case R.id.ll_good:

                break;
            case R.id.ll_other:

                break;
        }
    }

    @Override
    public void refreshOk() {
        super.refreshOk();
        binding.refresh.finishRefresh();
    }

    @Override
    public void refreshFail() {
        super.refreshFail();
        binding.refresh.finishRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.destory();
    }
}
