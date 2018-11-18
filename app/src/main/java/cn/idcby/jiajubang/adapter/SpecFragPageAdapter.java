package cn.idcby.jiajubang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import cn.idcby.jiajubang.fragment.SpecBaseFragment;

/**
 * Created on 2018/3/28.
 */

public class SpecFragPageAdapter<T extends SpecBaseFragment> extends FragmentPagerAdapter {

    private List<T> mFragmentList ;
    private T mCurrentFragment ;

    public T getCurrentFragment() {
        return mCurrentFragment;
    }

    public SpecFragPageAdapter(FragmentManager fm , List<T> list) {
        super(fm);
        this.mFragmentList = list ;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position) ;
    }

    @Override
    public int getCount() {
        return null == mFragmentList ? 0 : mFragmentList.size() ;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentFragment = (T) object;
        super.setPrimaryItem(container, position, object);
    }
}
