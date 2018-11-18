package cn.idcby.jiajubang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 我的发布
 * Created on 2018/4/16.
 */

public class AdapterMySendPage extends FragmentPagerAdapter {
    private String[] titles ;
    private List<Fragment> mFragmentList ;

    public AdapterMySendPage(FragmentManager fm, String[] titles ,List<Fragment> fragmentList) {
        super(fm);
        this.titles = titles ;
        this.mFragmentList = fragmentList ;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position) ;
    }

    @Override
    public int getCount() {
        return null == titles ? 0 : titles.length;
    }
}
