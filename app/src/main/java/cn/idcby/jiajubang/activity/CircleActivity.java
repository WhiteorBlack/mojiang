package cn.idcby.jiajubang.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.IndicatorFragmentAdapter;
import cn.idcby.jiajubang.adapter.MyGreenIndicatorAdapter;
import cn.idcby.jiajubang.fragment.CircleFollowFragment;
import cn.idcby.jiajubang.fragment.CircleHotFragment;
import cn.idcby.jiajubang.fragment.CircleSameCityFragment;
import cn.idcby.jiajubang.utils.LoginHelper;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * Created on 2018/5/7.
 * 圈子
 *
 * 2018-05-07 16:07:00
 * 从首页剥离出来的，暂时保持首页之前的格式
 *
 * 2018-05-26 10:53:00
 * 暂时还原成以前的，如果有需要，再改
 */

public class CircleActivity extends BaseActivity {
    private TextView mMessageCountTv ;
    private MagicIndicator magicIndicator;
    private ViewPager mViewPager;

    private static final int REQUEST_CODE_SEND_CIRCLE = 1001 ;
    private static final int REQUEST_CODE_SEND_CIRCLE_RESULT = 1002 ;


    @Override
    public int getLayoutID() {
        return R.layout.activity_circle ;
    }

    @Override
    public void initView() {
        mMessageCountTv = findViewById(R.id.frag_circle_msg_tv) ;
        magicIndicator = findViewById(R.id.acti_circle_magic_indicator);
        mViewPager = findViewById(R.id.acti_circle_vp);

        View addIv = findViewById(R.id.acti_circle_add_iv);
        View rightIv = findViewById(R.id.img_circle_right);
        addIv.setOnClickListener(this);
        rightIv.setOnClickListener(this);
    }

    @Override
    public void initData() {
        initIndicator();
    }

    @Override
    public void initListener() {

    }

    private void initIndicator() {
        String[] titles = {"关注", "热门", "同城"};
        MyGreenIndicatorAdapter myIndicatorAdapter = new MyGreenIndicatorAdapter(titles, mViewPager);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(myIndicatorAdapter);
        magicIndicator.setNavigator(commonNavigator);

        List<Fragment> fragmentList = new ArrayList<>();
        CircleFollowFragment followCircleFragment = new CircleFollowFragment();
        CircleHotFragment hotCircleFragment = new CircleHotFragment();
        CircleSameCityFragment mSameCityCircleFragment = new CircleSameCityFragment();
        fragmentList.add(followCircleFragment);
        fragmentList.add(hotCircleFragment);
        fragmentList.add(mSameCityCircleFragment);

        IndicatorFragmentAdapter orderFragmentAdapter = new IndicatorFragmentAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(orderFragmentAdapter);
        mViewPager.setOffscreenPageLimit(4) ;
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }


    @Override
    public void dealOhterClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_circle_add_iv == vId){
            sendCircle() ;
        }else if(R.id.img_circle_right == vId){
            SkipUtils.toMessageCenterActivity(mContext) ;
        }
    }


    private void sendCircle() {
        if(LoginHelper.isNotLogin(mContext)){
            SkipUtils.toLoginActivityForResult(mActivity ,REQUEST_CODE_SEND_CIRCLE);
            return ;
        }
        SkipUtils.toSendCircleActivity(mActivity ,REQUEST_CODE_SEND_CIRCLE_RESULT) ;
    }

    @Override
    public void onMessageCountChange(int count) {
        super.onMessageCountChange(count);

        mMessageCountTv.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        mMessageCountTv.setText(count > 99 ? "99+" : ("" + count));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_SEND_CIRCLE == requestCode){
            if(RESULT_OK == resultCode){
                SkipUtils.toSendCircleActivity(mContext);
            }
        }else if(REQUEST_CODE_SEND_CIRCLE_RESULT == requestCode){
            if(RESULT_OK == resultCode){

            }
        }

    }
}
