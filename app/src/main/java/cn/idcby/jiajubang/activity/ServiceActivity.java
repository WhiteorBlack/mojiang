package cn.idcby.jiajubang.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;

import cn.idcby.commonlibrary.base.BaseBindActivity;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.ServiceAdapter;
import cn.idcby.jiajubang.adapter.ServiceClassAdapter;
import cn.idcby.jiajubang.databinding.ActivityRefreshRecyclerviewNoBarBinding;
import cn.idcby.jiajubang.databinding.ViewServiceHeaderBinding;
import cn.idcby.jiajubang.utils.BannerImageLoader;
import cn.idcby.jiajubang.utils.RecycleViewUtils;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshLayout;
import cn.idcby.jiajubang.view.refresh.MaterialRefreshListener;
import cn.idcby.jiajubang.viewmodel.ServiceHeaderViewModel;
import cn.idcby.jiajubang.viewmodel.ServiceViewModel;

public class ServiceActivity extends BaseBindActivity {
    private ViewServiceHeaderBinding viewBinding;
    private ActivityRefreshRecyclerviewNoBarBinding binding;
    private ServiceViewModel viewModel;
    private ServiceAdapter adapter;
    private ServiceHeaderViewModel headerViewModel;
    private ServiceClassAdapter classAdapter;
    private float alpha = 0f;
    private float scrollY = 0f;

    @Override
    protected void initBinding() {
        viewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_service_header, null, false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refresh_recyclerview_no_bar);
    }

    private RecycleViewUtils mRecyUtils;

    @Override
    public void initView() {
        super.initView();

        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(true).flymeOSStatusBarFontColor(R.color.black).keyboardEnable(false).init();
        adapter = new ServiceAdapter(R.layout.item_service);
        adapter.setEnableLoadMore(true);
        adapter.addHeaderView(viewBinding.getRoot());
        adapter.setOnLoadMoreListener(() -> viewModel.loadMore(), binding.rvList);
        viewModel = new ServiceViewModel(this, adapter);
        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(adapter);
        binding.rvList.addOnItemTouchListener(viewModel.onItemTouchListener());
        mRecyUtils = new RecycleViewUtils().with(binding.rvList);
        initBanner(viewBinding.banner);
        classAdapter = new ServiceClassAdapter(R.layout.item_service_class);
        headerViewModel = new ServiceHeaderViewModel(this, classAdapter, viewBinding.banner);
        viewBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        viewBinding.recyclerView.setAdapter(classAdapter);
        viewBinding.recyclerView.addOnItemTouchListener(headerViewModel.onItemTouchListener());
        viewBinding.setViewModel(headerViewModel);

        binding.tvTitle.setText("服务");
        binding.tvTitle.setVisibility(View.INVISIBLE);
        binding.view.getLayoutParams().height = ResourceUtils.getStatusBarHeight(this);
        binding.rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollY = mRecyUtils.getScrollY()*1.0f;

                alpha = scrollY / viewBinding.banner.getHeight();
                if (alpha>1){
                    alpha=1;
                }

                if (alpha > 0.5) {
                    binding.tvTitle.setVisibility(View.VISIBLE);
                } else {
                    binding.tvTitle.setVisibility(View.INVISIBLE);
                }
                binding.view.setAlpha(alpha);
                binding.viewBar.setAlpha(alpha);
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.getData();
        headerViewModel.getData();
    }

    private void initBanner(final Banner mBanner) {
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置轮播时间
        mBanner.setDelayTime(5000);
        mBanner.setImageLoader(new BannerImageLoader());

    }

    @Override
    public void refreshFail() {
        super.refreshFail();
        binding.refresh.finishRefresh();
    }

    @Override
    public void refreshOk() {
        super.refreshOk();
        binding.refresh.finishRefresh();
    }

    @Override
    public void initListener() {
        binding.refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                viewModel.refresh();
                headerViewModel.getData();
            }
        });
    }

    @Override
    public void dealOhterClick(View view) {
        switch (view.getId()) {
            case R.id.iv_message:

                break;
            case R.id.tv_publish:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.destory();
        headerViewModel.destory();
    }
}
