package cn.idcby.jiajubang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.jiajubang.fragment.FragmentNews;

/**
 */

public class IndicatorNewsAdapter extends SpecFragPageAdapter {

    private List<FragmentNews> fragmentList = new ArrayList<>();

    public IndicatorNewsAdapter(FragmentManager fm, List<FragmentNews> fragmentList) {
        super(fm,fragmentList);
        this.fragmentList = fragmentList;
        LogUtils.showLog("fragmentList>>>"+fragmentList.size());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
