package cn.idcby.jiajubang.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.idcby.jiajubang.fragment.CategoryGoodFragment;

/**
 * 分类下的商品 pagerAdapter
 */

public class PageAdapterCategoryGood extends FragmentPagerAdapter {
    private List<String> titles ;
    private List<CategoryGoodFragment> mFragmentList ;

    public PageAdapterCategoryGood(FragmentManager fm , List<String> titles, List<CategoryGoodFragment> list) {
        super(fm);
        this.titles = titles ;
        this.mFragmentList = list ;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position) ;
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
