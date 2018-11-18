package cn.idcby.jiajubang.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.SPUtils;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.PagerAdapterGuide;

/**
 * 第一次启动导航
 * Created on 2018/6/6.
 */

public class FirstGuideActivity extends BaseActivity {

    public static void launch(Context context){
        Intent toGiIt = new Intent(context ,FirstGuideActivity.class) ;
        context.startActivity(toGiIt) ;
    }

    @Override
    public int getLayoutID() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_first_guide ;
    }

    @Override
    public void initView() {
        super.initView();

        initPageAdapter() ;
    }

    private void initPageAdapter(){
        SPUtils.newIntance(mContext).saveIsFirst(false);

        ViewPager viewPager = findViewById(R.id.acti_first_guide_vp) ;
        PagerAdapterGuide mAdapter = new PagerAdapterGuide(mActivity) ;
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(5) ;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void dealOhterClick(View view) {

    }

    @Override
    public void onBackPressed() {
        //屏蔽返回按钮
    }
}
