package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.List;

import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.view.ColorFlipPagerTitleView;


/**
 * //                                  _oo8oo_
 * //                                 o8888888o
 * //                                 88" . "88
 * //                                 (| -_- |)
 * //                                 0\  =  /0
 * //                               ___/'==='\___
 * //                             .' \\|     |// '.
 * //                            / \\|||  :  |||// \
 * //                           / _||||| -:- |||||_ \
 * //                          |   | \\\  -  /// |   |
 * //                          | \_|  ''\---/''  |_/ |
 * //                          \  .-\__  '-'  __/-.  /
 * //                        ___'. .'  /--.--\  '. .'___
 * //                     ."" '<  '.___\_<|>_/___.'  >' "".
 * //                    | | :  `- \`.:`\ _ /`:.`/ -`  : | |
 * //                    \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //                =====`-.____`.___ \_____/ ___.`____.-`=====
 * //                                  `=---=`
 * //
 * //
 * //               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * //
 * //                          佛祖保佑         永不宕机/永无bug
 * Created by mrrlb on 2017/4/19.
 */

public class MyIndicatorAdapter extends CommonNavigatorAdapter {


    private ViewPager viewpager;
    String[] titles;
    List<String> titleList;
    int indictorMode = LinePagerIndicator.MODE_EXACTLY;

    public MyIndicatorAdapter(String[] titles, ViewPager viewpager) {
        this.titles = titles;
        this.viewpager = viewpager;
    }

    public MyIndicatorAdapter(String[] titles, ViewPager viewpager,int mode) {
        this.titles = titles;
        this.viewpager = viewpager;
        this.indictorMode = mode ;
    }

    public MyIndicatorAdapter(List<String> titleList, ViewPager viewpager) {
        this.viewpager = viewpager;
        this.titleList = titleList;

    }

    @Override
    public int getCount() {
        if (titles != null) {
            return titles.length;
        } else if (titleList != null) {
            return titleList.size();
        }else{
            return 0;
        }
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {

        SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
        if (titles != null) {
            simplePagerTitleView.setText(titles[index]);
        } else if (titleList != null) {
            simplePagerTitleView.setText(titleList.get(index));
        }

        simplePagerTitleView.setNormalColor(context.getResources().getColor(android.R.color.darker_gray));
        simplePagerTitleView.setSelectedColor(context.getResources().getColor(android.R.color.black));
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItem(index);
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(indictorMode);
        indicator.setLineHeight(UIUtil.dip2px(context, 3));
        indicator.setLineWidth(UIUtil.dip2px(context, 30));
        indicator.setRoundRadius(UIUtil.dip2px(context, 3));
        indicator.setStartInterpolator(new AccelerateInterpolator());
        indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
        indicator.setColors(context.getResources().getColor(R.color.colorPrimary));
        return indicator;
    }
}
