package cn.idcby.jiajubang.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

/**
 * Created by Slingge on 2017/4/22 0022.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private List<Fragment> fragmentLst;
    private List<String> list_Title;


    public FragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentLst, List<String> list_Title) {
        super(fm);
        this.fragmentLst = fragmentLst;
        this.list_Title = list_Title;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (list_Title == null || list_Title.size() == 0) {
            return "";
        }
        return list_Title.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentLst.get(position);
    }

    @Override
    public int getCount() {
        return fragmentLst.size();
    }
}
