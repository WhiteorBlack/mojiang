package cn.idcby.jiajubang.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created on 2018/3/28.
 */

public class PageAdapterStoreGood extends FragmentPagerAdapter {
    private String[] titles = {"推荐商品" ,"全部商品" ,"店铺资料"} ;
    private List<Fragment> mFragmentList ;

    public PageAdapterStoreGood(FragmentManager fm , List<Fragment> list) {
        super(fm);
        this.mFragmentList = list ;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position] ;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position) ;
    }

    @Override
    public int getCount() {
        return null == mFragmentList ? 0 : mFragmentList.size() ;
    }

}
