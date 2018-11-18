package cn.idcby.jiajubang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.jiajubang.fragment.FragmentQuestion;

/**
 */

public class IndicatorQuestionAdapter extends SpecFragPageAdapter {

    private List<FragmentQuestion> fragmentList = new ArrayList<>();

    public IndicatorQuestionAdapter(FragmentManager fm, List<FragmentQuestion> fragmentList) {
        super(fm,fragmentList);
        this.fragmentList = fragmentList;
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
