package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.dialog.LoadingDialog;
import cn.idcby.jiajubang.Bean.UserInfo;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.IndicatorFragmentAdapter;
import cn.idcby.jiajubang.adapter.MyGreenIndicatorAdapter;
import cn.idcby.jiajubang.fragment.MoneyMgBzjFragment;
import cn.idcby.jiajubang.fragment.MoneyMgJifenFragment;
import cn.idcby.jiajubang.fragment.MoneyMgYueFragment;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestObjectCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.Urls;

/**
 * 余额、积分、保证金
 * Created on 2018/5/18.
 */

public class MoneyManagerActivity extends BaseActivity {
    private LoadingDialog mDialog ;

    private UserInfo mUserInfo ;

    private MoneyMgYueFragment mYueFragment ;
    private MoneyMgJifenFragment mJifenFragment ;

    public static void launch(Context context ,int index){
        Intent toMmIt = new Intent(context ,MoneyManagerActivity.class) ;
        toMmIt.putExtra(SkipUtils.INTENT_VP_INDEX ,index) ;
        context.startActivity(toMmIt) ;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_money_manager ;
    }

    @Override
    public void initView() {
        int index = getIntent().getIntExtra(SkipUtils.INTENT_VP_INDEX , 0) ;
        if(index < 0){
            index = 0 ;
        }else if(index > 2){
            index = 0 ;
        }

        MagicIndicator magicIndicator = findViewById(R.id.acti_money_manager_indicator);
        ViewPager mViewPager = findViewById(R.id.acti_money_manager_vp);

        String[] titles = {"余额", "积分", "保证金"};
        MyGreenIndicatorAdapter myIndicatorAdapter = new MyGreenIndicatorAdapter(titles, mViewPager);
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdjustMode(false);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(myIndicatorAdapter);
        magicIndicator.setNavigator(commonNavigator);

        List<Fragment> fragmentList = new ArrayList<>(3) ;
        mYueFragment = new MoneyMgYueFragment();
        mJifenFragment = new MoneyMgJifenFragment();
        MoneyMgBzjFragment mBzjFragment = new MoneyMgBzjFragment();
        fragmentList.add(mYueFragment);
        fragmentList.add(mJifenFragment);
        fragmentList.add(mBzjFragment);

        IndicatorFragmentAdapter orderFragmentAdapter
                = new IndicatorFragmentAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(orderFragmentAdapter);
        mViewPager.setOffscreenPageLimit(3) ;
        ViewPagerHelper.bind(magicIndicator, mViewPager);
        mViewPager.setCurrentItem(index) ;
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

    private void updateDisplay(){
        if(mUserInfo != null){
            if(mYueFragment != null){
                mYueFragment.updateFragDisplay(mUserInfo);
            }
            if(mJifenFragment != null){
                mJifenFragment.updateFragDisplay(mUserInfo);
            }
        }
    }

    /**
     * 获取个人信息
     */
    private void getMyInfo(){
        if(null == mDialog){
            mDialog = new LoadingDialog(mContext) ;
        }
        mDialog.show();

        Map<String,String> paramMap = ParaUtils.getParaWithToken(mContext) ;
        NetUtils.getDataFromServerByPost(mContext, Urls.MY_INFO, paramMap
                , new RequestObjectCallBack<UserInfo>("getMyInfo" ,mContext ,UserInfo.class) {
                    @Override
                    public void onSuccessResult(UserInfo bean) {
                        mDialog.dismiss();

                        mUserInfo = bean ;
                        updateDisplay();
                    }
                    @Override
                    public void onErrorResult(String str) {
                        mDialog.dismiss();
                        updateDisplay();
                    }
                    @Override
                    public void onFail(Exception e) {
                        mDialog.dismiss();
                        updateDisplay();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getMyInfo() ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetUtils.cancelTag("getMyInfo");
    }
}
