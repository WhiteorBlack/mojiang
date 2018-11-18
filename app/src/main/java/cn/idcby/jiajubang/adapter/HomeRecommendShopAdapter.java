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
import cn.idcby.jiajubang.Bean.HomeStoreParent;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.fragment.NearFragment;
import cn.idcby.jiajubang.view.RvGridManagerItemDecoration;

/**
 * Created on 2018/2/6.
 *
 * 2018-08-22 17:24:18
 * 店铺排版改为 2张方图
 */

public class HomeRecommendShopAdapter extends PagerAdapter {
    private Context context;
    private List<HomeStoreParent> mDataList ;

    public HomeRecommendShopAdapter(Context context,List<HomeStoreParent> mDataList) {
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

        GridLayoutManager layoutManager = new GridLayoutManager(context, NearFragment.MAX_STORE_SHOW) ;
        mRv.setLayoutManager(layoutManager);
        mRv.addItemDecoration(new RvGridManagerItemDecoration(context,0 , ResourceUtils.dip2px(context ,10)
                ,context.getResources().getDrawable(R.drawable.drawable_white_trans))) ;
        mRv.setNestedScrollingEnabled(false);
        mRv.setFocusable(false);
        if(mDataList.size() > position){
            HomeRecommendShopChildAdapter childAdapter = new HomeRecommendShopChildAdapter(context
                    ,mDataList.get(position).getStoreList()) ;
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
