package cn.idcby.jiajubang.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseFragment;
import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.FragmentPagerAdapter;
import cn.idcby.jiajubang.adapter.FragmentPagerOtherAdapter;
import cn.idcby.jiajubang.adapter.IndicatorFragmentAdapter;
import cn.idcby.jiajubang.adapter.MyGreenIndicatorAdapter;
import cn.idcby.jiajubang.events.BusEvent;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.SkipUtils;

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
 * Created by mrrlb on 2018/2/3.
 * 圈子
 */

public class CircleFragment extends BaseFragment implements View.OnClickListener {

    private String[] titles = {"关注", "热门", "同城"};
    private MyGreenIndicatorAdapter myIndicatorAdapter;
    private FragmentPagerOtherAdapter orderFragmentAdapter;
    private List<Fragment> fragmentList;
    private CircleSameCityFragment mSameCityCircleFragment;

    private TextView mMessageCountTv;
    private TabLayout magicIndicator;
    private ViewPager mViewPager;
    private ImageView mBackIv;

    private static final int REQUEST_CODE_SEND_CIRCLE = 1001 ;


    @Override
    protected void requestData() {
        loadPage.showSuccessPage();
        initIndicator();
    }

    private void initIndicator() {
//        myIndicatorAdapter = new MyGreenIndicatorAdapter(titles, mViewPager);
//        CommonNavigator commonNavigator = new CommonNavigator(mContext);
//        commonNavigator.setAdjustMode(true);
//        commonNavigator.setSkimOver(true);
//        commonNavigator.setAdapter(myIndicatorAdapter);
//        magicIndicator.setNavigator(commonNavigator);
        magicIndicator.setupWithViewPager(mViewPager);
        if (fragmentList == null)
            fragmentList = new ArrayList<>();
        CircleFollowFragment followCircleFragment = new CircleFollowFragment();
        CircleHotFragment mHotCircleFragment = new CircleHotFragment();
        mSameCityCircleFragment = new CircleSameCityFragment();
        fragmentList.add(followCircleFragment);
        fragmentList.add(mHotCircleFragment);
        fragmentList.add(mSameCityCircleFragment);

        orderFragmentAdapter
                = new FragmentPagerOtherAdapter(getChildFragmentManager(), fragmentList,titles);
        mViewPager.setAdapter(orderFragmentAdapter);
        mViewPager.setOffscreenPageLimit(4) ;
//        ViewPagerHelper.bind(magicIndicator, mViewPager);
        mViewPager.setCurrentItem(1) ;
    }

    @Override
    protected void initView(View view) {
        mMessageCountTv=view.findViewById(R.id.frag_home_msg_tv);
        magicIndicator = view.findViewById(R.id.magic_indicator);
        mViewPager = view.findViewById(R.id.viewpager);
        mBackIv = view.findViewById(R.id.img_circle_left);

        View statusView = view.findViewById(R.id.frag_circle_status_view);
        statusView.getLayoutParams().height = ResourceUtils.getStatusBarHeight(mContext) ;
        statusView.setBackgroundColor(StatusBarUtil.DEFAULT_STATUS_BAR_COLOR);
    }

    @Override
    protected int setSuccessViewId() {
        return R.layout.fragment_circle;
    }

    @Override
    protected void initListener() {
        mBackIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_circle_left:
                sendCircle() ;
                break;
            case R.id.iv_msg:
                SkipUtils.toMessageCenterActivity(mContext);
                SPUtils.newIntance(mContext).resetUnreadMessage();
                mMessageCountTv.setVisibility(View.GONE);
                break;
        }
    }

    private void sendCircle() {
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mFragment ,REQUEST_CODE_SEND_CIRCLE);
            return ;
        }

        SkipUtils.toSendCircleActivity(mContext);
    }

    public void changeItem(int index){
        mViewPager.setCurrentItem(index >= 0 ? index : 1) ;
    }

    /**
     * 刷新城市更换影响的模块
     */
    public void updateCityChangeDisplay(){
        //需要更新同城数据
        if(mSameCityCircleFragment != null){
            mSameCityCircleFragment.updateInfoByCityChange() ;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(BusEvent.UnreadMsgEvent ev) {
        mMessageCountTv.setVisibility(ev.isHas() ? View.VISIBLE : View.GONE);
//        mMessageCountTv.setText("") ;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_SEND_CIRCLE == requestCode){
            if(Activity.RESULT_OK == resultCode){
                sendCircle();
            }
        }
    }
}
