package cn.idcby.jiajubang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created on 2018/6/7.
 */

public class AdapterMyReceiveOrderListPage extends FragmentPagerAdapter {
    private String[] titles ;
    private List<Fragment> fragmentList ;

    public AdapterMyReceiveOrderListPage(FragmentManager fm, String[] titles,List<Fragment> fragmentList) {
        super(fm);
        this.titles = titles ;
        this.fragmentList = fragmentList ;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position) ;
    }

    @Override
    public int getCount() {
        return null == titles ? 0 : titles.length;
    }
}