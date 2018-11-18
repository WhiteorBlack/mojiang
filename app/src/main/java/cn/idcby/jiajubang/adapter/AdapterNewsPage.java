package cn.idcby.jiajubang.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.jiajubang.fragment.FragmentNeeds;

/**
 * Created on 2018/3/28.
 */

public class AdapterNewsPage extends SpecFragPageAdapter<FragmentNeeds> {
    private List<String> mTitleList = new ArrayList<>() ;

    public AdapterNewsPage(FragmentManager fm, List<FragmentNeeds> list , List<String> titleList) {
        super(fm, list);
        this.mTitleList = titleList ;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(mTitleList.size() > position){
            return mTitleList.get(position) ;
        }

        return super.getPageTitle(position);
    }
}
