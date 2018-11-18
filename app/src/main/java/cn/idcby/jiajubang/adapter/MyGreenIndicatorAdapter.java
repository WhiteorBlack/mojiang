package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
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

public class MyGreenIndicatorAdapter extends CommonNavigatorAdapter {
    private ViewPager viewpager;
    private int mTextSize = 14 ;
    String[] titles;
    List<String> titleList;
    int indictorMode = LinePagerIndicator.MODE_EXACTLY;

    public MyGreenIndicatorAdapter(String[] titles, ViewPager viewpager) {
        this.titles = titles;
        this.viewpager = viewpager;
    }
    public MyGreenIndicatorAdapter(String[] titles, ViewPager viewpager,int mode) {
        this.titles = titles;
        this.viewpager = viewpager;
        this.indictorMode = mode;
    }

    public MyGreenIndicatorAdapter(List<String> titleList, ViewPager viewpager) {
        this.viewpager = viewpager;
        this.titleList = titleList;
    }

    public MyGreenIndicatorAdapter(List<String> titleList, ViewPager viewpager,int mode) {
        this.viewpager = viewpager;
        this.titleList = titleList;
        this.indictorMode = mode ;
    }

    public MyGreenIndicatorAdapter(List<String> titleList, ViewPager viewpager,int mode,int textSize) {
        this.viewpager = viewpager;
        this.titleList = titleList;
        this.indictorMode = mode ;
        this.mTextSize = textSize ;
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

        simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,mTextSize) ;
        simplePagerTitleView.setNormalColor(context.getResources().getColor(android.R.color.black));
        simplePagerTitleView.setSelectedColor(context.getResources().getColor(R.color.colorPrimary));
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
