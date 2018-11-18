package cn.idcby.jiajubang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.idcby.jiajubang.fragment.FragmentNeedsOrder;

/**
 * Created on 2018/4/16.
 */

public class AdapterNeedsOrderListPage extends FragmentPagerAdapter {
    private String[] titles ;
    private int[] mOrderStatus ;
    private int mOrderType ;

    public AdapterNeedsOrderListPage(FragmentManager fm, String[] titles  , int orderType , int[] orderStatus) {
        super(fm);
        this.titles = titles ;
        this.mOrderStatus = orderStatus ;
        this.mOrderType = orderType ;
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentNeedsOrder.getInstance(mOrderStatus[position] , mOrderType) ;
    }

    @Override
    public int getCount() {
        return null == titles ? 0 : titles.length;
    }
}
