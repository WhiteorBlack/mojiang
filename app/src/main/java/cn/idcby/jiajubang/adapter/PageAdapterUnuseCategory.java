package cn.idcby.jiajubang.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.idcby.jiajubang.Bean.UnusedCategory;
import cn.idcby.jiajubang.Bean.UnuseCategoryPre;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.view.StationaryGridView;

/**
 * Created on 2018/2/6.
 */

public class PageAdapterUnuseCategory extends PagerAdapter {
    private Context context;
    private List<UnuseCategoryPre> mDataList ;

    public PageAdapterUnuseCategory(Context context,List<UnuseCategoryPre> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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
        View view = View.inflate(context, R.layout.adapter_unused_category_pager, null);
        container.addView(view);
        StationaryGridView gridView = view.findViewById(R.id.adapter_unused_category_pager_gv) ;

        final List<UnusedCategory> categoryLists = mDataList.get(position).getCategoryList() ;
        AdapterUnuseCategory adapter = new AdapterUnuseCategory(context ,categoryLists) ;
        gridView.setAdapter(adapter);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
