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
import cn.idcby.jiajubang.fragment.MyCollectionGoodFragment;
import cn.idcby.jiajubang.fragment.MyCollectionJobsFragment;
import cn.idcby.jiajubang.fragment.MyCollectionNeedsFragment;
import cn.idcby.jiajubang.fragment.MyCollectionNewsFragment;
import cn.idcby.jiajubang.fragment.MyCollectionUnuseFragment;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/3/28.
 *
 * 2018-05-24 14:29:07
 * 改：去掉服务、安装
 */

public class MyCollectionActivity extends BaseActivity {
    private String[] titles = {"需求", "资讯", "招聘", "闲置", "商品"};
    private MyGreenIndicatorAdapter myIndicatorAdapter;
    private IndicatorFragmentAdapter mIndFragAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>(titles.length) ;

    private MagicIndicator mMagicIndicator;
    private ViewPager mViewPager;


    @Override
    public int getLayoutID() {
        return R.layout.activity_my_collection;
    }

    @Override
    public void initView() {
        mMagicIndicator = findViewById(R.id.acti_my_collection_indicator);
        mViewPager = findViewById(R.id.acti_my_collection_vp);
    }

    @Override
    public void initData() {
        myIndicatorAdapter = new MyGreenIndicatorAdapter(titles, mViewPager);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(myIndicatorAdapter);
        mMagicIndicator.setNavigator(commonNavigator);
//
//        MyCollectionServiceFragment fragCollectionService = MyCollectionServiceFragment
//                .getInstance(SkipUtils.COLLECTION_TYPE_SERVICE);
//        mFragmentList.add(fragCollectionService);
//
//        MyCollectionServiceFragment fragCollectionInstallService = MyCollectionServiceFragment
//                .getInstance(SkipUtils.COLLECTION_TYPE_INSTALL);
//        mFragmentList.add(fragCollectionInstallService);

        MyCollectionNeedsFragment fragCollectionNeeds = MyCollectionNeedsFragment
                .getInstance(SkipUtils.COLLECTION_TYPE_NEEDS);
        mFragmentList.add(fragCollectionNeeds);

        MyCollectionNewsFragment fragCollectionNews = MyCollectionNewsFragment
                .getInstance(SkipUtils.COLLECTION_TYPE_NEWS);
        mFragmentList.add(fragCollectionNews);
        MyCollectionJobsFragment fragCollectionJobs = MyCollectionJobsFragment
                .getInstance(SkipUtils.COLLECTION_TYPE_JOBS);
        mFragmentList.add(fragCollectionJobs);

        MyCollectionUnuseFragment fragCollectionUnuse = MyCollectionUnuseFragment
                .getInstance(SkipUtils.COLLECTION_TYPE_USED);
        mFragmentList.add(fragCollectionUnuse);

        MyCollectionGoodFragment fragCollectionGood = MyCollectionGoodFragment
                .getInstance(SkipUtils.COLLECTION_TYPE_GOOD);
        mFragmentList.add(fragCollectionGood);

        mIndFragAdapter = new IndicatorFragmentAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mIndFragAdapter);
        mViewPager.setOffscreenPageLimit(titles.length) ;
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
    }
}
