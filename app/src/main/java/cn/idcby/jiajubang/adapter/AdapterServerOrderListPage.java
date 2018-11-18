package cn.idcby.jiajubang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created on 2018/4/12.
 */

public class AdapterServerOrderListPage extends FragmentPagerAdapter {
    private String[] titles ;
    private List<Fragment> fragments ;

    public AdapterServerOrderListPage(FragmentManager fm,String[] titles ,List<Fragment> fragments) {
        super(fm);
        this.titles = titles ;
        this.fragments = fragments ;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position) ;
    }

    @Override
    public int getCount() {
        return null == titles ? 0 : titles.length;
    }
}
