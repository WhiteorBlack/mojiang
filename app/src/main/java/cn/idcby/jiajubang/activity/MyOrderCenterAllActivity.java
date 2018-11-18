package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.ReceiveOrderCount;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterMyReceiveOrderListPage;
import cn.idcby.jiajubang.adapter.OrderIndicatorAdapter;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.fragment.FragmentMyOrderAfterSale;
import cn.idcby.jiajubang.fragment.FragmentMyReceiveOrder;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 我的订单--合并了订单样式和数据
 * Created on 2018/6/7.
 *
 * 订单状态：全部、待付款、进行中、待评价、售后
 */
public class MyOrderCenterAllActivity extends BaseActivity {
    private int[] mOrderCounts = new int[6] ;
    private OrderIndicatorAdapter mAdapter ;

    public static void launch(Context mContext ,int index){
        Intent toSvIt = new Intent(mContext , MyOrderCenterAllActivity.class) ;
        toSvIt.putExtra(SkipUtils.INTENT_VP_INDEX ,index) ;
        mContext.startActivity(toSvIt);
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_my_order_center_all;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this) ;

        int mIndex = getIntent().getIntExtra(SkipUtils.INTENT_VP_INDEX, 0) ;

        MagicIndicator mIndicator = findViewById(R.id.acti_my_order_center_all_indicator) ;
        ViewPager mViewPager = findViewById(R.id.acti_my_order_center_all_vp) ;

        String[] titles = {"全部","待付款", "待发货/待服务", "待收货/服务中", "待评价", "售后"};

        mAdapter = new OrderIndicatorAdapter(titles ,mOrderCounts ,mViewPager, LinePagerIndicator.MODE_WRAP_CONTENT) ;
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setLeftPadding(ResourceUtils.dip2px(mContext ,5));
        commonNavigator.setRightPadding(ResourceUtils.dip2px(mContext ,5));
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(mAdapter);
        mIndicator.setNavigator(commonNavigator);

        List<Fragment>  mFragList = new ArrayList<>(6) ;
        mFragList.add(FragmentMyReceiveOrder.getInstance(0)) ;
        mFragList.add(FragmentMyReceiveOrder.getInstance(1)) ;
        mFragList.add(FragmentMyReceiveOrder.getInstance(2)) ;
        mFragList.add(FragmentMyReceiveOrder.getInstance(3)) ;
        mFragList.add(FragmentMyReceiveOrder.getInstance(4)) ;
        mFragList.add(FragmentMyOrderAfterSale.getInstance(FragmentMyOrderAfterSale.ORDER_AFTER_SALE_FROM_RECEIVE)) ;

        AdapterMyReceiveOrderListPage mSubsOrderPageAdapter = new AdapterMyReceiveOrderListPage(getSupportFragmentManager()
                ,titles, mFragList) ;
        mViewPager.setAdapter(mSubsOrderPageAdapter);
        ViewPagerHelper.bind(mIndicator, mViewPager);

        if(mIndex >= 0 && mIndex < titles.length){
            mViewPager.setCurrentItem(mIndex) ;
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
    }

    @Override
    public void dealOhterClick(View view) {
    }


    @Override
    protected void onResume() {
        super.onResume();

        getOrderCount() ;
    }

    private void updateOrderCount(ReceiveOrderCount orderInfo){
        if(orderInfo != null){
            int waitPay = orderInfo.getWaitPay() ;
            int waitSend = orderInfo.getWaitSend() ;
            int waitReceive = orderInfo.getWaitReceive() ;
            int waitEvaluate = orderInfo.getWaitEvaluate() ;
            int afterSale = orderInfo.getAfterSale() ;

            mOrderCounts[0] = 0 ;//全部，默认为 0，即 不显示全部
            mOrderCounts[1] = waitPay ;
            mOrderCounts[2] = waitSend ;
            mOrderCounts[3] = waitReceive ;
            mOrderCounts[4] = waitEvaluate ;
            mOrderCounts[5] = afterSale ;

            mAdapter.notifyDataSetChanged() ;
        }
    }

    /**
     * 获取订单数量
     */
    private void getOrderCount(){
        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;

        NetUtils.getDataFromServerByPost(mContext, Urls.MY_RECEIVE_ORDER_COUNT, paramMap
                , new RequestObjectCallBack<ReceiveOrderCount>("getOrderCountAll" ,mContext ,ReceiveOrderCount.class) {
                    @Override
                    public void onSuccessResult(ReceiveOrderCount bean) {
                        updateOrderCount(bean) ;
                    }
                    @Override
                    public void onErrorResult(String str) {
                    }
                    @Override
                    public void onFail(Exception e) {
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.OrderCountRefresh ev){
        if(ev.isRefresh()){//需要刷新
            getOrderCount() ;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        NetUtils.cancelTag("getOrderCountAll") ;
    }
}
