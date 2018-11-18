package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.GoodListParent;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.DirectSellingActivity;
import cn.idcby.jiajubang.view.RvGridManagerItemDecoration;

/**
 * Created on 2018/2/6.
 */

public class DirectLatestGoodPageAdapter extends PagerAdapter {
    private Context context;
    private List<GoodListParent> mDataList ;

    public DirectLatestGoodPageAdapter(Context context, List<GoodListParent> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return null == mDataList ? 0 : mDataList.size() ;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_item_for_home_recommend_shop, container ,false);

        RecyclerView mRv = view.findViewById(R.id.adapter_home_recommend_shop_rv) ;

        GridLayoutManager layoutManager = new GridLayoutManager(context, DirectSellingActivity.MAX_GOOD_SHOW) ;
        mRv.setLayoutManager(layoutManager);
        if(mRv.getItemDecorationCount() <= 0){
            mRv.addItemDecoration(new RvGridManagerItemDecoration(context,0 , ResourceUtils.dip2px(context ,1)
                    ,context.getResources().getDrawable(R.drawable.drawable_white_trans))) ;
        }
        mRv.setNestedScrollingEnabled(false);
        mRv.setFocusable(false);
        if(mDataList.size() > position){
            DirectLatestGoodChildAdapter childAdapter = new DirectLatestGoodChildAdapter(context
                    ,mDataList.get(position).getChildGood()) ;
            mRv.setAdapter(childAdapter);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
