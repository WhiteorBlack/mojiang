package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import cn.idcby.jiajubang.R;


/**
 * 订单样式，有图标数量
 */

public class OrderIndicatorAdapter extends CommonNavigatorAdapter {
    private ViewPager viewpager;
    private String[] titles;
    private int[] counts;
    private int indictorMode = LinePagerIndicator.MODE_EXACTLY;

    public OrderIndicatorAdapter(String[] titles, int[] counts, ViewPager viewpager) {
        this.titles = titles;
        this.counts = counts;
        this.viewpager = viewpager;
    }
    public OrderIndicatorAdapter(String[] titles, int[] counts, ViewPager viewpager, int mode) {
        this.titles = titles;
        this.counts = counts;
        this.viewpager = viewpager;
        this.indictorMode = mode;
    }

    @Override
    public int getCount() {
        if (titles != null) {
            return titles.length;
        }else{
            return 0;
        }
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
        commonPagerTitleView.setContentView(R.layout.adapter_my_order_nav_item);

        final TextView mTitleTv = commonPagerTitleView.findViewById(R.id.adapter_my_order_nav_title_tv) ;
        final TextView mCountTv = commonPagerTitleView.findViewById(R.id.adapter_my_order_nav_count_tv) ;

        if (titles != null) {
            mTitleTv.setText(titles[index]);
        }
        if (counts != null) {
            int count = counts[index] ;
            mCountTv.setText(count + "");
            mCountTv.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }

        final int nomalColor = context.getResources().getColor(R.color.color_nomal_text) ;
        final int selectedColor = context.getResources().getColor(R.color.colorPrimary) ;
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
        indicator.setLineHeight(UIUtil.dip2px(context, 3));
        indicator.setLineWidth(UIUtil.dip2px(context, 30));
        indicator.setRoundRadius(UIUtil.dip2px(context, 3));
        indicator.setStartInterpolator(new AccelerateInterpolator());
        indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
        indicator.setColors(context.getResources().getColor(R.color.colorPrimary));
        return indicator;
    }
}
