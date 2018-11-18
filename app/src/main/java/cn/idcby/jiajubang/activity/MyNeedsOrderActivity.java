package cn.idcby.jiajubang.activity;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterNeedsOrderListPage;
import cn.idcby.jiajubang.adapter.MyGreenIndicatorAdapter;
import cn.idcby.jiajubang.fragment.FragmentNeedsOrder;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 我的需求订单
 * Created on 2018/4/16.
 *
 * 2018-04-19 20:31:08
 * 注意我的需求 和 我发起的需求 ，分别是  2 和 1
 *
 * 我发起的需求 就是 我发布的需求，然后选了别人中标，只能看不能操作
 * 我的需求  就是 我去报价，然后中标的需求，是别人发布的需求
 *
 * 2018-05-10 09:25:47
 * 文字调整
 * 我的需求 改成 需求订单 ；我发起的需求 改成 供应订单
 *
 */
@Deprecated
public class MyNeedsOrderActivity extends BaseActivity {
    private TextView mTypeReceiveTv;
    private TextView mTypeSendTv;

    private View mReceiveLay;
    private MagicIndicator mReceiveIndicator;
    private ViewPager mReceiveViewPager;

    private View mSendLay;
    private MagicIndicator mSendIndicator;
    private ViewPager mSendViewPager;

    private int mIndex = 0 ;
    private boolean mIsReceive = true ;//我的需求



    @Override
    public int getLayoutID() {
        return R.layout.activity_my_needs_order;
    }

    @Override
    public void initView() {
        mIndex = getIntent().getIntExtra(SkipUtils.INTENT_SERVER_ORDER_INDEX, 0) ;

        mTypeReceiveTv = findViewById(R.id.acti_my_needs_order_type_receive_tv) ;
        mTypeSendTv = findViewById(R.id.acti_my_needs_order_type_send_tv) ;

        mReceiveLay = findViewById(R.id.acti_my_needs_order_receive_lay) ;
        mReceiveIndicator = findViewById(R.id.acti_my_needs_order_receive_indicator) ;
        mReceiveViewPager = findViewById(R.id.acti_my_needs_order_receive_vp) ;

        mSendLay = findViewById(R.id.acti_my_needs_order_send_lay) ;
        mSendIndicator = findViewById(R.id.acti_my_needs_order_send_indicator) ;
        mSendViewPager = findViewById(R.id.acti_my_needs_order_send_vp) ;

        initReceiveOrder() ;
        initSendOrder() ;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        mTypeReceiveTv.setOnClickListener(this) ;
        mTypeSendTv.setOnClickListener(this) ;

    }

    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_my_needs_order_type_receive_tv == vId){
            changeSendType(true) ;
        }else if(R.id.acti_my_needs_order_type_send_tv == vId){
            changeSendType(false) ;
        }

    }

    /**
     * 初始化 我的需求
     */
    private void initReceiveOrder(){
        String[] titles = {"待付款", "待完成", "已完成", "已取消"};
        int[] orderStatus = {1,2,4,3};
        MyGreenIndicatorAdapter myIndicatorAdapter = new MyGreenIndicatorAdapter(titles
                , mReceiveViewPager,LinePagerIndicator.MODE_EXACTLY);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(myIndicatorAdapter);
        mReceiveIndicator.setNavigator(commonNavigator);

        AdapterNeedsOrderListPage mSubsOrderPageAdapter = new AdapterNeedsOrderListPage(getSupportFragmentManager()
                ,titles , FragmentNeedsOrder.NEED_ORDER_TYPE_MY, orderStatus) ;
        mReceiveViewPager.setAdapter(mSubsOrderPageAdapter);
        ViewPagerHelper.bind(mReceiveIndicator, mReceiveViewPager);

        if(mIndex >= 0 && mIndex < titles.length){
            mReceiveViewPager.setCurrentItem(mIndex) ;
        }
    }

    /**
     * 初始化 我发起的需求
     */
    private void initSendOrder(){
        String[] titles = {"待付款", "待完成", "已完成", "已取消"};
        int[] orderStatus = {1,2,4,3};
        MyGreenIndicatorAdapter myIndicatorAdapter = new MyGreenIndicatorAdapter(titles
                , mSendViewPager, LinePagerIndicator.MODE_WRAP_CONTENT);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(myIndicatorAdapter);
        mSendIndicator.setNavigator(commonNavigator);

        AdapterNeedsOrderListPage mSubsOrderPageAdapter = new AdapterNeedsOrderListPage(getSupportFragmentManager()
                ,titles ,FragmentNeedsOrder.NEED_ORDER_TYPE_SEND , orderStatus) ;
        mSendViewPager.setAdapter(mSubsOrderPageAdapter);
        ViewPagerHelper.bind(mSendIndicator, mSendViewPager);
    }

    /**
     *  切换类型
     *  @param isReceive is receive
     */
    private void changeSendType(boolean isReceive){
        if(isReceive == mIsReceive){
            return ;
        }

        if(mIsReceive){
            mTypeReceiveTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeReceiveTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }else{
            mTypeSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white_trans)) ;
            mTypeSendTv.setTextColor(getResources().getColor(R.color.color_theme)) ;
        }

        if(isReceive){
            mTypeReceiveTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeReceiveTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }else{
            mTypeSendTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_theme_small_bg)) ;
            mTypeSendTv.setTextColor(getResources().getColor(R.color.color_white)) ;
        }

        mIsReceive = isReceive ;

        mSendLay.setVisibility(mIsReceive ? View.GONE : View.VISIBLE) ;
        mReceiveLay.setVisibility(mIsReceive ? View.VISIBLE : View.GONE) ;
    }


}
