package cn.idcby.jiajubang.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterGoodOrderListPage;
import cn.idcby.jiajubang.adapter.MyGreenIndicatorAdapter;
import cn.idcby.jiajubang.fragment.FragmentGoodOrder;
import cn.idcby.jiajubang.fragment.FragmentMyOrderAfterSale;
import cn.idcby.jiajubang.fragment.MySendFragment;

/**
 * 闲置--我发布的、我卖出的
 * Created on 2018/5/19.
 */

public class MyCenterUnuseActivity extends BaseActivity {
    private TextView mTypeSendTv;
    private TextView mTypeOrderTv;

    private boolean mIsSend = true ;//我发布的

    //我卖出的
    private View mOrderLay;
    private MagicIndicator mOrderIndicator;
    private ViewPager mOrderViewPager;

    //我发布的
    private FrameLayout mSendLay;




    @Override
    public int getLayoutID() {
        return R.layout.activity_my_center_unuse;
    }

    @Override
    public void initView() {


        mTypeSendTv = findViewById(R.id.acti_my_center_type_left_tv) ;
        mTypeOrderTv = findViewById(R.id.acti_my_center_type_right_tv) ;
        mTypeSendTv.setOnClickListener(this);
        mTypeOrderTv.setOnClickListener(this);

        mOrderLay = findViewById(R.id.acti_my_center_unuse_order_lay) ;
        mOrderIndicator = findViewById(R.id.acti_my_center_unuse_order_indicator) ;
        mOrderViewPager = findViewById(R.id.acti_my_center_unuse_order_vp) ;

        mSendLay = findViewById(R.id.acti_my_center_unuse_send_lay);

        initMySend() ;
        initOrder() ;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    /**
     * 初始化 发布相关
     */
    private void initMySend(){
        FragmentManager manager = getSupportFragmentManager() ;
        manager.beginTransaction().add(mSendLay.getId()
                ,MySendFragment.getInstance(MySendFragment.SEND_TYPE_UNUSE)).commit() ;
    }

    /**
     * 初始化 订单相关
     */
    private void initOrder(){
        String[] titles = {"全部","待付款", "待发货", "待收货", "待评价", "售后"};
        MyGreenIndicatorAdapter myIndicatorAdapter = new MyGreenIndicatorAdapter(titles
                , mOrderViewPager, LinePagerIndicator.MODE_WRAP_CONTENT);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(false);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(myIndicatorAdapter);
        mOrderIndicator.setNavigator(commonNavigator);

        List<Fragment> fragments = new ArrayList<>(titles.length) ;
        fragments.add(FragmentGoodOrder.getInstance(0 , FragmentGoodOrder.GOOD_ORDER_TYPE_UNUSE)) ;
        fragments.add(FragmentGoodOrder.getInstance(1 , FragmentGoodOrder.GOOD_ORDER_TYPE_UNUSE)) ;
        fragments.add(FragmentGoodOrder.getInstance(2 , FragmentGoodOrder.GOOD_ORDER_TYPE_UNUSE)) ;
        fragments.add(FragmentGoodOrder.getInstance(3 , FragmentGoodOrder.GOOD_ORDER_TYPE_UNUSE)) ;
        fragments.add(FragmentGoodOrder.getInstance(4 , FragmentGoodOrder.GOOD_ORDER_TYPE_UNUSE)) ;
        fragments.add(FragmentMyOrderAfterSale.getInstance(FragmentMyOrderAfterSale.ORDER_AFTER_SALE_FROM_MY
                ,FragmentMyOrderAfterSale.ORDER_ITEM_TYPE_UNUSE)) ;

        AdapterGoodOrderListPage mSubsOrderPageAdapter = new AdapterGoodOrderListPage(getSupportFragmentManager()
                ,titles ,fragments) ;
        mOrderViewPager.setAdapter(mSubsOrderPageAdapter);
        ViewPagerHelper.bind(mOrderIndicator, mOrderViewPager);
    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_center_type_left_tv == vId){
            changeSendType(true) ;
        }else if(R.id.acti_my_center_type_right_tv == vId){
            changeSendType(false) ;
        }
    }

    /**
     *  切换类型
     *  @param isSend is mIsSend
     */
    private void changeSendType(boolean isSend){
        if(isSend == mIsSend){
            return ;
        }

        if(mIsSend){
            mTypeSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeSendTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }else{
            mTypeOrderTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeOrderTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }

        if(isSend){
            mTypeSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeSendTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }else{
            mTypeOrderTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeOrderTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }

        mIsSend = isSend ;

        mSendLay.setVisibility(mIsSend ? View.VISIBLE : View.GONE) ;
        mOrderLay.setVisibility(mIsSend ? View.GONE : View.VISIBLE) ;
    }

}
