package cn.idcby.jiajubang.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.FragmentPagerAdapter;
import cn.idcby.jiajubang.interf.OnLocationRefresh;

public class NearNewFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //    private String[] titls = new String[]{"附近服务", "附近商家", "附近工作", "附近的人", "附近的闲置"};
    private List<String> titls = new ArrayList<>();
    private OnLocationRefresh mLocationRefreshListener;

    public void setLocationRefreshListener(OnLocationRefresh mLocationRefreshListener) {
        this.mLocationRefreshListener = mLocationRefreshListener;
    }

    public void setCurLocation(String locationDesc) {
//        if (locationDesc != null) {
//            mLocationTv.setText(locationDesc);
//        } else {
//            mLocationTv.setText("正在定位");
//        }
    }

    /**
     * 刷新城市更换影响的模块
     */
    public void updateCityChangeDisplay() {

    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void initView(View view) {
        tabLayout = view.findViewById(R.id.tab);
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), getFragemnts(), titls));

        View statusView = view.findViewById(R.id.frag_near_status_view);
        statusView.getLayoutParams().height = ResourceUtils.getStatusBarHeight(mContext);
        statusView.setBackgroundColor(getResources().getColor(R.color.white));
        disableTabClick();
    }

    private void disableTabClick() {
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab=tabLayout.getTabAt(i);
//
//        }
    }

    private List<Fragment> getFragemnts() {
        List<Fragment> fragments = new ArrayList<>();
        Fragment serviceFragment = new NearServerListFragment();
        titls.add("附近服务");
        fragments.add(serviceFragment);
        Fragment storeFragment = new NearStoreFragment();
        titls.add("附近商家");
        fragments.add(storeFragment);
        Fragment jobFragment = new NearJobListFragment();
        titls.add("附近工作");
        fragments.add(jobFragment);
        Fragment userFragemnt = new NearUserListFragment();
        titls.add("附近的人");
        fragments.add(userFragemnt);
        Fragment goodsListFragment = new NearGoodListFragment();
        titls.add("附近的闲置");
        fragments.add(goodsListFragment);
        return fragments;
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_near_new;
    }

    @Override
    protected void initListener() {

    }
}
