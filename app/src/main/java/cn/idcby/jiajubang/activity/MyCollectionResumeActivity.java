package cn.idcby.jiajubang.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.IndicatorFragmentAdapter;
import cn.idcby.jiajubang.adapter.MyGreenIndicatorAdapter;
import cn.idcby.jiajubang.fragment.MyCollectionJobFragment;
import cn.idcby.jiajubang.fragment.MyCollectionResumeFragment;

/**
 * Created on 2018/3/28.
 */

public class MyCollectionResumeActivity extends BaseActivity {
    private String[] titles = {"职位", "简历"};
    private MyGreenIndicatorAdapter myIndicatorAdapter;
    private IndicatorFragmentAdapter mIndFragAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>(2) ;

    private MagicIndicator mMagicIndicator;
    private ViewPager mViewPager;


    @Override
    public int getLayoutID() {
        return R.layout.activity_my_collection_resume;
    }

    @Override
    public void initView() {
        mMagicIndicator = findViewById(R.id.acti_my_collection_resume_indicator);
        mViewPager = findViewById(R.id.acti_my_collection_resume_vp);
    }

    @Override
    public void initData() {
        myIndicatorAdapter = new MyGreenIndicatorAdapter(titles, mViewPager);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(myIndicatorAdapter);
        mMagicIndicator.setNavigator(commonNavigator);

        MyCollectionJobFragment fragCollectionJob = new MyCollectionJobFragment();
        MyCollectionResumeFragment fragCollectionResume = new MyCollectionResumeFragment();
        mFragmentList.add(fragCollectionJob);
        mFragmentList.add(fragCollectionResume);

        mIndFragAdapter = new IndicatorFragmentAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mIndFragAdapter);
        mViewPager.setOffscreenPageLimit(2) ;
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
    }
}
