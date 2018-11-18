package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.List;

import cn.idcby.jiajubang.R;


/**
 * 商品分类样式
 */

public class GoodIndicatorAdapter extends CommonNavigatorAdapter {
    private ViewPager viewpager;
    private List<String>  titles;
    private int indictorMode = LinePagerIndicator.MODE_WRAP_CONTENT;

    public GoodIndicatorAdapter(List<String> titles, ViewPager viewpager) {
        this.titles = titles;
        this.viewpager = viewpager;
    }

    @Override
    public int getCount() {
        if (titles != null) {
            return titles.size();
        }else{
            return 0;
        }
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
        commonPagerTitleView.setContentView(R.layout.adapter_good_category_nav_item);

        final TextView mTitleTv = commonPagerTitleView.findViewById(R.id.adapter_good_category_nav_title_tv) ;

        if (titles != null) {
            mTitleTv.setText(titles.get(index));
        }

        final int nomalColor = context.getResources().getColor(R.color.color_nomal_text) ;
        final int selectedColor = context.getResources().getColor(R.color.white) ;
        commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {
            @Override
            public void onSelected(int index, int totalCount) {
                mTitleTv.setTextColor(selectedColor);
            }

            @Override
            public void onDeselected(int index, int totalCount) {
                mTitleTv.setTextColor(nomalColor);
            }

            @Override
            public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
            }
            @Override
            public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
            }
        });

        commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItem(index);
            }
        });
        return commonPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(indictorMode);
        indicator.setLineHeight(0);
        indicator.setLineWidth(0);
        indicator.setRoundRadius(0);
        indicator.setStartInterpolator(new AccelerateInterpolator());
        indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
        indicator.setColors(context.getResources().getColor(R.color.colorPrimary));
        return indicator;
    }
}
